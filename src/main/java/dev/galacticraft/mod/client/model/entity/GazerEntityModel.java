/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.mod.content.entity.GazerEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

public class GazerEntityModel<T extends GazerEntity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private final ModelPart Body;
	private final ModelPart Left_Arm;
	private final ModelPart Right_Arm;
	private final ModelPart Left_leg;
	private final ModelPart Right_leg;
	private final ModelPart Head;

	public GazerEntityModel(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Left_Arm = root.getChild("Left_Arm");
		this.Right_Arm = root.getChild("Right_Arm");
		this.Left_leg = root.getChild("Left_leg");
		this.Right_leg = root.getChild("Right_leg");
		this.Head = root.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(82, 32).mirror().addBox(-4.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(87, 71).addBox(0.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		partdefinition.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(54, 59).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(58, 39).mirror().addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 13.0F, 3.0F));

		partdefinition.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(54, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(58, 39).addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 13.0F, 3.0F));

		partdefinition.addOrReplaceChild("Left_leg", CubeListBuilder.create().texOffs(58, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-2.0F, 18.0F, 1.0F));

		partdefinition.addOrReplaceChild("Right_leg", CubeListBuilder.create().texOffs(58, 0).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(2.0F, 18.0F, 2.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 39).addBox(-11.0F, -6.01F, -7.0F, 22.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 1.0F));

		head.addOrReplaceChild("Mouth", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -23.0F, -14.0F, 22.0F, 25.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(32, 59).addBox(-3.0F, -8.0F, -19.0F, 6.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 7.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		/*boolean bl = livingEntity.getFallFlyingTicks() > 4;
		boolean bl2 = livingEntity.isVisuallySwimming();
		this.Head.yRot = netHeadYaw * 0.017453292F;
		if (bl) {
			this.Head.xRot = -0.7853982F;
		}

			this.Head.xRot = headPitch * 0.017453292F;

		this.Body.yRot = 0.0F;
		this.Right_Arm.z = 0.0F;
		this.Right_Arm.x = -5.0F;
		this.Left_Arm.z = 0.0F;
		this.Left_Arm.x = 5.0F;
		float k = 1.0F;
		if (bl) {
			k = (float)livingEntity.getDeltaMovement().lengthSqr();
			k /= 0.2F;
			k *= k * k;
		}

		if (k < 1.0F) {
			k = 1.0F;
		}

		this.Right_Arm.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F / k;
		this.Left_Arm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / k;
		this.Right_Arm.zRot = 0.0F;
		this.Left_Arm.zRot = 0.0F;
		this.Right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / k;
		this.Left_leg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / k;
		this.Right_leg.yRot = 0.0F;
		this.Left_leg.yRot = 0.0F;
		this.Right_leg.zRot = 0.0F;
		this.Left_leg.zRot = 0.0F;

		this.Right_Arm.yRot = 0.0F;
		this.Left_Arm.yRot = 0.0F;
    	this.Body.xRot = 0.0F;
		this.Right_leg.z = 0.1F;
		this.Left_leg.z = 0.1F;
		this.Right_leg.y = 12.0F;
		this.Left_leg.y = 12.0F;
		this.Head.y = 0.0F;
		this.Body.y = 0.0F;
		this.Left_Arm.y = 2.0F;
		this.Right_Arm.y = 2.0F;
		AnimationUtils.animateZombieArms(this.Left_Arm, this.Right_Arm, false,10,ageInTicks);
*/
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Body.render(poseStack, buffer, packedLight, packedOverlay);
		Left_Arm.render(poseStack, buffer, packedLight, packedOverlay);
		Right_Arm.render(poseStack, buffer, packedLight, packedOverlay);
		Left_leg.render(poseStack, buffer, packedLight, packedOverlay);
		Right_leg.render(poseStack, buffer, packedLight, packedOverlay);
		Head.render(poseStack, buffer, packedLight, packedOverlay);
	}
}