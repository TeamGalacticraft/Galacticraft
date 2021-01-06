/*
 * Copyright (c) 2020 HRZN LTD
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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class FluidTankWidget extends AbstractWidget {
    private final TankComponent component;
    private final int x;
    private final int y;
    private final int tank;
    private final World world;
    private final BlockPos pos;
    private final int[] data;
    private final int scale;

    public FluidTankWidget(TankComponent component, int x, int y, int tank, World world, BlockPos pos) {
        this(component, x, y, tank, world, pos, 1);
    }

    public FluidTankWidget(TankComponent component, int x, int y, int tank, World world, BlockPos pos, int scale) {
        this.component = component;
        this.x = x;
        this.y = y;
        this.tank = tank;
        this.world = world == null ? this.client.world : world;
        this.pos = pos == null ? new BlockPos(0, 1, 0) : pos;
        this.scale = scale;
        this.data = getData(getComponent().getMaxCapacity(this.tank));
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinates.FLUID_TANK_WIDTH, this.data[2])) {
            FluidVolume volume = getComponent().getContents(this.tank);
            if (volume.isEmpty()) {
                this.client.currentScreen.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.fluid_widget.empty").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), mouseX, mouseY);
                return;
            }
            MutableText amount;
            if (Screen.hasShiftDown()) {
                amount = new LiteralText(volume.getAmount().toString() + "B");
            } else {
                amount = new LiteralText((int)(volume.getAmount().doubleValue() * 1000.0d) + "mB");
            }

            List<Text> lines = new ArrayList<>(2);
            lines.add(new TranslatableText("ui.galacticraft-rewoven.fluid_widget.fluid").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(new LiteralText(getName(volume.getFluid())).setStyle(Style.EMPTY.withColor(Formatting.AQUA))));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.fluid_widget.amount").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(amount.setStyle(Style.EMPTY.withColor(Formatting.WHITE))));

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

        FluidVolume content = getComponent().getContents(this.tank);
        if (content.isEmpty()) return;
        matrices.push();
        double scale = content.getAmount().divide(this.getComponent().getMaxCapacity(this.tank)).doubleValue();
        Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(content.getFluid()).getFluidSprites(world, pos, content.getFluid().getDefaultState())[0];
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

    protected int[] getData(Fraction tankCap) {
        if (tankCap.doubleValue() != Math.floor(tankCap.doubleValue())) {
            throw new UnsupportedOperationException("NYI");
        }
        int size = tankCap.intValue() * scale;

        if (size < 1 || size > 16) {
            throw new UnsupportedOperationException("NYI");
        }
        if (size > 8 && size % 2 == 1) {
            throw new UnsupportedOperationException("NYI");
        }

        if (size == 8 || size == 16) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_8_16_X,
                    Constants.TextureCoordinates.FLUID_TANK_8_16_Y,
                    Constants.TextureCoordinates.FLUID_TANK_8_16_HEIGHT
            };
        } else if (size == 7 || size == 14) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_7_14_X,
                    Constants.TextureCoordinates.FLUID_TANK_7_14_Y,
                    Constants.TextureCoordinates.FLUID_TANK_7_14_HEIGHT
            };
        } else if (size == 6 || size == 12) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_6_12_X,
                    Constants.TextureCoordinates.FLUID_TANK_6_12_Y,
                    Constants.TextureCoordinates.FLUID_TANK_6_12_HEIGHT
            };
        } else if (size == 5 || size == 10) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_5_10_X,
                    Constants.TextureCoordinates.FLUID_TANK_5_10_Y,
                    Constants.TextureCoordinates.FLUID_TANK_5_10_HEIGHT
            };
        } else if (size == 4) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_4_8_X,
                    Constants.TextureCoordinates.FLUID_TANK_4_8_Y,
                    Constants.TextureCoordinates.FLUID_TANK_4_8_HEIGHT
            };
        } else if (size == 3) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_3_6_X,
                    Constants.TextureCoordinates.FLUID_TANK_3_6_Y,
                    Constants.TextureCoordinates.FLUID_TANK_3_6_HEIGHT
            };
        } else if (size == 2) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_2_4_X,
                    Constants.TextureCoordinates.FLUID_TANK_2_4_Y,
                    Constants.TextureCoordinates.FLUID_TANK_2_4_HEIGHT
            };
        } else if (size == 1) {
            return new int[]{
                    Constants.TextureCoordinates.FLUID_TANK_1_2_X,
                    Constants.TextureCoordinates.FLUID_TANK_1_2_Y,
                    Constants.TextureCoordinates.FLUID_TANK_1_2_HEIGHT
            };
        }
        throw new RuntimeException();
    }


    public TankComponent getComponent() {
        return component;
    }
}
