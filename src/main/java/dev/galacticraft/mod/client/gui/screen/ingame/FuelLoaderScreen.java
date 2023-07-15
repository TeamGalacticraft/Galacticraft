/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.FuelLoaderBlockEntity;
import dev.galacticraft.mod.screen.FuelLoaderMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class FuelLoaderScreen extends MachineScreen<FuelLoaderBlockEntity, FuelLoaderMenu> {
    public static final int RED_X_U = 176;
    public static final int RED_X_V = 40;
    public static final int RED_X_WIDTH = 11;
    public static final int RED_X_HEIGHT = 11;

    public static final int ROCKET_FACE_U = 176;
    public static final int ROCKET_FACE_V = 53;
    public static final int ROCKET_FACE_WIDTH = 14;
    public static final int ROCKET_FACE_HEIGHT = 35;

    public static final int TANK_OVERLAY_U = 176;
    public static final int TANK_OVERLAY_V = 0;
    public static final int TANK_OVERLAY_WIDTH = 38;
    public static final int TANK_OVERLAY_HEIGHT = 38;

    public FuelLoaderScreen(FuelLoaderMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.FUEL_LOADER_SCREEN);
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(graphics, mouseX, mouseY, delta);

        FluidResourceSlot slot = this.menu.fluidStorage.getSlot(FuelLoaderBlockEntity.FUEL_TANK);
        if (!slot.isEmpty()) {
            graphics.pose().pushPose();
            TextureAtlasSprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(slot.getResource()).getFluidSprites(null, null, slot.getResource().defaultFluidState())[0];
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            graphics.blit(this.leftPos + 106, this.topPos + 46, 0, -TANK_OVERLAY_WIDTH, (int) -(((double) TANK_OVERLAY_HEIGHT) * ((float) slot.getAmount() / (float) slot.getCapacity())), sprite);
            graphics.pose().popPose();
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 68, this.topPos + 8, TANK_OVERLAY_U, TANK_OVERLAY_V, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }

        if (this.menu.configuration.getStatus().getType() == MachineStatus.Type.MISSING_RESOURCE) {
            graphics.blit( Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 116, this.topPos + 53, RED_X_U, RED_X_V, RED_X_WIDTH, RED_X_HEIGHT);
        }

        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 145, this.topPos + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (this.menu.fluid != null) {
                graphics.pose().pushPose();
                TextureAtlasSprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(menu.fluid).getFluidSprites(null, null, menu.fluid.defaultFluidState())[0];
                RenderSystem.setShaderTexture(0, sprite.atlasLocation());
                graphics.blit(this.leftPos + 158, this.topPos + 72, 0, -ROCKET_FACE_WIDTH, (int) -(((double) ROCKET_FACE_HEIGHT) * ((float) menu.fluidAmount / (float) menu.fluidCapacity)), sprite);
                graphics.pose().popPose();
            }
        } else {
            graphics.blit(Constant.ScreenTexture.FUEL_LOADER_SCREEN, this.leftPos + 145, this.topPos + 37, ROCKET_FACE_U, ROCKET_FACE_V, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT);
        }
        graphics.drawCenteredString(this.minecraft.font, I18n.get("block.galacticraft.fuel_loader"), (this.width / 2), this.topPos - 18, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        List<FormattedCharSequence> list = new ArrayList<>();
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 69, this.topPos + 17, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            if (this.menu.fluidStorage.getSlot(FuelLoaderBlockEntity.FUEL_TANK).isEmpty()) {
                list.add(Component.translatable("tooltip.galacticraft.no_fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)).getVisualOrderText());
            } else {
//                FluidAmount fraction = this.machine.fluidInv().getInvFluid(0).getAmount_F().mul(FluidAmount.ONE); //every action forces simplification of the fraction
//                FluidUtil.createFluidTooltip(list, fraction);
//                if (Screen.hasControlDown()) {
//                    list.add(Component.translatable("tooltip.galacticraft.fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(Component.literal(BuiltInRegistries.FLUID.getId(this.machine.fluidInv().getInvFluid(0).getRawFluid()).toString()).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).asOrderedText());
//                }
            }
            graphics.renderTooltip(this.font, list, mouseX, mouseY);
        }
        list.clear();

        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 145, this.topPos + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (this.menu.fluid != null) {
//                                FluidAmount fraction = ((RocketEntity) entity).getTank().getInvFluid(0).getAmount_F();
//                                FluidUtil.createFluidTooltip(list, fraction);
//                                if (Screen.hasControlDown()) {
//                                    list.add(Component.translatable("tooltip.galacticraft.fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(Component.literal(BuiltInRegistries.FLUID.getKey(((RocketEntity) entity).getTank().getInvFluid(0).getRawFluid()).toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))).getVisualOrderText());
//                                }
            } else {
                list.add(Component.translatable("tooltip.galacticraft.no_fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)).getVisualOrderText());
            }
            if (!list.isEmpty()) {
                graphics.renderTooltip(this.font, list, mouseX, mouseY);
            }
        }
    }
}