/*
 * Copyright (c) 2019 HRZN LTD
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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.block.entity.FuelLoaderBlockEntity;
import com.hrznstudio.galacticraft.screen.FuelLoaderScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
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

    public static final ContainerFactory<HandledScreen> FACTORY = createFactory(FuelLoaderBlockEntity.class, FuelLoaderScreen::new);
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.FUEL_LOADER_SCREEN));

    public static final int X_X = 177;
    public static final int X_Y = 42;
    public static final int X_WIDTH = 12;
    public static final int X_HEIGHT = 12;

    public static final int TANK_OVERLAY_X = 176;
    public static final int TANK_OVERLAY_Y = 0;
    public static final int TANK_OVERLAY_WIDTH = 38;
    public static final int TANK_OVERLAY_HEIGHT = 38;

    private final FuelLoaderBlockEntity blockEntity;

    public FuelLoaderScreen(int syncId, PlayerEntity playerEntity, FuelLoaderBlockEntity blockEntity) {
        super(new FuelLoaderScreenHandler(syncId, playerEntity, blockEntity), playerEntity.inventory, playerEntity.world, blockEntity.getPos(), new LiteralText(""));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        this.drawTexture(stack, leftPos, topPos, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawEnergyBufferBar(stack, this.x + 10, this.y + 9);
        if (this.blockEntity.getTank().getContents(0).getFluid() != null
        && this.blockEntity.getTank().getContents(0).getFluid() != Fluids.EMPTY) {
            stack.push();
            Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(this.blockEntity.getTank().getContents(0).getFluid()).getFluidSprites(null, null, this.blockEntity.getTank().getContents(0).getFluid().getDefaultState())[0];
            this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
            drawSprite(stack, x + 106, y + 46, getZOffset(), TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT, sprite);  //todo scaling
            stack.pop();
            this.client.getTextureManager().bindTexture(BACKGROUND);
            drawTexture(stack, x + 69, y + 9, TANK_OVERLAY_X, TANK_OVERLAY_Y, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT);
        }
        if (blockEntity.status == FuelLoaderBlockEntity.FuelLoaderStatus.NO_ROCKET) {
            drawTexture(stack, x + 155, y + 44, X_X, X_Y, X_WIDTH, X_HEIGHT);
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
        this.drawEnergyTooltip(stack, mouseX, mouseY, this.x + 10, this.y + 9);
        if (check(mouseX, mouseY, x + 69, y + 9, TANK_OVERLAY_WIDTH, TANK_OVERLAY_HEIGHT)) {
            List<MutableText> list = new ArrayList<>();
            if (blockEntity.getTank().getContents(0).isEmpty()) {
                list.add(new TranslatableText("tooltip.galacticraft-rewoven.no_fluid"));
            } else {
                Fraction fraction = blockEntity.getTank().getContents(0).getAmount().multiply(Fraction.ONE); //the multiplication simplifies it apparently
                if (fraction.getDenominator() == 1) {
                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.getNumerator()));
                } else {
                    if (!Screen.hasShiftDown()) {
                        if (fraction.doubleValue() > 1.0D) {
                            int whole = (int) (fraction.doubleValue() - (fraction.doubleValue() % 1));
                            int numerator = fraction.getNumerator() % fraction.getDenominator();
                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_mixed_number", whole, numerator, fraction.getDenominator()));
                        } else {
                            list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_fraction", fraction.getNumerator(), fraction.getDenominator()));
                        }
                    } else {
                        list.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.doubleValue()));
                    }
                }
                if (Screen.hasControlDown()) {
                    list.add(new TranslatableText("tooltip.galacticraft-rewoven.fluid").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(new LiteralText(Registry.FLUID.getId(blockEntity.getTank().getContents(0).getFluid()).toString()).setStyle(Style.EMPTY.withColor(Formatting.DARK_BLUE))));
                }
                list.get(0).setStyle(Style.EMPTY.withColor(Formatting.GOLD));
            }
            this.renderTooltip(stack, list, mouseX, mouseY);
        }
    }
}
