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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.util.Graphics;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RocketWorkbenchScreen extends AbstractContainerScreen<RocketWorkbenchMenu> implements VariableSizedContainer.Listener {
    private static final int NORMAL_SLOT_U = 7;
    private static final int NORMAL_SLOT_V = 224;
    private static final int NORMAL_SLOT_SIZE = 18;

    private static final int UI_WIDTH = 248;
    private static final int MAIN_UI_WIDTH = 176;
    private static final int UI_HEIGHT = 249;

    private static final int ROCKET_PREVIEW_X = 133;
    private static final int ROCKET_PREVIEW_Y = 100;

    private final RocketEntity entity;

    public RocketWorkbenchScreen(RocketWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.entity = new RocketEntity(GCEntityTypes.ROCKET, menu.workbench.getLevel());
        this.inventoryLabelX = this.inventoryLabelY = Integer.MAX_VALUE;
        this.entity.setData(menu.previewRocket());
        this.entity.setYRot(60);

        menu.workbench.ingredients.addListener(this);
    }

    @Override
    public void onSizeChanged() {
        this.onItemChanged();
    }

    @Override
    public void onItemChanged() {
        this.entity.setData(this.menu.previewRocket());
    }

    @Override
    protected void init() {
        this.imageWidth = MAIN_UI_WIDTH;
        this.imageHeight = UI_HEIGHT;
        super.init();
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.workbench.ingredients.removeListener(this);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        this.inventoryLabelY = this.imageHeight - 96;

        try (Graphics graphics = Graphics.managed(guiGraphics, this.font)) {
            try (Graphics.Texture texture = graphics.texture(Constant.ScreenTexture.ROCKET_WORKBENCH_SCREEN, 256, 256)) {
                texture.blit(this.leftPos, this.topPos, 0, 0, UI_WIDTH, UI_HEIGHT);

                for (Slot slot : this.menu.slots) {
                    if (slot.container instanceof VariableSizedContainer) {
                        texture.blit(this.leftPos + slot.x - 1, this.topPos + slot.y - 1, NORMAL_SLOT_U, NORMAL_SLOT_V, NORMAL_SLOT_SIZE, NORMAL_SLOT_SIZE);
                    }
                }
            }
        }

        renderEntityInInventory(guiGraphics, this.leftPos + ROCKET_PREVIEW_X, this.topPos + ROCKET_PREVIEW_Y, 15, SmithingScreen.ARMOR_STAND_ANGLE, null, this.entity);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.entity.setYRot(this.entity.getYRot() + delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + UI_WIDTH) || mouseY >= (double)(top + UI_HEIGHT);
    }

    public static void renderEntityInInventory(
            GuiGraphics guiGraphics, double x, double y, int scale, Quaternionf pose, @Nullable Quaternionf cameraOrientation, RocketEntity entity
    ) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 50.0);
        guiGraphics.pose().mulPose(new Matrix4f().scaling((float)scale, (float)scale, (float)(-scale)));
        guiGraphics.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            cameraOrientation.conjugate();
            entityRenderDispatcher.overrideCameraOrientation(cameraOrientation);
        }

        entityRenderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT));
        guiGraphics.flush();
        entityRenderDispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }
}
