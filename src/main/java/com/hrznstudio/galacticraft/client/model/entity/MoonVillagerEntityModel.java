/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.model.entity;

import com.google.common.collect.Iterables;
import com.hrznstudio.galacticraft.entity.MoonVillagerEntity;
import java.util.Collections;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;

public class MoonVillagerEntityModel extends VillagerModel<MoonVillagerEntity> {
    private final ModelPart brain;

    public MoonVillagerEntityModel(float scale, int textureWidth, int textureHeight) {
        super(scale, textureWidth, textureHeight);
        this.hat.visible = false; // set invisible
        this.hat = new ModelPart(this);

        this.brain = new ModelPart(this).setTexSize(textureWidth, textureHeight);
        this.brain.setPos(0.0F, 0.0F, 0.0F);
        this.brain.texOffs(0, 38).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F, scale, false);
        //                                                           .addCuboid    -4.0F,    -10.0F,     -4.0F,       8.0F,        10.0F,      8.0F, scale)
    }

    public MoonVillagerEntityModel(float v) {
        this(v, 64, 64);
    }

    @Override
    public void setAngles(MoonVillagerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.brain.visible = head.visible;
        this.brain.yRot = this.head.yRot;
        this.brain.xRot = this.head.xRot;
        this.brain.x = this.head.x;
        this.brain.y = this.head.y;
        this.brain.z = this.head.z;
        this.brain.zRot = this.head.zRot;
//        this.brain.mirror = this.head.mirror;
    }

    @Override
    public Iterable<ModelPart> parts() {
        return Iterables.concat(Collections.singletonList(brain), super.parts());
    }

    @Override
    public void hatVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = false;
        this.hatRim.visible = false;
    }
}
