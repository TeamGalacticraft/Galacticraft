/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.compat.rei.client.category;

import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.ElectricArcFurnaceDisplay;
import dev.galacticraft.mod.util.Translations;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static dev.galacticraft.mod.Constant.ElectricArcFurnace.*;
import static dev.galacticraft.mod.content.GCBlocks.ELECTRIC_ARC_FURNACE;

@Environment(EnvType.CLIENT)
public class ElectricArcFurnaceCategory implements DisplayCategory<ElectricArcFurnaceDisplay> {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

    @Override
    public CategoryIdentifier<? extends ElectricArcFurnaceDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.ELECTRIC_BLASTING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(ELECTRIC_ARC_FURNACE));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.ELECTRIC_ARC_FURNACE);
    }

    @Override
    public @NotNull List<Widget> setupDisplay(ElectricArcFurnaceDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.x + (bounds.width - REI_WIDTH) / 2 - REI_X, bounds.y - REI_Y + 5);

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SCREEN_TEXTURE, startPoint.x + PROGRESS_X, startPoint.y + PROGRESS_Y, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V, PROGRESS_WIDTH, PROGRESS_HEIGHT));

        double processingTime = recipeDisplay.getProcessingTime() * 50.0D;
        widgets.add(new CustomArrowWidget(SCREEN_TEXTURE, new Rectangle(startPoint.x + PROGRESS_X, startPoint.y + PROGRESS_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT), PROGRESS_U, PROGRESS_V, processingTime));
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 5),
                Component.translatable(Translations.RecipeCategory.REI_TIME, FORMAT.format(processingTime / 1000.0D))).noShadow().centered().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + INPUT_X, startPoint.y + INPUT_Y)).markInput().entries(recipeDisplay.getInputEntries().get(0)));

        Point outputPoint = new Point(startPoint.x + OUTPUT_X_1, startPoint.y + OUTPUT_Y_1);
        widgets.add(Widgets.createResultSlotBackground(outputPoint));
        widgets.add(Widgets.createSlot(outputPoint).disableBackground().markOutput().entries(recipeDisplay.getOutputEntries().get(0)));

        outputPoint = new Point(startPoint.x + OUTPUT_X_2, startPoint.y + OUTPUT_Y_2);
        widgets.add(Widgets.createResultSlotBackground(outputPoint));
        widgets.add(Widgets.createSlot(outputPoint).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return REI_HEIGHT + 10;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }
}