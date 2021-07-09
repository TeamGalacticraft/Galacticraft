/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.energy.api.CapacitorView;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CapacitorWidget extends AbstractWidget {
    private final CapacitorView view;
    private final int x;
    private final int y;
    private final int height;
    private final Supplier<Collection<? extends Text>> tooltipSupplier;
    private final Supplier<MachineStatus> statusSupplier;

    public CapacitorWidget(CapacitorView view, int x, int y, int height, Supplier<Collection<? extends Text>> tooltipSupplier, Supplier<MachineStatus> statusSupplier) {
        this.view = view;
        this.x = x;
        this.y = y;
        this.height = height;
        this.tooltipSupplier = tooltipSupplier;
        this.statusSupplier = statusSupplier;
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            List<Text> lines = new LinkedList<>();
            MachineStatus status = statusSupplier.get();
            if (status != MachineStatus.NULL) lines.add(new TranslatableText("ui.galacticraft.machine.status").setStyle(Constant.Text.GRAY_STYLE).append(status.getName()));
            lines.add(new TranslatableText("ui.galacticraft.machine.current_energy", EnergyUtil.getDisplay(this.getView().getEnergy()).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.GOLD_STYLE));
            lines.add(new TranslatableText("ui.galacticraft.machine.max_energy", EnergyUtil.getDisplay(this.getView().getMaxCapacity()).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.RED_STYLE));
            lines.addAll(tooltipSupplier.get());

            this.client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        double scale = ((double) this.getView().getEnergy()) / ((double) this.getView().getMaxCapacity());

        int height = this.height;
        while (height != 0) {
            int renderHeight = Math.min(height, Constant.TextureCoordinate.OVERLAY_HEIGHT);
            this.render(matrices, height, scale);
            height -= renderHeight;
        }
    }

    private void render(MatrixStack matrices, int height, double scale) {
        DrawableUtil.drawProgressTexture(matrices, this.x, this.y, this.getZOffset(), Constant.TextureCoordinate.ENERGY_DARK_X, Constant.TextureCoordinate.ENERGY_DARK_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, height, 128, 128);
        DrawableUtil.drawProgressTexture(matrices, this.x, (int) ((this.y - (height * scale)) + height), this.getZOffset(), Constant.TextureCoordinate.ENERGY_LIGHT_X, Constant.TextureCoordinate.ENERGY_LIGHT_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, (float) (height * scale), 128, 128);
    }

    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, u, v, width, height, 128, 128);
    }

    public CapacitorView getView() {
        return this.view;
    }
}
