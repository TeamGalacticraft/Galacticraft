// Made with Blockbench 3.6.6
	// Exported for Minecraft version 1.15
	// Paste this class into your mod and generate all required imports

package com.hrznstudio.galacticraft.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.entity.EvolvedPillagerEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class EvolvedPillagerEntityModel extends CompositeEntityModel<EvolvedPillagerEntity> implements ModelWithArms, ModelWithHead {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart torso;
	private final ModelPart arms;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart rightAttackingArm;
	private final ModelPart leftAttackingArm;
	private final ModelPart mask;
	private final ModelPart tank;

	public EvolvedPillagerEntityModel() {
		textureWidth = 64;
		textureHeight = 64;
		head = new ModelPart(this);
		head.setPivot(0.0F, 0.0F, 0.0F);
		head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);

		hat = new ModelPart(this);
		hat.setPivot(0.0F, -2.0F, 0.0F);
		head.addChild(hat);
		hat.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
		this.hat.visible = false;

		torso = new ModelPart(this);
		torso.setPivot(-5.0F, 2.0F, 0.0F);
		torso.setTextureOffset(16, 36).addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		arms = new ModelPart(this);
		arms.setPivot(-2.0F, 12.0F, 0.0F);
		arms.setTextureOffset(0, 36).addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		rightLeg = new ModelPart(this);
		rightLeg.setPivot(2.0F, 12.0F, 0.0F);
		rightLeg.setTextureOffset(0, 36).addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

		leftLeg = new ModelPart(this);
		leftLeg.setPivot(5.0F, 2.0F, 0.0F);
		leftLeg.setTextureOffset(16, 36).addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

		rightAttackingArm = new ModelPart(this);
		rightAttackingArm.setPivot(0.0F, 0.0F, 0.0F);
		rightAttackingArm.setTextureOffset(0, 18).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, false);

		leftAttackingArm = new ModelPart(this);
		leftAttackingArm.setPivot(0.0F, 0.0F, 0.0F);
		leftAttackingArm.setTextureOffset(36, 26).addCuboid(-4.0F, 10.0F, -3.0F, 8.0F, 8.0F, 6.0F, 0.5F, false);

		mask = new ModelPart(this);
		mask.setPivot(0.0F, -24.0F, 0.0F);
		mask.setTextureOffset(21, 41).addCuboid(-5.0F, -10.75F, -6.0F, 10.0F, 12.0F, 11.0F, 0.0F, false);

		tank = new ModelPart(this);
		tank.setPivot(0.0F, -24.0F, 0.0F);
		tank.setTextureOffset(40, 16).addCuboid(-4.0F, 1.0F, 3.0F, 8.0F, 6.0F, 4.0F, 0.0F, false);

		ModelPart pipe = new ModelPart(this);
		pipe.setPivot(0.0F, 2.0F, 0.0F);
		tank.addChild(pipe);
		pipe.setTextureOffset(40, 3).addCuboid(-2.0F, -4.0F, 1.0F, 4.0F, 5.0F, 8.0F, 0.0F, false);
	}

	@Override
	public void setAngles(EvolvedPillagerEntity entity, float f, float g, float h, float i, float j) {
		this.mask.yaw = this.head.yaw = i * 0.017453292F;
		this.mask.pitch = this.head.pitch = j * 0.017453292F;
		this.arms.pivotY = 3.0F;
		this.arms.pivotZ = -1.0F;
		this.arms.pitch = -0.75F;
		if (this.riding) {
			this.rightAttackingArm.pitch = -0.62831855F;
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = -0.62831855F;
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = 0.31415927F;
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = -0.31415927F;
			this.leftLeg.roll = -0.07853982F;
		} else {
			this.rightAttackingArm.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F;
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.rightLeg.yaw = 0.0F;
			this.rightLeg.roll = 0.0F;
			this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.5F;
			this.leftLeg.yaw = 0.0F;
			this.leftLeg.roll = 0.0F;
		}

		IllagerEntity.State state = entity.getState();
		if (state == IllagerEntity.State.ATTACKING) {
			if (entity.getMainHandStack().isEmpty()) {
				CrossbowPosing.method_29352(this.leftAttackingArm, this.rightAttackingArm, true, this.handSwingProgress, h);
			} else {
				CrossbowPosing.method_29351(this.rightAttackingArm, this.leftAttackingArm, entity, this.handSwingProgress, h);
			}
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.rightAttackingArm.roll = 2.3561945F;
			this.leftAttackingArm.roll = -2.3561945F;
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.rightAttackingArm.yaw = -0.1F + this.head.yaw;
			this.rightAttackingArm.pitch = -1.5707964F + this.head.pitch;
			this.leftAttackingArm.pitch = -0.9424779F + this.head.pitch;
			this.leftAttackingArm.yaw = this.head.yaw - 0.4F;
			this.leftAttackingArm.roll = 1.5707964F;
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			CrossbowPosing.hold(this.rightAttackingArm, this.leftAttackingArm, this.head, true);
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			CrossbowPosing.charge(this.rightAttackingArm, this.leftAttackingArm, entity, true);
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.rightAttackingArm.roll = 2.670354F;
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.leftAttackingArm.roll = -2.3561945F;
			this.leftAttackingArm.yaw = 0.0F;
		}

		boolean bl = state == IllagerEntity.State.CROSSED;
		this.arms.visible = bl;
		this.leftAttackingArm.visible = !bl;
		this.rightAttackingArm.visible = !bl;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.of(this.head, this.mask, this.torso, this.tank, this.rightLeg, this.leftLeg, this.arms, this.rightAttackingArm, this.leftAttackingArm);
	}

	private ModelPart getArmModel(Arm arm) {
		return arm == Arm.LEFT ? this.leftAttackingArm : this.rightAttackingArm;
	}

	public ModelPart getHat() {
		return this.hat;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getArmModel(arm).rotate(matrices);
	}
}