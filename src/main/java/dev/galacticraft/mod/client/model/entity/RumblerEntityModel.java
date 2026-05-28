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

package dev.galacticraft.mod.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class RumblerEntityModel<T extends Entity> extends EntityModel<T> {
    private static final String LEFT_CLAW = "left_claw";
    private static final String RIGHT_CLAW = "right_claw";
    private static final String LEFT_CLAW_TOP = "left_claw_top";
    private static final String RIGHT_CLAW_TOP = "right_claw_top";
    private static final String FRONT_LEFT_LEG = "front_left_leg";
    private static final String MIDDLE_LEFT_LEG = "middle_left_leg";
    private static final String BACK_LEFT_LEG = "back_left_leg";
    private static final String FRONT_RIGHT_LEG = "front_right_leg";
    private static final String MIDDLE_RIGHT_LEG = "middle_right_leg";
    private static final String BACK_RIGHT_LEG = "back_right_leg";
    private final ModelPart root;
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
        this.root = root;
        this.body = root.getChild(PartNames.BODY);
        this.leftClaw = root.getChild(LEFT_CLAW);
        this.rightClaw = root.getChild(RIGHT_CLAW);
        this.frontLeftLeg = root.getChild(FRONT_LEFT_LEG);
        this.middleLeftLeg = root.getChild(MIDDLE_LEFT_LEG);
        this.backLeftLeg = root.getChild(BACK_LEFT_LEG);
        this.frontRightLeg = root.getChild(FRONT_RIGHT_LEG);
        this.middleRightLeg = root.getChild(MIDDLE_RIGHT_LEG);
        this.backRightLeg = root.getChild(BACK_RIGHT_LEG);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -18.0F, -8.0F, 20.0F, 10.0F, 15.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(-6.0F, -24.0F, -8.0F, 12.0F, 6.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition leftClaw = partDefinition.addOrReplaceChild(LEFT_CLAW, CubeListBuilder.create().texOffs(36, 34).mirror().addBox(-3.0F, 2.0F, -4.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-11.0F, 14.0F, -8.0F));
        leftClaw.addOrReplaceChild(LEFT_CLAW_TOP, CubeListBuilder.create().texOffs(33, 25).mirror().addBox(0.0F, -3.0F, -3.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-3.0F, 3.0F, -1.0F));

        PartDefinition rightClaw = partDefinition.addOrReplaceChild(RIGHT_CLAW, CubeListBuilder.create().texOffs(36, 34).addBox(-8.0F, 2.0F, -4.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(11.0F, 14.0F, -8.0F));
        rightClaw.addOrReplaceChild(RIGHT_CLAW_TOP, CubeListBuilder.create().texOffs(33, 25).addBox(-10.0F, -3.0F, -3.0F, 11.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(2.0F, 3.0F, -1.0F));

        partDefinition.addOrReplaceChild(FRONT_LEFT_LEG, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-10.0F, 14.0F, -4.5F));
        partDefinition.addOrReplaceChild(MIDDLE_LEFT_LEG, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-10.0F, 14.0F, 0.5F));
        partDefinition.addOrReplaceChild(BACK_LEFT_LEG, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-10.0F, 14.0F, 5.5F));
        partDefinition.addOrReplaceChild(FRONT_RIGHT_LEG, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(10.0F, 14.0F, -4.5F));
        partDefinition.addOrReplaceChild(MIDDLE_RIGHT_LEG, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(10.0F, 14.0F, 0.5F));
        partDefinition.addOrReplaceChild(BACK_RIGHT_LEG, CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(10.0F, 14.0F, 5.5F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(poseStack, vertices, light, overlay);
    }
}