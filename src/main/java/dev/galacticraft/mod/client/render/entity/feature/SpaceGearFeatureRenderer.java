/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.client.render.entity.feature;

import dev.galacticraft.mod.Constant;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SpaceGearFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/entity/oxygen_gear.png");
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart tank;
    private final @Nullable ModelPart pipe;

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        ModelPart root, head, body;
        if (context.getModel() instanceof SinglePartEntityModel<?> model) {
            root = model.getPart();
            head = root.getChild(EntityModelPartNames.HEAD);
            body = root.getChild(EntityModelPartNames.BODY);
        } else if (context.getModel() instanceof BipedEntityModel<?> model){
            head = model.head;
            body = model.body;
        } else if (context.getModel() instanceof AnimalModel<?> model){
            head = model.getHeadParts().iterator().next();
            body = model.getBodyParts().iterator().next();;
        } else {
            this.mask = null;
            this.tank = null;
            this.pipe = null;
            return;
        }
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        if (head != null) {
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_MASK, ModelPartBuilder.create().uv(0, 10).cuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, Dilation.NONE), ModelTransform.pivot(head.pivotX, head.pivotY, head.pivotZ));
        }

        if (body != null) {
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_TANK, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 1.0F, 2.0F, 8, 6, 4, Dilation.NONE), ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_PIPE, ModelPartBuilder.create().uv(40, 17).cuboid(-2.0F, -3.0F, 0.0F, 4, 5, 8, Dilation.NONE), ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
        }

        root = modelPartData.createPart(64, 32);

        if (head != null) {
            this.mask = root.getChild(Constant.ModelPartName.OXYGEN_MASK);
        } else {
            this.mask = null;
        }

        if (body != null) {
            this.tank = root.getChild(Constant.ModelPartName.OXYGEN_TANK);
            this.pipe = root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
        } else {
            this.tank = null;
            this.pipe = null;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(entity), true));
        if (mask != null) {
            this.mask.yaw = headYaw;
            this.mask.pitch = headPitch;
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        }
        if (this.tank != null) {
            assert this.pipe != null;
            this.tank.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        }
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
