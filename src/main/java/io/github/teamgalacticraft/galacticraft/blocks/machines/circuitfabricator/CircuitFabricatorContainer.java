package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;

public class CircuitFabricatorContainer extends Container {

    private Inventory inventory;

    private BlockPos blockPos;
    private CoalGeneratorBlockEntity generator;
    private PlayerEntity playerEntity;


    protected CircuitFabricatorContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }
}
