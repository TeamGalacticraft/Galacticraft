/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.content.entity.boss.SkeletonBoss;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class EvolvedSkeletonBossModel extends EntityModel<SkeletonBoss> implements ArmedModel {
    private final ModelPart root;
    private ModelPart upperHead;
    private ModelPart pelvis;
    private ModelPart sternum;
    private ModelPart rightLeg;
    private ModelPart rightArm;
    private ModelPart spine;
    private ModelPart leftArm;
    private ModelPart leftLeg;
    private ModelPart leftFrontBotRib;
    private ModelPart leftFrontTopRib;
    private ModelPart leftFront2ndRib;
    private ModelPart leftFront3rdRib;
    private ModelPart leftSideBotRib;
    private ModelPart leftSide3rdRib;
    private ModelPart leftSide2ndRib;
    private ModelPart leftSideTopRib;
    private ModelPart rightSideTopRib;
    private ModelPart rightSide2ndRib;
    private ModelPart rightSide3rdRib;
    private ModelPart rightSideBotRib;
    private ModelPart rightFrontBotRib;
    private ModelPart rightFront3rdRib;
    private ModelPart rightFront2ndRib;
    private ModelPart rightFrontTopRib;
    private ModelPart leftBackTopRib;
    private ModelPart leftBack2ndRib;
    private ModelPart leftBack3rdRib;
    private ModelPart leftBackBotRib;
    private ModelPart rightBackBotRib;
    private ModelPart rightBack3rdRib;
    private ModelPart rightBack2ndRib;
    private ModelPart rightBackTopRib;

    public EvolvedSkeletonBossModel(ModelPart root) {
        this.root = root;
        float halfPI = Mth.HALF_PI;

        this.upperHead = root.getChild("upper_head");
        this.setRotation(this.upperHead, 0.122173F, 0F, 0F);
        this.pelvis = root.getChild("pelvis");
        this.sternum = root.getChild("sternum");
        this.rightLeg = root.getChild("right_leg");
        this.rightArm = root.getChild("right_arm");
        this.spine = root.getChild("spine");
        this.leftArm = root.getChild("left_arm");
        this.leftLeg = root.getChild("left_leg");
        this.leftFrontBotRib = root.getChild("left_front_bot_rib");
        this.setRotation(this.leftFrontBotRib, 0F, -halfPI, 0F);
        this.leftFrontTopRib = root.getChild("left_front_top_rib");
        this.setRotation(this.leftFrontTopRib, 0F, -halfPI, 0F);
        this.leftFront2ndRib = root.getChild("left_front_second_rib");
        this.setRotation(this.leftFront2ndRib, 0F, -halfPI, 0F);
        this.leftFront3rdRib = root.getChild("left_front_third_rib");
        this.setRotation(this.leftFront3rdRib, 0F, -halfPI, 0F);
        this.leftSideBotRib = root.getChild("left_side_bot_rib");
        this.leftSide3rdRib = root.getChild("left_side_third_rib");
        this.leftSide2ndRib = root.getChild("left_side_second_rib");
        this.leftSideTopRib = root.getChild("left_side_top_rib");
        this.rightSideTopRib = root.getChild("right_side_top_rib");
        this.rightSide2ndRib = root.getChild("right_side_second_rib");
        this.rightSide3rdRib = root.getChild("right_side_third_rib");
        this.setRotation(this.rightSide3rdRib, 0F, 0F, 0F);
        this.rightSideBotRib = root.getChild("right_side_bot_rib");
        this.setRotation(this.rightSideBotRib, 0F, 0F, 0F);
        this.rightFrontBotRib = root.getChild("right_front_bot_rib");
        this.setRotation(this.rightFrontBotRib, 0F, halfPI, 0F);
        this.rightFront3rdRib = root.getChild("right_front_third_rib");
        this.setRotation(this.rightFront3rdRib, 0F, halfPI, 0F);
        this.rightFront2ndRib = root.getChild("right_front_second_rib");
        this.setRotation(this.rightFront2ndRib, 0F, halfPI, 0F);
        this.rightFrontTopRib = root.getChild("right_front_top_rib");
        this.setRotation(this.rightFrontTopRib, 0F, halfPI, 0F);
        this.leftBackTopRib = root.getChild("left_back_top_rib");
        this.setRotation(this.leftBackTopRib, 0F, -halfPI, 0F);
        this.leftBack2ndRib = root.getChild("left_back_second_rib");
        this.setRotation(this.leftBack2ndRib, 0F, -halfPI, 0F);
        this.leftBack3rdRib = root.getChild("left_back_third_rib");
        this.setRotation(this.leftBack3rdRib, 0F, -halfPI, 0F);
        this.leftBackBotRib = root.getChild("left_back_bot_rib");
        this.setRotation(this.leftBackBotRib, 0F, -halfPI, 0F);
        this.rightBackBotRib = root.getChild("right_back_bot_rib");
        this.setRotation(this.rightBackBotRib, 0F, halfPI, 0F);
        this.rightBack3rdRib = root.getChild("right_back_third_rib");
        this.setRotation(this.rightBack3rdRib, 0F, halfPI, 0F);
        this.rightBack2ndRib = root.getChild("right_back_second_rib");
        this.setRotation(this.rightBack2ndRib, 0F, halfPI, 0F);
        this.rightBackTopRib = root.getChild("right_back_top_rib");
        this.setRotation(this.rightBackTopRib, 0F, halfPI, 0F);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        float halfPI = Mth.HALF_PI;

        root.addOrReplaceChild("upper_head", CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-4F, -8F, -6F, 8, 8, 8)
                .mirror(),
                PartPose.offset(0F, -24F, 6F));
        root.addOrReplaceChild("pelvis", CubeListBuilder.create()
                .texOffs(32, 19)
                .addBox(-6F, 0F, -3F, 12, 5, 5)
                .mirror(),
                PartPose.offset(0F, -2F, 5F));
        root.addOrReplaceChild("sternum", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1.5F, 0F, -1F, 3, 9, 1)
                .mirror(),
                PartPose.offset(0F, -21F, 2F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(56, 33)
                .addBox(-2F, 0F, -2F, 3, 26, 3),
                PartPose.offset(-5F, 0F, 5F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(56, 33)
                .addBox(-2F, -2F, -1.5F, 3, 24, 3)
                .mirror(),
                PartPose.offset(-8F, -20F, 5F));
        root.addOrReplaceChild("spine", CubeListBuilder.create()
                .texOffs(32, 33)
                .addBox(-1.5F, 0F, -1F, 3, 22, 2)
                .mirror(),
                PartPose.offset(0F, -24F, 6F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(56, 33)
                .addBox(-1F, -2F, -1.5F, 3, 24, 3)
                .mirror(),
                PartPose.offset(8F, -20F, 5F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(56, 33)
                .addBox(-2F, 0F, -2F, 3, 26, 3)
                .mirror(),
                PartPose.offset(6F, 0F, 5F));
        root.addOrReplaceChild("left_front_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -13F, 2F));
        root.addOrReplaceChild("left_front_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -22F, 2F));
        root.addOrReplaceChild("left_front_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -19F, 2F));
        root.addOrReplaceChild("left_front_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -16F, 2F));
        root.addOrReplaceChild("left_side_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -13F, 7F));
        root.addOrReplaceChild("left_side_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -16F, 7F));
        root.addOrReplaceChild("left_side_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -19F, 7F));
        root.addOrReplaceChild("left_side_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -22F, 7F));
        root.addOrReplaceChild("right_side_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -22F, 7F));
        root.addOrReplaceChild("right_side_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -19F, 7F));
        root.addOrReplaceChild("right_side_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -16F, 7F));
        root.addOrReplaceChild("right_side_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, -6F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -13F, 7F));
        root.addOrReplaceChild("right_front_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -13F, 2F));
        root.addOrReplaceChild("right_front_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -16F, 2F));
        root.addOrReplaceChild("right_front_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -19F, 2F));
        root.addOrReplaceChild("right_front_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -22F, 2F));
        root.addOrReplaceChild("left_back_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -22F, 7F));
        root.addOrReplaceChild("left_back_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -19F, 7F));
        root.addOrReplaceChild("left_back_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -16F, 7F));
        root.addOrReplaceChild("left_back_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(7F, -13F, 7F));
        root.addOrReplaceChild("right_back_bot_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -13F, 7F));
        root.addOrReplaceChild("right_back_third_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -16F, 7F));
        root.addOrReplaceChild("right_back_second_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -19F, 7F));
        root.addOrReplaceChild("right_back_top_rib", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0F, 0F, 0F, 1, 2, 6)
                .mirror(),
                PartPose.offset(-7F, -22F, 7F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void setRotation(ModelPart model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void setupAnim(SkeletonBoss boss, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        final float floatPI = 3.1415927F;
        this.upperHead.yRot = headYaw / Mth.RAD_TO_DEG;
        this.upperHead.xRot = headPitch / Mth.RAD_TO_DEG;
        this.rightArm.xRot = Mth.cos(limbAngle * 0.6662F + floatPI) * 2.0F * limbDistance * 0.5F;
        this.leftArm.xRot = Mth.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightLeg.xRot = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.6662F + floatPI) * 1.4F * limbDistance;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;

        if (this.riding) {
            this.rightArm.xRot += -(floatPI / 5F);
            this.leftArm.xRot += -(floatPI / 5F);
            this.rightLeg.xRot = -(floatPI * 2F / 5F);
            this.leftLeg.xRot = -(floatPI * 2F / 5F);
            this.rightLeg.yRot = floatPI / 10F;
            this.leftLeg.yRot = -(floatPI / 10F);
        }

        this.rightArm.yRot = headYaw / Mth.RAD_TO_DEG;
        this.leftArm.yRot = headYaw / Mth.RAD_TO_DEG;
        float var7;
        float var8;

        if (this.attackTime > -9990.0F) {
            var7 = this.attackTime;
            this.spine.yRot = Mth.sin(Mth.sqrt(var7) * Mth.TWO_PI) * 0.2F;

            this.rightArm.z = Mth.sin(this.spine.yRot) * 5.0F;
            this.rightArm.x = -Mth.cos(this.spine.yRot) * 5.0F;
            this.leftArm.z = -Mth.sin(this.spine.yRot) * 5.0F;
            this.leftArm.x = Mth.cos(this.spine.yRot) * 5.0F;
            this.rightArm.yRot += this.spine.yRot;
            this.leftArm.yRot += this.spine.yRot;
            this.leftArm.xRot += this.spine.yRot;
            var7 = 1.0F - this.attackTime;
            var7 *= var7;
            var7 *= var7;
            var7 = 1.0F - var7;
            var8 = Mth.sin(var7 * floatPI);
            final float var9 = Mth.sin(this.attackTime * floatPI) * -(this.upperHead.xRot - 0.7F) * 0.75F;
            this.rightArm.xRot = (float) (this.rightArm.xRot - (var8 * 1.2D + var9));
            this.rightArm.yRot += this.spine.yRot * 2.0F;
            this.rightArm.zRot = Mth.sin(this.attackTime * floatPI) * -0.4F;
        }

        final float f6 = Mth.sin(this.attackTime * floatPI);
        final float f7 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * floatPI);
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightArm.yRot = 0.5F * (headYaw / Mth.RAD_TO_DEG) + -(0.1F - f6 * 0.6F);
        this.leftArm.yRot = 0.5F * (headYaw / Mth.RAD_TO_DEG) + 0.1F - f6 * 0.6F;
        this.rightArm.xRot = -(floatPI / 2F);
        this.leftArm.xRot = -(floatPI / 2F);
        this.rightArm.xRot -= f6 * 1.2F - f7 * 0.4F;
        this.leftArm.xRot -= f6 * 1.2F - f7 * 0.4F;
        this.rightArm.zRot += Mth.cos(animationProgress * 0.09F) * 0.05F + 0.05F;
        this.leftArm.zRot -= Mth.cos(animationProgress * 0.09F) * 0.05F + 0.05F;
        this.rightArm.xRot += Mth.sin(animationProgress * 0.067F) * 0.05F;
        this.leftArm.xRot -= Mth.sin(animationProgress * 0.067F) * 0.05F;

        if (boss.deathTime > 0) {
            this.leftArm.xRot = -(floatPI / 2F) + (float) (Math.pow(boss.deathTime, 2) / 5.0F) / 3.0F / Mth.RAD_TO_DEG;
            this.rightArm.xRot = -(floatPI / 2F) + (float) (Math.pow(boss.deathTime, 2) / 5.0F) / 2.0F / Mth.RAD_TO_DEG;
        }

        if (boss.throwTimer + boss.postThrowDelay > 0) {
            this.rightArm.xRot -= Mth.cos((boss.throwTimer + boss.postThrowDelay) * 0.05F) * 1.2F + 0.05F;
            this.leftArm.xRot -= Mth.cos((boss.throwTimer + boss.postThrowDelay) * 0.05F) * 1.2F + 0.05F;
        }
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack matrices) {
        this.getArm(arm).translateAndRotate(matrices);
    }

    protected ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }
}
