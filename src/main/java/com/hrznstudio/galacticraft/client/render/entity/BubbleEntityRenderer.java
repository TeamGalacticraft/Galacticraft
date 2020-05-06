package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.machines.bubbledistributor.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.PlayerContainer;
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
        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(PlayerContainer.BLOCK_ATLAS_TEXTURE, false, false))
                .transparency(new RenderPhase.Transparency("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }, RenderSystem::disableBlend))
                .diffuseLighting(new RenderPhase.DiffuseLighting(true))
                .alpha(new RenderPhase.Alpha(0.003921569F))
                .lightmap(new RenderPhase.Lightmap(true))
                .cull(new RenderPhase.Cull(false))
                .depthTest(new RenderPhase.DepthTest(519)) //THIS MAKES THE TEXTURE NOT GLITCHY (bad shading or smth). HOWEVER it causes really weird effects when viewed from far away (https://imgur.com/a/eUqc8Pt). SO THIS IS A TENPORARY FIX - I've spent to much time tweaking this dumb render layer. If you can - try and fix this :) -marcus8448
                .shadeModel(new RenderPhase.ShadeModel(true))
                .overlay(new RenderPhase.Overlay(true))
                .build(true);
        return RenderLayer.of("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, multiPhaseParameters);
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

        renderManager.textureManager.bindTexture(PlayerContainer.BLOCK_ATLAS_TEXTURE);
        RenderSystem.pushMatrix();
        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale((float) size, (float) size, (float) size);
        RenderSystem.color4f(0.08235294f, 0.32941177f, 0.64705884f, 0.1F);
        RenderSystem.disableCull();
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(blockEntity.getWorld(), model,
                GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState(), new BlockPos(0, 0, 0),
                matrices,
                vertexConsumers.getBuffer(getBubbleLayer()),
                false,
                random,
                seed,
                OverlayTexture.DEFAULT_UV);
        matrices.pop();
        RenderSystem.popMatrix();
    }

    @Override
    public boolean shouldRender(BubbleEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Identifier getTexture(BubbleEntity entity) {
        return PlayerContainer.BLOCK_ATLAS_TEXTURE;
    }
}
