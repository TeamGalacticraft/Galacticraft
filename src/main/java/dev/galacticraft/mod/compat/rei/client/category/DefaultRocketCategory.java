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

import com.google.common.collect.Lists;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.DefaultRocketDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import dev.galacticraft.mod.util.Translations;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

@Environment(EnvType.CLIENT)
public class DefaultRocketCategory implements DisplayCategory<DefaultRocketDisplay> {

    public static RocketEntity ROCKET_ENTITY = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level);

    static {
        ROCKET_ENTITY.setYRot(90.0F);
    }

    @Override
    public CategoryIdentifier<? extends DefaultRocketDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.ROCKET;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(GCBlocks.ROCKET_WORKBENCH));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.ROCKET_WORKBENCH);
    }

    public @NotNull List<Widget> setupDisplay(DefaultRocketDisplay recipeDisplay, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - ROCKET_WORKBENCH_WIDTH / 2, bounds.getCenterY() - ROCKET_WORKBENCH_HEIGHT / 2);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(new RocketBaseWidget(startPoint));
        List<EntryIngredient> input = recipeDisplay.getInputEntries();
        List<Slot> slots = Lists.newArrayList();

        // Cone
        slots.add(Widgets.createSlot(new Point(startPoint.x + 38, startPoint.y + 6)).markInput());

        // Body
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 2; ++x) {
                slots.add(Widgets.createSlot(new Point(startPoint.x + (x * 18) + 29, startPoint.y + (y * 18) + 24)).markInput());
            }
        }

        // Fins
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 2; ++y) {
                slots.add(Widgets.createSlot(new Point(startPoint.x + (x * 54) + 11, startPoint.y + ((y + 4) * 18) + 6)).markInput());
            }
        }

        // Engine
        slots.add(Widgets.createSlot(new Point(startPoint.x + 38, startPoint.y + (5 * 18) + 6)).markInput());

        // Chest
        slots.add(new SlotSpriteWidget(new Point(startPoint.x + 38, startPoint.y + (6 * 18) + 12), Constant.SlotSprite.CHEST).markInput());

        for (int i = 0; i < input.size(); ++i) {
            if (!input.get(i).isEmpty()) {
                if (i == 11 || i == 12) {
                    slots.get(i).entries(input.get(i).stream().map(
                            entry -> (EntryStack<ItemStack>) entry.withRenderer(MirroredEntryRenderer.INSTANCE)
                    ).toList());
                } else {
                    slots.get(i).entries(input.get(i));
                }
            }
        }

        widgets.addAll(slots);
        widgets.add(Widgets.createSlot(new Point(startPoint.x + ROCKET_OUTPUT_X, startPoint.y + ROCKET_OUTPUT_Y)).disableBackground().markOutput().entries(recipeDisplay.getOutputEntries().get(0)));
        widgets.add(new RocketPreviewWidget(startPoint, ROCKET_ENTITY));
        return widgets;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }

    @Override
    public int getDisplayHeight() {
        return ROCKET_WORKBENCH_HEIGHT + 8;
    }

    @Override
    public int getDisplayWidth(DefaultRocketDisplay display) {
        return ROCKET_WORKBENCH_WIDTH + 8;
    }
}