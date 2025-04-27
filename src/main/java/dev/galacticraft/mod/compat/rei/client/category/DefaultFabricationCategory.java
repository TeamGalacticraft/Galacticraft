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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

@Environment(EnvType.CLIENT)
public class DefaultFabricationCategory implements DisplayCategory<DefaultFabricationDisplay> {

    @Override
    public CategoryIdentifier<? extends DefaultFabricationDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.CIRCUIT_FABRICATION;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(GCBlocks.CIRCUIT_FABRICATOR.asItem().getDefaultInstance());
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.CIRCUIT_FABRICATOR);
    }

    @Override
    public @NotNull List<Widget> setupDisplay(DefaultFabricationDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - CIRCUIT_FABRICATOR_WIDTH / 2, bounds.getCenterY() - CIRCUIT_FABRICATOR_HEIGHT / 2);

        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(RECIPE_VIEWER_DISPLAY_TEXTURE, new Rectangle(startPoint.x, startPoint.y, CIRCUIT_FABRICATOR_WIDTH, CIRCUIT_FABRICATOR_HEIGHT)));

        DecimalFormat df = new DecimalFormat("###.##");
        double processingTime = recipeDisplay.getProcessingTime();
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 5),
                Component.translatable("category.rei.campfire.time", df.format(processingTime / 20.0D))).noShadow().color(0xFF404040, 0xFFBBBBBB));

        // Diamond ingredients
        // Silicon
        // Silicon
        // Redstone
        // User ingredients
        // Output
        widgets.add(Widgets.createSlot(new Point(startPoint.x + DIAMOND_X, startPoint.y + DIAMOND_Y)).entries(recipeDisplay.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + SILICON_X_1, startPoint.y + SILICON_Y_1)).entries(recipeDisplay.getInputEntries().get(1)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + SILICON_X_2, startPoint.y + SILICON_Y_2)).entries(recipeDisplay.getInputEntries().get(2)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + REDSTONE_X, startPoint.y + REDSTONE_Y)).entries(recipeDisplay.getInputEntries().get(3)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + INGREDIENT_X, startPoint.y + INGREDIENT_Y)).entries(recipeDisplay.getInputEntries().get(4)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + WAFER_X, startPoint.y + WAFER_Y)).markOutput().entries(recipeDisplay.getOutputEntries().get(0)));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return CIRCUIT_FABRICATOR_HEIGHT + 8;
    }

    @Override
    public int getDisplayWidth(DefaultFabricationDisplay display) {
        return CIRCUIT_FABRICATOR_WIDTH + 8;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }
}