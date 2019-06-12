package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper{

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Shadow @Final private MinecraftClient client;

    private static final int OXYGEN_X = 0;
    private static final int OXYGEN_Y = 40;

    private static final int OXYGEN_WIDTH = 12;
    private static final int OXYGEN_HEIGHT = 40;

    private static final int OXYGEN_OVERLAY_X = 24;
    private static final int OXYGEN_OVERLAY_Y = 80;

    private static final int OXYGEN_OVERLAY_WIDTH = 12;
    private static final int OXYGEN_OVERLAY_HEIGHT = 40;

    @Inject(method = "draw", at = @At(value = "TAIL"))
    private void draw(float float_1, CallbackInfo ci) {
        this.client.getProfiler().push("jumpBar"); //Totally the jump bar
        GlStateManager.pushMatrix();
        client.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY)));
        this.blit(this.scaledWidth - 17, this.scaledHeight - 235, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);
        this.blit(this.scaledWidth - 29, this.scaledHeight - 235, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);

        SimpleFixedItemInv gearInventory = ((GCPlayerAccessor) this.client.player).getGearInventory();
        if (gearInventory.getInvStack(6).getItem() instanceof OxygenTankItem) {
            this.blit(this.scaledWidth - 17, this.scaledHeight - 235, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, OXYGEN_OVERLAY_WIDTH, ((gearInventory.getInvStack(6).getDurability() / gearInventory.getInvStack(6).getDamage()) * 100) / 40);
        }
        if (gearInventory.getInvStack(7).getItem() instanceof OxygenTankItem) {
            this.blit(this.scaledWidth - 29, this.scaledHeight - 235, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, OXYGEN_OVERLAY_WIDTH, ((gearInventory.getInvStack(7).getDurability() / gearInventory.getInvStack(7).getDamage()) * 100) / 40);
        }

        GlStateManager.popMatrix();
        this.client.getProfiler().pop();
    }
}
