/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.BatchedRenderer;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketWorkbenchScreen extends AbstractContainerScreen<RocketWorkbenchMenu> {
    private static final int RED_SLOT_U = 472;
    private static final int RED_SLOT_V = 0;

    private static final int BLUE_SLOT_U = 492;
    private static final int BLUE_SLOT_V = 0;

    private static final int YELLOW_SLOT_U = 472;
    private static final int YELLOW_SLOT_V = 20;

    private static final int GREEN_SLOT_U = 492;
    private static final int GREEN_SLOT_V = 20;

    private static final int COLOURED_SLOT_SIZE = 20;

    private static final int OUTPUT_SLOT_V = 40;
    private static final int OUTPUT_SLOT_SIZE = 34;

    private static final int NORMAL_SLOT_U = 452;
    private static final int NORMAL_SLOT_V = 0;
    private static final int NORMAL_SLOT_SIZE = 18;

    private static final int SELECTION_SCREEN_WIDTH = 147;
    private static final int SELECTION_SCREEN_HEIGHT = 248;
    private static final int SCREEN_SPACING = 4;

    private static final int TAB_SPACING = 1;
    private static final int TAB_SELECTED_U = 219;
    private static final int TAB_SELECTED_V = 127;
    private static final int TAB_U = 224;
    private static final int TAB_V = 154;
    private static final int TAB_HEIGHT = 26;
    private static final int TAB_WIDTH = 30;
    private static final int TAB_SELECTED_WIDTH = 35;

    private static final int FILTER_ALL_U = 202;
    private static final int FILTER_KNOWN_U = 230;
    private static final int FILTER_V = 0;
    private static final int FILTER_V_HOVER = 18;
    private static final int FILTER_WIDTH = 26;
    private static final int FILTER_HEIGHT = 16;

    private static final int RECIPE_KNOWN_U = 204;
    private static final int RECIPE_UNKNOWN_U = 231;
    private static final int RECIPE_V = 36;
    private static final int RECIPE_WIDTH = 25;
    private static final int RECIPE_HEIGHT = 25;

    private static final int BASE_UI_WIDTH = 234;
    private static final int MAIN_UI_WIDTH = 177;
    private static final int BASE_UI_HEIGHT = 245;
    private static final int CAP_HEIGHT = 4;
    private static final int HEIGHT_EXT_U = 234;

    private Tab openTab;

    private int page = 0;
    private @Nullable List<RocketPartRecipe<?, ?>> recipes = null;

    public RocketWorkbenchScreen(RocketWorkbenchMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.setOpenTab(Tab.CONE);
    }

    @Override
    protected void init() {
        this.imageWidth = BASE_UI_WIDTH;
        this.imageHeight = BASE_UI_HEIGHT + CAP_HEIGHT + this.menu.additionalHeight;
        this.inventoryLabelY = this.imageHeight - 96;
        super.init();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        drawSelection(graphics, mouseX, mouseY);

        try (BatchedRenderer render = new BatchedRenderer(Tesselator.getInstance().getBuilder(), graphics.pose(), Constant.ScreenTexture.ROCKET_WORKBENCH_SCREEN, 512, 256)) {
            render.blit(this.leftPos, this.topPos + this.menu.additionalHeight + CAP_HEIGHT, 0, 0, BASE_UI_WIDTH, BASE_UI_HEIGHT);

            if (this.menu.additionalHeight > 0) {
                render.blit(this.leftPos, this.topPos + CAP_HEIGHT, HEIGHT_EXT_U, 0, MAIN_UI_WIDTH, this.menu.additionalHeight);
            }

            render.blit(this.leftPos, this.topPos, 0, BASE_UI_HEIGHT, BASE_UI_WIDTH, CAP_HEIGHT);

            for (Slot slot : this.menu.slots) {
                if (slot.container instanceof VariableSizedContainer) {
                    batchNormalSlot(render, slot.x - 1, slot.y - 1);
                }
            }
        }
    }

    private void drawSlots(BatchedRenderer render, float delta, int mouseX, int mouseY) {

//        RocketPartRecipe<?, ?> bottomSelection = this.menu.bottom.getSelection();
//
//        int midsectionWidth = Math.max((bottomSelection != null ? bottomSelection.width() : 0), Math.max((this.menu.body.getSelection() != null ? this.menu.body.getSelection().width() : 0), (this.menu.cone.getSelection() != null ? this.menu.cone.getSelection().width() : 0)));
//        int leftEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - midsectionWidth / 2;
//        int rightSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X + midsectionWidth / 2;
//
//        if (bottomSelection != null) {
//            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - bottomSelection.width() / 2;
//            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalHeight;
//            centered(render, bottomSelection, this.menu.bottom.inventory, leftSide, bottomEdge);
//        }
//
//        if (this.menu.body.getSelection() != null) {
//            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - this.menu.body.getSelection().width() / 2;
//            int bottomEdge = (RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalHeight) - (bottomSelection != null ? bottomSelection.height() + RocketWorkbenchMenu.SPACING : 0);
//            centered(render, this.menu.body.getSelection(), this.menu.body.inventory, leftSide, bottomEdge);
//        }
//
//        if (this.menu.cone.getSelection() != null) {
//            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - this.menu.cone.getSelection().width() / 2;
//            int bottomEdge = (RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalHeight) - (bottomSelection != null ? bottomSelection.height() + RocketWorkbenchMenu.SPACING : 0) - (this.menu.body.getSelection() != null ? this.menu.body.getSelection().height() + RocketWorkbenchMenu.SPACING : 0);
//            centered(render, this.menu.cone.getSelection(), this.menu.cone.inventory, leftSide, bottomEdge);
//        }
//
//        if (this.menu.booster.getSelection() != null) {
//            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalHeight;
//            mirrored(render, this.menu.booster.getSelection(), this.menu.booster.inventory, leftEdge, rightSide, bottomEdge);
//        }
//
//        if (this.menu.fins.getSelection() != null) {
//            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalHeight - (this.menu.booster.getSelection() != null ? this.menu.booster.getSelection().height() + RocketWorkbenchMenu.SPACING : 0);
//            mirrored(render, this.menu.fins.getSelection(), this.menu.fins.inventory, leftEdge, rightSide, bottomEdge);
//        }
//
//        final int baseX = 11 - 2;
//        int y = 58 + this.menu.additionalHeight;
//        int x = baseX;
////        for (int k = 0; k < this.menu.upgradeCapacity; k++) { //fixme
////            batchUpgradeSlot(render, x, y);
////            if (x == baseX) {
////                x += 21;
////            } else {
////                x = baseX;
////                y -= 21;
////            }
////        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | clickSelection(mouseX, mouseY, button);
    }

    public boolean clickSelection(double mouseX, double mouseY, int button) {
        mouseX -= this.leftPos - SELECTION_SCREEN_WIDTH - SCREEN_SPACING;
        mouseY -= this.topPos;

        {
            int y = 4;
            for (Tab tab : Tab.values()) {
                if (tab != this.openTab) {
                    if (mouseIn(mouseX, mouseY, -TAB_WIDTH, y, TAB_WIDTH, TAB_HEIGHT)) {
                        this.setOpenTab(tab);
                        return true;
                    }
                }
                y += TAB_SPACING + TAB_HEIGHT;
            }
        }

        if (this.openTab != Tab.COLOR) {
            assert this.recipes != null;
            int i = this.page * 5 * 7;
            for (int y = 0; y < 7; y++) {
                for (int x = 0; x < 5; x++) {
                    if (mouseIn(mouseX, mouseY, 11 + x * RECIPE_WIDTH, 29 + y * RECIPE_HEIGHT, RECIPE_WIDTH, RECIPE_HEIGHT)) {
                        if (i < this.recipes.size()) {
                            this.getSelection().setSelection(this.recipes.get(i));
                            return true;
                        }
                    }
                    ++i;
                }
            }
        } else {

        }
        return false;
    }


    public void setOpenTab(Tab openTab) {
        this.openTab = openTab;
        this.page = 0;

        if (openTab != Tab.COLOR) {
            RocketWorkbenchMenu.RecipeCollection recipes = getRecipes();
            List<RocketPartRecipe<?, ?>> joined = new ArrayList<>(recipes.getCraftable().size() + recipes.getUncraftable().size());
            joined.addAll(recipes.getCraftable());
            joined.addAll(recipes.getUncraftable()); //fixme
            this.recipes = joined;
        } else {
            this.recipes = null;
        }
    }

    private void drawSelection(GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.leftPos - SELECTION_SCREEN_WIDTH - SCREEN_SPACING, this.topPos, 0);
        mouseX -= this.leftPos - SELECTION_SCREEN_WIDTH - SCREEN_SPACING;
        mouseY -= this.topPos;

        try (BatchedRenderer render = new BatchedRenderer(Tesselator.getInstance().getBuilder(), pose, Constant.ScreenTexture.ROCKET_SELECTION, 256, 256)) {
            render.blit(0, 0, 0, 0, SELECTION_SCREEN_WIDTH, SELECTION_SCREEN_HEIGHT);

            {
                int y = 4;
                for (Tab tab : Tab.values()) {
                    if (tab == this.openTab) {
                        render.blit(-(TAB_SELECTED_WIDTH - 3), y, TAB_SELECTED_U, TAB_SELECTED_V, TAB_SELECTED_WIDTH, TAB_HEIGHT);
                    } else {
                        render.blit(-TAB_WIDTH, y, TAB_U, TAB_V, TAB_WIDTH, TAB_HEIGHT);
                    }
                    y += TAB_SPACING + TAB_HEIGHT;
                }
            }

            if (this.openTab != Tab.COLOR) {
                assert this.recipes != null;
//                int u = this.showAll ? FILTER_ALL_U : FILTER_KNOWN_U;
//                int v = mouseIn(mouseX, mouseY, 110, 10, FILTER_WIDTH, FILTER_HEIGHT) ? FILTER_V_HOVER : FILTER_V;
//                render.blit(110, 10, u, v, FILTER_WIDTH, FILTER_HEIGHT);

                final int pages = Math.floorDiv(this.recipes.size(), 35) + this.recipes.size() % 35 == 0 ? 0 : 1;

                if (this.page >= pages) {
                    this.page = 0;
                }
                int i = this.page * 5 * 7;
                for (int y = 0; y < 7 && i < this.recipes.size(); y++) {
                    for (int x = 0; x < 5 && i < this.recipes.size(); x++) {
                        RocketPartRecipe<?, ?> recipe = this.recipes.get(i);
                        render.blit(11 + x * RECIPE_WIDTH, 29 + y * RECIPE_HEIGHT, isKnown(recipe) ? RECIPE_KNOWN_U : RECIPE_UNKNOWN_U, RECIPE_V, RECIPE_WIDTH, RECIPE_HEIGHT);
                        if (recipe != null && mouseIn(mouseX, mouseY, 11 + x * RECIPE_WIDTH, 29 + y * RECIPE_HEIGHT, RECIPE_WIDTH, RECIPE_HEIGHT)) {
                            setTooltipForNextRenderPass(Component.literal(recipe.output().key().location().toString())); //todo
                        }
                        i++;
                    }
                }
            } else {
                //todo color
            }
        }
        //todo: draw partS
        pose.popPose();
    }

    private boolean isKnown(RocketPartRecipe<?, ?> recipe) {
        return true;
    }

    private void batchUpgradeSlot(BatchedRenderer renderer, int x, int y) {
        renderer.blit(this.leftPos + x, this.topPos + y, BLUE_SLOT_U, BLUE_SLOT_V, COLOURED_SLOT_SIZE, COLOURED_SLOT_SIZE);
    }

    private void mirrored(BatchedRenderer renderer, RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftEdge, int rightSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (RocketPartRecipeSlot slot : slots) { //fixme add mirrored property to slot
            int x = slot.x() > 0 ? slot.x() + 1 : slot.x() - 1;
            if (x < 0) {
                batchNormalSlot(renderer, leftEdge - x - 18 - RocketWorkbenchMenu.SPACING - 1, bottomEdge - recipe.height() + slot.y() - 1);
            } else {
                batchNormalSlot(renderer, rightSide + x + RocketWorkbenchMenu.SPACING - 1, bottomEdge - recipe.height() + slot.y() - 1);
            }
        }
    }

    private void centered(BatchedRenderer renderer, RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (RocketPartRecipeSlot slot : slots) {
            batchNormalSlot(renderer, slot.x() + leftSide - 1, bottomEdge - recipe.height() + slot.y() - 1);
        }
    }

    private void batchNormalSlot(BatchedRenderer renderer, int x, int y) {
        renderer.blit(this.leftPos + x, this.topPos + y, NORMAL_SLOT_U, NORMAL_SLOT_V, NORMAL_SLOT_SIZE, NORMAL_SLOT_SIZE);
    }

    private static boolean mouseIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    private RocketWorkbenchBlockEntity.RecipeSelection getSelection() {
        return switch (openTab.type) {
            case CONE -> this.menu.cone;
            case BODY -> this.menu.body;
            case FIN -> this.menu.fins;
            case BOOSTER -> this.menu.booster;
            case BOTTOM -> this.menu.bottom;
            case UPGRADE -> this.menu.upgrade;
        };
    }

    private RocketWorkbenchMenu.RecipeCollection getRecipes() {
        return switch (openTab.type) {
            case CONE -> this.menu.coneRecipes;
            case BODY -> this.menu.bodyRecipes;
            case FIN -> this.menu.finsRecipes;
            case BOOSTER -> this.menu.boosterRecipes;
            case BOTTOM -> this.menu.bottomRecipes;
            case UPGRADE -> this.menu.upgradeRecipes;
        };
    }

    private enum Tab {
        CONE(RocketPartTypes.CONE),
        BODY(RocketPartTypes.BODY),
        FINS(RocketPartTypes.FIN),
        BOOSTER(RocketPartTypes.BOOSTER),
        BOTTOM(RocketPartTypes.BOTTOM),
        UPGRADE(RocketPartTypes.UPGRADE),
        COLOR(null);

        private final RocketPartTypes type;

        Tab(RocketPartTypes type) {
            this.type = type;
        }
    }
}
