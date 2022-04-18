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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.client.gui.widget.machine.CapacitorWidget;
import dev.galacticraft.mod.screen.RecipeMachineScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricFurnaceScreen extends MachineHandledScreen<ElectricFurnaceBlockEntity, RecipeMachineScreenHandler<Inventory, SmeltingRecipe, ElectricFurnaceBlockEntity>> {
    private static final int ARROW_X = 74;
    private static final int ARROW_Y = 34;
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;
    private static final int ARROW_WIDTH = 30;
    private static final int ARROW_HEIGHT = 16;

    public ElectricFurnaceScreen(RecipeMachineScreenHandler<Inventory, SmeltingRecipe, ElectricFurnaceBlockEntity> handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.ELECTRIC_FURNACE_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new CapacitorWidget(this, this.x + 8, this.y + 8, 48));
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        if (this.machine.maxProgress() > 0 && this.machine.progress() != 0) {
            double scale = ((double)handler.machine.progress()) / ((double)handler.machine.maxProgress());

            DrawableUtil.drawProgressTexture(matrices, this.x + ARROW_X, this.y + ARROW_Y, ARROW_U, ARROW_V, (float)(((double)ARROW_WIDTH) * scale), ARROW_HEIGHT);
        }
    }
}
