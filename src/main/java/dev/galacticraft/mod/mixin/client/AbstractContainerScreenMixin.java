package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.GCCreativeGuiSlots;
import dev.galacticraft.mod.accessor.GCInventoryFlag;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void gc$beforeCarried(GuiGraphics g, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Object self = this;
        if (self instanceof GCCreativeGuiSlots gc) {
            gc.gc$renderGcSlots(g, mouseX, mouseY);
        }
    }

}
