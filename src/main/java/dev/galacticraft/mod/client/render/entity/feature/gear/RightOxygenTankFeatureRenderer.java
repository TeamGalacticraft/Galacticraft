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
import dev.galacticraft.mod.client.render.entity.feature.ModelTransformer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class RightOxygenTankFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.GEAR);
    final ModelTransformer<T> rightTankTransforms;
    final ModelPart rightOxygenTankLight;
    final ModelPart rightOxygenTankMedium;
    final ModelPart rightOxygenTankHeavy;
    final ModelPart rightOxygenTankInfinite;
    public OxygenTankTextureOffset textureType;

    public RightOxygenTankFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> rightTankTransforms, OxygenTankTextureOffset textureType) {
        super(context);
        this.rightTankTransforms = rightTankTransforms;

        this.rightOxygenTankLight = new ModelPart(Constant.FeatureRendererTexture.GEAR_WIDTH, Constant.FeatureRendererTexture.GEAR_HEIGHT, 0, 0);
        this.rightOxygenTankLight.setPivot(0.0F, 2.0F, 0.0F);
        this.rightOxygenTankLight.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extra);
        this.rightOxygenTankMedium = new ModelPart(Constant.FeatureRendererTexture.GEAR_WIDTH, Constant.FeatureRendererTexture.GEAR_HEIGHT, 0, 0);
        this.rightOxygenTankMedium.setPivot(0.0F, 2.0F, 0.0F);
        this.rightOxygenTankMedium.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extra);
        this.rightOxygenTankHeavy = new ModelPart(Constant.FeatureRendererTexture.GEAR_WIDTH, Constant.FeatureRendererTexture.GEAR_HEIGHT, 0, 0);
        this.rightOxygenTankHeavy.setPivot(0.0F, 2.0F, 0.0F);
        this.rightOxygenTankHeavy.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extra);
        this.rightOxygenTankInfinite = new ModelPart(Constant.FeatureRendererTexture.GEAR_WIDTH, Constant.FeatureRendererTexture.GEAR_HEIGHT, 0, 0);
        this.rightOxygenTankInfinite.setPivot(0.0F, 2.0F, 0.0F);
        this.rightOxygenTankInfinite.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extra);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        this.rightTankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        switch (this.textureType) {
            case SMALL_TANK:
                this.rightOxygenTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case MEDIUM_TANK:
                this.rightOxygenTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case HEAVY_TANK:
                this.rightOxygenTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case INFINITE_TANK:
                this.rightOxygenTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
        }
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();
        this.rightTankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        switch (this.textureType) {
            case SMALL_TANK:
                this.rightOxygenTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case MEDIUM_TANK:
                this.rightOxygenTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case HEAVY_TANK:
                this.rightOxygenTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case INFINITE_TANK:
                this.rightOxygenTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
        }
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
