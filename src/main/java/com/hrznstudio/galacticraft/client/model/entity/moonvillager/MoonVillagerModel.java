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

package com.hrznstudio.galacticraft.client.model.entity.moonvillager;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.entity.moonvillager.MoonVillagerEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
public class MoonVillagerModel extends VillagerResemblingModel<MoonVillagerEntity> {
    public ModelPart brain;

    public MoonVillagerModel(float scale) {
        this(scale, 64, 64);
    }

    public MoonVillagerModel(float scale, int texWidth, int texHeight) {
        super(scale, texWidth, texHeight);
        this.head = new ModelPart(this).setTextureSize(texWidth, texHeight);
        this.head.setPivot(0.0F, 0.0F, 0.0F);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8, scale + 0.001F);

        this.nose.setPivot(0.0F, 2.0F, 0.0F);
        this.nose.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2, 4, 2, scale + 0.002F);
        this.head.addChild(this.nose);

        this.torso.setPivot(0.0F, 0.0F, 0.0F);
        this.torso.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8, 12, 6, scale + 0.003F);
        this.torso.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8, 18, 6, scale + 0.5F + 0.004F);

        this.arms.setPivot(0.0F, 2.0F, 0.0F);
        this.arms.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4, 8, 4, scale + 0.005F);
        this.arms.setTextureOffset(44, 22).addCuboid(4.0F, -2.0F, -2.0F, 4, 8, 4, scale + 0.0001F);
        this.arms.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8, 4, 4, scale + 0.0004F);

        this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
        this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.0006F);

        this.leftLeg.mirror = true;
        this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
        this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.0002F);

        this.brain = new ModelPart(this).setTextureSize(texWidth, texHeight);
        this.brain.setPivot(0.0F, 0.0F, 0.0F);
        this.brain.setTextureOffset(32, 0).addCuboid(-4.0F, -16.0F, -4.0F, 8, 8, 8, scale + 0.5F);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.brain, this.torso, this.rightLeg, this.leftLeg, this.arms);
    }

    @Override
    public void setAngles(MoonVillagerEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, customAngle, headYaw, headPitch);

        this.brain.pivotX = this.head.pivotX;
        this.brain.pivotY = this.head.pivotY;
        this.brain.pivotZ = this.head.pivotZ;

    }

    @Override
    public void setHatVisible(boolean visible) {
//        this.head.visible = visible;
//        this.field_17141.visible = visible;
//        this.field_17142.visible = visible;
    }
}