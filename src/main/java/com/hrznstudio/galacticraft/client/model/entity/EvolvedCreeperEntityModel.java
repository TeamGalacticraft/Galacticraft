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

import com.hrznstudio.galacticraft.entity.EvolvedCreeperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
@Environment(EnvType.CLIENT)
public class EvolvedCreeperEntityModel extends EntityModel<EvolvedCreeperEntity> {
    public final ModelPart body;
    public final ModelPart creeperArmor;
    public final ModelPart head;
    public final ModelPart leg1;
    public final ModelPart leg4;
    public final ModelPart leg2;
    public final ModelPart leg3;
    public final ModelPart oxygenMask;
    public final ModelPart oxygenTank1;
    public final ModelPart oxygenTankPipe1;

    public EvolvedCreeperEntityModel(float scale) {
        super();
        this.texWidth = 128;
        this.texHeight = 64;
        this.body = new ModelPart(this, 16, 16);
        this.body.setPos(0.0F, 6.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
        this.oxygenMask = new ModelPart(this, 0, 44);
        this.oxygenMask.setPos(0.0F, 6.0F, 0.0F);
        this.oxygenMask.addBox(-5.0F, -9.0F, -5.0F, 10, 10, 10, scale);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.setPos(-2.0F, 18.0F, -4.0F);
        this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.setPos(-2.0F, 18.0F, 4.0F);
        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale);
        this.oxygenTank1 = new ModelPart(this, 0, 34);
        this.oxygenTank1.setPos(0.0F, 6.0F, 0.0F);
        this.oxygenTank1.addBox(-4.0F, 1.0F, 2.0F, 8, 6, 4, scale);
        this.creeperArmor = new ModelPart(this, 32, 0);
        this.creeperArmor.setPos(0.0F, 6.0F, 0.0F);
        this.creeperArmor.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        this.head = new ModelPart(this, 0, 0);
        this.head.setPos(0.0F, 6.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale);
        this.oxygenTankPipe1 = new ModelPart(this, 40, 51);
        this.oxygenTankPipe1.setPos(0.0F, 2.0F, 0.0F);
        this.oxygenTankPipe1.addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, scale);
        this.leg4 = new ModelPart(this, 0, 16);
        this.leg4.setPos(2.0F, 18.0F, -4.0F);
        this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.setPos(2.0F, 18.0F, 4.0F);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale);
        this.oxygenTank1.addChild(this.oxygenTankPipe1);
    }

    @Override
    public void setAngles(EvolvedCreeperEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        this.oxygenMask.yRot = (float) (headYaw / (180.0F / Math.PI));
        this.oxygenMask.xRot = (float) (headPitch / (180.0F / Math.PI));
        this.head.yRot = (float) (headYaw / (180.0F / Math.PI));
        this.head.xRot = (float) (headPitch / (180.0F / Math.PI));

        this.leg1.xRot = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leg2.xRot = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.leg3.xRot = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.leg4.xRot = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.young) {
            this.body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.oxygenMask.render(matrices, vertexConsumer, light, overlay);
            this.leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.oxygenTank1.render(matrices, vertexConsumer, light, overlay);
            this.creeperArmor.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        } else {
            matrices.pushPose();
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(0.0F, 1.0F, 0.0F);
            this.head.render(matrices, vertexConsumer, light, overlay);
            this.oxygenMask.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.popPose();
            matrices.pushPose();
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0F, 1.52083F, 0.0F);
            this.body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.oxygenTank1.render(matrices, vertexConsumer, light, overlay);
            this.creeperArmor.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg4.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leg2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.popPose();
        }
    }
}
