package com.hrznstudio.galacticraft.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenHooks {
    @Accessor("x")
    int gc_getX();

    @Accessor("y")
    int gc_getY();

    @Accessor("backgroundWidth")
    int gc_getBackgroundWidth();

    @Accessor("backgroundHeight")
    int gc_getBackgroundHeight();
}
