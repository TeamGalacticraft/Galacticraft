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

public class GazerEntityModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private final ModelPart Body;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart head;

	public GazerEntityModel(ModelPart root) {
		this.Body = root.getChild("Body");
		this.leftArm = root.getChild("Left_Arm");
		this.rightArm = root.getChild("Right_Arm");
		this.leftLeg = root.getChild("Left_leg");
		this.rightLeg = root.getChild("Right_leg");
		this.head = root.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(82, 32).mirror().addBox(-4.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(87, 71).addBox(0.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Left_Arm = partdefinition.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(54, 59).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(58, 39).mirror().addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 13.0F, 3.0F));

		PartDefinition Right_Arm = partdefinition.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(54, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(58, 39).addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 13.0F, 3.0F));

		PartDefinition Left_leg = partdefinition.addOrReplaceChild("Left_leg", CubeListBuilder.create().texOffs(58, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-2.0F, 18.0F, 1.0F));

		PartDefinition Right_leg = partdefinition.addOrReplaceChild("Right_leg", CubeListBuilder.create().texOffs(58, 0).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(2.0F, 18.0F, 2.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 39).addBox(-11.0F, -6.01F, -7.0F, 22.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 1.0F));

		PartDefinition Mouth = Head.addOrReplaceChild("Mouth", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -23.0F, -14.0F, 22.0F, 25.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(32, 59).addBox(-3.0F, -8.0F, -19.0F, 6.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 7.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
		Body.render(poseStack, vertices, light, overlay);
		leftArm.render(poseStack, vertices, light, overlay);
		rightArm.render(poseStack, vertices, light, overlay);
		leftLeg.render(poseStack, vertices, light, overlay);
		rightLeg.render(poseStack, vertices, light, overlay);
		head.render(poseStack, vertices, light, overlay);
	}
}