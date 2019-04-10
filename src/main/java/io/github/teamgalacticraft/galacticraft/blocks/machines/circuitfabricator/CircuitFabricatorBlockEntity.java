package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.recipes.FabricationRecipe;
import io.github.teamgalacticraft.galacticraft.recipes.GalacticraftRecipes;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;

public class CircuitFabricatorBlockEntity extends BlockEntity implements Tickable {

    SimpleFixedItemInv inventory = new SimpleFixedItemInv(7);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);
    private final int maxProgress = 300;
    private int progress;

    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;
    public final Item[] mandatoryMaterials = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};

    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_INPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_BLOCK_ENTITY_BLOCK_ENTITY_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        this.energy.listen(this::markDirty);
        selectedOptions.put(Direction.SOUTH, SideOptions.POWER_INPUT);
    }

    @Override
    public void tick() {
        int prev = energy.getCurrentEnergy();

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOptions.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.energy.setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
                }
            }
        }
        attemptChargeFromStack(this.inventory.getInvStack(0));


        if (status == CircuitFabricatorStatus.IDLE) {
            //this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            this.progress = 0;
        }


        if (getEnergy().getCurrentEnergy() <= 0) {
            status = CircuitFabricatorStatus.INACTIVE;
        } else {
            status = CircuitFabricatorStatus.IDLE;
        }


        if (status == CircuitFabricatorStatus.INACTIVE) {
            //this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            this.progress = 0;
            return;
        }


        if (isValidRecipe(this.inventory.getInvStack(5))) {
            if (canPutStackInResultSlot(getResultFromRecipeStack())) {
                this.status = CircuitFabricatorStatus.PROCESSING;
            }
        } else {
            if (this.status != CircuitFabricatorStatus.INACTIVE) {
                this.status = CircuitFabricatorStatus.IDLE;
            }
        }

        if (status == CircuitFabricatorStatus.PROCESSING) {

            ItemStack resultStack = getResultFromRecipeStack();
            if (inventory.getInvStack(6).isEmpty() || inventory.getInvStack(6).getItem() == resultStack.getItem()) {
                if (inventory.getInvStack(6).getAmount() < resultStack.getMaxAmount()) {
                    if (this.progress <= this.maxProgress) {

                        ++progress;
                        this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    } else {
                        System.out.println("Finished crafting an item.");
                        this.progress = 0;

                        inventory.getInvStack(1).subtractAmount(1);
                        inventory.getInvStack(2).subtractAmount(1);
                        inventory.getInvStack(3).subtractAmount(1);
                        inventory.getInvStack(4).subtractAmount(1);
                        inventory.getInvStack(5).subtractAmount(1);

                        if (!inventory.getInvStack(6).isEmpty()) {
                            inventory.getInvStack(6).addAmount(resultStack.getAmount());
                        } else {
                            inventory.setInvStack(6, resultStack, Simulation.ACTION);
                        }
                    }
                }
            }
        }
    }

    private ItemStack getResultFromRecipeStack() {
        BasicInventory inv = new BasicInventory(inventory.getInvStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        FabricationRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("No recipe present????"));
        return recipe.craft(inv);
    }

    private Optional<FabricationRecipe> getRecipe(BasicInventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.FABRICATION_TYPE, input, this.world);
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        if (inventory.getInvStack(6).isEmpty()) {
            return true;
        } else if (inventory.getInvStack(6).getItem() == itemStack.getItem()) {
            return inventory.getInvStack(6).getAmount() < itemStack.getMaxAmount();
        } else {
            return false;
        }
    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    public FixedItemInv getItems() {
        return this.inventory;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    private boolean isValidRecipe(ItemStack input) {
        // TODO check up on this
        return getRecipe(new BasicInventory(input)).isPresent() && hasMandatoryMaterials();
//        return !input.isEmpty() && hasMandatoryMaterials();
    }

    private boolean hasMandatoryMaterials() {
        return inventory.getInvStack(1).getItem() == mandatoryMaterials[0] &&
                inventory.getInvStack(2).getItem() == mandatoryMaterials[1] &&
                inventory.getInvStack(3).getItem() == mandatoryMaterials[2] &&
                inventory.getInvStack(4).getItem() == mandatoryMaterials[3];
    }

    // Tries charging the block entity with the given itemstack
    // if it has energy in it's tag.
    private void attemptChargeFromStack(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (GalacticraftEnergy.isEnergyItem(itemStack)) {
                if (itemStack.getTag().getInt("Energy") > 0 && energy.getCurrentEnergy() < energy.getMaxEnergy()) {
                    this.energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    itemStack.getTag().putInt("Energy", itemStack.getTag().getInt("Energy") - 1);
                    itemStack.setDamage(itemStack.getDamage() + 1);
                }
            }
        }
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.putInt("Energy", energy.getCurrentEnergy());
        tag.putInt("Progress", this.progress);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.energy.setCurrentEnergy(tag.getInt("Energy"));
        this.progress = tag.getInt("Progress");
    }
}
