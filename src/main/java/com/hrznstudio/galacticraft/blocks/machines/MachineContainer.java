package com.hrznstudio.galacticraft.blocks.machines;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Property;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public abstract class MachineContainer<T extends MachineBlockEntity> extends Container {

    @FunctionalInterface
    public interface MachineContainerConstructor<C, T> {
        C create(int syncId, PlayerEntity player, T blockEntity);
    }

    public final PlayerEntity playerEntity;
    public final T blockEntity;

    public final Property energy = Property.create();

    protected MachineContainer(int syncId, PlayerEntity playerEntity, T blockEntity) {
        super(null, syncId);
        this.playerEntity = playerEntity;
        this.blockEntity = blockEntity;
        addProperty(energy);
    }

    public static <C extends MachineContainer<T>, T extends MachineBlockEntity> ContainerFactory<Container> createFactory(
        Class<T> machineClass, MachineContainerConstructor<C, T> constructor) 
    {
        return (syncId, id, player, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            BlockEntity be = player.world.getBlockEntity(pos);
            if (machineClass.isInstance(be)) {
                return constructor.create(syncId, player, machineClass.cast(be));
            } else {
                return null;
            }
        };
    }

    @Override
    public void sendContentUpdates() {
        energy.set(blockEntity.getEnergy().getCurrentEnergy());
        super.sendContentUpdates();
    }

    public int getMaxEnergy() {
        return blockEntity.getMaxEnergy();
    }
}
