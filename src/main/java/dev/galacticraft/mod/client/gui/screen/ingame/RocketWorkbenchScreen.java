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
import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.BatchedRenderer;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
        super.init();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        this.inventoryLabelY = this.imageHeight - 96 + this.menu.additionalHeight;

        drawSelection(graphics, mouseX, mouseY, delta);

        try (BatchedRenderer render = new BatchedRenderer(Tesselator.getInstance().getBuilder(), graphics.pose(), Constant.ScreenTexture.ROCKET_WORKBENCH_SCREEN, 512, 256)) {
            render.blit(this.leftPos, this.topPos + this.menu.additionalHeight + CAP_HEIGHT, 0, 0, BASE_UI_WIDTH, BASE_UI_HEIGHT);

            if (this.menu.additionalHeight > 0) {
                render.blit(this.leftPos, this.topPos + CAP_HEIGHT, HEIGHT_EXT_U, 0, MAIN_UI_WIDTH, this.menu.additionalHeight);
            }

            render.blit(this.leftPos, this.topPos, 0, BASE_UI_HEIGHT, BASE_UI_WIDTH, CAP_HEIGHT);

            for (Slot slot : this.menu.slots) {
                if (slot.container instanceof VariableSizedContainer) {
                    render.blit(this.leftPos + slot.x - 1, this.topPos + slot.y - 1, NORMAL_SLOT_U, NORMAL_SLOT_V, NORMAL_SLOT_SIZE, NORMAL_SLOT_SIZE);
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
                            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                                this.getSelection().setSelection(this.recipes.get(i));
                                return true;
                            } else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                                //todo
                                return true;
                            }
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
            List<RocketPartRecipe<?, ?>> joined = new ArrayList<>(recipes.getCraftable().size() + recipes.getUncraftable().size() + 1);
            joined.add(null);
            joined.addAll(recipes.getCraftable());
            joined.addAll(recipes.getUncraftable()); //fixme
            this.recipes = joined;
        } else {
            this.recipes = null;
        }
    }

    private void drawSelection(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(this.leftPos - SELECTION_SCREEN_WIDTH - SCREEN_SPACING, this.topPos, 0);
        mouseX -= this.leftPos - SELECTION_SCREEN_WIDTH - SCREEN_SPACING;
        mouseY -= this.topPos;

        int size = this.recipes != null ? this.recipes.size() : 0;
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
                final int pages = Math.floorDiv(size, 35) + size % 35 == 0 ? 0 : 1;

                if (this.page >= pages) {
                    this.page = 0;
                }
                int i = this.page * 5 * 7;
                for (int y = 0; y < 7 && i < size; y++) {
                    for (int x = 0; x < 5 && i < size; x++) {
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

        {
            int y = 8;
            for (Tab tab : Tab.values()) {
                if (tab == Tab.COLOR) {
                    graphics.renderFakeItem(new ItemStack(Items.RED_DYE), -TAB_WIDTH + 8, y);
                } else {
                    RocketPartRendererRegistry.INSTANCE.getRenderer(tab.part).renderGUI(graphics, -TAB_WIDTH + 8, y, mouseX, mouseY, delta);
                }
                y += TAB_SPACING + TAB_HEIGHT;
            }
        }
        if (this.openTab != Tab.COLOR) {
            assert this.recipes != null;

            int i = this.page * 5 * 7;
            for (int y = 0; y < 7 && i < size; y++) {
                for (int x = 0; x < 5 && i < size; x++) {
                    RocketPartRecipe<?, ?> recipe = this.recipes.get(i);
                    if (recipe != null) {
                        RocketPartRenderer renderer = RocketPartRendererRegistry.INSTANCE.getRenderer(recipe.output().key());
                        renderer.renderGUI(graphics, 11 + x * RECIPE_WIDTH + 4, 29 + y * RECIPE_HEIGHT + 4, mouseX, mouseY, delta);
                    }
                    i++;
                }
            }
        } else {
            
        }
        graphics.drawString(this.font, this.openTab.name, 12, 14, 0xEEEEEE, true);
        pose.popPose();
    }

    private boolean isKnown(RocketPartRecipe<?, ?> recipe) {
        return true;
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
        CONE(RocketPartTypes.CONE, GCRocketParts.TIER_1_CONE, Component.translatable("ui.galacticraft.cone")),
        BODY(RocketPartTypes.BODY, GCRocketParts.TIER_1_BODY, Component.translatable("ui.galacticraft.body")),
        FINS(RocketPartTypes.FIN, GCRocketParts.TIER_1_FIN, Component.translatable("ui.galacticraft.fins")),
        BOOSTER(RocketPartTypes.BOOSTER, GCRocketParts.TIER_1_BOOSTER, Component.translatable("ui.galacticraft.booster")),
        BOTTOM(RocketPartTypes.BOTTOM, GCRocketParts.TIER_1_BOTTOM, Component.translatable("ui.galacticraft.engine")),
        UPGRADE(RocketPartTypes.UPGRADE, GCRocketParts.STORAGE_UPGRADE, Component.translatable("ui.galacticraft.upgrade")),
        COLOR(null, null, Component.translatable("ui.galacticraft.color"));

        private final RocketPartTypes type;
        private final ResourceKey<? extends RocketPart<?, ?>> part;
        private final Component name;

        Tab(RocketPartTypes type, ResourceKey<? extends RocketPart<?, ?>> part, Component name) {
            this.type = type;
            this.part = part;
            this.name = name;
        }
    }
}
