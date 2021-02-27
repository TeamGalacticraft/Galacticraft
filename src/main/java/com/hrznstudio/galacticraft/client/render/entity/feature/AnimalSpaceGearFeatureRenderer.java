/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.AnimalGearAccessor;
import com.hrznstudio.galacticraft.accessor.AnimalModelGearAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimalSpaceGearFeatureRenderer<T extends AnimalEntity, M extends AnimalModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/oxygen_gear_animal.png");
    private final M model;
    private final List<ModelPart> maskModels = new ArrayList<>();

    public AnimalSpaceGearFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        this.model = context.getModel();
        for (ModelPart head : ((AnimalModelGearAccessor)model).getAnimalHeadParts()) {
            ModelPart modelPart = new ModelPart(64, 32, 0, 10);
            this.model.accept(modelPart);
            modelPart.setPivot(head.pivotX, head.pivotY, head.pivotZ);
            modelPart.mirror = head.mirror;
            for (ModelPart.Cuboid cuboid : head.cuboids) {
                modelPart.cuboids.add(new ModelPart.Cuboid(0, 10, cuboid.minX, cuboid.minY, cuboid.minZ, cuboid.maxX - cuboid.minX, cuboid.maxY - cuboid.minY, cuboid.maxZ - cuboid.minZ, 1.0F, 1.0F, 1.0F, head.mirror, 64, 32));
                break;
            }
            maskModels.add(modelPart);
            break;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (((AnimalGearAccessor) entity).hasOxygenMask()) {
            VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE, true));
            Iterator<ModelPart> headParts = ((AnimalModelGearAccessor) model).getAnimalHeadParts().iterator();
            for (ModelPart modelPart : maskModels) {
                ModelPart head = headParts.next();
                modelPart.pitch = head.pitch;
                modelPart.yaw = head.yaw;
                modelPart.roll = head.roll;
                modelPart.visible = head.visible;
            }

            if (!model.child) {
                for (ModelPart oxygenMaskModel : maskModels) {
                    oxygenMaskModel.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
                }
            } else {
                matrices.push();
                float g;
                if (((AnimalModelGearAccessor) model).isHeadScaled()) {
                    g = 1.5F / ((AnimalModelGearAccessor) model).getInvertedChildHeadScale();
                    matrices.scale(g, g, g);
                }

                matrices.translate(0.0D, (((AnimalModelGearAccessor) model).getChildHeadYOffset() / 16.0F), (((AnimalModelGearAccessor) model).getChildHeadZOffset() / 16.0F));
                for (ModelPart oxygenMaskModel : this.maskModels) {
                    oxygenMaskModel.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
            }
        }
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
