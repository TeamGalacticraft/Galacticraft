package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.client.render.entity.feature.SpaceGearFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;

public class EvolvedSkeletonEntityRenderer extends SkeletonEntityRenderer {
    public EvolvedSkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.addFeature(new SpaceGearFeatureRenderer<>(this, 0.0F,
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                    stack.translate(0.0D, -0.4D, 0.0D);
                    stack.scale(0.9f, 0.9f, 0.9f);
                },
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}, 0.0F, 6.0F, 0.0F)
        );
    }
}
