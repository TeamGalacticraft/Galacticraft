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
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.util.Translations.RecipeCategory;
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

import static dev.galacticraft.mod.Constant.CircuitFabricator.*;

@Environment(EnvType.CLIENT)
public class DefaultFabricationCategory implements DisplayCategory<DefaultFabricationDisplay> {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

    @Override
    public CategoryIdentifier<? extends DefaultFabricationDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.FABRICATION;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(RecipeCategory.CIRCUIT_FABRICATOR);
    }

    @Override
    public @NotNull List<Widget> setupDisplay(DefaultFabricationDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.x - RECIPE_VIEWER_X + 5, bounds.y - RECIPE_VIEWER_Y + 5);

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SCREEN_TEXTURE, startPoint.x + PROGRESS_X, startPoint.y + PROGRESS_Y, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V, PROGRESS_WIDTH, PROGRESS_HEIGHT));

        double processingTime = recipeDisplay.getProcessingTime() / 20.0D;
        widgets.add(new FabricationProgressWidget(startPoint.x + PROGRESS_X, startPoint.y + PROGRESS_Y, recipeDisplay.getProcessingTime()));
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 5),
                Component.translatable(RecipeCategory.REI_TIME, FORMAT.format(processingTime))).noShadow().color(0xFF404040, 0xFFBBBBBB));

        // Diamond ingredients
        // Silicon
        // Silicon
        // Redstone
        // User ingredients
        // Output
        widgets.add(Widgets.createSlot(new Point(startPoint.x + DIAMOND_X, startPoint.y + DIAMOND_Y)).markInput().entries(recipeDisplay.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + SILICON_X_1, startPoint.y + SILICON_Y_1)).markInput().entries(recipeDisplay.getInputEntries().get(1)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + SILICON_X_2, startPoint.y + SILICON_Y_2)).markInput().entries(recipeDisplay.getInputEntries().get(2)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + REDSTONE_X, startPoint.y + REDSTONE_Y)).markInput().entries(recipeDisplay.getInputEntries().get(3)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + INGREDIENT_X, startPoint.y + INGREDIENT_Y)).markInput().entries(recipeDisplay.getInputEntries().get(4)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + OUTPUT_X, startPoint.y + OUTPUT_Y)).markOutput().entries(recipeDisplay.getOutputEntries().get(0)));
        return widgets;
    }

    @Override
    public int getDisplayWidth(DefaultFabricationDisplay display) {
        return RECIPE_VIEWER_WIDTH + 10;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_VIEWER_HEIGHT + 10;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }
}