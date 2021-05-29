package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MachineBlockEntityTicker<T extends BlockEntity> implements BlockEntityTicker<T> {
    protected static final MachineBlockEntityTicker<? extends MachineBlockEntity> INSTANCE = new MachineBlockEntityTicker<>();

    private MachineBlockEntityTicker() {}

    public static <T extends BlockEntity> MachineBlockEntityTicker<T> getInstance() {
        return (MachineBlockEntityTicker<T>) INSTANCE;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, T machine) {
        ((MachineBlockEntity) machine).tick(world, pos, state);
    }
}
