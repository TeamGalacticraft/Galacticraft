package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BubbleEntityRenderer extends EntityRenderer<BubbleEntity> {
    private static final Random random = new Random();
    private static BakedModel model = null;
    private static long seed = 0;

    public BubbleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public static RenderLayer getBubbleLayer() {
        return TexturedRenderLayers.getEntityTranslucentCull();
    }

    @Override
    public void render(BubbleEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        BlockEntity blockEntity = entity.world.getBlockEntity(entity.getBlockPos());
        if (!(blockEntity instanceof BubbleDistributorBlockEntity) || entity.removed) {
            entity.remove();
            return;
        }
        if (!((BubbleDistributorBlockEntity) blockEntity).bubbleVisible) {
            return;
        }
        double size = ((BubbleDistributorBlockEntity) blockEntity).getSize();
        if (model == null) {
            model = MinecraftClient.getInstance().getBlockRenderManager().getModel(GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState());
            seed = GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState().getRenderingSeed(blockEntity.getPos());
        }

        dispatcher.textureManager.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale((float) size, (float) size, (float) size);
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(blockEntity.getWorld(), model,
                GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState(), new BlockPos(0, 0, 0),
                matrices,
                vertexConsumers.getBuffer(getBubbleLayer()),
                false,
                random,
                seed,
                OverlayTexture.DEFAULT_UV
        );
        matrices.pop();
    }

    @Override
    public boolean shouldRender(BubbleEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Identifier getTexture(BubbleEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}
