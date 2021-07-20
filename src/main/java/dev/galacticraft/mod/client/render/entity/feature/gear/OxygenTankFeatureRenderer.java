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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenTankFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T,M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, Constant.FeatureRendererTexture.OXYGEN_TANK);
    public final @Nullable ModelPart leftTankLight;
    public final @Nullable ModelPart leftTankMedium;
    public final @Nullable ModelPart leftTankHeavy;
    public final @Nullable ModelPart leftTankInfinite;
    public final @Nullable ModelPart rightTankLight;
    public final @Nullable ModelPart rightTankMedium;
    public final @Nullable ModelPart rightTankHeavy;
    public final @Nullable ModelPart rightTankInfinite;
    public final @Nullable ModelPart pipeLeft;
    public final @Nullable ModelPart pipeRight;
    public @Nullable OxygenTankTextureOffset textureTypeLeft;
    public @Nullable OxygenTankTextureOffset textureTypeRight;

    public OxygenTankFeatureRenderer(FeatureRendererContext<T,M> context, @NotNull OxygenTankTextureOffset textureTypeLeft, @NotNull OxygenTankTextureOffset textureTypeRight) {
        super(context);
        this.textureTypeLeft  = textureTypeLeft;
        this.textureTypeRight = textureTypeRight;

        ModelPart root, body;
        if (context.getModel() instanceof SinglePartEntityModel<?> model) {
            root = model.getPart();
            body = root.getChild(EntityModelPartNames.BODY);
        } else if (context.getModel() instanceof BipedEntityModel<?> model) {
            body = model.body;
        } else if (context.getModel() instanceof AnimalModel<?> model) {
            body = model.getBodyParts().iterator().next();
        } else {
            this.leftTankLight     = null;
            this.leftTankMedium    = null;
            this.leftTankHeavy     = null;
            this.leftTankInfinite  = null;
            this.rightTankLight    = null;
            this.rightTankMedium   = null;
            this.rightTankHeavy    = null;
            this.rightTankInfinite = null;
            this.pipeLeft  = null;
            this.pipeRight = null;
            return;
        }

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        if (body != null) {
            modelPartData.addChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_LIGHT,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.SMALL_TANK.X, OxygenTankTextureOffset.SMALL_TANK.Y)
                            .cuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_MEDIUM,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.MEDIUM_TANK.X, OxygenTankTextureOffset.MEDIUM_TANK.Y)
                            .cuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_HEAVY,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.HEAVY_TANK.X, OxygenTankTextureOffset.HEAVY_TANK.Y)
                            .cuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_INFINITE,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.INFINITE_TANK.X, OxygenTankTextureOffset.INFINITE_TANK.Y)
                            .cuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));

            modelPartData.addChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_LIGHT,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.SMALL_TANK.X, OxygenTankTextureOffset.SMALL_TANK.Y)
                            .cuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_MEDIUM,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.MEDIUM_TANK.X, OxygenTankTextureOffset.MEDIUM_TANK.Y)
                            .cuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_HEAVY,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.HEAVY_TANK.X, OxygenTankTextureOffset.HEAVY_TANK.Y)
                            .cuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_INFINITE,
                    ModelPartBuilder.create()
                            .uv(OxygenTankTextureOffset.INFINITE_TANK.X, OxygenTankTextureOffset.INFINITE_TANK.Y)
                            .cuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));

            modelPartData.addChild(Constant.ModelPartName.LEFT_OXYGEN_PIPE, ModelPartBuilder.create()
                            .uv(40, 17)
                            .cuboid(-2.0F, -3.0F, 0.0F, 1, 5, 8, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
            modelPartData.addChild(Constant.ModelPartName.RIGHT_OXYGEN_PIPE, ModelPartBuilder.create()
                            .uv(40, 17)
                            .cuboid(1.0F, -3.0F, 0.0F, 1, 5, 8, Dilation.NONE),
                    ModelTransform.pivot(body.pivotX, body.pivotY, body.pivotZ));
        }

        root = modelPartData.createPart(Constant.FeatureRendererTexture.OXYGEN_TANK_WIDTH, Constant.FeatureRendererTexture.OXYGEN_TANK_HEIGHT);

        if (body != null) {
            this.leftTankLight     = root.getChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_LIGHT);
            this.leftTankMedium    = root.getChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_MEDIUM);
            this.leftTankHeavy     = root.getChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_HEAVY);
            this.leftTankInfinite  = root.getChild(Constant.ModelPartName.LEFT_OXYGEN_TANK_INFINITE);
            this.rightTankLight    = root.getChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_LIGHT);
            this.rightTankMedium   = root.getChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_MEDIUM);
            this.rightTankHeavy    = root.getChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_HEAVY);
            this.rightTankInfinite = root.getChild(Constant.ModelPartName.RIGHT_OXYGEN_TANK_INFINITE);
            this.pipeLeft  = root.getChild(Constant.ModelPartName.LEFT_OXYGEN_PIPE);
            this.pipeRight = root.getChild(Constant.ModelPartName.RIGHT_OXYGEN_PIPE);
        } else {
            this.leftTankLight     = null;
            this.leftTankMedium    = null;
            this.leftTankHeavy     = null;
            this.leftTankInfinite  = null;
            this.rightTankLight    = null;
            this.rightTankMedium   = null;
            this.rightTankHeavy    = null;
            this.rightTankInfinite = null;
            this.pipeLeft  = null;
            this.pipeRight = null;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        renderTank(matrices, light, vertexConsumer);
    }

    public void renderWithGlint(MatrixStack matrices, VertexConsumer vertexConsumer, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        renderTank(matrices, light, vertexConsumer);
    }

    private void renderTank(MatrixStack matrices, int light, VertexConsumer vertexConsumer) {
        if (this.textureTypeLeft != null) {
            switch (this.textureTypeLeft) {
                case SMALL_TANK -> {
                    if (this.leftTankLight != null)
                        this.leftTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case MEDIUM_TANK -> {
                    if (this.leftTankMedium != null)
                        this.leftTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case HEAVY_TANK -> {
                    if (this.leftTankHeavy != null)
                        this.leftTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case INFINITE_TANK -> {
                    if (this.leftTankInfinite != null)
                        this.leftTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
            }
        }
        if (this.textureTypeRight != null) {
            switch (this.textureTypeRight) {
                case SMALL_TANK -> {
                    if (this.rightTankLight != null)
                        this.rightTankLight.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case MEDIUM_TANK -> {
                    if (this.rightTankMedium != null)
                        this.rightTankMedium.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case HEAVY_TANK -> {
                    if (this.rightTankHeavy != null)
                        this.rightTankHeavy.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
                case INFINITE_TANK -> {
                    if (this.rightTankInfinite != null)
                        this.rightTankInfinite.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                }
            }
        }
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
