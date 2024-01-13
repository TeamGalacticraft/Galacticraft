/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.machinelib.api.util.StorageHelper;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.Container;

public class OxygenOverlay {
    public static void onHudRender(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = CelestialBody.getByDimension(mc.level).orElse(null);
            if (body != null && !body.atmosphere().breathable()) {
                graphics.fill(mc.getWindow().getGuiScaledWidth() - (Constant.TextureCoordinate.OVERLAY_WIDTH) - 11, 4, mc.getWindow().getGuiScaledWidth() - Constant.TextureCoordinate.OVERLAY_WIDTH - 9, 6 + Constant.TextureCoordinate.OVERLAY_HEIGHT, 0);
                graphics.fill(mc.getWindow().getGuiScaledWidth() - Constant.TextureCoordinate.OVERLAY_WIDTH - 6, 4, mc.getWindow().getGuiScaledWidth() - 4, 6 + Constant.TextureCoordinate.OVERLAY_HEIGHT, 0);

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
                assert mc.player != null;
                Container inv = mc.player.galacticraft$getOxygenTanks();
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    Storage<FluidVariant> storage = ContainerItemContext.withConstant(inv.getItem(i)).find(FluidStorage.ITEM);
                    if (storage != null) {
                        long amount = StorageHelper.calculateCapacity(FluidVariant.of(Gases.OXYGEN), storage, null);
                        long capacity = StorageHelper.calculateCapacity(FluidVariant.of(Gases.OXYGEN), storage, null);

                        if (capacity > 0) {
                            DrawableUtil.drawOxygenBuffer(graphics.pose(), mc.getWindow().getGuiScaledWidth() - (Constant.TextureCoordinate.OVERLAY_WIDTH * i) - (5 * (i + 4)), 5, amount, capacity);
                        }
                    } else if (mc.player.isCreative()) {
                        DrawableUtil.drawOxygenBuffer(graphics.pose(), mc.getWindow().getGuiScaledWidth() - (Constant.TextureCoordinate.OVERLAY_WIDTH * i) - (5 * (i + 4)), 5, 1, 1);
                    }
                }
            }
        }
    }
}
