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

package dev.galacticraft.mod.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class RumblerEntityModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart leftClaw;
    private final ModelPart rightClaw;
    private final ModelPart frontLeftLeg;
    private final ModelPart middleLeftLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart middleRightLeg;
    private final ModelPart backRightLeg;

    public RumblerEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.leftClaw = root.getChild("Left_Claw");
        this.rightClaw = root.getChild("Right_Claw");
        this.frontLeftLeg = root.getChild("Front_Left_Leg");
        this.middleLeftLeg = root.getChild("Middle_Left_Leg");
        this.backLeftLeg = root.getChild("Back_Left_Leg");
        this.frontRightLeg = root.getChild("Front_Right_Leg");
        this.middleRightLeg = root.getChild("Middle_Right_Leg");
        this.backRightLeg = root.getChild("Back_Right_Leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -18.0F, -8.0F, 20.0F, 10.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-6.0F, -24.0F, -8.0F, 12.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Left_Claw = partdefinition.addOrReplaceChild("Left_Claw", CubeListBuilder.create().texOffs(36, 34).mirror().addBox(-3.0F, 2.0F, -4.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-11.0F, 14.0F, -8.0F));

        PartDefinition Top_Claw_L = Left_Claw.addOrReplaceChild("Top_Claw_L", CubeListBuilder.create().texOffs(33, 25).mirror().addBox(0.0F, -3.0F, -3.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-3.0F, 3.0F, -1.0F));

        PartDefinition Right_Claw = partdefinition.addOrReplaceChild("Right_Claw", CubeListBuilder.create().texOffs(36, 34).addBox(-8.0F, 2.0F, -4.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(11.0F, 14.0F, -8.0F));

        PartDefinition Top_Claw_R = Right_Claw.addOrReplaceChild("Top_Claw_R", CubeListBuilder.create().texOffs(33, 25).addBox(-10.0F, -3.0F, -3.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(2.0F, 3.0F, -1.0F));

        PartDefinition Front_Left_Leg = partdefinition.addOrReplaceChild("Front_Left_Leg", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 14.0F, -4.5F));

        PartDefinition Middle_Left_Leg = partdefinition.addOrReplaceChild("Middle_Left_Leg", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 14.0F, 0.5F));

        PartDefinition Back_Left_Leg = partdefinition.addOrReplaceChild("Back_Left_Leg", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 14.0F, 5.5F));

        PartDefinition Front_Right_Leg = partdefinition.addOrReplaceChild("Front_Right_Leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(10.0F, 14.0F, -4.5F));

        PartDefinition Middle_Right_Leg = partdefinition.addOrReplaceChild("Middle_Right_Leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(10.0F, 14.0F, 0.5F));

        PartDefinition Back_Right_Leg = partdefinition.addOrReplaceChild("Back_Right_Leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(10.0F, 14.0F, 5.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
        body.render(poseStack, vertices, light, overlay);
        leftClaw.render(poseStack, vertices, light, overlay);
        rightClaw.render(poseStack, vertices, light, overlay);
        frontLeftLeg.render(poseStack, vertices, light, overlay);
        middleLeftLeg.render(poseStack, vertices, light, overlay);
        backLeftLeg.render(poseStack, vertices, light, overlay);
        frontRightLeg.render(poseStack, vertices, light, overlay);
        middleRightLeg.render(poseStack, vertices, light, overlay);
        backRightLeg.render(poseStack, vertices, light, overlay);
    }
}