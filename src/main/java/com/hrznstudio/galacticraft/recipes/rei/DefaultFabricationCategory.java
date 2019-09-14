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

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.Renderer;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.SlotWidget;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
public class DefaultFabricationCategory implements RecipeCategory<DefaultFabricationDisplay> {
    private static final Identifier DISPLAY_TEXTURE = new Identifier("galacticraft-rewoven", "textures/gui/rei_display.png");

    public Identifier getIdentifier() {
        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
    }

    @Override
    public Renderer getIcon() {
        return Renderer.fromItemStack(GalacticraftBlocks.CIRCUIT_FABRICATOR.asItem().getStackForRender());
    }

    public String getCategoryName() {
        return I18n.translate("category.rei.circuit_fabricator");
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public List<Widget> setupDisplay(Supplier<DefaultFabricationDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 81, bounds.getCenterY() - 41);
//        final Point startPoint = new Point((int) bounds.getCenterX() - 41, (int) bounds.getCenterY() - 27);

        class NamelessClass_1 extends RecipeBaseWidget {
            private NamelessClass_1(Rectangle bounds) {
                super(bounds);
            }

            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GuiLighting.disable();
                MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultFabricationCategory.DISPLAY_TEXTURE);
                this.blit(startPoint.x, startPoint.y, 0, 0, 162, 82);

                int height = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 14.0D / 1.0D);
                this.blit(startPoint.x + 2, startPoint.y + 21 + (14 - height), 82, 77 + (14 - height), 14, height);
                int width = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 24.0D / 1.0D);
                this.blit(startPoint.x + 24, startPoint.y + 18, 82, 91, width, 17);
            }
        }

        DefaultFabricationDisplay recipeDisplay = recipeDisplaySupplier.get();
        List<Widget> widgets = new LinkedList<>(Collections.singletonList(new NamelessClass_1(bounds)));

        // Diamond input
        // Silicon
        // Silicon
        // Redstone
        // User input
        // Output
        widgets.add(new SlotWidget(startPoint.x + (18 * 0) + 1, startPoint.y + 1, Collections.singletonList(Renderer.fromItemStack(new ItemStack(Items.DIAMOND))), false, true, true));
        widgets.add(new SlotWidget(startPoint.x + (18 * 7) + 1, startPoint.y + 1, recipeDisplay.getInput().get(0).stream().map(Renderer::fromItemStack).collect(Collectors.toList()), false, true, true));

        widgets.add(new SlotWidget(startPoint.x + (18 * 3) + 1, startPoint.y + 47, Collections.singletonList(Renderer.fromItemStack(new ItemStack(GalacticraftItems.RAW_SILICON))), false, true, true));
        widgets.add(new SlotWidget(startPoint.x + (18 * 3) + 1, startPoint.y + 47 + 18, Collections.singletonList(Renderer.fromItemStack(new ItemStack(GalacticraftItems.RAW_SILICON))), false, true, true));
        widgets.add(new SlotWidget(startPoint.x + (18 * 6) + 1, startPoint.y + 47, Collections.singletonList(Renderer.fromItemStack(new ItemStack(Items.REDSTONE))), false, true, true));

        widgets.add(new SlotWidget(startPoint.x + (18 * 8) + 1, startPoint.y + 47 + 18, recipeDisplay.getOutput().stream().map(Renderer::fromItemStack).collect(Collectors.toList()), false, true, true));
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
    public int getMaximumRecipePerPage() {
        return 99;
    }
}