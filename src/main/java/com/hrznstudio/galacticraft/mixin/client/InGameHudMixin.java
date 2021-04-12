/*
 * Copyright (c) 2019-2021 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.mixin.client;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.GearInventoryProvider;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(Gui.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin extends GuiComponent {

    private static final int OXYGEN_X = 0;
    private static final int OXYGEN_Y = 40;
    private static final int OXYGEN_WIDTH = 12;
    private static final int OXYGEN_HEIGHT = 40;
    private static final int OXYGEN_OVERLAY_X = 24;
    private static final int OXYGEN_OVERLAY_Y = 80;
    @Shadow
    private int screenWidth;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void draw(PoseStack matrices, float delta, CallbackInfo ci) {
        if (CelestialBodyType.getByDimType(minecraft.player.level.dimension()).isPresent() && !CelestialBodyType.getByDimType(minecraft.player.level.dimension()).get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
            minecraft.getTextureManager().bind(new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY)));

            this.blit(matrices, this.screenWidth - 17, 5, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);
            this.blit(matrices, this.screenWidth - 34, 5, OXYGEN_X, OXYGEN_Y, OXYGEN_WIDTH, OXYGEN_HEIGHT);

            if (!minecraft.player.isCreative()) {
                FixedItemInv inv = ((GearInventoryProvider) minecraft.player).getGearInv();
                if (inv.getInvStack(6).getItem() instanceof OxygenTankItem) {
                    this.blit(matrices, this.screenWidth - 17 + OXYGEN_WIDTH, 5 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double) OXYGEN_HEIGHT - ((double) OXYGEN_HEIGHT * (((double) inv.getInvStack(6).getMaxDamage() - (double) inv.getInvStack(6).getDamageValue()) / (double) inv.getInvStack(6).getMaxDamage()))));
                } else if (minecraft.player.isCreative()) {
                    this.blit(matrices, this.screenWidth - 17 + OXYGEN_WIDTH, 5 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, -OXYGEN_HEIGHT);
                }
                if (inv.getInvStack(7).getItem() instanceof OxygenTankItem) {
                    this.blit(matrices, this.screenWidth - 34 + OXYGEN_WIDTH, 5 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, (int) -((double) OXYGEN_HEIGHT - ((double) OXYGEN_HEIGHT * (((double) inv.getInvStack(7).getMaxDamage() - (double) inv.getInvStack(7).getDamageValue()) / (double) inv.getInvStack(7).getMaxDamage()))));
                } else if (minecraft.player.isCreative()) {
                    this.blit(matrices, this.screenWidth - 34 + OXYGEN_WIDTH, 5 + OXYGEN_HEIGHT, OXYGEN_OVERLAY_X, OXYGEN_OVERLAY_Y, -OXYGEN_WIDTH, -OXYGEN_HEIGHT);
                }
            } else {
                this.blit(matrices, this.screenWidth - 17, 5, 12, 40, OXYGEN_WIDTH, OXYGEN_HEIGHT);
                this.blit(matrices, this.screenWidth - 34, 5, 12, 40, OXYGEN_WIDTH, OXYGEN_HEIGHT);
            }
        }
    }
}
