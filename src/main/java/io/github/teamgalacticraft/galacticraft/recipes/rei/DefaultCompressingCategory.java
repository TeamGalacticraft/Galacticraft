package io.github.teamgalacticraft.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import me.shedaniel.rei.api.DisplaySettings;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.ItemSlotWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultCompressingCategory implements RecipeCategory<DefaultCompressingDisplay> {
    private static final Identifier DISPLAY_TEXTURE = new Identifier("galacticraft-rewoven", "textures/gui/rei_display.png");

    public Identifier getIdentifier() {
        return GalacticraftREIPlugin.COMPRESSING;
    }

    public ItemStack getCategoryIcon() {
        return new ItemStack(GalacticraftBlocks.COMPRESSOR_BLOCK);
    }

    public String getCategoryName() {
        return I18n.translate("category.rei.compressing");
    }

    public List<Widget> setupDisplay(Supplier<DefaultCompressingDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point((int) bounds.getCenterX() - 68, (int) bounds.getCenterY() - 37);
//        final Point startPoint = new Point((int) bounds.getCenterX() - 41, (int) bounds.getCenterY() - 27);

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
        List<ItemSlotWidget> slots = Lists.newArrayList();

        // 3x3 grid
        // Output
        int i;
        for (i = 0; i < 3; ++i) {
            for (int x = 0; x < 3; ++x) {
                slots.add(new ItemSlotWidget(startPoint.x + (x * 18) + 1, startPoint.y + (i * 18) + 1, Lists.newArrayList(), false, true, true));
            }
        }
        for (i = 0; i < input.size(); ++i) {
            if (recipeDisplaySupplier.get() != null) {
                if (!input.get(i).isEmpty()) {
                    slots.get(this.getSlotWithSize(recipeDisplaySupplier.get(), i)).setItemList(input.get(i));
                }
            } else if (!input.get(i).isEmpty()) {
                slots.get(i).setItemList(input.get(i));
            }
        }

        widgets.addAll(slots);
        widgets.add(new ItemSlotWidget(startPoint.x + 120, startPoint.y + (18 * 1) + 3, recipeDisplay.getOutput(), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (2 * 18) + 1, startPoint.y + (18 * 3) + 4, AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().stream().map(ItemStack::new).collect(Collectors.toList()), false, true, true));
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
    public DisplaySettings<DefaultCompressingDisplay> getDisplaySettings() {
        return new DisplaySettings<DefaultCompressingDisplay>() {
            @Override
            public int getDisplayHeight(RecipeCategory recipeCategory) {
                return 84;
            }

            @Override
            public int getDisplayWidth(RecipeCategory recipeCategory, DefaultCompressingDisplay recipeDisplay) {
                return 146;
            }

            @Override
            public int getMaximumRecipePerPage(RecipeCategory recipeCategory) {
                return 99;
            }
        };
    }
}