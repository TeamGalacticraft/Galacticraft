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
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ParrotOxygenGearRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/gear/parrot_gear.png");
    private final @Nullable ModelPart head;
    private final @Nullable ModelPart feather;
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart pipe;
    private final @Nullable ModelPart root;

    public ParrotOxygenGearRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (context.getModel() instanceof ParrotModel model) {
            ModelPart modelRoot = model.root();
            this.head = modelRoot.getChild(PartNames.HEAD);
            this.body = modelRoot.getChild(PartNames.BODY);
            this.feather = this.head.getChild("feather");
        } else if (context.getModel() instanceof PlayerModel model) {
            this.head = null;
            this.body = null;
            this.feather = null;
        } else {
            this.head = null;
            this.feather = null;
            this.mask = null;
            this.body = null;
            this.pipe = null;
            this.root = null;
            return;
        }

        modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.75F, -3.5F, 5, 5, 5, CubeDeformation.NONE), PartPose.ZERO);
        modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(21, -1).addBox(0.0F, -4.0F, 1.5F, 0, 6, 5, CubeDeformation.NONE), PartPose.ZERO);
        modelPartData.addOrReplaceChild(Constant.Item.SMALL_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0.0F, 1.5F, 3, 6, 3, CubeDeformation.NONE), PartPose.ZERO);
        modelPartData.addOrReplaceChild(Constant.Item.MEDIUM_OXYGEN_TANK, CubeListBuilder.create().texOffs(12, 10).addBox(-1.5F, 0.0F, 1.5F, 3, 6, 3, CubeDeformation.NONE), PartPose.ZERO);
        modelPartData.addOrReplaceChild(Constant.Item.LARGE_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 21).addBox(-1.5F, 0.0F, 1.5F, 3, 6, 3, CubeDeformation.NONE), PartPose.ZERO);
        modelPartData.addOrReplaceChild(Constant.Item.INFINITE_OXYGEN_TANK, CubeListBuilder.create().texOffs(12, 21).addBox(-1.5F, 0.0F, 1.5F, 3, 6, 3, CubeDeformation.NONE), PartPose.ZERO);

        this.root = modelPartData.bake(32, 32);
        this.mask = this.root.getChild(Constant.ModelPartName.OXYGEN_MASK);
        this.pipe = this.root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof Player) {
            this.renderOnShoulder(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, headYaw, headPitch, true);
            this.renderOnShoulder(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, headYaw, headPitch, false);
            return;
        } else {
            matrices.pushPose();
            this.render(matrices, vertexConsumers, light, entity, headYaw, headPitch, true, true, Constant.Item.SMALL_OXYGEN_TANK);
            matrices.popPose();
        }
    }

    private void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float headYaw, float headPitch, boolean hasMask, boolean hasGear, String tankSize) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        ModelPart tank = this.root != null && this.root.hasChild(tankSize) ? this.root.getChild(tankSize) : null;

        if (this.mask != null && hasMask) {
            if (this.head != null) {
                this.mask.copyFrom(this.head);
            } else {
                this.mask.x = 0.0F;
                this.mask.y = 15.69F;
                this.mask.z = -2.76F;
                this.mask.yRot = headYaw * Mth.DEG_TO_RAD;
                this.mask.xRot = headPitch * Mth.DEG_TO_RAD;
            }
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.pipe != null && hasGear) {
            if (this.body != null) {
                this.pipe.copyFrom(this.body);
            } else {
                this.pipe.x = 0.0F;
                this.pipe.y = 16.5F;
                this.pipe.z = -3.0F;
                this.pipe.xRot = 0.4937F;
            }
            this.pipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (tank != null) {
            if (this.body != null) {
                tank.copyFrom(this.body);
            } else {
                tank.x = 0.0F;
                tank.y = 16.5F;
                tank.z = -3.0F;
                tank.xRot = 0.4937F;
            }
            tank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.feather != null) {
            this.feather.visible = !hasMask;
        }
    }

    private void renderOnShoulder(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float headYaw, float headPitch, boolean leftShoulder) {
        Player player = (Player) entity;
        CompoundTag compoundTag = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.byString(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
                matrices.pushPose();
                matrices.translate(leftShoulder ? 0.4F : -0.4F, player.isCrouching() ? -1.3F : -1.5F, 0.0F);
                this.render(matrices, vertexConsumers, light, entity, headYaw, headPitch, compoundTag.getBoolean(Constant.Nbt.HAS_MASK), compoundTag.getBoolean(Constant.Nbt.HAS_GEAR), compoundTag.getString(Constant.Nbt.OXYGEN_TANK));
                matrices.popPose();
        });
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
