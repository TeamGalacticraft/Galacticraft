/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
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
import net.minecraft.world.entity.monster.AbstractIllager;
import org.jetbrains.annotations.Nullable;

public class EvolvedIllagerMaskRenderLayer<T extends AbstractIllager, M extends IllagerModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/gear/illager_gear.png");
    private final @Nullable ModelPart head;
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart pipe;

    public EvolvedIllagerMaskRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        if (context.getModel() instanceof HierarchicalModel<?> model) {
            this.head = model.root().getChild(PartNames.HEAD);
            this.body = model.root().getChild(PartNames.BODY);
        } else {
            this.head = null;
            this.mask = null;
            this.body = null;
            this.pipe = null;
            return;
        }

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        if (this.head != null) {
            PartDefinition maskPartData = partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -11.01F, -5.0F, 10, 12, 10, CubeDeformation.NONE), PartPose.ZERO);
            maskPartData.addOrReplaceChild(Constant.ModelPartName.ILLAGER_NOSE_COMPARTMENT, CubeListBuilder.create().texOffs(10, 23).addBox(-2.0F, -4.01F, -7.0F, 4, 6, 3, CubeDeformation.NONE), PartPose.ZERO);
        }
        if (this.body != null) {
            partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(40, 6).addBox(-2.0F, -3.0F, 2.0F, 4, 6, 8, CubeDeformation.NONE), PartPose.ZERO);
        }

        ModelPart modelRoot = partDefinition.bake(64, 32);
        this.mask = this.head != null ? modelRoot.getChild(Constant.ModelPartName.OXYGEN_MASK) : null;
        this.pipe = this.body != null ? modelRoot.getChild(Constant.ModelPartName.OXYGEN_PIPE) : null;
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));

        if (this.mask != null && entity.galacticraft$hasMask()) {
            this.mask.copyFrom(this.head);
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.pipe != null && entity.galacticraft$hasGear()) {
            this.pipe.copyFrom(this.body);
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
