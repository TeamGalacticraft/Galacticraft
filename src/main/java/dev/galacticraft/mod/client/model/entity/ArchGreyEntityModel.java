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

public class ArchGreyEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart Body;
	private final ModelPart Left_Arm;
	private final ModelPart Right_Arm;
	private final ModelPart Left_Leg;
	private final ModelPart Right_Leg;
	private final ModelPart Head;

	public ArchGreyEntityModel(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Left_Arm = root.getChild("Left_Arm");
		this.Right_Arm = root.getChild("Right_Arm");
		this.Left_Leg = root.getChild("Left_Leg");
		this.Right_Leg = root.getChild("Right_Leg");
		this.Head = root.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -15.0F, -2.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-3.0F, -7.0F, -2.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Left_Arm = partdefinition.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(24, 16).mirror().addBox(-2.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, 10.0F, 0.0F));

		PartDefinition Right_Arm = partdefinition.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(24, 16).addBox(0.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 0.0F));

		PartDefinition Left_Leg = partdefinition.addOrReplaceChild("Left_Leg", CubeListBuilder.create().texOffs(22, 29).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 17.0F, 1.0F));

		PartDefinition Right_Leg = partdefinition.addOrReplaceChild("Right_Leg", CubeListBuilder.create().texOffs(22, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 17.0F, 1.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 8).addBox(-4.0F, -14.0F, -3.0F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
		Body.render(poseStack, vertices, light, overlay);
		Left_Arm.render(poseStack, vertices, light, overlay);
		Right_Arm.render(poseStack, vertices, light, overlay);
		Left_Leg.render(poseStack, vertices, light, overlay);
		Right_Leg.render(poseStack, vertices, light, overlay);
		Head.render(poseStack, vertices, light, overlay);
	}
}