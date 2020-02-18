/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class DefaultCompressingCategory implements RecipeCategory<DefaultCompressingDisplay> {
    private static final Identifier DISPLAY_TEXTURE = new Identifier("galacticraft-rewoven", "textures/gui/rei_display.png");

    public Identifier getIdentifier() {
        return GalacticraftREIPlugin.COMPRESSING;
    }

    @Override
    public EntryStack getLogo() {
        return EntryStack.create(new ItemStack(GalacticraftBlocks.COMPRESSOR));
    }

    public String getCategoryName() {
        return I18n.translate("category.rei.compressing");
    }

    public List<Widget> setupDisplay(Supplier<DefaultCompressingDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 68, bounds.getCenterY() - 37);

        class NamelessClass_1 extends RecipeBaseWidget {
            NamelessClass_1(Rectangle bounds) {
                super(bounds);
            }

            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                DiffuseLighting.disable();
                MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultCompressingCategory.DISPLAY_TEXTURE);
                this.blit(startPoint.x, startPoint.y, 0, 83, 137, 157);

                int height = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 14.0D / 1.0D);
                this.blit(startPoint.x + 2, startPoint.y + 21 + (14 - height), 82, 77 + (14 - height), 14, height);
                int width = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 24.0D / 1.0D);
                this.blit(startPoint.x + 24, startPoint.y + 18, 82, 91, width, 17);
            }
        }

        DefaultCompressingDisplay recipeDisplay = recipeDisplaySupplier.get();
        List<Widget> widgets = new LinkedList<>(Collections.singletonList(new NamelessClass_1(bounds)));
        List<List<EntryStack>> input = recipeDisplaySupplier.get().getInputEntries();
        List<EntryWidget> slots = Lists.newArrayList();

        // 3x3 grid
        // Output
        int i;
        for (i = 0; i < 3; ++i) {
            for (int x = 0; x < 3; ++x) {
                slots.add(EntryWidget.create(startPoint.x + (x * 18) + 1, startPoint.y + (i * 18) + 1));
            }
        }
        for (i = 0; i < input.size(); ++i) {
            if (recipeDisplay != null) {
                if (!input.get(i).isEmpty()) {
                    slots.get(this.getSlotWithSize(recipeDisplay, i)).entries(input.get(i));
                }
            } else if (!input.get(i).isEmpty()) {
                slots.get(i).entries(input.get(i));
            }
        }

        widgets.addAll(slots);
        widgets.add(EntryWidget.create(startPoint.x + 120, startPoint.y + (18) + 3).entries(new ArrayList<>(Objects.requireNonNull(recipeDisplay).getOutputEntries())));

        widgets.add(EntryWidget.create(startPoint.x + (2 * 18) + 1, startPoint.y + (18 * 3) + 4).entries(AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().stream().map(EntryStack::create).collect(Collectors.toList())));
        return widgets;
    }

    private int getSlotWithSize(DefaultCompressingDisplay recipeDisplay, int num) {
        if (recipeDisplay.getWidth() == 1) {
            if (num == 1) {
                return 3;
            }

            if (num == 2) {
                return 6;
            }
        }

        if (recipeDisplay.getWidth() == 2) {
            if (num == 2) {
                return 3;
            }

            if (num == 3) {
                return 4;
            }

            if (num == 4) {
                return 6;
            }

            if (num == 5) {
                return 7;
            }
        }

        return num;
    }

    @Override
    public int getMaximumRecipePerPage() {
        return 99;
    }

    @Override
    public int getDisplayHeight() {
        return 84;
    }

    @Override
    public int getDisplayWidth(DefaultCompressingDisplay display) {
        return 146;
    }
}