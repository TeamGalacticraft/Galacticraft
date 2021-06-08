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

//
//package dev.galacticraft.mod.recipe.rei;
//
//import dev.galacticraft.mod.block.GalacticraftBlock;
//import dev.galacticraft.mod.item.GalacticraftItems;
//import me.shedaniel.math.Point;
//import me.shedaniel.math.Rectangle;
//import me.shedaniel.rei.api.EntryStack;
//import me.shedaniel.rei.api.RecipeCategory;
//import me.shedaniel.rei.api.widgets.Widgets;
//import me.shedaniel.rei.gui.widget.Widget;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.Element;
//import net.minecraft.client.render.DiffuseLighting;
//import net.minecraft.client.resource.language.I18n;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.MathHelper;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
// */
//@Environment(EnvType.CLIENT)
//public class DefaultFabricationCategory implements RecipeCategory<DefaultFabricationDisplay> {
//    private static final Identifier DISPLAY_TEXTURE = new Identifier(Constant.MOD_ID, "textures/gui/rei_display.png");
//
//    public @NotNull Identifier getIdentifier() {
//        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
//    }
//
//    @Override
//    @Environment(EnvType.CLIENT)
//    public @NotNull EntryStack getLogo() {
//        return EntryStack.create(GalacticraftBlock.CIRCUIT_FABRICATOR.asItem().getDefaultStack());
//    }
//
//    @Environment(EnvType.CLIENT)
//    public @NotNull String getCategoryName() {
//        return I18n.translate("category.rei.circuit_fabricator");
//    }
//
//    @Override
//    public @NotNull List<Widget> setupDisplay(DefaultFabricationDisplay recipeDisplay, Rectangle bounds) {
//        final Point startPoint = new Point(bounds.getCenterX() - 81, bounds.getCenterY() - 41);
//
//        class BaseWidget extends Widget {
//            private BaseWidget() {
//            }
//
//            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//                DiffuseLighting.disableGuiDepthLighting();
//                RenderSystem.setShaderTexture(0, DefaultFabricationCategory.DISPLAY_TEXTURE);
//                this.drawTexture(matrices, startPoint.x, startPoint.y, 0, 0, 162, 82);
//
//                int height = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 14.0D);
//                this.drawTexture(matrices, startPoint.x + 2, startPoint.y + 21 + (14 - height), 82, 77 + (14 - height), 14, height);
//                int width = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 24.0D);
//                this.drawTexture(matrices, startPoint.x + 24, startPoint.y + 18, 82, 91, width, 17);
//            }
//
//            @Override
//            public List<? extends Element> children() {
//                return Collections.emptyList();
//            }
//        }
//
//        List<Widget> widgets = new LinkedList<>();
//        widgets.add(new BaseWidget());
//
//        // Diamond input
//        // Silicon
//        // Silicon
//        // Redstone
//        // User input
//        // Output
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entry(EntryStack.create(new ItemStack(Items.DIAMOND))));
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 7) + 1, startPoint.y + 1)).entries(recipeDisplay.getInput().get(0)));
//
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 3) + 1, startPoint.y + 47)).entry(EntryStack.create(new ItemStack(GalacticraftItems.RAW_SILICON))));
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 3) + 1, startPoint.y + 47 + 18)).entry(EntryStack.create(new ItemStack(GalacticraftItems.RAW_SILICON))));
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 6) + 1, startPoint.y + 47)).entry(EntryStack.create(new ItemStack(Items.REDSTONE))));
//
//        widgets.add(Widgets.createSlot(new Point(startPoint.x + (18 * 8) + 1, startPoint.y + 47 + 18)).entries(recipeDisplay.getOutputEntries()));
//        return widgets;
//    }
//
//    @Override
//    public int getDisplayHeight() {
//        return 90;
//    }
//
//    @Override
//    public int getDisplayWidth(DefaultFabricationDisplay display) {
//        return 170;
//    }
//
//    @Override
//    public int getMaximumRecipePerPage() {
//        return 99;
//    }
//}