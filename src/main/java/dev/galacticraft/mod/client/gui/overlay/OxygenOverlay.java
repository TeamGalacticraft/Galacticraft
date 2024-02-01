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
import net.minecraft.world.Container;

public class OxygenOverlay {
    public static void onHudRender(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && !mc.player.isSpectator()) {
            CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = CelestialBody.getByDimension(mc.level).orElse(null);
            if (body != null && !body.atmosphere().breathable()) {
                Container inv = mc.player.galacticraft$getOxygenTanks();
                final int y = 4;
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    Storage<FluidVariant> storage = ContainerItemContext.withConstant(inv.getItem(i)).find(FluidStorage.ITEM);
                    int x = mc.getWindow().getGuiScaledWidth() - ((Constant.TextureCoordinate.OVERLAY_WIDTH + y) * (i + 1));

                    int outline = 0x99FFFFFF;

                    long amount = 0;
                    long capacity = 1;

                    if (storage != null) {
                        amount = StorageHelper.calculateAmount(FluidVariant.of(Gases.OXYGEN), storage, null);
                        capacity = StorageHelper.calculateCapacity(FluidVariant.of(Gases.OXYGEN), storage, null);
                    } else if (mc.player.isCreative()) {
                        amount = capacity;
                    }

                    graphics.fill(x - 1, y - 1, x + Constant.TextureCoordinate.OVERLAY_WIDTH + 1, y + Constant.TextureCoordinate.OVERLAY_HEIGHT + 1, outline);
                    DrawableUtil.drawOxygenBuffer(graphics.pose(), x, y, amount, capacity);
                }
            }
        }
    }
}
