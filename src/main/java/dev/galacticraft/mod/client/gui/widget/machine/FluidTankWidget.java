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

package dev.galacticraft.mod.client.gui.widget.machine;

import alexiil.mc.lib.attributes.fluid.SingleFluidTankView;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.screen.MachineHandledScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class FluidTankWidget extends AbstractWidget {
    private static final int[] FLUID_TANK_8_16_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_8_16_X,
            Constants.TextureCoordinates.FLUID_TANK_8_16_Y,
            Constants.TextureCoordinates.FLUID_TANK_8_16_HEIGHT
    };
    private static final int[] FLUID_TANK_7_14_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_7_14_X,
            Constants.TextureCoordinates.FLUID_TANK_7_14_Y,
            Constants.TextureCoordinates.FLUID_TANK_7_14_HEIGHT
    };
    private static final int[] FLUID_TANK_6_12_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_6_12_X,
            Constants.TextureCoordinates.FLUID_TANK_6_12_Y,
            Constants.TextureCoordinates.FLUID_TANK_6_12_HEIGHT
    };
    private static final int[] FLUID_TANK_5_10_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_5_10_X,
            Constants.TextureCoordinates.FLUID_TANK_5_10_Y,
            Constants.TextureCoordinates.FLUID_TANK_5_10_HEIGHT
    };
    private static final int[] FLUID_TANK_4_8_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_4_8_X,
            Constants.TextureCoordinates.FLUID_TANK_4_8_Y,
            Constants.TextureCoordinates.FLUID_TANK_4_8_HEIGHT
    };
    private static final int[] FLUID_TANK_3_6_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_3_6_X,
            Constants.TextureCoordinates.FLUID_TANK_3_6_Y,
            Constants.TextureCoordinates.FLUID_TANK_3_6_HEIGHT
    };
    private static final int[] FLUID_TANK_2_4_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_2_4_X,
            Constants.TextureCoordinates.FLUID_TANK_2_4_Y,
            Constants.TextureCoordinates.FLUID_TANK_2_4_HEIGHT
    };
    private static final int[] FLUID_TANK_1_2_DATA = new int[]{
            Constants.TextureCoordinates.FLUID_TANK_1_2_X,
            Constants.TextureCoordinates.FLUID_TANK_1_2_Y,
            Constants.TextureCoordinates.FLUID_TANK_1_2_HEIGHT
    };

    private final SingleFluidTankView tankView;
    private final int x;
    private final int y;
    private final World world;
    private final BlockPos pos;
    private final int[] data;
    private final int scale;

    public FluidTankWidget(SingleFluidTankView tankView, int x, int y, World world, BlockPos pos) {
        this(tankView, x, y, world, pos, 1);
    }

    public FluidTankWidget(SingleFluidTankView tankView, int x, int y, World world, BlockPos pos, int scale) {
        this.tankView = tankView;
        this.x = x;
        this.y = y;
        this.world = world == null ? this.client.world : world;
        this.pos = pos == null ? new BlockPos(0, 1, 0) : pos;
        this.scale = scale;
        this.data = this.getPositionData(this.getTankView().getMaxAmount_F());
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2])) {
            FluidVolume volume = this.getTankView().get();
            if (volume.isEmpty()) {
                this.client.currentScreen.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.fluid_widget.empty").setStyle(Constants.Styles.GRAY_STYLE), mouseX, mouseY);
                return;
            }
            MutableText amount;
            if (Screen.hasShiftDown()) {
                amount = new LiteralText(volume.getAmount_F().toString() + "B");
            } else {
                amount = new LiteralText((volume.getAmount_F().asInt(1000, RoundingMode.HALF_DOWN)) + "mB");
            }

            List<Text> lines = new ArrayList<>(2);
            lines.add(new TranslatableText("ui.galacticraft-rewoven.fluid_widget.fluid").setStyle(Constants.Styles.GRAY_STYLE).append(new LiteralText(getName(volume.getRawFluid())).setStyle(Constants.Styles.BLUE_STYLE)));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.fluid_widget.amount").setStyle(Constants.Styles.GRAY_STYLE).append(amount.setStyle(Style.EMPTY.withColor(Formatting.WHITE))));

            this.client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    private String getName(Fluid fluid) {
        Identifier id = Registry.FLUID.getId(fluid);
        if (I18n.hasTranslation("block." + id.getNamespace() + "." + id.getPath())) {
            return WordUtils.capitalizeFully(I18n.translate("block." + id.getNamespace() + "." + id.getPath()));
        }
        return WordUtils.capitalizeFully(id.getPath().replace("flowing", "").replace("still", "").replace("_", " ").trim());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.client.getTextureManager().bindTexture(MachineHandledScreen.OVERLAY);
        this.drawTexture(matrices, this.x, this.y, this.data[0], this.data[1] + Constants.TextureCoordinates.FLUID_TANK_UNDERLAY_OFFSET, Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2]);

        FluidVolume content = getTankView().get();
        if (content.isEmpty()) return;
        matrices.push();
        double scale = content.getAmount_F().div(this.getTankView().getMaxAmount_F()).asInexactDouble();
        Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(content.getRawFluid()).getFluidSprites(world, pos, content.getRawFluid().getDefaultState())[0];
        this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
        drawSprite(matrices, this.x + 1, ((this.y + 1) - (int)(this.data[2] * scale)) + this.data[2], 0, Constants.TextureCoordinates.FLUID_TANK_WIDTH - 2, (int)(this.data[2] * scale) - 2, sprite);
        matrices.pop();
        this.client.getTextureManager().bindTexture(MachineHandledScreen.OVERLAY);
        this.drawTexture(matrices, this.x, this.y, this.data[0], this.data[1], Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2]);
    }

    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, u, v, width, height, 128, 128);
    }

    protected int[] getPositionData(FluidAmount capacity) {
        if (capacity.asInexactDouble() != Math.floor(capacity.asInexactDouble())) {
            throw new UnsupportedOperationException("NYI");
        }
        int size = capacity.asInt(1) * scale;

        if (size < 1 || size > 16) {
            throw new UnsupportedOperationException("NYI");
        }
        if (size > 8 && size % 2 == 1) {
            throw new UnsupportedOperationException("NYI");
        }

        if (size == 8 || size == 16) {
            return FLUID_TANK_8_16_DATA;
        } else if (size == 7 || size == 14) {
            return FLUID_TANK_7_14_DATA;
        } else if (size == 6 || size == 12) {
            return FLUID_TANK_6_12_DATA;
        } else if (size == 5 || size == 10) {
            return FLUID_TANK_5_10_DATA;
        } else if (size == 4) {
            return FLUID_TANK_4_8_DATA;
        } else if (size == 3) {
            return FLUID_TANK_3_6_DATA;
        } else if (size == 2) {
            return FLUID_TANK_2_4_DATA;
        } else if (size == 1) {
            return FLUID_TANK_1_2_DATA;
        }
        throw new AssertionError();
    }


    public SingleFluidTankView getTankView() {
        return tankView;
    }
}
