/*
 * Copyright (c) 2018-2019 Horizon Studio
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
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.Renderer;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.SlotWidget;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    public Renderer getIcon() {
        return Renderer.fromItemStack(new ItemStack(GalacticraftBlocks.COMPRESSOR));
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
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GuiLighting.disable();
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
        List<List<ItemStack>> input = recipeDisplaySupplier.get().getInput();
        List<SlotWidget> slots = Lists.newArrayList();

        // 3x3 grid
        // Output
        int i;
        for (i = 0; i < 3; ++i) {
            for (int x = 0; x < 3; ++x) {
                slots.add(new SlotWidget(startPoint.x + (x * 18) + 1, startPoint.y + (i * 18) + 1, Lists.newArrayList(), false, true, true));
            }
        }
        for (i = 0; i < input.size(); ++i) {
            if (recipeDisplay != null) {
                if (!input.get(i).isEmpty()) {
                    slots.get(this.getSlotWithSize(recipeDisplay, i)).setItemList(input.get(i));
                }
            } else if (!input.get(i).isEmpty()) {
                slots.get(i).setItemList(input.get(i));
            }
        }

        widgets.addAll(slots);
        widgets.add(new SlotWidget(startPoint.x + 120, startPoint.y + (18 * 1) + 3, recipeDisplay.getOutput().stream().map(Renderer::fromItemStack).collect(Collectors.toList()), false, true, true));/* {

            protected String getItemCountOverlay(ItemStack currentStack) {
                if (currentStack.getCount() == 1)
                    return "";
                if (currentStack.getCount() < 1)
                    return "§c" + currentStack.getCount();
                return currentStack.getCount() + "";
            }
        });*/
        widgets.add(new SlotWidget(startPoint.x + (2 * 18) + 1, startPoint.y + (18 * 3) + 4,
                AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().stream().map(ItemStack::new)
                        .collect(Collectors.toList()).stream().map(Renderer::fromItemStack).collect(Collectors.toList()), false, true, true));
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