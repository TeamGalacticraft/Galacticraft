package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.world.dimension.SpaceDimension;
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

    @Inject(method = "draw", at = @At(value = "TAIL"))
    private void draw(float float_1, CallbackInfo ci) {

        if (client.player.world.dimension instanceof SpaceDimension && !((SpaceDimension) client.player.world.dimension).hasOxygen()) {
            this.client.getProfiler().push("jumpBar"); //Totally the jump bar
            GlStateManager.pushMatrix();
            client.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY)));
            this.blit(this.scaledWidth - 17, this.scaledHeight - 235, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);
            this.blit(this.scaledWidth - 34, this.scaledHeight - 235, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);

            SimpleFixedItemInv gearInventory = ((GCPlayerAccessor) this.client.player).getGearInventory();
            if (gearInventory.getInvStack(6).getItem() instanceof OxygenTankItem) {
                this.blit(this.scaledWidth - 17 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double) OXYGEN_HEIGHT - ((double) OXYGEN_HEIGHT * (((double) gearInventory.getInvStack(6).getMaxDamage() - (double) gearInventory.getInvStack(6).getDamage()) / (double) gearInventory.getInvStack(6).getMaxDamage()))));
            } else if (client.player.isCreative()) {
                this.blit(this.scaledWidth - 17 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, -OXYGEN_HEIGHT);
            }
            if (gearInventory.getInvStack(7).getItem() instanceof OxygenTankItem) {
                this.blit(this.scaledWidth - 34 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double) OXYGEN_HEIGHT - ((double) OXYGEN_HEIGHT * (((double) gearInventory.getInvStack(7).getMaxDamage() - (double) gearInventory.getInvStack(7).getDamage()) / (double) gearInventory.getInvStack(7).getMaxDamage()))));
            } else if (client.player.isCreative()) {
                this.blit(this.scaledWidth - 34 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, -OXYGEN_HEIGHT);
            }


            //this.blit(this.scaledWidth - 17 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double)OXYGEN_HEIGHT - ((double)OXYGEN_HEIGHT * ((3000D - 1000D) / 3000D))));
            //this.blit(this.scaledWidth - 34 + OXYGEN_WIDTH, this.scaledHeight - 235 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double)OXYGEN_HEIGHT - ((double)OXYGEN_HEIGHT * ((3000D - 2500D) / 3000D))));

            GlStateManager.popMatrix();
            this.client.getProfiler().pop();
        }
    }
}
