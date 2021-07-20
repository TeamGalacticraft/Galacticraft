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

package dev.galacticraft.mod.client.render.entity.feature.gear;

import dev.galacticraft.mod.Constant;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class SensorGlassesFeatureRenderer<T extends Entity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.GEAR);
    public final @Nullable ModelPart sensorGlasses;

    public SensorGlassesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);

        ModelPart root, head;
        if (context.getModel() instanceof SinglePartEntityModel<?> model) {
            root = model.getPart();
            head = root.getChild(EntityModelPartNames.HEAD);
        } else if (context.getModel() instanceof BipedEntityModel<?> model){
            head = model.head;
        } else if (context.getModel() instanceof AnimalModel<?> model){
            head = model.getHeadParts().iterator().next();
        } else {
            this.sensorGlasses = null;
            return;
        }
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        if (head != null) {
            modelPartData.addChild(Constant.ModelPartName.SENSOR_GLASSES,
                    ModelPartBuilder.create()
                            .uv(64, 10)
                            .cuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, Dilation.NONE),
                    ModelTransform.pivot(head.pivotX, head.pivotY, head.pivotZ));
        }

        root = modelPartData.createPart(Constant.FeatureRendererTexture.GEAR_WIDTH, Constant.FeatureRendererTexture.GEAR_HEIGHT);

        if (head != null) {
            this.sensorGlasses = root.getChild(Constant.ModelPartName.SENSOR_GLASSES);
        } else {
            this.sensorGlasses = null;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (this.sensorGlasses != null) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(entity), true));
            this.sensorGlasses.yaw = (float) Math.toRadians(headYaw);
            this.sensorGlasses.pitch = (float) Math.toRadians(headPitch);
            this.sensorGlasses.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        }
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
