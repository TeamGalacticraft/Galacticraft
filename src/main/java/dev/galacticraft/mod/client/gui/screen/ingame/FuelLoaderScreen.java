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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.google.common.collect.Lists;
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
    public static final int RED_X_U = 176;
    public static final int RED_X_V = 40;
    public static final int RED_X_WIDTH = 11;
    public static final int RED_X_HEIGHT = 11;

    public static final int ROCKET_FACE_U = 176;
    public static final int ROCKET_FACE_V = 49;
    public static final int ROCKET_FACE_WIDTH = 14;
    public static final int ROCKET_FACE_HEIGHT = 34;

    public static final int TANK_OVERLAY_U = 177;
    public static final int TANK_OVERLAY_V = 1;
    public static final int TANK_OVERLAY_WIDTH = 38;
    public static final int TANK_OVERLAY_HEIGHT = 47;

    public FuelLoaderScreen(FuelLoaderMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.FUEL_LOADER_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);

        FluidResourceSlot slot = this.menu.fluidStorage.getSlot(FuelLoaderBlockEntity.FUEL_TANK);
        if (!slot.isEmpty()) {
            GraphicsUtil.drawFluid(graphics, this.leftPos + 69, this.topPos + 8, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT, slot.getCapacity(), FluidVariant.of(slot.getResource(), slot.getTag()), slot.getAmount());
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 69, this.topPos + 8, TANK_OVERLAY_U, TANK_OVERLAY_V, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }

        if (this.menu.rocketCapacity == 0) {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 158, this.topPos + 72, RED_X_U, RED_X_V, RED_X_WIDTH, RED_X_HEIGHT);
        }

        GraphicsUtil.drawFluid(graphics, this.leftPos + 145, this.topPos + 29, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT, this.menu.rocketCapacity, FluidVariant.of(GCFluids.FUEL), this.menu.rocketAmount);

        if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 145, this.topPos + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 145, this.topPos + 29, ROCKET_FACE_U, ROCKET_FACE_V, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        List<Component> list = new ArrayList<>();
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 69, this.topPos + 17, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            FluidResourceSlot slot = this.menu.fluidStorage.getSlot(FuelLoaderBlockEntity.FUEL_TANK);
            DisplayUtil.createFluidTooltip(list, slot.getResource(), slot.getTag(), slot.getAmount(), slot.getCapacity());
        } else if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 145, this.topPos + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            DisplayUtil.createFluidTooltip(list, GCFluids.FUEL, null, this.menu.rocketAmount, this.menu.rocketCapacity);
        }

        if (!list.isEmpty()) {
            setTooltipForNextRenderPass(Lists.transform(list, Component::getVisualOrderText));
        }
    }
}