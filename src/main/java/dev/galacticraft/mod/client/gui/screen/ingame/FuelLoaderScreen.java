/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.FuelLoaderBlockEntity;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.entity.RocketEntity;
import dev.galacticraft.mod.screen.FuelLoaderScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class FuelLoaderScreen extends MachineHandledScreen<FuelLoaderBlockEntity, FuelLoaderScreenHandler> {
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

    public FuelLoaderScreen(FuelLoaderScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.FUEL_LOADER_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new CapacitorWidget(this, this.x + 8, this.y + 8, 48));
    }

    @Override
    protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);

        if (this.machine.fluidInv().getInvFluid(0).getRawFluid() != null
                && this.machine.fluidInv().getInvFluid(0).getRawFluid() != Fluids.EMPTY) {
            matrices.push();
            TextureAtlasSprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(this.machine.fluidInv().getInvFluid(0).getRawFluid()).getFluidSprites(null, null, this.machine.fluidInv().getInvFluid(0).getRawFluid().getDefaultState())[0];
            this.minecraft.getTextureManager().bindTexture(sprite.getAtlas().getId());
            drawSprite(matrices, this.x + 106, this.y + 46, getZOffset(), -TANK_OVERLAY_WIDTH, (int) -(((double) TANK_OVERLAY_HEIGHT) * (this.machine.fluidInv().getInvFluid(0).getAmount_F().asInexactDouble() / this.machine.fluidInv().getMaxAmount_F(0).asInexactDouble())), sprite);
            matrices.pop();
            this.minecraft.getTextureManager().bindTexture(Constant.ScreenTexture.FUEL_LOADER_SCREEN);
            drawTexture(matrices, this.x + 68, this.y + 8, TANK_OVERLAY_U, TANK_OVERLAY_V, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }

        if (this.machine.getStatus().getType() == MachineStatus.StatusType.MISSING_RESOURCE) {
            this.minecraft.getTextureManager().bindTexture(Constant.ScreenTexture.FUEL_LOADER_SCREEN);
            drawTexture(matrices, this.x + 116, this.y + 53, RED_X_U, RED_X_V, RED_X_WIDTH, RED_X_HEIGHT);
        }

        if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 145, this.y + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (this.machine.getConnectionPos().isWithinDistance(this.pos, 3.0)) {
                BlockEntity be = world.getBlockEntity(this.machine.getConnectionPos());
                if (be instanceof RocketLaunchPadBlockEntity) {
                    if (((RocketLaunchPadBlockEntity) be).hasRocket()) {
                        Entity entity = world.getEntityById(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
                        if (entity instanceof RocketEntity) {
                            if (!((RocketEntity) entity).getTank().getInvFluid(0).isEmpty()) {
                                double amount = ((RocketEntity) entity).getTank().getInvFluid(0).getAmount_F().asInexactDouble();
                                double max = ((RocketEntity) entity).getTank().getMaxAmount_F(0).asInexactDouble();
                                matrices.push();
                                Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(((RocketEntity) entity).getTank().getInvFluid(0).getRawFluid()).getFluidSprites(null, null, ((RocketEntity) entity).getTank().getInvFluid(0).getRawFluid().getDefaultState())[0];
                                this.minecraft.getTextureManager().bindTexture(sprite.getAtlas().getId());
                                DrawableHelper.drawSprite(matrices, this.x + 158, this.y + 72, getZOffset(), -ROCKET_FACE_WIDTH, (int) -(((double) ROCKET_FACE_HEIGHT) * (amount / max)), sprite);
                                matrices.pop();
                            }
                        }
                    }
                }
            }
        } else {
            this.minecraft.getTextureManager().bindTexture(Constant.ScreenTexture.FUEL_LOADER_SCREEN);
            drawTexture(matrices, this.x + 145, this.y + 37, ROCKET_FACE_U, ROCKET_FACE_V, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT);
        }
        drawCenteredText(matrices, this.minecraft.textRenderer, I18n.translate("block.galacticraft.fuel_loader"), (this.width / 2), this.y - 18, Formatting.DARK_GRAY.getColorValue());
    }

    @Override
    public void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
        super.renderTooltip(matrices, mouseX, mouseY);
        List<FormattedCharSequence> list = new ArrayList<>();
        if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 69, this.y + 17, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            if (this.machine.fluidInv().getInvFluid(0).isEmpty()) {
                list.add(Component.translatable("tooltip.galacticraft.no_fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)).getVisualOrderText());
            } else {
                FluidAmount fraction = this.machine.fluidInv().getInvFluid(0).getAmount_F().mul(FluidAmount.ONE); //every action forces simplification of the fraction
                FluidUtil.createFluidTooltip(list, fraction);
                if (Screen.hasControlDown()) {
                    list.add(Component.translatable("tooltip.galacticraft.fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(Component.literal(Registry.FLUID.getId(this.machine.fluidInv().getInvFluid(0).getRawFluid()).toString()).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).asOrderedText());
                }
            }
            this.renderTooltip(matrices, list, mouseX, mouseY);
        }
        list.clear();

        if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 145, this.y + 37, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (this.machine.getConnectionPos().isWithinDistance(this.pos, 3.0)) {
                BlockEntity be = world.getBlockEntity(this.machine.getConnectionPos());
                if (be instanceof RocketLaunchPadBlockEntity) {
                    if (((RocketLaunchPadBlockEntity) be).hasRocket()) {
                        Entity entity = world.getEntity(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
                        if (entity instanceof RocketEntity) {
                            if (((RocketEntity) entity).getTank().getInvFluid(0).isEmpty()) {
                                list.add(Component.translatable("tooltip.galacticraft.no_fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)).getVisualOrderText());
                            } else {
                                FluidAmount fraction = ((RocketEntity) entity).getTank().getInvFluid(0).getAmount_F();
                                FluidUtil.createFluidTooltip(list, fraction);
                                if (Screen.hasControlDown()) {
                                    list.add(Component.translatable("tooltip.galacticraft.fluid").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(Component.literal(Registry.FLUID.getKey(((RocketEntity) entity).getTank().getInvFluid(0).getRawFluid()).toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))).getVisualOrderText());
                                }
                            }
                        }
                    }
                }
            }
            if (!list.isEmpty()) {
                this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
            }
        }
    }
}
