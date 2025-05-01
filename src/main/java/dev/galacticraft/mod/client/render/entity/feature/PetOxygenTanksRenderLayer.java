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
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.WolfModel;
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
import net.minecraft.world.Container;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.Nullable;

public class PetOxygenTanksRenderLayer<T extends TamableAnimal, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/entity/gear/oxygen_tanks.png");
    private final @Nullable ModelPart tanks;
    private float xRot = Mth.HALF_PI;
    private float tankY = 0.0F;
    private float tankZ = 0.0F;

    public PetOxygenTanksRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        ModelPart root, body;
        float x = -2.0F;
        float y = 0.0F;
        float z = 0.0F;
        if (context.getModel() instanceof AnimalModelAgeableListModel model) {
            body = model.callGetBodyParts().iterator().next();

            if (context.getModel() instanceof WolfModel) {
                y = -4.0F;
                z = 3.0F;
            } else if (context.getModel() instanceof CatModel) {
                y = 4.0F;
                z = -2.0F;
            }
        } else if (context.getModel() instanceof ParrotModel model) {
            root = model.root();
            body = root.getChild(PartNames.BODY);
            this.xRot = 0.4937F;
            z = 1.0F;
        } else {
            this.tanks = null;
            return;
        }

        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        if (body != null) {
            modelPartData.addOrReplaceChild(Constant.Item.SMALL_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offsetAndRotation(body.x, body.y, body.z, this.xRot, 0.0F, 0.0F));
            modelPartData.addOrReplaceChild(Constant.Item.MEDIUM_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 0).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offsetAndRotation(body.x, body.y, body.z, this.xRot, 0.0F, 0.0F));
            modelPartData.addOrReplaceChild(Constant.Item.LARGE_OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offsetAndRotation(body.x, body.y, body.z, this.xRot, 0.0F, 0.0F));
            modelPartData.addOrReplaceChild(Constant.Item.INFINITE_OXYGEN_TANK, CubeListBuilder.create().texOffs(16, 16).addBox(x, y, z, 4, 8, 4, CubeDeformation.NONE), PartPose.offsetAndRotation(body.x, body.y, body.z, this.xRot, 0.0F, 0.0F));
        }

        this.tanks = modelPartData.bake(32, 32);
        this.tankY = this.tanks.getChild(Constant.Item.SMALL_OXYGEN_TANK).y;
        this.tankZ = this.tanks.getChild(Constant.Item.SMALL_OXYGEN_TANK).z;
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (this.tanks == null) return;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        TamableAnimal animal = (TamableAnimal) entity;
        ModelPart tank = this.tanks.getChild(Constant.Item.SMALL_OXYGEN_TANK);

        if (tank != null) {
            float angle = this.xRot;
            float y = this.tankY;
            float z = this.tankZ;
            if (animal.isInSittingPose()) {
                if (animal instanceof Wolf) {
                    angle = 0.7853982F;
                    y += 4.0F;
                    z -= 2.0F;
                } else if (animal instanceof Cat) {
                    angle = 0.7853982F;
                    y -= 4.1F;
                    z += 4.9F;
                } else if (animal instanceof Parrot) {
                    // y += 1.0F;
                    z += 1.0F;
                }
            } else if (animal.isCrouching() && animal instanceof Cat) {
                y += 1.0F;
            }

            tank.xRot = angle;
            tank.y = y;
            tank.z = z;
            tank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
