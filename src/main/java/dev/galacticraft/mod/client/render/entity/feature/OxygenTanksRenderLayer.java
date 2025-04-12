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
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.WitchModel;
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
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OxygenTanksRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/gear/oxygen_tanks.png");
    private final @Nullable ModelPart tanks;

    public OxygenTanksRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        ModelPart root, body;
        boolean rotate = false;
        float x = 0.0F;
        float y = context.getModel() instanceof EndermanModel ? 2.0F : 1.0F;
        float z = context.getModel() instanceof IllagerModel || context.getModel() instanceof WitchModel ? 3.01F : 2.01F;
        if (context.getModel() instanceof SpiderModel model) {
            root = model.root();
            body = root.getChild("body1");
            rotate = true;
            y = -5.0F;
            z = 4.01F;
        } else if (context.getModel() instanceof HierarchicalModel<?> model) {
            root = model.root();
            body = root.getChild(PartNames.BODY);
        } else if (context.getModel() instanceof HumanoidModel<?> model) {
            body = model.body;
        } else if (context.getModel() instanceof AnimalModelAgeableListModel model) {
            body = model.callGetBodyParts().iterator().next();
            rotate = true;
        } else {
            this.tanks = null;
            return;
        }

        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (body != null) {
            modelPartData.addOrReplaceChild(Constant.Item.SMALL_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild(Constant.Item.MEDIUM_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild(Constant.Item.LARGE_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
            modelPartData.addOrReplaceChild(Constant.Item.INFINITE_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offset(body.x, body.y, body.z));
        }

        this.tanks = modelPartData.bake(32, 32);

        if (rotate) {
            float angle = (float) (Math.PI / 2.0);
            this.tanks.getChild(Constant.Item.SMALL_OXYGEN_TANK).xRot = angle;
            this.tanks.getChild(Constant.Item.MEDIUM_OXYGEN_TANK).xRot = angle;
            this.tanks.getChild(Constant.Item.LARGE_OXYGEN_TANK).xRot = angle;
            this.tanks.getChild(Constant.Item.INFINITE_OXYGEN_TANK).xRot = angle;
        }
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        LivingEntity livingEntity = (LivingEntity) entity;
        boolean hasTank1 = true;
        boolean hasTank2 = true;
        String tankSize1 = Constant.Item.MEDIUM_OXYGEN_TANK;
        String tankSize2 = Constant.Item.MEDIUM_OXYGEN_TANK;

        if (livingEntity instanceof Player player) {
            Container inv = livingEntity.galacticraft$getOxygenTanks();
            hasTank1 = inv.getItem(0).is(GCTags.OXYGEN_TANKS);
            hasTank2 = inv.getItem(1).is(GCTags.OXYGEN_TANKS);
            if (hasTank1) {
                tankSize1 = inv.getItem(0).getDescriptionId().replace("item.galacticraft.", "");
            }
            if (hasTank2) {
                tankSize2 = inv.getItem(1).getDescriptionId().replace("item.galacticraft.", "");
            }
        } else if (livingEntity instanceof Zombie) {
            Zombie zombie = (Zombie) entity;
            if (zombie.isBaby()) {
                matrices.scale(0.75F, 0.75F, 0.75F);
            }
        } else if (livingEntity instanceof AbstractIllager || livingEntity instanceof Witch) {
            tankSize1 = Constant.Item.LARGE_OXYGEN_TANK;
            tankSize2 = Constant.Item.LARGE_OXYGEN_TANK;
        }

        if (this.tanks != null && hasTank1) {
            this.tanks.getChild(tankSize1).render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (this.tanks != null && hasTank2) {
            matrices.translate(-0.25F, 0.0F, 0.0F);
            this.tanks.getChild(tankSize2).render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
