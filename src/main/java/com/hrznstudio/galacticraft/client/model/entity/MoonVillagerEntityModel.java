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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

import java.util.Collections;

public class MoonVillagerEntityModel extends VillagerResemblingModel<MoonVillagerEntity> {
    private final ModelPart brain;

    public MoonVillagerEntityModel(float scale, int textureWidth, int textureHeight) {
        super(scale, textureWidth, textureHeight);
        this.field_17141.visible = false; // set invisible
        this.field_17141 = new ModelPart(this);

        this.brain = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.brain.setPivot(0.0F, 0.0F, 0.0F);
        this.brain.setTextureOffset(0, 38).addCuboid(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F, scale, false);
        //                                                           .addCuboid    -4.0F,    -10.0F,     -4.0F,       8.0F,        10.0F,      8.0F, scale)
    }

    public MoonVillagerEntityModel(float v) {
        this(v, 64, 64);
    }

    @Override
    public void setAngles(MoonVillagerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.brain.visible = head.visible;
        this.brain.yaw = this.head.yaw;
        this.brain.pitch = this.head.pitch;
        this.brain.pivotX = this.head.pivotX;
        this.brain.pivotY = this.head.pivotY;
        this.brain.pivotZ = this.head.pivotZ;
        this.brain.roll = this.head.roll;
//        this.brain.mirror = this.head.mirror;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return Iterables.concat(Collections.singletonList(brain), super.getParts());
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.head.visible = visible;
        this.field_17141.visible = false;
        this.field_17142.visible = false;
    }
}
