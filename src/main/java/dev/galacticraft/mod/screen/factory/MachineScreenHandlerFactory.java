package dev.galacticraft.mod.screen.factory;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class MachineScreenHandlerFactory<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> {
    private final MachineScreenHandlerSupplier<B, T> supplier;

    public MachineScreenHandlerFactory(MachineScreenHandlerSupplier<B, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T create(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        return this.create(syncId, inventory, buf.readBlockPos());
    }

    public T create(int syncId, PlayerInventory inventory, B machine) {
        return this.supplier.create(syncId, inventory.player, machine);
    }

    public T create(int syncId, PlayerInventory inventory, BlockPos pos) {
        return this.create(syncId, inventory, (B)inventory.player.world.getBlockEntity(pos));
    }

    @FunctionalInterface
    public interface MachineScreenHandlerSupplier<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> {
        T create(int syncId, PlayerEntity player, B machine);
    }
}
