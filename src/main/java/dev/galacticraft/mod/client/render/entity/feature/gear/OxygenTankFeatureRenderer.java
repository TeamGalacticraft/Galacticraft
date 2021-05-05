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
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public abstract class OxygenTankFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.OXYGEN_TANK);
    public final ModelTransformer<T> tankTransforms;
    public final ModelPart oxygenTankLight;
    public final ModelPart oxygenTankMedium;
    public final ModelPart oxygenTankHeavy;
    public final ModelPart oxygenTankInfinite;
    public OxygenTankTextureOffset textureType;

    public OxygenTankFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> tankTransforms, @NotNull OxygenTankTextureOffset textureType) {
        super(context);
        this.tankTransforms = tankTransforms;
        this.textureType = textureType;
        this.oxygenTankLight = new ModelPart(Constant.FeatureRendererTexture.OXYGEN_TANK_WIDTH, Constant.FeatureRendererTexture.OXYGEN_TANK_HEIGHT, OxygenTankTextureOffset.SMALL_TANK.X, OxygenTankTextureOffset.SMALL_TANK.Y);
        this.oxygenTankMedium = new ModelPart(Constant.FeatureRendererTexture.OXYGEN_TANK_WIDTH, Constant.FeatureRendererTexture.OXYGEN_TANK_HEIGHT, OxygenTankTextureOffset.MEDIUM_TANK.X, OxygenTankTextureOffset.MEDIUM_TANK.Y);
        this.oxygenTankHeavy = new ModelPart(Constant.FeatureRendererTexture.OXYGEN_TANK_WIDTH, Constant.FeatureRendererTexture.OXYGEN_TANK_HEIGHT, OxygenTankTextureOffset.HEAVY_TANK.X, OxygenTankTextureOffset.HEAVY_TANK.Y);
        this.oxygenTankInfinite = new ModelPart(Constant.FeatureRendererTexture.OXYGEN_TANK_WIDTH, Constant.FeatureRendererTexture.OXYGEN_TANK_HEIGHT, OxygenTankTextureOffset.INFINITE_TANK.X, OxygenTankTextureOffset.INFINITE_TANK.Y);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        this.tankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        switch (this.textureType) {
            case SMALL_TANK:
                this.oxygenTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case MEDIUM_TANK:
                this.oxygenTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case HEAVY_TANK:
                this.oxygenTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case INFINITE_TANK:
                this.oxygenTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
        }
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();
        this.tankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        switch (this.textureType) {
            case SMALL_TANK:
                this.oxygenTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case MEDIUM_TANK:
                this.oxygenTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case HEAVY_TANK:
                this.oxygenTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case INFINITE_TANK:
                this.oxygenTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
        }
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}