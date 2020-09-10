package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.client.render.entity.feature.SpaceGearFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PillagerEntityRenderer;

public class EvolvedPillagerEntityRenderer extends PillagerEntityRenderer {
    public EvolvedPillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.addFeature(new SpaceGearFeatureRenderer<>(this, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                    stack.translate(0.0F, -0.1F, 0.0F);
//                    stack.scale(0.0F, 1.1F, 0.0F);
                },
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}
                )
        );
    }

}
