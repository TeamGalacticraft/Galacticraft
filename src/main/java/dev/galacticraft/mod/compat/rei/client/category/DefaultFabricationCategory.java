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

package dev.galacticraft.mod.compat.rei.client.category;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.compat.rei.client.GalacticraftREIClientPlugin;
import dev.galacticraft.mod.compat.rei.client.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.item.GalacticraftItem;
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
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class DefaultFabricationCategory implements DisplayCategory<DefaultFabricationDisplay> {
    private static final Identifier DISPLAY_TEXTURE = new Identifier(Constant.MOD_ID, "textures/gui/rei_display.png");

    @Override
    public CategoryIdentifier<? extends DefaultFabricationDisplay> getCategoryIdentifier() {
        return GalacticraftREIClientPlugin.CIRCUIT_FABRICATION;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(GalacticraftBlock.CIRCUIT_FABRICATOR.asItem().getDefaultStack());
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("category.rei.circuit_fabricator");
    }

    @Override
    public @NotNull List<Widget> setupDisplay(DefaultFabricationDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 81, bounds.getCenterY() - 41);

        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(DISPLAY_TEXTURE, new Rectangle(startPoint.x, startPoint.y, 162, 82)));

        // Diamond input
        // Silicon
        // Silicon
        // Redstone
        // User input
        // Output
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entry(EntryStacks.of(new ItemStack(Items.DIAMOND))));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 7) + 1, startPoint.y + 1)).markOutput().entries(recipeDisplay.getInputEntries().get(0)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 3) + 1, startPoint.y + 47)).entry(EntryStacks.of(new ItemStack(GalacticraftItem.RAW_SILICON))));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 3) + 1, startPoint.y + 47 + 18)).entry(EntryStacks.of(new ItemStack(GalacticraftItem.RAW_SILICON))));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 6) + 1, startPoint.y + 47)).entry(EntryStacks.of(new ItemStack(Items.REDSTONE))));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 8) + 1, startPoint.y + 47 + 18)).markOutput().entries(recipeDisplay.getOutputEntries().get(0)));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }

    @Override
    public int getDisplayWidth(DefaultFabricationDisplay display) {
        return 170;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }
}