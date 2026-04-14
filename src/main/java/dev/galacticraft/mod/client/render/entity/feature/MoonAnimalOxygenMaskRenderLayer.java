/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MoonAnimalOxygenMaskRenderLayer<T extends Animal, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id(Constant.GearTexture.OXYGEN_GEAR);

    public static final Geometry COW = new Geometry(1.08F, 1.08F, 1.08F, 0.0F, 3.65F, -2.2F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, Mth.HALF_PI);
    public static final Geometry SHEEP = new Geometry(1.02F, 1.02F, 1.08F, 0.0F, 2.55F, -1.55F, 0.9F, 0.9F, 0.9F, 0.0F, 0.0F, 0.0F, Mth.HALF_PI);
    public static final Geometry CHICKEN = new Geometry(0.62F, 0.62F, 0.62F, 0.0F, -0.8F, -1.25F, 0.7F, 0.7F, 0.7F, 0.0F, -0.6F, 1.7F, Mth.HALF_PI);

    private final Geometry geometry;
    private final @Nullable ModelPart head;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart pipe;

    public MoonAnimalOxygenMaskRenderLayer(RenderLayerParent<T, M> context, Geometry geometry) {
        super(context);
        this.geometry = geometry;

        if (!(context.getModel() instanceof AnimalModelAgeableListModel model)) {
            this.head = null;
            this.body = null;
            this.mask = null;
            this.pipe = null;
            return;
        }

        this.head = model.callGetHeadParts().iterator().next();
        this.body = model.callGetBodyParts().iterator().next();

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK,
                CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -9.0F, -5.0F, 10, 10, 10, new CubeDeformation(-0.1F)),
                PartPose.ZERO
        );
        partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE,
                CubeListBuilder.create().texOffs(40, 6)
                .addBox(-2.0F, -3.0F, 1.0F, 4, 6, 8, CubeDeformation.NONE),
                PartPose.ZERO
        );

        ModelPart modelRoot = partDefinition.bake(64, 32);
        this.mask = modelRoot.getChild(Constant.ModelPartName.OXYGEN_MASK);
        this.pipe = modelRoot.getChild(Constant.ModelPartName.OXYGEN_PIPE);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));

        if (this.mask != null && entity.galacticraft$hasMask()) {
            this.mask.copyFrom(this.head);
            this.mask.x += this.geometry.maskOffsetX();
            this.mask.y += this.geometry.maskOffsetY();
            this.mask.z += this.geometry.maskOffsetZ();
            this.mask.xScale = this.geometry.maskScaleX();
            this.mask.yScale = this.geometry.maskScaleY();
            this.mask.zScale = this.geometry.maskScaleZ();
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        if (this.pipe != null && entity.galacticraft$hasGear()) {
            this.pipe.copyFrom(this.body);
            this.pipe.x += this.geometry.pipeOffsetX();
            this.pipe.y += this.geometry.pipeOffsetY();
            this.pipe.z += this.geometry.pipeOffsetZ();
            this.pipe.xScale = this.geometry.pipeScaleX();
            this.pipe.yScale = this.geometry.pipeScaleY();
            this.pipe.zScale = this.geometry.pipeScaleZ();
            this.pipe.xRot = this.geometry.bodyXRot();
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public record Geometry(
            float maskScaleX,
            float maskScaleY,
            float maskScaleZ,
            float maskOffsetX,
            float maskOffsetY,
            float maskOffsetZ,
            float pipeScaleX,
            float pipeScaleY,
            float pipeScaleZ,
            float pipeOffsetX,
            float pipeOffsetY,
            float pipeOffsetZ,
            float bodyXRot
    ) {
    }
}