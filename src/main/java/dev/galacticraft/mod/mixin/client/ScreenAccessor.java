package dev.galacticraft.mod.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {


    @Accessor("minecraft")
    Minecraft getMinecraft();

    @Accessor("font")
    Font gcGetFont();

    @Invoker("addRenderableWidget")
    <T extends GuiEventListener & Renderable & NarratableEntry> T gc$addRenderableWidget(T drawableElement);

    @Invoker("removeWidget")
    void gc$removeWidget(GuiEventListener child);
}
