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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.jetbrains.annotations.Nullable;

public class OxygenTanksRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id(Constant.GearTexture.OXYGEN_TANKS);
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart tanks;
    private float xRot = 0.0F;

    public OxygenTanksRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        float x = 0.0F;
        float y = context.getModel() instanceof EndermanModel ? 2.0F : 1.0F;
        float z = context.getModel() instanceof IllagerModel || context.getModel() instanceof WitchModel ? 3.01F : 2.01F;
        if (context.getModel() instanceof SpiderModel model) {
            this.body = model.root().getChild("body1");
            this.xRot = Mth.HALF_PI;
            y = -5.0F;
            z = 4.01F;
        } else if (context.getModel() instanceof HierarchicalModel<?> model) {
            this.body = model.root().getChild(PartNames.BODY);
        } else if (context.getModel() instanceof HumanoidModel<?> model) {
            this.body = model.body;
        } else if (context.getModel() instanceof AnimalModelAgeableListModel model) {
            this.body = model.callGetBodyParts().iterator().next();
            this.xRot = Mth.HALF_PI;
        } else {
            this.body = null;
            this.tanks = null;
            return;
        }

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        if (this.body != null) {
            partDefinition.addOrReplaceChild(Constant.Item.SMALL_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.ZERO);
            partDefinition.addOrReplaceChild(Constant.Item.MEDIUM_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.ZERO);
            partDefinition.addOrReplaceChild(Constant.Item.LARGE_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.ZERO);
            partDefinition.addOrReplaceChild(Constant.Item.INFINITE_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.ZERO);
        }

        this.tanks = partDefinition.bake(32, 32);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (this.tanks == null) return;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));

        String tankSize1 = entity.galacticraft$tankSize(0);
        String tankSize2 = entity.galacticraft$tankSize(1);
        ModelPart tank1 = this.tanks.hasChild(tankSize1) ? this.tanks.getChild(tankSize1) : null;
        ModelPart tank2 = this.tanks.hasChild(tankSize2) ? this.tanks.getChild(tankSize2) : null;

        matrices.pushPose();
        if (entity instanceof Zombie zombie && zombie.isBaby()) {
            matrices.scale(0.75F, 0.75F, 0.75F);
            matrices.translate(0.0F, 1.0F, 0.0F);
        }

        if (tank1 != null) {
            tank1.copyFrom(this.body);
            if (this.xRot != 0.0F) {
                tank1.xRot += this.xRot;
            }
            tank1.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (tank2 != null) {
            matrices.translate(-0.25F, 0.0F, 0.0F);
            tank2.copyFrom(this.body);
            if (this.xRot != 0.0F) {
                tank2.xRot += this.xRot;
            }
            tank2.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        matrices.popPose();
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
