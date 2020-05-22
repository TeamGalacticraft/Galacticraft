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

package com.hrznstudio.galacticraft.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.entity.EvolvedZombieEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
@Environment(EnvType.CLIENT)
public class EvolvedZombieModel extends BipedEntityModel<EvolvedZombieEntity> {
    private final ModelPart oxygenTank0;
    private final ModelPart oxygenTank1;
    private final ModelPart oxygenTankWire0;
    private final ModelPart oxygenTankWire1;
    private final ModelPart oxygenTankWire2;
    private final ModelPart oxygenTankWire3;
    private final ModelPart oxygenTankWire4;
    private final ModelPart oxygenTankWire5;
    private final ModelPart oxygenMask;

    public EvolvedZombieModel(float scale, int textureWidth, int textureHeight) {
        super(scale, scale, textureWidth, textureHeight);

        this.oxygenTankWire0 = new ModelPart(this, 44, 10);
        this.oxygenTankWire1 = new ModelPart(this, 44, 10);
        this.oxygenTankWire2 = new ModelPart(this, 44, 10);
        this.oxygenTankWire3 = new ModelPart(this, 44, 10);
        this.oxygenTankWire4 = new ModelPart(this, 44, 10);
        this.oxygenTankWire5 = new ModelPart(this, 44, 10);
        this.oxygenTank0 = new ModelPart(this, 32, 6);
        this.oxygenTank1 = new ModelPart(this, 32, 6);
        this.oxygenMask = new ModelPart(this, 0, 32);

        this.oxygenTankWire0.addCuboid(2.0F, 3.0F, 5.0F, 1, 2, 1, scale, false);
        this.oxygenTankWire1.addCuboid(2.0F, -1.0F, 6.0F, 1, 5, 1, scale, false);
        this.oxygenTankWire2.addCuboid(2.0F, -2.0F, 5.0F, 1, 2, 1, scale, false);
        this.oxygenTankWire3.addCuboid(-3.0F, -1.0F, 6.0F, 1, 5, 1, scale, false);
        this.oxygenTankWire4.addCuboid(-3.0F, 3.0F, 5.0F, 1, 2, 1, scale, false);
        this.oxygenTankWire5.addCuboid(2.0F, -2.0F, 5.0F, 1, 2, 1, scale, false);

        this.oxygenTank0.addCuboid(1.0F, 2.0F, 2.0F, 3, 7, 3, scale, false);
        this.oxygenTank1.addCuboid(-4.0F, 2.0F, 2.0F, 3, 7, 3, scale, false);
        this.oxygenMask.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, scale, false);

        this.torso = new ModelPart(this, 16, 16);

        this.torso.addChild(oxygenTankWire0);
        this.torso.addChild(oxygenTankWire1);
        this.torso.addChild(oxygenTankWire2);
        this.torso.addChild(oxygenTankWire3);
        this.torso.addChild(oxygenTankWire4);
        this.torso.addChild(oxygenTankWire5);
        this.torso.addChild(oxygenTank0);
        this.torso.addChild(oxygenTank1);
        this.torso.addChild(oxygenMask);
        this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false);

        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.torso.addChild(this.head);

        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
        this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale, false);
        this.torso.addChild(this.rightArm);

        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
        this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale, true);
        this.torso.addChild(this.leftArm);

        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
        this.rightLeg.addCuboid(-2.1F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.torso.addChild(this.rightLeg);

        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
        this.leftLeg.addCuboid(-1.9F, 0.0F, -2.0F, 4, 12, 4, scale, true);
        this.torso.addChild(this.leftLeg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.child) {
            super.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        } else {
            float ratio = 2.0F;
            matrices.push();
            matrices.scale(1.5F / ratio, 1.5F / ratio, 1.5F / ratio);
            matrices.translate(0.0F, 16.0F, 0.0F);
            this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.oxygenMask.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

            matrices.pop();
            matrices.push();
            matrices.scale(1.0F / ratio, 1.0F / ratio, 1.0F / ratio);
            matrices.translate(0.0F, 24.0F, 0.0F);
            this.oxygenTank0.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.oxygenTank1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

            this.torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leftArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.pop();
        }
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.helmet, this.oxygenTankWire0, this.oxygenTankWire1,
                this.oxygenTankWire2, this.oxygenTankWire3, this.oxygenTankWire4, this.oxygenTankWire5, this.oxygenMask, this.oxygenTank0, this.oxygenTank1
        );
    }
}
