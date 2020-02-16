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

package com.hrznstudio.galacticraft.entity.moonvillager;

import net.minecraft.client.model.Box;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
public class MoonVillagerModel<MOON_VILLAGER_ENTITY> extends VillagerResemblingModel<EntityMoonVillager> {

	private final Model baseModel;

	public MoonVillagerModel(int scale, int textureWidth, int textureHeight) {
		super(scale, textureWidth, textureHeight);
		this.baseModel = new Model();
		this.hat.visible = false;

		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.boxes.add(new Box(head, 0, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F, false));
		this.head.boxes.add(new Box(head, 0, 38, -5.0F, -16.0F, -5.0F, 10, 8, 10, 0.0F, false));

		this.nose.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.head.addChild(nose);
		this.nose.boxes.add(new Box(nose, 24, 0, -1.0F, -1.0F, -6.0F, 2, 4, 2, 0.0F, false));

		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		body.boxes.add(new Box(body, 16, 20, -4.0F, -24.0F, -3.0F, 8, 12, 6, 0.0F, false));

		arms.setRotationPoint(0.0F, 2.0F, 0.0F);
		arms.setRotationPoint(-0.7854F, 0.0F, 0.0F);
		arms.boxes.add(new Box(arms, 40, 38, -4.0F, 2.0F, -2.0F, 8, 4, 4, 0.0F, false));
		arms.boxes.add(new Box(arms, 44, 22, -8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, false));
		arms.boxes.add(new Box(arms, 44, 22, 4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, false));

		leftLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		leftLeg.boxes.add(new Box(leftLeg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		rightLeg.boxes.add(new Box(rightLeg, 0, 22, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
	}

	@Override
	public void render(EntityMoonVillager entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
	}
}