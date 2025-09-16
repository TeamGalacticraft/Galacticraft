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
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WolfModel;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.Nullable;

public class PetOxygenMaskRenderLayer<T extends TamableAnimal, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id(Constant.GearTexture.PET_GEAR);
    private final @Nullable ModelPart head;
    private final @Nullable ModelPart realHead;
    private final @Nullable ModelPart mask;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart pipe;
    private final @Nullable ModelPart pipeSitting;

    public PetOxygenMaskRenderLayer(RenderLayerParent<T, M> context) {
        super(context);
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        ModelPart realHead = null;
        if (context.getModel() instanceof AnimalModelAgeableListModel model) {
            this.head = model.callGetHeadParts().iterator().next();
            this.body = model.callGetBodyParts().iterator().next();

            if (context.getModel() instanceof WolfModel) {
                realHead = this.head.getChild("real_head");
                PartDefinition maskPart = partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create(), PartPose.offset(-1.0F, 13.5F, -7.0F));
                maskPart.addOrReplaceChild(Constant.ModelPartName.REAL_OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -5.0F, -6.0F, 8, 8, 8, new CubeDeformation(0.1F)), PartPose.ZERO);
                partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -11.0F, 2.0F, 0, 9, 8, CubeDeformation.NONE), PartPose.ZERO);
                partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE_SITTING, CubeListBuilder.create().texOffs(32, 0).addBox(0.0F, -11.0F, 2.0F, 0, 9, 8, CubeDeformation.NONE), PartPose.ZERO);
            } else if (context.getModel() instanceof CatModel) {
                partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, -4.0F, -4.9F, 7, 7, 7, CubeDeformation.NONE), PartPose.ZERO);
                partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(48, 18).addBox(0.0F, 0.0F, -3.0F, 0, 6, 8, CubeDeformation.NONE), PartPose.ZERO);
                partDefinition.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE_SITTING, CubeListBuilder.create().texOffs(32, 18).addBox(0.0F, 0.0F, -3.0F, 0, 6, 8, CubeDeformation.NONE), PartPose.ZERO);
            }
        } else {
            this.head = null;
            this.realHead = null;
            this.mask = null;
            this.body = null;
            this.pipe = null;
            this.pipeSitting = null;
            return;
        }

        ModelPart modelRoot = partDefinition.bake(64, 32);
        this.realHead = realHead;
        this.mask = this.head != null ? modelRoot.getChild(Constant.ModelPartName.OXYGEN_MASK) : null;
        this.pipe = this.body != null ? modelRoot.getChild(Constant.ModelPartName.OXYGEN_PIPE) : null;
        this.pipeSitting = this.body != null ? modelRoot.getChild(Constant.ModelPartName.OXYGEN_PIPE_SITTING) : null;
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        TamableAnimal animal = (TamableAnimal) entity;

        if (this.mask != null && animal.galacticraft$hasMask()) {
            matrices.pushPose();
            if (animal.isBaby()) {
                if (animal instanceof Wolf) {
                    matrices.translate(0.0F, 0.3125F, 0.125F);
                } else if (animal instanceof Cat) {
                    matrices.scale(0.75F, 0.75F, 0.75F);
                    matrices.translate(0.0F, 0.625F, 0.25F);
                }
            }

            this.mask.copyFrom(this.head);
            if (this.realHead != null) {
                this.mask.getChild(Constant.ModelPartName.REAL_OXYGEN_MASK).zRot = this.realHead.zRot;
            }
            this.mask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        if (animal.galacticraft$hasGear()) {
            ModelPart oxygenPipe = animal.isInSittingPose() ? this.pipeSitting : this.pipe;
            if (oxygenPipe != null) {
                matrices.pushPose();
                oxygenPipe.copyFrom(this.body);

                if (animal.isBaby()) {
                    matrices.scale(0.5F, 0.5F, 0.5F);
                    matrices.translate(0.0F, 1.5F, 0.0F);

                    if (animal instanceof Cat) {
                        if (animal.isInSittingPose()) {
                            oxygenPipe.y += 2.12132F;
                            oxygenPipe.z += 2.12132F;
                        } else {
                            oxygenPipe.z += 3.0F;
                        }
                    }
                }

                oxygenPipe.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
                matrices.popPose();
            }
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
