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
import dev.galacticraft.mod.compat.rei.common.display.DefaultCanningDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.util.Translations;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

@Environment(EnvType.CLIENT)
public class DefaultCanningCategory implements DisplayCategory<DefaultCanningDisplay> {

    @Override
    public CategoryIdentifier<? extends DefaultCanningDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.CANNING;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(GCBlocks.FOOD_CANNER.asItem().getDefaultInstance());
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.CANNING);
    }

    @Override
    public @NotNull List<Widget> setupDisplay(DefaultCanningDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - FOOD_CANNER_WIDTH / 2, bounds.getCenterY() - FOOD_CANNER_HEIGHT / 2);

        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(RECIPE_VIEWER_DISPLAY_TEXTURE, startPoint.x, startPoint.y, FOOD_CANNER_U, FOOD_CANNER_V, FOOD_CANNER_WIDTH, FOOD_CANNER_HEIGHT));

        List<EntryIngredient> ingredients = recipeDisplay.getInputEntries();
        for (int i = 0; i < ingredients.size(); i++) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + GRID_X + 18 * (i % 4), startPoint.y + GRID_Y + 18 * (i / 4))).entries(recipeDisplay.getInputEntries().get(i)));
        }

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + MIDDLE_SLOT_Y)).markOutput().entries(recipeDisplay.getOutputEntries().get(0)));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return FOOD_CANNER_HEIGHT + 8;
    }

    @Override
    public int getDisplayWidth(DefaultCanningDisplay display) {
        return FOOD_CANNER_WIDTH + 8;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }
}