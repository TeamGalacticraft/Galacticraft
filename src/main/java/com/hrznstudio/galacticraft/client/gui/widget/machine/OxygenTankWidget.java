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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.fluid.TankComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OxygenTankWidget extends AbstractWidget {
    private final TankComponent component;
    private final int tank;
    private final int x;
    private final int y;
    private final int height;
    
    public OxygenTankWidget(TankComponent component, int tank, int x, int y, int height) {
        this.component = component;
        this.tank = tank;
        this.x = x;
        this.y = y;
        this.height = height;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.client.getTextureManager().bindTexture(MachineHandledScreen.OVERLAY);
        float scale = getComponent().getContents(tank).getAmount().divide(getComponent().getMaxCapacity(tank)).floatValue();

        int height = this.height;
        while (height > 0) {
            int renderHeight = Math.min(height, Constants.TextureCoordinates.OVERLAY_HEIGHT);
            render(matrices, height, scale);
            height -= renderHeight;
        }
    }

    private void render(MatrixStack matrices, int height, float scale) {
        this.drawTexture(matrices, this.x, this.y, Constants.TextureCoordinates.OXYGEN_DARK_X, Constants.TextureCoordinates.OXYGEN_DARK_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, height);
        this.drawTexture(matrices, this.x, (int) ((this.y - (height * scale)) + height), Constants.TextureCoordinates.OXYGEN_LIGHT_X, Constants.TextureCoordinates.OXYGEN_LIGHT_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) (height * scale));
    }

    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, u, v, width, height, 128, 128);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinates.OVERLAY_WIDTH, Constants.TextureCoordinates.OVERLAY_HEIGHT)) {
            List<Text> lines = new ArrayList<>(2);
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_oxygen", new LiteralText(Screen.hasShiftDown() ? getComponent().getContents(tank).getAmount().toString() + "B" : (((int) (getComponent().getContents(tank).getAmount().doubleValue() * 1000.0D)) + "mB")).setStyle(Style.EMPTY.withColor(Formatting.BLUE))).setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_oxygen", new LiteralText(String.valueOf((int)(OxygenCollectorBlockEntity.MAX_OXYGEN.doubleValue() * 1000.0D))).setStyle(Style.EMPTY.withColor(Formatting.BLUE))).setStyle(Style.EMPTY.withColor(Formatting.RED)));

            this.client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    public TankComponent getComponent() {
        return component;
    }
}
