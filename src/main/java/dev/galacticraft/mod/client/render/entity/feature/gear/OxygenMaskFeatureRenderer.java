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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenMaskFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.OXYGEN_MASK);
    public final ModelTransformer<T> maskTransforms;
    public final DyeColor color;
    public final ModelPart oxygenMaskNormal;
    public final ModelPart oxygenMaskWhite;
    public final ModelPart oxygenMaskGrey;
    public final ModelPart oxygenMaskBlack;
    public final ModelPart oxygenMaskOrange;
    public final ModelPart oxygenMaskMagenta;
    public final ModelPart oxygenMaskLightBlue;
    public final ModelPart oxygenMaskYellow;
    public final ModelPart oxygenMaskLime;
    public final ModelPart oxygenMaskPink;
    public final ModelPart oxygenMaskLightGrey;
    public final ModelPart oxygenMaskCyan;
    public final ModelPart oxygenMaskPurple;
    public final ModelPart oxygenMaskBlue;
    public final ModelPart oxygenMaskBrown;
    public final ModelPart oxygenMaskGreen;
    public final ModelPart oxygenMaskRed;

    public OxygenMaskFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, DyeColor color) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.color = color;

        this.oxygenMaskNormal = OxygenMaskTextureOffset.createModelPart(null);
        this.oxygenMaskWhite = OxygenMaskTextureOffset.createModelPart(DyeColor.WHITE);
        this.oxygenMaskGrey = OxygenMaskTextureOffset.createModelPart(DyeColor.GRAY);
        this.oxygenMaskBlack = OxygenMaskTextureOffset.createModelPart(DyeColor.BLACK);
        this.oxygenMaskOrange = OxygenMaskTextureOffset.createModelPart(DyeColor.ORANGE);
        this.oxygenMaskMagenta = OxygenMaskTextureOffset.createModelPart(DyeColor.MAGENTA);
        this.oxygenMaskLightBlue = OxygenMaskTextureOffset.createModelPart(DyeColor.LIGHT_BLUE);
        this.oxygenMaskYellow = OxygenMaskTextureOffset.createModelPart(DyeColor.YELLOW);
        this.oxygenMaskLime = OxygenMaskTextureOffset.createModelPart(DyeColor.LIME);
        this.oxygenMaskPink = OxygenMaskTextureOffset.createModelPart(DyeColor.PINK);
        this.oxygenMaskLightGrey = OxygenMaskTextureOffset.createModelPart(DyeColor.LIGHT_GRAY);
        this.oxygenMaskCyan = OxygenMaskTextureOffset.createModelPart(DyeColor.CYAN);
        this.oxygenMaskPurple = OxygenMaskTextureOffset.createModelPart(DyeColor.PURPLE);
        this.oxygenMaskBlue = OxygenMaskTextureOffset.createModelPart(DyeColor.BLUE);
        this.oxygenMaskBrown = OxygenMaskTextureOffset.createModelPart(DyeColor.BROWN);
        this.oxygenMaskGreen = OxygenMaskTextureOffset.createModelPart(DyeColor.GREEN);
        this.oxygenMaskRed = OxygenMaskTextureOffset.createModelPart(DyeColor.RED);

        float pivotX = 0.0F, pivotY = 0.0F, pivotZ = 0.0F;
        float x = -5.0F, y = -9.0F, z = -5.0F;
        int sizeX = 10, sizeY = 10, sizeZ = 10;
        for (ModelPart part : new ModelPart[]{
                this.oxygenMaskNormal,
                this.oxygenMaskWhite,
                this.oxygenMaskGrey,
                this.oxygenMaskBlack,
                this.oxygenMaskOrange,
                this.oxygenMaskMagenta,
                this.oxygenMaskLightBlue,
                this.oxygenMaskYellow,
                this.oxygenMaskLime,
                this.oxygenMaskPink,
                this.oxygenMaskLightGrey,
                this.oxygenMaskCyan,
                this.oxygenMaskPurple,
                this.oxygenMaskBlue,
                this.oxygenMaskBrown,
                this.oxygenMaskGreen,
                this.oxygenMaskRed
        }) {
            part.setPivot(pivotX, pivotY, pivotZ);
            part.addCuboid(x, y, z, sizeX, sizeY, sizeZ, extra);
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
        switch (color) {
            case WHITE:
                this.oxygenMaskWhite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case GRAY:
                this.oxygenMaskGrey.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case BLACK:
                this.oxygenMaskBlack.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case ORANGE:
                this.oxygenMaskOrange.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case MAGENTA:
                this.oxygenMaskMagenta.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case LIGHT_BLUE:
                this.oxygenMaskLightBlue.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case YELLOW:
                this.oxygenMaskYellow.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case LIME:
                this.oxygenMaskLime.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case PINK:
                this.oxygenMaskPink.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case LIGHT_GRAY:
                this.oxygenMaskLightGrey.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case CYAN:
                this.oxygenMaskCyan.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case PURPLE:
                this.oxygenMaskPurple.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case BLUE:
                this.oxygenMaskBlue.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case BROWN:
                this.oxygenMaskBrown.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case GREEN:
                this.oxygenMaskGreen.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            case RED:
                this.oxygenMaskRed.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
            default:
                this.oxygenMaskNormal.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                break;
        }
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
