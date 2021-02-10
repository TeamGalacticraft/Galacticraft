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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity.MachineStatus;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.energy.CapacitorComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CapacitorWidget extends AbstractWidget {
    private final CapacitorComponent component;
    private final int x;
    private final int y;
    private final int height;
    private final Supplier<Collection<? extends Text>> tooltipSupplier;
    private final Supplier<MachineStatus> statusSupplier;

    public CapacitorWidget(CapacitorComponent component, int x, int y, int height, Supplier<Collection<? extends Text>> tooltipSupplier, Supplier<MachineStatus> statusSupplier) {
        this.component = component;
        this.x = x;
        this.y = y;
        this.height = height;
        this.tooltipSupplier = tooltipSupplier;
        this.statusSupplier = statusSupplier;
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinates.OVERLAY_WIDTH, Constants.TextureCoordinates.OVERLAY_HEIGHT)) {
            List<Text> lines = new LinkedList<>();
            MachineStatus status = statusSupplier.get();
            if (status != MachineStatus.EMPTY) lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(status.getName()));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).append(GalacticraftEnergy.GALACTICRAFT_JOULES.getDisplayAmount(this.getComponent().getCurrentEnergy()).setStyle(Style.EMPTY.withColor(Formatting.BLUE))));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_energy").setStyle(Style.EMPTY.withColor(Formatting.RED)).append(GalacticraftEnergy.GALACTICRAFT_JOULES.getDisplayAmount(this.getComponent().getMaxEnergy()).setStyle(Style.EMPTY.withColor(Formatting.BLUE))));
            lines.addAll(tooltipSupplier.get());

            this.client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.client.getTextureManager().bindTexture(MachineHandledScreen.OVERLAY);
        float scale = ((float)getComponent().getCurrentEnergy() / (float)getComponent().getMaxEnergy());

        int height = this.height;
        while (height != 0) {
            int renderHeight = Math.min(height, Constants.TextureCoordinates.OVERLAY_HEIGHT);
            render(matrices, height, scale);
            height -= renderHeight;
        }
    }

    private void render(MatrixStack matrices, int height, float scale) {
        this.drawTexture(matrices, this.x, this.y, Constants.TextureCoordinates.ENERGY_DARK_X, Constants.TextureCoordinates.ENERGY_DARK_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, height);
        this.drawTexture(matrices, this.x, (int) ((this.y - (height * scale)) + height), Constants.TextureCoordinates.ENERGY_LIGHT_X, Constants.TextureCoordinates.ENERGY_LIGHT_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) (height * scale));
    }

    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, u, v, width, height, 128, 128);
    }

    public CapacitorComponent getComponent() {
        return component;
    }
}
