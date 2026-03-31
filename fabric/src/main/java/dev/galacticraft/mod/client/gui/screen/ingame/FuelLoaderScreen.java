/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.machinelib.client.api.util.DisplayUtil;
import dev.galacticraft.machinelib.client.api.util.GraphicsUtil;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.entity.machine.FuelLoaderBlockEntity;
import dev.galacticraft.mod.screen.FuelLoaderMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FuelLoaderScreen extends MachineScreen<FuelLoaderBlockEntity, FuelLoaderMenu> {
    public static final int RED_CROSS_X = 158;
    public static final int RED_CROSS_Y = 72;
    public static final int RED_CROSS_U = 190;
    public static final int RED_CROSS_V = 49;
    public static final int RED_CROSS_WIDTH = 11;
    public static final int RED_CROSS_HEIGHT = 11;

    public static final int ROCKET_FACE_X = 145;
    public static final int ROCKET_FACE_Y = 28;
    public static final int ROCKET_FACE_U = 176;
    public static final int ROCKET_FACE_V = 49;
    public static final int ROCKET_FACE_WIDTH = 14;
    public static final int ROCKET_FACE_HEIGHT = 34;

    public static final int TANK_OVERLAY_X = 69;
    public static final int TANK_OVERLAY_Y = 21;
    public static final int TANK_OVERLAY_U = 177;
    public static final int TANK_OVERLAY_V = 1;
    public static final int TANK_OVERLAY_WIDTH = 38;
    public static final int TANK_OVERLAY_HEIGHT = 47;

    public static final int L1 = 8;
    public static final int L2 = 36;
    public static final int L3 = 18;
    public static final int L4 = 19;
    public static final int LENGTH = L1 + L2 + L3 + L4;

    public FuelLoaderScreen(FuelLoaderMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.FUEL_LOADER_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);

        if (this.menu.rocketCapacity == 0) {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + RED_CROSS_X, this.topPos + RED_CROSS_Y, RED_CROSS_U, RED_CROSS_V, RED_CROSS_WIDTH, RED_CROSS_HEIGHT);
        }

        GraphicsUtil.drawFluid(graphics, this.leftPos + ROCKET_FACE_X, this.topPos + ROCKET_FACE_Y, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT, this.menu.rocketCapacity, FluidVariant.of(GCFluids.FUEL), this.menu.rocketAmount);

        if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + ROCKET_FACE_X, this.topPos + ROCKET_FACE_Y, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + ROCKET_FACE_X, this.topPos + ROCKET_FACE_Y, ROCKET_FACE_U, ROCKET_FACE_V, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT);
        }

        this.drawProgressBar(graphics.pose());
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        List<Component> list = new ArrayList<>();
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + TANK_OVERLAY_X, this.topPos + TANK_OVERLAY_Y, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            FluidResourceSlot slot = this.menu.fluidStorage.slot(FuelLoaderBlockEntity.FUEL_TANK);
            DisplayUtil.createFluidTooltip(list, slot.getResource(), slot.getComponents(), slot.getAmount(), slot.getCapacity());
        } else if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + ROCKET_FACE_X, this.topPos + ROCKET_FACE_Y, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            DisplayUtil.createFluidTooltip(list, GCFluids.FUEL, null, this.menu.rocketAmount, this.menu.rocketCapacity);
        }

        if (!list.isEmpty()) {
            setTooltipForNextRenderPass(Lists.transform(list, Component::getVisualOrderText));
        }
    }

    @Override
    protected void drawTanks(GuiGraphics graphics, int mouseX, int mouseY) {
        super.drawTanks(graphics, mouseX, mouseY);

        FluidResourceSlot slot = this.menu.fluidStorage.slot(FuelLoaderBlockEntity.FUEL_TANK);
        if (!slot.isEmpty()) {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + TANK_OVERLAY_X, this.topPos + TANK_OVERLAY_Y, TANK_OVERLAY_U, TANK_OVERLAY_V, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }
    }

    private void drawProgressBar(PoseStack matrices) {
        assert this.minecraft != null;
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.FUEL_LOADER_SCREEN);

        if (this.menu.getProgress() == 0) {
            return;
        } else if (this.menu.getProgress() == FuelLoaderBlockEntity.MAX_PROGRESS) {
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + 86, this.topPos + 56, 176, 83, 56, 21);
            return;
        }

        float progress = 0.5F * this.menu.getProgress();
        DrawableUtil.drawProgressTexture(matrices, this.leftPos + 86, this.topPos + 69, 238, 96, 3, Math.min(L1, progress));

        progress -= L1;
        if (progress > 0) {
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + 88, this.topPos + 74, 178, 101, Math.min(L2, progress), 3);
        } else return;

        progress -= L2;
        if (progress > 0) {
            float min = Math.min(L3, progress);
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + 121, this.topPos + 74 - min, 247, 101 - min, 3, min);
        } else return;

        progress -= L3;
        if (progress > 0) {
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + 123, this.topPos + 56, 213, 83, Math.min(L4, progress), 3);
        }
    }
}