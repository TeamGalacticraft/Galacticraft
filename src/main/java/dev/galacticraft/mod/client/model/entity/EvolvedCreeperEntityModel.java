/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.EvolvedCreeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
@Environment(EnvType.CLIENT)
public class EvolvedCreeperEntityModel extends EntityModel<EvolvedCreeperEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart mask;
    private final ModelPart tank;
    private final ModelPart pipe;

    public EvolvedCreeperEntityModel(ModelPart root, boolean gear) {
        this.root = root;
        this.head = root.getChild(PartNames.HEAD);
        this.body = root.getChild(PartNames.BODY);
        this.leftHindLeg = root.getChild(PartNames.LEFT_HIND_LEG);
        this.rightHindLeg = root.getChild(PartNames.RIGHT_HIND_LEG);
        this.leftFrontLeg = root.getChild(PartNames.LEFT_FRONT_LEG);
        this.rightFrontLeg = root.getChild(PartNames.RIGHT_FRONT_LEG);
        if (gear) {
            this.mask = root.getChild(Constant.ModelPartName.OXYGEN_MASK);
            this.tank = root.getChild(Constant.ModelPartName.OXYGEN_TANK);
            this.pipe = root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
        } else {
            this.mask = null;
            this.tank = null;
            this.pipe = null;
        }
    }

    public static LayerDefinition getTexturedModelData(CubeDeformation dilation) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), PartPose.offset(0.0F, 6.0F, 0.0F));
        modelPartData.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), PartPose.offset(0.0F, 6.0F, 0.0F));
        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation);
        modelPartData.addOrReplaceChild(PartNames.RIGHT_HIND_LEG, modelPartBuilder, PartPose.offset(-2.0F, 18.0F, 4.0F));
        modelPartData.addOrReplaceChild(PartNames.LEFT_HIND_LEG, modelPartBuilder, PartPose.offset(2.0F, 18.0F, 4.0F));
        modelPartData.addOrReplaceChild(PartNames.RIGHT_FRONT_LEG, modelPartBuilder, PartPose.offset(-2.0F, 18.0F, -4.0F));
        modelPartData.addOrReplaceChild(PartNames.LEFT_FRONT_LEG, modelPartBuilder, PartPose.offset(2.0F, 18.0F, -4.0F));
        if (dilation == CubeDeformation.NONE) {
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_MASK, CubeListBuilder.create().texOffs(0, 44).addBox(-5.0F, -9.0F, -5.0F, 10, 10, 10, dilation), PartPose.offset(0.0F, 6.0F, 0.0F));
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_TANK, CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, 1.0F, 2.0F, 8, 6, 4, dilation), PartPose.offset(0.0F, 6.0F, 0.0F));
            modelPartData.addOrReplaceChild(Constant.ModelPartName.OXYGEN_PIPE, CubeListBuilder.create().texOffs(40, 51).addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, dilation), PartPose.offset(0.0F, 2.0F, 0.0F));
        }
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void setupAnim(EvolvedCreeperEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        if (this.mask != null) {
            this.mask.yRot = (float) (headYaw / (180.0F / Math.PI));
            this.mask.xRot = (float) (headPitch / (180.0F / Math.PI));
        }
        this.head.yRot = (float) (headYaw / (180.0F / Math.PI));
        this.head.xRot = (float) (headPitch / (180.0F / Math.PI));

        this.leftHindLeg.xRot = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftFrontLeg.xRot = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.rightFrontLeg.xRot = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.rightHindLeg.xRot = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.young) {
            this.root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        } else {
            matrices.pushPose();
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(0.0F, 1.0F, 0.0F);
            this.head.render(matrices, vertexConsumer, light, overlay);
            if (this.mask != null) this.mask.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.popPose();
            matrices.pushPose();
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0F, 1.52083F, 0.0F);
            this.body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            if (this.tank != null) this.tank.render(matrices, vertexConsumer, light, overlay);
            if (this.pipe != null) this.pipe.render(matrices, vertexConsumer, light, overlay);
            this.leftHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leftFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.popPose();
        }
    }
}
