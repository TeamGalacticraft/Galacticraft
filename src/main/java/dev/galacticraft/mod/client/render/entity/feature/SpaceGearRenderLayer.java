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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpaceGearRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/oxygen_gear.png");
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart pipe;
    private @Nullable ModelPart root;

    public SpaceGearRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        ModelPart head, body;
        if (context.getModel() instanceof HierarchicalModel<?> model) {
            this.root = model.root();
            head = this.root.getChild(PartNames.HEAD);
            body = this.root.getChild(PartNames.BODY);
        } else if (context.getModel() instanceof HumanoidModel<?> model){
            head = model.head;
            body = model.body;
        } else if (context.getModel() instanceof AnimalModelAgeableListModel model){
            head = model.callGetHeadParts().iterator().next();
            body = model.callGetBodyParts().iterator().next();
        } else {
            this.mask = null;
            this.pipe = null;
            this.root = null;
            return;
        }
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (head != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.01F, -5.0F, 10, 10, 10, CubeDeformation.NONE), PartPose.offset(head.x, head.y, head.z));
        }

        if (body != null) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(40, 7).addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("small_left", CubeListBuilder.create().texOffs(0, 20).addBox(0.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("small_right", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("medium_left", CubeListBuilder.create().texOffs(32, 20).addBox(0.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("medium_right", CubeListBuilder.create().texOffs(48, 20).addBox(-4.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("large_left", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("large_right", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("infinite_left", CubeListBuilder.create().texOffs(32, 32).addBox(0.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild("infinite_right", CubeListBuilder.create().texOffs(48, 32).addBox(-4.0F, 1.0F, 2.0F, 4, 6, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
        }

        this.root = modelPartData.bake(64, 64);

        if (head != null) {
            this.mask = this.root.getChild(Constant.ModelPartName.OXYGEN_MASK);
        } else {
            this.mask = null;
        }

        if (body != null) {
            this.pipe = this.root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
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
        boolean hasTank1 = true;
        boolean hasTank2 = true;
        String tankSize1 = "medium_left";
        String tankSize2 = "medium_right";

        if (livingEntity instanceof Player) {
            Container inv = livingEntity.galacticraft$getGearInv();
            hasMask = inv.getItem(GCAccessorySlots.OXYGEN_MASK_SLOT).is(GCTags.OXYGEN_MASKS);
            hasGear = inv.getItem(GCAccessorySlots.OXYGEN_GEAR_SLOT).is(GCTags.OXYGEN_GEAR);
            hasTank1 = inv.getItem(GCAccessorySlots.OXYGEN_TANK_1_SLOT).is(GCTags.OXYGEN_TANKS);
            hasTank2 = inv.getItem(GCAccessorySlots.OXYGEN_TANK_2_SLOT).is(GCTags.OXYGEN_TANKS);
            switch (inv.getItem(GCAccessorySlots.OXYGEN_TANK_1_SLOT).getDescriptionId().replace("item.galacticraft.", "")) {
                case Constant.Item.SMALL_OXYGEN_TANK:
                    tankSize1 = "small_left";
                    break;
                case Constant.Item.MEDIUM_OXYGEN_TANK:
                    tankSize1 = "medium_left";
                    break;
                case Constant.Item.LARGE_OXYGEN_TANK:
                    tankSize1 = "large_left";
                    break;
                case Constant.Item.INFINITE_OXYGEN_TANK:
                    tankSize1 = "infinite_left";
                    break;
                default:
                    tankSize1 = "medium_left";
            }
            switch (inv.getItem(GCAccessorySlots.OXYGEN_TANK_2_SLOT).getDescriptionId().replace("item.galacticraft.", "")) {
                case Constant.Item.SMALL_OXYGEN_TANK:
                    tankSize2 = "small_right";
                    break;
                case Constant.Item.MEDIUM_OXYGEN_TANK:
                    tankSize2 = "medium_right";
                    break;
                case Constant.Item.LARGE_OXYGEN_TANK:
                    tankSize2 = "large_right";
                    break;
                case Constant.Item.INFINITE_OXYGEN_TANK:
                    tankSize2 = "infinite_right";
                    break;
                default:
                    tankSize2 = "medium_right";
            }
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
        if (this.root != null && hasTank1) {
            this.root.getChild(tankSize1).render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.root != null && hasTank2) {
            this.root.getChild(tankSize2).render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
