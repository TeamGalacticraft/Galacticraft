package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.api.configurable.SideOptions;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.util.BlockOptionUtils;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlockEntity extends MachineBlockEntity implements Tickable {
    public final Item[] mandatoryMaterials = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};
    private final int maxProgress = 300;
    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;
    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_INPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();
    private int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        selectedOptions.put(Direction.SOUTH, SideOptions.POWER_INPUT);
    }

    @Override
    protected int getInvSize() {
        return 7;
    }

    @Override
    public void tick() {
        int prev = getEnergy().getCurrentEnergy();

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOptions.POWER_INPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.getEnergy().setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
                }
            }
        }
        // Inventory stack 0 will only ever be a battery because the slot only accepts that type.
        attemptChargeFromStack(this.getInventory().getInvStack(0));


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


        if (isValidRecipe(this.getInventory().getInvStack(5))) {
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
            if (getInventory().getInvStack(6).isEmpty() || getInventory().getInvStack(6).getItem() == resultStack.getItem()) {
                if (getInventory().getInvStack(6).getAmount() < resultStack.getMaxAmount()) {
                    if (this.progress < this.maxProgress) {
                        ++progress;
                        this.getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    } else {
                        System.out.println("Finished crafting an item.");
                        this.progress = 0;

                        if (!world.isClient) {
                            getInventory().getInvStack(1).subtractAmount(1);
                            getInventory().getInvStack(2).subtractAmount(1);
                            getInventory().getInvStack(3).subtractAmount(1);
                            getInventory().getInvStack(4).subtractAmount(1);
                            getInventory().getInvStack(5).subtractAmount(1);

                            if (!getInventory().getInvStack(6).isEmpty()) {
                                getInventory().getInvStack(6).addAmount(resultStack.getAmount());
                            } else {
                                getInventory().setInvStack(6, resultStack, Simulation.ACTION);
                            }
                        }

                        markDirty();
                    }
                }
            }
        }
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        BasicInventory inv = new BasicInventory(getInventory().getInvStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        FabricationRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("No recipe present????"));
        return recipe.craft(inv);
    }

    private Optional<FabricationRecipe> getRecipe(BasicInventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.FABRICATION_TYPE, input, this.world);
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        if (getInventory().getInvStack(6).isEmpty()) {
            return true;
        } else if (getInventory().getInvStack(6).getItem() == itemStack.getItem()) {
            return (getInventory().getInvStack(6).getAmount() + itemStack.getAmount()) <= itemStack.getMaxAmount();
        } else {
            return false;
        }
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

    // This is just for testing
    private boolean isValidRecipe(ItemStack input) {
        // TODO check up on this
        return getRecipe(new BasicInventory(input)).isPresent() && hasMandatoryMaterials();
//        return !input.isEmpty() && hasMandatoryMaterials();
    }

    private boolean hasMandatoryMaterials() {
        return getInventory().getInvStack(1).getItem() == mandatoryMaterials[0] &&
                getInventory().getInvStack(2).getItem() == mandatoryMaterials[1] &&
                getInventory().getInvStack(3).getItem() == mandatoryMaterials[2] &&
                getInventory().getInvStack(4).getItem() == mandatoryMaterials[3];
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Progress", this.progress);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        progress = tag.getInt("Progress");
    }
}