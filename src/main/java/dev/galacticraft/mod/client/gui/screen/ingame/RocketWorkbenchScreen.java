package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;

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

    private static final int OUTPUT_SLOT_U = 478;
    private static final int OUTPUT_SLOT_V = 40;
    private static final int OUTPUT_SLOT_SIZE = 34;

    private static final int NORMAL_SLOT_U = 27;
    private static final int NORMAL_SLOT_V = 121;
    private static final int NORMAL_SLOT_SIZE = 18;

    public RocketWorkbenchScreen(RocketWorkbenchMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        this.imageWidth = 223;
        this.imageHeight = 204 + 4 + this.menu.additionalDistance;
        super.init();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        beginBatch(buffer, Constant.ScreenTexture.ROCKET_WORKBENCH_SCREEN);

        batchBlit(buffer, poseStack, this.leftPos, this.topPos, 0, 204, 195, 4, 512, 256);

        if (this.menu.additionalDistance > 0) {
            batchBlit(buffer, poseStack, this.leftPos, this.topPos + 4, 223, 0, 195, this.menu.additionalDistance, 512, 256);
        }

        batchBlit(buffer, poseStack, this.leftPos, this.topPos + 4 + this.menu.additionalDistance, 0, 0, this.imageWidth, 204, 512, 256);

        int midsectionWidth = Math.max((this.menu.bottomRecipe != null ? this.menu.bottomRecipe.width() : 0), Math.max((this.menu.bodyRecipe != null ? this.menu.bodyRecipe.width() : 0), (this.menu.coneRecipe != null ? this.menu.coneRecipe.width() : 0)));
        int leftEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - midsectionWidth / 2;
        int rightSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X + midsectionWidth / 2;

        if (this.menu.bottomRecipe != null) {
            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - this.menu.bottomRecipe.width() / 2;
            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalDistance;
            centered(buffer, poseStack, this.menu.bottomRecipe, this.menu.bottom, leftSide, bottomEdge);
        }

        if (this.menu.bodyRecipe != null) {
            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - this.menu.bodyRecipe.width() / 2;
            int bottomEdge = (RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalDistance) - (this.menu.bottomRecipe != null ? this.menu.bottomRecipe.height() + RocketWorkbenchMenu.SPACING : 0);
            centered(buffer, poseStack, this.menu.bodyRecipe, this.menu.body, leftSide, bottomEdge);
        }

        if (this.menu.coneRecipe != null) {
            int leftSide = RocketWorkbenchMenu.SCREEN_CENTER_BASE_X - this.menu.coneRecipe.width() / 2;
            int bottomEdge = (RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalDistance) - (this.menu.bottomRecipe != null ? this.menu.bottomRecipe.height() + RocketWorkbenchMenu.SPACING : 0) - (this.menu.bodyRecipe != null ? this.menu.bodyRecipe.height() + RocketWorkbenchMenu.SPACING : 0);
            centered(buffer, poseStack, this.menu.coneRecipe, this.menu.cone, leftSide, bottomEdge);
        }

        if (this.menu.boosterRecipe != null) {
            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalDistance;
            mirrored(buffer, poseStack, this.menu.boosterRecipe, this.menu.booster, leftEdge, rightSide, bottomEdge);
        }

        if (this.menu.finsRecipe != null) {
            int bottomEdge = RocketWorkbenchMenu.SCREEN_CENTER_BASE_Y + this.menu.additionalDistance - (this.menu.boosterRecipe != null ? this.menu.boosterRecipe.height() + RocketWorkbenchMenu.SPACING : 0);
            mirrored(buffer, poseStack, this.menu.finsRecipe, this.menu.fins, leftEdge, rightSide, bottomEdge);
        }

        if (this.menu.upgradeCapacity > 0) {
            final int baseX = 11 - 2;
            int y = 58 + this.menu.additionalDistance;
            int x = baseX;
            for (int k = 0; k < this.menu.upgradeCapacity; k++) {
                batchUpgradeSlot(buffer, poseStack, x, y);
                if (x == baseX) {
                    x += 21;
                } else {
                    x = baseX;
                    y -= 21;
                }
            }
        }
        endBatch(buffer);
    }

    private void batchUpgradeSlot(BufferBuilder buffer, PoseStack poseStack, int x, int y) {
        batchBlit(buffer, poseStack, this.leftPos + x, this.topPos + y, BLUE_SLOT_U, BLUE_SLOT_V, COLOURED_SLOT_SIZE, COLOURED_SLOT_SIZE, 512, 256);
    }

    private void mirrored(BufferBuilder buffer, PoseStack poseStack, RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftEdge, int rightSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (RocketPartRecipeSlot slot : slots) { //fixme add mirrored property to slot
            int x = slot.x() > 0 ? slot.x() + 1 : slot.x() - 1;
            if (x < 0) {
                batchNormalSlot(buffer, poseStack, leftEdge - x - 18 - RocketWorkbenchMenu.SPACING - 1, bottomEdge - recipe.height() + slot.y() - 1);
            } else {
                batchNormalSlot(buffer, poseStack, rightSide + x + RocketWorkbenchMenu.SPACING - 1, bottomEdge - recipe.height() + slot.y() - 1);
            }
        }
    }

    private void centered(BufferBuilder buffer, PoseStack poseStack, RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (RocketPartRecipeSlot slot : slots) {
            batchNormalSlot(buffer, poseStack, slot.x() + leftSide - 1, bottomEdge - recipe.height() + slot.y() - 1);
        }
    }

    private void batchNormalSlot(BufferBuilder buffer, PoseStack poseStack, int x, int y) {
        batchBlit(buffer, poseStack, this.leftPos + x, this.topPos + y, NORMAL_SLOT_U, NORMAL_SLOT_V, NORMAL_SLOT_SIZE, NORMAL_SLOT_SIZE, 512, 256);
    }

    private static void beginBatch(BufferBuilder buffer, ResourceLocation texture) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    }

    private static void endBatch(BufferBuilder buffer) {
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void batchBlit(BufferBuilder buffer, PoseStack poseStack, int i, int j, int k, int l, float f, float g, int m, int n, int o, int p) {
        batchBlit(buffer, poseStack, i, i + k, j, j + l, m, n, f, g, o, p);
    }

    private static void batchBlit(BufferBuilder buffer, PoseStack poseStack, int i, int j, float f, float g, int k, int l, int m, int n) {
        batchBlit(buffer, poseStack, i, j, k, l, f, g, k, l, m, n);
    }

    private static void batchBlit(BufferBuilder buffer, PoseStack poseStack, int i, int j, int k, int l, int n, int o, float f, float g, int p, int q) {
        innerBatchBlit(buffer, poseStack.last().pose(), i, j, k, l, (f + 0.0F) / (float)p, (f + (float)n) / (float)p, (g + 0.0F) / (float)q, (g + (float)o) / (float)q);
    }

    private static void innerBatchBlit(BufferBuilder buffer, Matrix4f matrix, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2) {
        buffer.vertex(matrix, (float)x1, (float)y1, 0).uv(u1, v1).endVertex();
        buffer.vertex(matrix, (float)x1, (float)y2, 0).uv(u1, v2).endVertex();
        buffer.vertex(matrix, (float)x2, (float)y2, 0).uv(u2, v2).endVertex();
        buffer.vertex(matrix, (float)x2, (float)y1, 0).uv(u2, v1).endVertex();
    }
}
