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
import dev.galacticraft.mod.recipe.RocketRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.galacticraft.mod.Constant.RocketWorkbench.*;

@Environment(EnvType.CLIENT)
public class DefaultRocketCategory implements DisplayCategory<DefaultRocketDisplay> {
    // Do not replace with Constant.SlotSprite.CHEST or it will be displayed as a missing texture!
    private static final ResourceLocation CHEST_SLOT_SPRITE = Constant.id("textures/gui/slot/chest.png");
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
        final Point startPoint = new Point(bounds.x - RECIPE_VIEWER_X + 4, bounds.y - RECIPE_VIEWER_Y + 4);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        List<EntryIngredient> input = recipeDisplay.getInputEntries();
        List<Slot> slots = Lists.newArrayList();

        for (RocketRecipe.RocketSlotData data : RocketRecipe.slotData(recipeDisplay.bodyHeight, recipeDisplay.hasBoosters)) {
            slots.add(Widgets.createSlot(new Point(startPoint.x + data.x(), startPoint.y + data.y())).markInput());
        }

        // Chest
        final Point chestPoint = new Point(startPoint.x + CHEST_X, startPoint.y + CHEST_Y);
        widgets.add(Widgets.createTexturedWidget(SCREEN_TEXTURE, chestPoint.x - 2, chestPoint.y - 2, CHEST_U, CHEST_V, CHEST_WIDTH, CHEST_HEIGHT));
        slots.add(new SlotSpriteWidget(chestPoint, CHEST_SLOT_SPRITE).markInput());

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

        final Point outputPoint = new Point(startPoint.x + OUTPUT_X, startPoint.y + OUTPUT_Y);
        widgets.add(Widgets.createTexturedWidget(SCREEN_TEXTURE, outputPoint.x - OUTPUT_X_OFFSET, outputPoint.y - OUTPUT_Y_OFFSET, OUTPUT_U, OUTPUT_V, OUTPUT_WIDTH, OUTPUT_HEIGHT));
        widgets.add(Widgets.createSlotBase(new Rectangle(outputPoint.x - 4, outputPoint.y - 4, OUTPUT_INNER_WIDTH, OUTPUT_INNER_HEIGHT)));
        widgets.add(Widgets.createSlot(outputPoint).disableBackground().markOutput().entries(recipeDisplay.getOutputEntries().get(0)));

        widgets.add(new RocketPreviewWidget(startPoint, ROCKET_ENTITY));
        return widgets;
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 99;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_VIEWER_HEIGHT + 8;
    }

    @Override
    public int getDisplayWidth(DefaultRocketDisplay display) {
        return RECIPE_VIEWER_WIDTH + 8;
    }
}