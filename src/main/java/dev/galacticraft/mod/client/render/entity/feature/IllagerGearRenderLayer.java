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
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
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
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class IllagerGearRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/illager_gear.png");
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart nose;
    private final @Nullable ModelPart pipe;
    private final @Nullable ModelPart tank;

    public IllagerGearRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        ModelPart root, head, body;
        if (context.getModel() instanceof HierarchicalModel<?> model) {
            root = model.root();
            head = root.getChild(PartNames.HEAD);
            body = root.getChild(PartNames.BODY);
        } else if (context.getModel() instanceof HumanoidModel<?> model){
            head = model.head;
            body = model.body;
        } else if (context.getModel() instanceof AnimalModelAgeableListModel model){
            head = model.callGetHeadParts().iterator().next();
            body = model.callGetBodyParts().iterator().next();
        } else {
            this.mask = null;
            this.nose = null;
            this.pipe = null;
            this.tank = null;
            return;
        }
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (head != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -11.01F, -5.0F, 10, 12, 10, CubeDeformation.NONE), PartPose.offset(head.x, head.y, head.z));
            modelPartData.addOrReplaceChild(Constant.ModelPartName.ILLAGER_NOSE_COMPARTMENT, CubeListBuilder.create().texOffs(10, 23).addBox(-2.0F, -4.01F, -7.0F, 4, 6, 3, CubeDeformation.NONE), PartPose.offset(head.x, head.y, head.z));
        }

        if (body != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(40, 7).addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_TANK, CubeListBuilder.create().texOffs(40, 20).addBox(-4.0F, 1.0F, 3.01F, 8, 8, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
        }

        root = modelPartData.bake(64, 32);

        if (head != null) {
            this.mask = root.getChild(Constant.ModelPartName.OXYGEN_MASK);
            this.nose = root.getChild(Constant.ModelPartName.ILLAGER_NOSE_COMPARTMENT);
        } else {
            this.mask = null;
            this.nose = null;
        }

        if (body != null) {
            this.pipe = root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
            this.tank = root.getChild(Constant.ModelPartName.OXYGEN_TANK);
        } else {
            this.pipe = null;
            this.tank = null;
        }
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));

        if (this.mask != null) {
            this.mask.yRot = headYaw * (float) (Math.PI / 180.0);
            this.mask.xRot = headPitch * (float) (Math.PI / 180.0);
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            this.nose.yRot = this.mask.yRot;
            this.nose.xRot = this.mask.xRot;
            this.nose.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.pipe != null) {
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.tank != null) {
            this.tank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
