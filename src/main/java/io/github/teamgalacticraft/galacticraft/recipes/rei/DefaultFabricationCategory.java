package io.github.teamgalacticraft.galacticraft.recipes.rei;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.ItemSlotWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class DefaultFabricationCategory implements RecipeCategory<DefaultFabricationDisplay> {
    private static final Identifier DISPLAY_TEXTURE = new Identifier("galacticraft-rewoven", "textures/gui/rei_display.png");

    public Identifier getIdentifier() {
        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
    }

    public ItemStack getCategoryIcon() {
        return new ItemStack(GalacticraftBlocks.CIRCUIT_FABRICATOR_BLOCK);
    }

    public String getCategoryName() {
        return I18n.translate("category.rei.fabrication");
    }

    public List<Widget> setupDisplay(Supplier<DefaultFabricationDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point((int) bounds.getCenterX() - 72, (int) bounds.getCenterY() - 47);
//        final Point startPoint = new Point((int) bounds.getCenterX() - 41, (int) bounds.getCenterY() - 27);

        class NamelessClass_1 extends RecipeBaseWidget {
            NamelessClass_1(Rectangle bounds) {
                super(bounds);
            }

            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GuiLighting.disable();
                MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultFabricationCategory.DISPLAY_TEXTURE);
                this.blit(startPoint.x, startPoint.y, 0, 54, 82, 54);
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
        widgets.add(new ItemSlotWidget(startPoint.x, startPoint.y + 15, Arrays.asList(new ItemStack(Items.DIAMOND)), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (18 * 3), startPoint.y + 61, Arrays.asList(new ItemStack(GalacticraftItems.RAW_SILICON)), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (18 * 3), startPoint.y + 61 - 18, Arrays.asList(new ItemStack(GalacticraftItems.RAW_SILICON)), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (18 * 6), startPoint.y + 61 - 18, Arrays.asList(new ItemStack(Items.REDSTONE)), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (18 * 7), startPoint.y + 15, recipeDisplay.getInput().get(0), false, true, true));
        widgets.add(new ItemSlotWidget(startPoint.x + (18 * 7), startPoint.y + 61, recipeDisplay.getOutput(), false, true, true));

        return widgets;
    }
}