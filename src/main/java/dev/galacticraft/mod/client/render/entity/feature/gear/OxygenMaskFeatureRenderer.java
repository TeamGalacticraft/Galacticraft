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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.HashMap;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenMaskFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.OXYGEN_MASK);
    public final ModelTransformer<T> maskTransforms;
    public final DyeColor color;

    private final HashMap<DyeColor,ModelPart> oxygenMaskColors;
    public final ModelPart oxygenMaskNormal;

    public OxygenMaskFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, DyeColor color) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.color = color;

        float pivotX = 0.0F, pivotY = 0.0F, pivotZ = 0.0F;
        float x = -5.0F, y = -10.0F, z = -5.0F;
        int sizeX = 10, sizeY = 10, sizeZ = 10;

        this.oxygenMaskNormal = createModelPart(null);
        this.oxygenMaskNormal.setPivot(pivotX, pivotY, pivotZ);
        this.oxygenMaskNormal.addCuboid(x, y, z, sizeX, sizeY, sizeZ, extra);

        this.oxygenMaskColors = new HashMap<>();
        for (DyeColor dye : DyeColor.values()) {
            this.oxygenMaskColors.put(dye, createModelPart(dye));
            this.oxygenMaskColors.get(dye).setPivot(pivotX, pivotY, pivotZ);
            this.oxygenMaskColors.get(dye).addCuboid(x, y, z, sizeX, sizeY, sizeZ, extra);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        this.maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        matrices.multiply(new Quaternion(Vector3f.POSITIVE_Y, headYaw, true));
        matrices.multiply(new Quaternion(Vector3f.POSITIVE_X, headPitch, true));
        this.renderColor(matrices, vertexConsumer, light, this.color);
        matrices.pop();
    }

    protected void renderColor(MatrixStack matrices, VertexConsumer vertexConsumer, int light, DyeColor color) {
        if (color == null) {
            this.oxygenMaskNormal.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            return;
        }
        this.oxygenMaskColors.get(color).render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }

    private static ModelPart createModelPart(DyeColor color) {
        return new ModelPart(
                Constant.FeatureRendererTexture.OXYGEN_MASK_WIDTH,
                Constant.FeatureRendererTexture.OXYGEN_MASK_HEIGHT,
                OxygenMaskTextureOffset.getX(color),
                OxygenMaskTextureOffset.getY(color));
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
