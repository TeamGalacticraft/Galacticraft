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

package com.hrznstudio.galacticraft.client.gui.widget.machine;

import alexiil.mc.lib.attributes.fluid.SingleFluidTankView;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.text.WordUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
    private final Level world;
    private final BlockPos pos;
    private final int[] data;
    private final int scale;

    public FluidTankWidget(SingleFluidTankView tankView, int x, int y, Level world, BlockPos pos) {
        this(tankView, x, y, world, pos, 1);
    }

    public FluidTankWidget(SingleFluidTankView tankView, int x, int y, Level world, BlockPos pos, int scale) {
        this.tankView = tankView;
        this.x = x;
        this.y = y;
        this.world = world == null ? this.client.level : world;
        this.pos = pos == null ? new BlockPos(0, 1, 0) : pos;
        this.scale = scale;
        this.data = this.getPositionData(this.getTankView().getMaxAmount_F());
    }

    @Override
    public void drawMouseoverTooltip(PoseStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2])) {
            FluidVolume volume = this.getTankView().get();
            if (volume.isEmpty()) {
                this.client.screen.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.fluid_widget.empty").setStyle(Constants.Styles.GRAY_STYLE), mouseX, mouseY);
                return;
            }
            MutableComponent amount;
            if (Screen.hasShiftDown()) {
                amount = new TextComponent(volume.getAmount_F().toString() + "B");
            } else {
                amount = new TextComponent((volume.getAmount_F().asInt(1000, RoundingMode.HALF_DOWN)) + "mB");
            }

            List<Component> lines = new ArrayList<>(2);
            lines.add(new TranslatableComponent("ui.galacticraft-rewoven.fluid_widget.fluid").setStyle(Constants.Styles.GRAY_STYLE).append(new TextComponent(getName(volume.getRawFluid())).setStyle(Constants.Styles.BLUE_STYLE)));
            lines.add(new TranslatableComponent("ui.galacticraft-rewoven.fluid_widget.amount").setStyle(Constants.Styles.GRAY_STYLE).append(amount.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))));

            this.client.screen.renderComponentTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    private String getName(Fluid fluid) {
        ResourceLocation id = Registry.FLUID.getKey(fluid);
        if (I18n.exists("block." + id.getNamespace() + "." + id.getPath())) {
            return WordUtils.capitalizeFully(I18n.get("block." + id.getNamespace() + "." + id.getPath()));
        }
        return WordUtils.capitalizeFully(id.getPath().replace("flowing", "").replace("still", "").replace("_", " ").trim());
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.client.getTextureManager().bind(MachineHandledScreen.OVERLAY);
        this.blit(matrices, this.x, this.y, this.data[0], this.data[1] + Constants.TextureCoordinates.FLUID_TANK_UNDERLAY_OFFSET, Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2]);

        FluidVolume content = getTankView().get();
        if (content.isEmpty()) return;
        matrices.pushPose();
        double scale = content.getAmount_F().div(this.getTankView().getMaxAmount_F()).asInexactDouble();
        TextureAtlasSprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(content.getRawFluid()).getFluidSprites(world, pos, content.getRawFluid().defaultFluidState())[0];
        this.client.getTextureManager().bind(sprite.atlas().location());
        blit(matrices, this.x + 1, ((this.y + 1) - (int)(this.data[2] * scale)) + this.data[2], 0, Constants.TextureCoordinates.FLUID_TANK_WIDTH - 2, (int)(this.data[2] * scale) - 2, sprite);
        matrices.popPose();
        this.client.getTextureManager().bind(MachineHandledScreen.OVERLAY);
        this.blit(matrices, this.x, this.y, this.data[0], this.data[1], Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2]);
    }

    @Override
    public void blit(PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        blit(matrices, x, y, u, v, width, height, 128, 128);
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
