package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

public class SimpleMachineScreenHandler<T extends MachineBlockEntity> extends MachineScreenHandler<T> {
    private final Supplier<ScreenHandlerType<MachineScreenHandler<T>>> type;

    protected SimpleMachineScreenHandler(int syncId, PlayerEntity player, T machine, Supplier<ScreenHandlerType<MachineScreenHandler<T>>> type, int invX, int invY) {
        super(syncId, player, machine, null);
        this.type = type;
        this.addPlayerInventorySlots(invX, invY);
    }

    public static <T extends MachineBlockEntity> SimpleMachineScreenHandler<T> create(int syncId, PlayerEntity playerEntity, T machine, Supplier<ScreenHandlerType<MachineScreenHandler<T>>> handlerType, int invX, int invY) {
        return new SimpleMachineScreenHandler<>(syncId, playerEntity, machine, handlerType, invX, invY);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return this.type.get();
    }
}
