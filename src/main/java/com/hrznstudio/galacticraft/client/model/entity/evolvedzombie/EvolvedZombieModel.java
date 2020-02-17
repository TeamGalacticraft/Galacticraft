/*
 * Copyright (c) 2020 HRZN LTD
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
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.model.ZombieEntityModel;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
public class EvolvedZombieModel<EVOLVEDZOMBIE_ENTITY> extends ZombieEntityModel<EvolvedZombieEntity> {

	/**
	 * Work in Progress. This model should work, but it refuses to show up :(
	 */

	private final Model baseModel;

	public EvolvedZombieModel(float scale, int textureWidth, int textureHeight) {
		super(scale, scale, textureWidth, textureHeight);
		this.baseModel = new Model();

		this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body.boxes.clear();
		this.body.boxes.add(new Box(this.body, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 32, 6, -4.0F, 2.0F, 2.0F, 3, 7, 3, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 32, 6, 1.0F, 2.0F, 2.0F, 3, 7, 3, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, -3.0F, 3.0F, 5.0F, 1, 2, 1, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, 2.0F, 3.0F, 5.0F, 1, 2, 1, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, 2.0F, -1.0F, 6.0F, 1, 5, 1, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, -3.0F, -1.0F, 6.0F, 1, 5, 1, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, -3.0F, -2.0F, 5.0F, 1, 2, 1, 0.0F, false));
		this.body.boxes.add(new Box(this.body, 44, 10, 2.0F, -2.0F, 5.0F, 1, 2, 1, 0.0F, false));

		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.boxes.clear();
		this.body.addChild(this.head);
		this.head.boxes.add(new Box(this.head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		this.head.boxes.add(new Box(this.head, 0, 32, -5.0F, -9.0F, -5.0F, 10, 10, 10, 0.0F, false));

		this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.rightArm.boxes.clear();
		this.body.addChild(this.rightArm);
		this.rightArm.boxes.add(new Box(this.rightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		//rightItem.setRotationPoint(-1.0F, 7.0F, 1.0F);
		//rightArm.addChild(rightItem);

		//this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.leftArm.boxes.clear();
		//this.body.addChild(this.leftArm);
		//this.leftArm.boxes.add(new Box(this.leftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

		this.rightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.rightLeg.boxes.clear();
		this.body.addChild(this.rightLeg);
		this.rightLeg.boxes.add(new Box(this.rightLeg, 0, 16, -2.1F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.leftLeg.boxes.clear();
		this.body.addChild(this.leftLeg);
		this.leftLeg.boxes.add(new Box(this.leftLeg, 0, 16, -1.9F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
	}

	@Override
	public void render(EvolvedZombieEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
	}
}
