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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.block.entity.FuelLoaderBlockEntity;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.entity.RocketEntity;
import com.hrznstudio.galacticraft.screen.FuelLoaderScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class FuelLoaderScreen extends MachineHandledScreen<FuelLoaderScreenHandler> {

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.FUEL_LOADER_SCREEN));

    public static final int X_X = 176;
    public static final int X_Y = 40;
    public static final int X_WIDTH = 11;
    public static final int X_HEIGHT = 11;

    public static final int ROCKET_FACE_X = 176;
    public static final int ROCKET_FACE_Y = 53;
    public static final int ROCKET_FACE_WIDTH = 14;
    public static final int ROCKET_FACE_HEIGHT = 35;

    public static final int TANK_OVERLAY_X = 176;
    public static final int TANK_OVERLAY_Y = 0;
    public static final int TANK_OVERLAY_WIDTH = 38;
    public static final int TANK_OVERLAY_HEIGHT = 38;

    private final FuelLoaderBlockEntity fuelLoader;

    public FuelLoaderScreen(FuelLoaderScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title);
        this.fuelLoader = handler.machine;
        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 10, 9, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        this.drawTexture(stack, leftPos, topPos, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.fuelLoader.getFluidTank().getContents(0).getFluid() != null
        && this.fuelLoader.getFluidTank().getContents(0).getFluid() != Fluids.EMPTY) {
            stack.push();
            Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(this.fuelLoader.getFluidTank().getContents(0).getFluid()).getFluidSprites(null, null, this.fuelLoader.getFluidTank().getContents(0).getFluid().getDefaultState())[0];
            this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
            drawSprite(stack, x + 106, y + 46, getZOffset(), -TANK_OVERLAY_WIDTH, (int)-(((double)TANK_OVERLAY_HEIGHT) * (fuelLoader.getFluidTank().getContents(0).getAmount().doubleValue() / fuelLoader.getFluidTank().getMaxCapacity(0).doubleValue())), sprite);
            stack.pop();
            this.client.getTextureManager().bindTexture(BACKGROUND);
            drawTexture(stack, x + 68, y + 8, TANK_OVERLAY_X, TANK_OVERLAY_Y, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }

        if (fuelLoader.getStatus().getType() == ConfigurableMachineBlockEntity.MachineStatus.StatusType.MISSING_RESOURCE) {
            this.client.getTextureManager().bindTexture(BACKGROUND);
            drawTexture(stack, x + 116, y + 45, X_X, X_Y, X_WIDTH, X_HEIGHT);
        }

        if (check(mouseX, mouseY, x + 145, y + 29, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (fuelLoader.getConnectionPos() != null) {
                BlockEntity be = world.getBlockEntity(fuelLoader.getConnectionPos());
                if (be instanceof RocketLaunchPadBlockEntity) {
                    if (((RocketLaunchPadBlockEntity) be).hasRocket()) {
                        Entity entity = world.getEntityById(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
                        if (entity instanceof RocketEntity) {
                            if (!((RocketEntity) entity).getTank().getContents(0).isEmpty()) {
                                double amount = ((RocketEntity) entity).getTank().getContents(0).getAmount().doubleValue();
                                double max = ((RocketEntity) entity).getTank().getMaxCapacity(0).doubleValue();
                                stack.push();
                                Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(((RocketEntity) entity).getTank().getContents(0).getFluid()).getFluidSprites(null, null, ((RocketEntity) entity).getTank().getContents(0).getFluid().getDefaultState())[0];
                                this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
                                DrawableHelper.drawSprite(stack, x + 159, y + 64, getZOffset(), -ROCKET_FACE_WIDTH, (int) -(((double) ROCKET_FACE_HEIGHT) * (amount / max)), sprite);
                                stack.pop();
                            }
                        }
                    }
                }
            }
        } else {
            this.client.getTextureManager().bindTexture(BACKGROUND);
            drawTexture(stack, x + 145, y + 29, ROCKET_FACE_X, ROCKET_FACE_Y, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT);
        }
        // 115 44
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.fuel_loader").getKey(), (this.width / 2), this.y - 18, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        List<OrderedText> list = new ArrayList<>();
        if (check(mouseX, mouseY, x + 69, y + 9, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            if (fuelLoader.getFluidTank().getContents(0).isEmpty()) {
                list.add(new TranslatableText("tooltip.galacticraft-rewoven.no_fluid").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
            } else {
                Fraction fraction = fuelLoader.getFluidTank().getContents(0).getAmount().multiply(Fraction.ONE); //every action forces simplification of the fraction
                if (fraction.getDenominator() == 1) {
                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.getNumerator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                } else {
                    if (!Screen.hasShiftDown()) {
                        if (fraction.doubleValue() > 1.0D) {
                            int whole = (int) (fraction.doubleValue() - (fraction.doubleValue() % 1));
                            int numerator = fraction.getNumerator() % fraction.getDenominator();
                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_mixed_number", whole, numerator, fraction.getDenominator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                        } else {
                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_fraction", fraction.getNumerator(), fraction.getDenominator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                        }
                    } else {
                        list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.doubleValue()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                    }
                }
                if (Screen.hasControlDown()) {
                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.fluid").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(new LiteralText(Registry.FLUID.getId(fuelLoader.getFluidTank().getContents(0).getFluid()).toString()).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).asOrderedText());
                }
            }
            this.renderOrderedTooltip(stack, list, mouseX, mouseY);
        }
        list.clear();

        if (check(mouseX, mouseY, x + 145, y + 29, ROCKET_FACE_WIDTH, ROCKET_FACE_HEIGHT)) {
            if (fuelLoader.getConnectionPos() != null) {
                BlockEntity be = world.getBlockEntity(fuelLoader.getConnectionPos());
                if (be instanceof RocketLaunchPadBlockEntity) {
                    if (((RocketLaunchPadBlockEntity) be).hasRocket()) {
                        Entity entity = world.getEntityById(((RocketLaunchPadBlockEntity) be).getRocketEntityId());
                        if (entity instanceof RocketEntity) {
                            if (((RocketEntity) entity).getTank().isEmpty()) {
                                list.add(new TranslatableText("tooltip.galacticraft-rewoven.no_fluid").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                            } else {
                                Fraction fraction = ((RocketEntity) entity).getTank().getContents(0).getAmount().multiply(Fraction.ONE); //every action forces simplification of the fraction
                                if (fraction.getDenominator() == 1) {
                                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.getNumerator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                                } else {
                                    if (!Screen.hasShiftDown()) {
                                        if (fraction.doubleValue() > 1.0D) {
                                            int whole = (int) (fraction.doubleValue() - (fraction.doubleValue() % 1));
                                            int numerator = fraction.getNumerator() % fraction.getDenominator();
                                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_mixed_number", whole, numerator, fraction.getDenominator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                                        } else {
                                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_fraction", fraction.getNumerator(), fraction.getDenominator()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                                        }
                                    } else {
                                        list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.doubleValue()).setStyle(Style.EMPTY.withColor(Formatting.GOLD)).asOrderedText());
                                    }
                                }
                                if (Screen.hasControlDown()) {
                                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.fluid").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(new LiteralText(Registry.FLUID.getId(((RocketEntity) entity).getTank().getContents(0).getFluid()).toString()).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).asOrderedText());
                                }
                            }
                        }
                    }
                }
            }
            if (!list.isEmpty()) {
                this.renderOrderedTooltip(stack, list, mouseX, mouseY);
            }
        }
    }
}
