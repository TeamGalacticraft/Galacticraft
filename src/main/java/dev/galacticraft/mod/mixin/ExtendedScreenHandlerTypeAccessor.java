package dev.galacticraft.mod.mixin;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ExtendedScreenHandlerType.class, remap = false)
public interface ExtendedScreenHandlerTypeAccessor<T extends ScreenHandler> {
    @Accessor(value = "factory", remap = false)
    ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> getFactory();
}
