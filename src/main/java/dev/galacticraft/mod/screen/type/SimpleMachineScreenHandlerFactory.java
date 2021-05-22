package dev.galacticraft.mod.screen.type;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.screen.SimpleMachineScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

public class SimpleMachineScreenHandlerFactory<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> extends MachineScreenHandlerFactory<B, T> {
    protected SimpleMachineScreenHandlerFactory(Supplier<ScreenHandlerType<T>> type, int invX, int invY) {
        super((syncId, player, machine) -> (T) SimpleMachineScreenHandler.create(syncId, player, machine, () -> (ScreenHandlerType<MachineScreenHandler<B>>) type.get(), invX, invY));
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type, int invX, int invY) {
        return new SimpleMachineScreenHandlerFactory<>(type, invX, invY);
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type, int invY) {
        return create(type, 0, invY);
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type) {
        return create(type, 84);
    }
}
