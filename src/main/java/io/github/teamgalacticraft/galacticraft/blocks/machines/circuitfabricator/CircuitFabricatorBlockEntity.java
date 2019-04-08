package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class CircuitFabricatorBlockEntity extends BlockEntity implements Tickable {

    SimpleFixedItemInv inventory = new SimpleFixedItemInv(7);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);
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

        if (status == CircuitFabricatorStatus.ACTIVE) {
            progress++;
        }

        if (status == CircuitFabricatorStatus.IDLE) {
            this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
        }

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOptions.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.energy.setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
                }
            }
        }
    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    public FixedItemInv getItems() {
        return this.inventory;
    }


    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Inventory", inventory.toTag());
        tag.put("Energy", energy.toTag());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        inventory.fromTag(tag.getCompound("Inventory"));
        energy.fromTag(tag.getTag("Energy"));
    }
}
