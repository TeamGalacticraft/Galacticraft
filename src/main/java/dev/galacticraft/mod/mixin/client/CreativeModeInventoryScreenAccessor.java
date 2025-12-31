package dev.galacticraft.mod.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor("searchBox") EditBox getSearchBox();

    @Invoker("renderTabButton")
    void GCrenderTabButton(GuiGraphics graphics, CreativeModeTab group);

    @Invoker("canScroll")
    boolean GCcanScroll();

    @Accessor("SCROLLER_SPRITE")
    ResourceLocation get_SCROLLER_SPRITE();
    @Accessor("SCROLLER_DISABLED_SPRITE") ResourceLocation get_SCROLLER_DISABLED_SPRITE();

    @Accessor("scrollOffs") float getScrollOffs();
}
