package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.machines.bubbledistributor.BubbleDistributorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class BubbleDistributorBlockEntityRenderer extends BlockEntityRenderer<BubbleDistributorBlockEntity> {
    public BubbleDistributorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BubbleDistributorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        BlockRenderLayerMap.INSTANCE.putBlock(GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK, RenderLayer.getTranslucent());//fixme
        RenderSystem.pushMatrix();
        matrices.push();
        RenderSystem.color4f(0.08235294f, 0.32941177f, 0.64705884f, 0.25F);
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale(blockEntity.getSize() / 110F, blockEntity.getSize() / 110F, blockEntity.getSize() / 110F);
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(blockEntity.getWorld(), MinecraftClient.getInstance().getBlockRenderManager().getModel(GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState()), GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState(), new BlockPos(0, 0, 0), matrices, consumers.getBuffer(RenderLayers.getBlockLayer(GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState())), false, new Random(), GalacticraftBlocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK.getDefaultState().getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
        matrices.pop();
        RenderSystem.popMatrix();
    }

    @Override
    public boolean rendersOutsideBoundingBox(BubbleDistributorBlockEntity blockEntity) {
        return true;
    }
}
