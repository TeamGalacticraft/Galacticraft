package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class CircuitFabricatorBlockEntity extends BlockEntity implements Tickable {

    SimpleFixedItemInv inventory = new SimpleFixedItemInv(7);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);

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

    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    public FixedItemInv getItems() {
        return this.inventory;
    }
}
