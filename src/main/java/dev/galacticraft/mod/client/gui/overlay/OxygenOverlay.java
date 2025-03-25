/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.overlay;

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.machinelib.api.util.StorageHelper;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;

public class OxygenOverlay {
    private static long tickCount;
    private static final Component WARNING_TEXT = Component.translatable(Translations.Ui.OXYGEN_WARNING);
    private static final Component INVALID_SETUP = Component.translatable(Translations.Ui.OXYGEN_SETUP_INVALID);

    public static void onHudRender(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.hideGui && mc.level != null && mc.player != null && !(mc.player.isSpectator() || mc.player.isCreative())) {
            if (mc.player.getVehicle() instanceof LanderEntity) {
                return;
            }

            Holder<CelestialBody<?, ?>> body = mc.level.galacticraft$getCelestialBody();
            boolean nonBreathable = (body != null) && !body.value().atmosphere().breathable();
            boolean hasMaskAndGear = mc.player.galacticraft$hasMaskAndGear();
            if (nonBreathable || hasMaskAndGear) {
                boolean hasOxygen = false;
                Container inv = mc.player.galacticraft$getOxygenTanks();
                final int outline = 0x99FFFFFF;
                final int y = 4;
                final int n = inv.getContainerSize();
                for (int i = n; i > 0; i--) {
                    Storage<FluidVariant> storage = ContainerItemContext.withConstant(inv.getItem(n - i)).find(FluidStorage.ITEM);
                    int x = mc.getWindow().getGuiScaledWidth() - ((Constant.TextureCoordinate.OVERLAY_WIDTH + y) * i);

                    long amount = 0;
                    long capacity = 1;

                    if (storage != null) {
                        amount = StorageHelper.calculateAmount(FluidVariant.of(Gases.OXYGEN), storage);
                        capacity = StorageHelper.theoreticalCapacity(storage);
                    }
                    hasOxygen = hasOxygen || amount > 0;

                    graphics.fill(x - 1, y - 1, x + Constant.TextureCoordinate.OVERLAY_WIDTH + 1, y + Constant.TextureCoordinate.OVERLAY_HEIGHT + 1, outline);
                    DrawableUtil.drawOxygenBuffer(graphics.pose(), x, y, amount, capacity);
                }

                if (nonBreathable && !((hasMaskAndGear && hasOxygen) || mc.level.isBreathable(mc.player.blockPosition().above()))) {
                    final Window scaledresolution = mc.getWindow();
                    final int width = scaledresolution.getGuiScaledWidth();
                    final int height = scaledresolution.getGuiScaledHeight();
                    final int textWidth = mc.font.width(WARNING_TEXT);
                    final int topY = height / 8 - 16;
                    final float offset = 0.25F * (float) (width - 2 * textWidth + 2);

                    graphics.pose().pushPose();
                    graphics.pose().scale(2.0F, 2.0F, 0.0F);
                    graphics.pose().translate(offset, 0.0F, 0.0F);

                    graphics.blit(Constant.ScreenTexture.WARNING_SIGN, -11, topY, 0.0F, 0.0F, 7, 7, 8, 8);
                    graphics.drawString(mc.font, WARNING_TEXT, 0, topY, FastColor.ARGB32.color(255, 255, 0, 0), false);
                    graphics.blit(Constant.ScreenTexture.WARNING_SIGN, textWidth + 3, topY, 0.0F, 0.0F, 7, 7, 8, 8);

                    if (mc.player.isAlive()) {
                        graphics.pose().translate(-offset, 0.0F, 0.0F);

                        final int alpha = (int) (200 * (Math.sin(tickCount) * 0.5F + 0.5F)) + 5;
                        graphics.drawString(mc.font, INVALID_SETUP, width / 4 - mc.font.width(INVALID_SETUP) / 2, height / 8,
                                FastColor.ARGB32.color(alpha, alpha, alpha, alpha), false);
                    }

                    graphics.pose().popPose();
                } else if (nonBreathable && (!hasMaskAndGear || !hasOxygen)) {
                    // Less obtrusive warning for if the player currently has oxygen, but has an invalid oxygen setup
                    graphics.pose().pushPose();
                    graphics.pose().scale(2.0F, 2.0F, 0.0F);
                    int x = mc.getWindow().getGuiScaledWidth() - Constant.TextureCoordinate.OVERLAY_WIDTH - 13;
                    graphics.pose().translate(0.5F * x, 0.5F * y + 0.25F * (Constant.TextureCoordinate.OVERLAY_HEIGHT - 14), 0.0F);
                    graphics.blit(Constant.ScreenTexture.WARNING_SIGN, 0, 0, 0.0F, 0.0F, 7, 7, 8, 8);
                    graphics.pose().popPose();
                }
            }
        }
    }

    public static void clientTick() {
        tickCount++;
    }
}
