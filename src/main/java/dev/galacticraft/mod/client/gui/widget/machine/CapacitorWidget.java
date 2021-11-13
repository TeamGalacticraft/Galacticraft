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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.util.DrawableUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import team.reborn.energy.api.EnergyStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CapacitorWidget extends AbstractWidget {
    private final EnergyStorage storage;
    private final int height;
    private final Consumer<List<Text>> tooltipTransformer;
    private final Supplier<MachineStatus> statusSupplier;

    public CapacitorWidget(MachineHandledScreen<?, ?> screen, int x, int y, int height) {
        super(x, y, 16, height);
        this.storage = screen.getScreenHandler().machine.capacitorView();
        this.height = height;
        this.tooltipTransformer = screen::appendEnergyTooltip;
        this.statusSupplier = screen.getScreenHandler().machine::getStatus;
    }

    @Override
    public void renderForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderForeground(matrices, mouseX, mouseY);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        double scale = ((double) this.getStorage().getAmount()) / ((double) this.getStorage().getCapacity());

        int height = this.height;
        int y = 0;
        while (height != 0) {
            int renderHeight = Math.min(height, Constant.TextureCoordinate.OVERLAY_HEIGHT);
            this.render(matrices, height, y, scale);
            y += renderHeight;
            height -= renderHeight;
        }
    }

    @Override
    public void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        super.renderTooltips(matrices, mouseX, mouseY);
        List<Text> lines = new LinkedList<>();
        MachineStatus status = statusSupplier.get();
        if (status != MachineStatus.NULL) {
            lines.add(new TranslatableText("ui.galacticraft.machine.status").setStyle(Constant.Text.GRAY_STYLE).append(status.getName()));
        }
        lines.add(new TranslatableText("ui.galacticraft.machine.current_energy", DrawableUtil.getEnergyDisplay(this.getStorage().getAmount()).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.GOLD_STYLE));
        lines.add(new TranslatableText("ui.galacticraft.machine.max_energy", DrawableUtil.getEnergyDisplay(this.getStorage().getCapacity()).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.RED_STYLE));
        tooltipTransformer.accept(lines);

        assert this.client.currentScreen != null;
        this.client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
    }

    private void render(MatrixStack matrices, int height, int y, double scale) {
        DrawableUtil.drawProgressTexture(matrices, this.x, this.y + y, this.getZOffset(), Constant.TextureCoordinate.ENERGY_DARK_X, Constant.TextureCoordinate.ENERGY_DARK_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, height, 128, 128);
        DrawableUtil.drawProgressTexture(matrices, this.x, (float)(this.y - (height * scale) + height) + y, this.getZOffset(), Constant.TextureCoordinate.ENERGY_LIGHT_X, Constant.TextureCoordinate.ENERGY_LIGHT_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, (float) (height * scale), 128, 128);
    }

    public EnergyStorage getStorage() {
        return this.storage;
    }
}
