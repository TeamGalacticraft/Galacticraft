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
import net.minecraft.client.model.EndermanModel;
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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OxygenMaskRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/gear/oxygen_gear.png");
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart pipe;

    public OxygenMaskRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        ModelPart root, head, body;
        boolean rotate = false;
        float maskX = -5.0F;
        float maskY = -9.0F;
        float maskZ = -5.0F;
        float pipeX = -2.0F;
        float pipeY = context.getModel() instanceof EndermanModel ? -2.0F : -3.0F;
        float pipeZ = 2.0F;
        if (context.getModel() instanceof HierarchicalModel<?> model) {
            root = model.root();
            head = root.getChild(PartNames.HEAD);
            body = root.getChild(PartNames.BODY);
        } else if (context.getModel() instanceof HumanoidModel<?> model) {
            head = model.head;
            body = model.body;
        } else if (context.getModel() instanceof AnimalModelAgeableListModel model) {
            head = model.callGetHeadParts().iterator().next();
            body = model.callGetBodyParts().iterator().next();
            rotate = true;
        } else {
            this.mask = null;
            this.pipe = null;
            return;
        }
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (head != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(maskX, maskY, maskZ, 10, 10, 10, new CubeDeformation(-0.1F)), PartPose.offset(head.x, head.y, head.z));
        }
        if (body != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(40, 6).addBox(pipeX, pipeY, pipeZ, 4, 6, 8, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
        }

        root = modelPartData.bake(64, 32);

        if (head != null) {
            this.mask = root.getChild(Constant.ModelPartName.OXYGEN_MASK);
        } else {
            this.mask = null;
        }

        if (body != null) {
            this.pipe = root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
            if (rotate) {
                this.pipe.xRot = (float) (Math.PI / 2.0);
            }
        } else {
            this.pipe = null;
        }
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        LivingEntity livingEntity = (LivingEntity) entity;
        boolean hasMask = true;
        boolean hasGear = true;

        if (livingEntity instanceof Player player) {
            Container inv = livingEntity.galacticraft$getGearInv();
            hasMask = inv.getItem(GCAccessorySlots.OXYGEN_MASK_SLOT).is(GCTags.OXYGEN_MASKS);
            hasGear = inv.getItem(GCAccessorySlots.OXYGEN_GEAR_SLOT).is(GCTags.OXYGEN_GEAR);
        } else if (livingEntity instanceof Zombie) {
            Zombie zombie = (Zombie) entity;
            if (zombie.isBaby()) {
                matrices.scale(0.75F, 0.75F, 0.75F);
                matrices.translate(0.0F, 1.0F, 0.0F);
            }
        }

        if (this.mask != null && hasMask) {
            this.mask.yRot = headYaw * (float) (Math.PI / 180.0);
            this.mask.xRot = headPitch * (float) (Math.PI / 180.0);
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.pipe != null && hasGear) {
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
