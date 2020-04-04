/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.client.model.entity.evolvedzombie;

import com.hrznstudio.galacticraft.entity.evolvedzombie.EvolvedZombieEntity;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.lwjgl.opengl.GL11;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
public class EvolvedZombieModel<EVOLVEDZOMBIE_ENTITY> extends BipedEntityModel<EvolvedZombieEntity> {

	private boolean renderGear;

	Cuboid oxygenTank0;
	Cuboid oxygenTank1;
	Cuboid oxygenTankWire0;
	Cuboid oxygenTankWire1;
	Cuboid oxygenTankWire2;
	Cuboid oxygenTankWire3;
	Cuboid oxygenTankWire4;
	Cuboid oxygenTankWire5;
	Cuboid oxygenMask;

	public EvolvedZombieModel(float scale, int textureWidth, int textureHeight) {
		super(scale, scale, textureWidth, textureHeight);

		renderGear = true;

		this.oxygenTankWire0.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTank0.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTank1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenMask.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.oxygenTankWire0 = new Cuboid(this, 0, 0);
		this.oxygenTankWire0.boxes.add(new Box(this.oxygenTankWire0, 44, 10, 2.0F, 3.0F, 5.0F, 1, 2, 1, scale, false));
		this.oxygenTankWire1 = new Cuboid(this, 0, 0);
		this.oxygenTankWire1.boxes.add(new Box(this.oxygenTankWire1, 44, 10, 2.0F, -1.0F, 6.0F, 1, 5, 1, scale, false));
		this.oxygenTankWire2 = new Cuboid(this, 0, 0);
		this.oxygenTankWire2.boxes.add(new Box(this.oxygenTankWire2, 44, 10, 2.0F, -2.0F, 5.0F, 1, 2, 1, scale, false));
		this.oxygenTankWire3 = new Cuboid(this, 0, 0);
		this.oxygenTankWire3.boxes.add(new Box(this.oxygenTankWire3, 44, 10, -3.0F, -1.0F, 6.0F, 1, 5, 1, scale, false));
		this.oxygenTankWire4 = new Cuboid(this, 0, 0);
		this.oxygenTankWire4.boxes.add(new Box(this.oxygenTankWire4, 44, 10, -3.0F, 3.0F, 5.0F, 1, 2, 1, scale, false));
		this.oxygenTankWire5 = new Cuboid(this, 0, 0);
		this.oxygenTankWire5.boxes.add(new Box(this.oxygenTankWire5, 44, 10, 2.0F, -2.0F, 5.0F, 1, 2, 1, scale, false));
		this.oxygenTank0 = new Cuboid(this, 0, 0);
		this.oxygenTank0.boxes.add(new Box(this.oxygenTank0, 32, 6, 1.0F, 2.0F, 2.0F, 3, 7, 3, scale, false));
		this.oxygenTank1 = new Cuboid(this, 0,0);
		this.oxygenTank1.boxes.add(new Box(this.oxygenTank1, 32, 6, -4.0F, 2.0F, 2.0F, 3, 7, 3, scale, false));
		this.oxygenMask = new Cuboid(this, 0, 0);
		this.oxygenMask.boxes.add(new Box(this.oxygenMask, 0, 32, -5.0F, -9.0F, -5.0F, 10, 10, 10, scale, false));
		this.body.addChild(oxygenTankWire0);
		this.body.addChild(oxygenTankWire1);
		this.body.addChild(oxygenTankWire2);
		this.body.addChild(oxygenTankWire3);
		this.body.addChild(oxygenTankWire4);
		this.body.addChild(oxygenTankWire5);
		this.body.addChild(oxygenTank0);
		this.body.addChild(oxygenTank1);
		this.body.addChild(oxygenMask);

		this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body.boxes.clear();
		this.body.boxes.add(new Box(this.body, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false));

		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.boxes.clear();
		this.body.addChild(this.head);
		this.head.boxes.add(new Box(this.head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false));

		this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.rightArm.boxes.clear();
		this.body.addChild(this.rightArm);
		this.rightArm.boxes.add(new Box(this.rightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, scale, false));

		this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.leftArm.boxes.clear();
		this.body.addChild(this.leftArm);
		this.leftArm.boxes.add(new Box(this.leftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, scale, true));

		this.rightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.rightLeg.boxes.clear();
		this.body.addChild(this.rightLeg);
		this.rightLeg.boxes.add(new Box(this.rightLeg, 0, 16, -2.1F, 0.0F, -2.0F, 4, 12, 4, scale, false));

		this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.leftLeg.boxes.clear();
		this.body.addChild(this.leftLeg);
		this.leftLeg.boxes.add(new Box(this.leftLeg, 0, 16, -1.9F, 0.0F, -2.0F, 4, 12, 4, scale, true));
	}

	@Override
	public void render(EvolvedZombieEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		//super.render(entity, f, f1, f2, f3, f4, f5);

		if (this.isChild) {
			float f6 = 2.0F;
			GL11.glPushMatrix();
			GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
			GL11.glTranslatef(0.0F, 16.0F * f5, 0.0F);
			this.head.render(f5);
			if (this.renderGear) {
				this.oxygenMask.render(f5);
			}
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F);
			if (this.renderGear) {
				this.oxygenTank0.render(f5);
				this.oxygenTank1.render(f5);

			}
			this.body.render(f5);
			this.rightArm.render(f5);
			this.leftArm.render(f5);
			this.rightLeg.render(f5);
			this.leftLeg.render(f5);
			this.head.render(f5);
			GL11.glPopMatrix();
		} else {
			if (this.renderGear) {
				this.oxygenTankWire0.render(f5);
				this.oxygenTankWire1.render(f5);
				this.oxygenTankWire2.render(f5);
				this.oxygenTankWire3.render(f5);
				this.oxygenTankWire4.render(f5);
				this.oxygenTankWire5.render(f5);
				this.oxygenTank0.render(f5);
				this.oxygenTank1.render(f5);
				this.oxygenMask.render(f5);
			}
			this.head.render(f5);
			this.body.render(f5);
			this.rightArm.render(f5);
			this.leftArm.render(f5);
			this.rightLeg.render(f5);
			this.leftLeg.render(f5);
			this.headwear.render(f5);
		}
	}
}
