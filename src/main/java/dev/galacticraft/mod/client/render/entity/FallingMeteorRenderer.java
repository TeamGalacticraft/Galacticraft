package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.entity.FallingMeteorEntity;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class FallingMeteorRenderer extends EntityRenderer<FallingMeteorEntity> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/block/fallen_meteor.png");
    private static final BlockState METEOR_BLOCK_STATE = GCBlocks.FALLEN_METEOR.defaultBlockState();

    private final BlockRenderDispatcher dispatcher;

    public FallingMeteorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 1.0f;
        this.dispatcher = ctx.getBlockRenderDispatcher();
    }

    @Override
    public void render(FallingMeteorEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        float scale = entity.getSize() * 0.5f;

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        matrices.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        matrices.scale(scale, scale, scale);
        matrices.translate(-0.5, -0.25, -0.5);

        this.dispatcher.renderBatched(
                METEOR_BLOCK_STATE,
                entity.blockPosition(),
                entity.level(),
                matrices,
                vertexConsumers.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(METEOR_BLOCK_STATE)),
                false,
                entity.getRandom()
        );

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FallingMeteorEntity entity) {
        return TEXTURE;
    }
}
