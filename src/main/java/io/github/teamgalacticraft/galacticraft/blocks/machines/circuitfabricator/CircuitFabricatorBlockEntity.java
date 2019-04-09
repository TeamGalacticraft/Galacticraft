package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.Galacticraft;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class CircuitFabricatorBlockEntity extends BlockEntity implements Tickable {

    SimpleFixedItemInv inventory = new SimpleFixedItemInv(7);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);
    private final int maxProgress = 300;
    private int progress;

    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;

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
            this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
        }

        if (getEnergy().getCurrentEnergy() < 1) {
            status = CircuitFabricatorStatus.INACTIVE;
        }

        if (status == CircuitFabricatorStatus.INACTIVE) {
            this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            return;
        }

        if (isValidRecipe(this.inventory.getInvStack(5))) {
            if (canPutStackInResultSlot(getResultFromRecipeStack())) {
                this.status = CircuitFabricatorStatus.ACTIVE;
            }
        }
        else {
            this.status = CircuitFabricatorStatus.IDLE;
        }

        if (status == CircuitFabricatorStatus.ACTIVE) {

            ItemStack resultStack = getResultFromRecipeStack();
            if (inventory.getInvStack(6) == null || inventory.getInvStack(6).getItem() == resultStack.getItem()) {
                if (inventory.getInvStack(6).getAmount() < resultStack.getMaxAmount()) {
                    if (progress <= maxProgress) {

                        ++progress;
                        this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    } else {
                        progress = 0;

                        for (int i = 0; i < inventory.getSlotCount() - 1; i++) {
                            inventory.getInvStack(i).subtractAmount(1);

                            if (inventory.getInvStack(6) != null) {
                                inventory.getInvStack(6).addAmount(1);
                            } else {
                                inventory.setInvStack(6, resultStack, Simulation.ACTION);
                            }
                        }
                    }
                }
            }
        }
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        return new ItemStack(Items.DIRT);
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {

        if (inventory.getInvStack(6) == null) {
            return true;
        }
        if (inventory.getInvStack(6).getItem() == itemStack.getItem()) {
            return inventory.getInvStack(6).getAmount() < itemStack.getMaxAmount();
        }
        return false;
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

    // This is just for testing
    private boolean isValidRecipe(ItemStack input) {
        return input != null;
    }

    // Tries charging the block entity with the given itemstack
    // if it has energy in it's tag.
    private void attemptChargeFromStack(ItemStack itemStack) {

        if (itemStack != null) {
            if (GalacticraftEnergy.isEnergyItem(itemStack)) {
                if (itemStack.getTag().getInt("Energy") > 0 && energy.canInsertEnergy()) {
                    energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    itemStack.getTag().putInt("Energy", itemStack.getTag().getInt("Energy") - 1);
                }
            }
        }
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.put("Energy", energy.toTag());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        inventory.fromTag(tag.getCompound("Inventory"));
        energy.fromTag(tag.getTag("Energy"));
    }
}
