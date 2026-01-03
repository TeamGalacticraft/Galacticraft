package dev.galacticraft.mod.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos") int getLeftPos();
    @Accessor("topPos") int getTopPos();
    @Accessor("imageWidth") int getImageWidth();
    @Accessor("imageHeight") int getImageHeight();

    @Invoker("renderSlot")
    void gcRenderSlot(GuiGraphics graphics, Slot region);

    @Accessor("hoveredSlot") Slot getHoveredSlot();
    @Accessor("hoveredSlot") void setHoveredSlot(@Nullable Slot slot);

    @Invoker("isHovering")
    boolean gcIsHovering(Slot slot, double pointX, double pointY);

    @Invoker("getTooltipFromContainerItem")
    List<Component> gcGetTooltipFromContainerItem(@Nullable ItemStack stack);

    @Invoker("findSlot")
    Slot gcFindSlot(double x, double y);
}
