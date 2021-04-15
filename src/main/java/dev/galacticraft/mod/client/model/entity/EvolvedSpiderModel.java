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

package dev.galacticraft.mod.client.model.entity;

import dev.galacticraft.mod.entity.EvolvedSpiderEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class EvolvedSpiderModel<T extends EvolvedSpiderEntity> extends EntityModel<T> {
    public final ModelPart body;
    public final ModelPart rearEnd;
    public final ModelPart leg8;
    public final ModelPart leg6;
    public final ModelPart leg4;
    public final ModelPart leg2;
    public final ModelPart leg7;
    public final ModelPart leg5;
    public final ModelPart leg3;
    public final ModelPart leg1;
    public final ModelPart head;

    public final ModelPart oxygenMask;
    public final ModelPart tank1;
    public final ModelPart tank2;
    public final ModelPart tube1;
    public final ModelPart tube2;
    public final ModelPart tube3;
    public final ModelPart tube4;
    public final ModelPart tube5;
    public final ModelPart tube6;
    public final ModelPart tube7;
    public final ModelPart tube8;
    public final ModelPart tube9;
    public final ModelPart tube10;
    public final ModelPart tube11;
    public final ModelPart tube12;
    public final ModelPart tube13;
    public final ModelPart tube15;
    public final ModelPart tube14;
    public final ModelPart tube16;
    public final ModelPart tube17;
    public final ModelPart tube18;

    public EvolvedSpiderModel(float par1) {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body = new ModelPart(this, 0, 0);
        this.body.addCuboid(-3F, -3F, -3F, 6, 6, 6, par1);
        this.body.setPivot(0F, 15F, 0F);
        this.body.setTextureSize(128, 64);
        this.body.mirror = true;
        this.rearEnd = new ModelPart(this, 0, 12);
        this.rearEnd.addCuboid(-5F, -4F, -6F, 10, 8, 12, par1);
        this.rearEnd.setPivot(0F, 15F, 9F);
        this.rearEnd.setTextureSize(128, 64);
        this.rearEnd.mirror = true;
        this.leg8 = new ModelPart(this, 18, 0);
        this.leg8.addCuboid(-1F, -1F, -1F, 16, 2, 2, par1);
        this.leg8.setPivot(4F, 15F, -1F);
        this.leg8.setTextureSize(128, 64);
        this.leg8.mirror = true;
        this.setRotation(this.leg8, 0.5759587F, 0.1919862F);
        this.leg6 = new ModelPart(this, 18, 0);
        this.leg6.addCuboid(-1F, -1F, -1F, 16, 2, 2, par1);
        this.leg6.setPivot(4F, 15F, 0F);
        this.leg6.setTextureSize(128, 64);
        this.leg6.mirror = true;
        this.setRotation(this.leg6, 0.2792527F, 0.1919862F);
        this.leg4 = new ModelPart(this, 18, 0);
        this.leg4.addCuboid(-1F, -1F, -1F, 16, 2, 2, par1);
        this.leg4.setPivot(4F, 15F, 1F);
        this.leg4.setTextureSize(128, 64);
        this.leg4.mirror = true;
        this.setRotation(this.leg4, -0.2792527F, 0.1919862F);
        this.leg2 = new ModelPart(this, 18, 0);
        this.leg2.addCuboid(-1F, -1F, -1F, 16, 2, 2, par1);
        this.leg2.setPivot(4F, 15F, 2F);
        this.leg2.setTextureSize(128, 64);
        this.leg2.mirror = true;
        this.setRotation(this.leg2, -0.5759587F, 0.1919862F);
        this.leg7 = new ModelPart(this, 18, 0);
        this.leg7.addCuboid(-15F, -1F, -1F, 16, 2, 2, par1);
        this.leg7.setPivot(-4F, 15F, -1F);
        this.leg7.setTextureSize(128, 64);
        this.leg7.mirror = true;
        this.setRotation(this.leg7, -0.5759587F, -0.1919862F);
        this.leg5 = new ModelPart(this, 18, 0);
        this.leg5.addCuboid(-15F, -1F, -1F, 16, 2, 2, par1);
        this.leg5.setPivot(-4F, 15F, 0F);
        this.leg5.setTextureSize(128, 64);
        this.leg5.mirror = true;
        this.setRotation(this.leg5, -0.2792527F, -0.1919862F);
        this.leg3 = new ModelPart(this, 18, 0);
        this.leg3.addCuboid(-15F, -1F, -1F, 16, 2, 2, par1);
        this.leg3.setPivot(-4F, 15F, 1F);
        this.leg3.setTextureSize(128, 64);
        this.leg3.mirror = true;
        this.setRotation(this.leg3, 0.2792527F, -0.1919862F);
        this.leg1 = new ModelPart(this, 18, 0);
        this.leg1.addCuboid(-15F, -1F, -1F, 16, 2, 2, par1);
        this.leg1.setPivot(-4F, 15F, 2F);
        this.leg1.setTextureSize(128, 64);
        this.leg1.mirror = true;
        this.setRotation(this.leg1, 0.5759587F, -0.1919862F);
        this.head = new ModelPart(this, 32, 4);
        this.head.addCuboid(-4F, -4F, -8F, 8, 8, 8, par1);
        this.head.setPivot(0F, 15F, -3F);
        this.head.setTextureSize(128, 64);
        this.head.mirror = true;
        this.oxygenMask = new ModelPart(this, 0, 32);
        this.oxygenMask.addCuboid(-5F, -5F, -9F, 10, 10, 10, par1);
        this.oxygenMask.setPivot(0F, 15F, -3F);
        this.oxygenMask.setTextureSize(128, 64);
        this.oxygenMask.mirror = true;
        this.tank1 = new ModelPart(this, 40, 34);
        this.tank1.addCuboid(1F, -3F, 0F, 3, 3, 7, par1);
        this.tank1.setPivot(0F, 11F, 4F);
        this.tank1.setTextureSize(128, 64);
        this.tank1.mirror = true;
        this.tank2 = new ModelPart(this, 40, 34);
        this.tank2.addCuboid(-4F, -3F, 0F, 3, 3, 7, par1);
        this.tank2.setPivot(0F, 11F, 4F);
        this.tank2.setTextureSize(128, 64);
        this.tank2.mirror = true;
        this.tube1 = new ModelPart(this, 40, 32);
        this.tube1.addCuboid(2F, 0F, -6.5F, 1, 1, 1, par1);
        this.tube1.setPivot(0F, 11F, 4F);
        this.tube1.setTextureSize(128, 64);
        this.tube1.mirror = true;
        this.tube2 = new ModelPart(this, 40, 32);
        this.tube2.addCuboid(2F, -1F, -5.5F, 1, 1, 1, par1);
        this.tube2.setPivot(0F, 11F, 4F);
        this.tube2.setTextureSize(128, 64);
        this.tube2.mirror = true;
        this.tube3 = new ModelPart(this, 40, 32);
        this.tube3.addCuboid(2F, -1F, -4.5F, 1, 1, 1, par1);
        this.tube3.setPivot(0F, 11F, 4F);
        this.tube3.setTextureSize(128, 64);
        this.tube3.mirror = true;
        this.tube4 = new ModelPart(this, 40, 32);
        this.tube4.addCuboid(2F, -2F, -3.5F, 1, 1, 1, par1);
        this.tube4.setPivot(0F, 11F, 4F);
        this.tube4.setTextureSize(128, 64);
        this.tube4.mirror = true;
        this.tube5 = new ModelPart(this, 40, 32);
        this.tube5.addCuboid(2F, -3F, -2.5F, 1, 1, 1, par1);
        this.tube5.setPivot(0F, 11F, 4F);
        this.tube5.setTextureSize(128, 64);
        this.tube5.mirror = true;
        this.tube6 = new ModelPart(this, 40, 32);
        this.tube6.addCuboid(2F, -4F, -2.5F, 1, 1, 1, par1);
        this.tube6.setPivot(0F, 11F, 4F);
        this.tube6.setTextureSize(128, 64);
        this.tube6.mirror = true;
        this.tube7 = new ModelPart(this, 40, 32);
        this.tube7.addCuboid(2F, -5F, -1.5F, 1, 1, 1, par1);
        this.tube7.setPivot(0F, 11F, 4F);
        this.tube7.setTextureSize(128, 64);
        this.tube7.mirror = true;
        this.tube8 = new ModelPart(this, 40, 32);
        this.tube8.addCuboid(2F, -5F, -0.5F, 1, 1, 1, par1);
        this.tube8.setPivot(0F, 11F, 4F);
        this.tube8.setTextureSize(128, 64);
        this.tube8.mirror = true;
        this.tube9 = new ModelPart(this, 40, 32);
        this.tube9.addCuboid(2F, -4F, 0.5F, 1, 1, 1, par1);
        this.tube9.setPivot(0F, 11F, 4F);
        this.tube9.setTextureSize(128, 64);
        this.tube9.mirror = true;
        this.tube10 = new ModelPart(this, 40, 32);
        this.tube10.addCuboid(-3F, 0F, -6.5F, 1, 1, 1, par1);
        this.tube10.setPivot(0F, 11F, 4F);
        this.tube10.setTextureSize(128, 64);
        this.tube10.mirror = true;
        this.tube11 = new ModelPart(this, 40, 32);
        this.tube11.addCuboid(-3F, -1F, -5.5F, 1, 1, 1, par1);
        this.tube11.setPivot(0F, 11F, 4F);
        this.tube11.setTextureSize(128, 64);
        this.tube11.mirror = true;
        this.tube12 = new ModelPart(this, 40, 32);
        this.tube12.addCuboid(-3F, -1F, -4.5F, 1, 1, 1, par1);
        this.tube12.setPivot(0F, 11F, 4F);
        this.tube12.setTextureSize(128, 64);
        this.tube12.mirror = true;
        this.tube13 = new ModelPart(this, 40, 32);
        this.tube13.addCuboid(-3F, -2F, -3.5F, 1, 1, 1, par1);
        this.tube13.setPivot(0F, 11F, 4F);
        this.tube13.setTextureSize(128, 64);
        this.tube13.mirror = true;
        this.tube15 = new ModelPart(this, 40, 32);
        this.tube15.addCuboid(-3F, -4F, -2.5F, 1, 1, 1, par1);
        this.tube15.setPivot(0F, 11F, 4F);
        this.tube15.setTextureSize(128, 64);
        this.tube15.mirror = true;
        this.tube14 = new ModelPart(this, 40, 32);
        this.tube14.addCuboid(-3F, -3F, -2.5F, 1, 1, 1, par1);
        this.tube14.setPivot(0F, 11F, 4F);
        this.tube14.setTextureSize(128, 64);
        this.tube14.mirror = true;
        this.tube16 = new ModelPart(this, 40, 32);
        this.tube16.addCuboid(-3F, -5F, -1.5F, 1, 1, 1, par1);
        this.tube16.setPivot(0F, 11F, 4F);
        this.tube16.setTextureSize(128, 64);
        this.tube16.mirror = true;
        this.tube17 = new ModelPart(this, 40, 32);
        this.tube17.addCuboid(-3F, -5F, -0.5F, 1, 1, 1, par1);
        this.tube17.setPivot(0F, 11F, 4F);
        this.tube17.setTextureSize(128, 64);
        this.tube17.mirror = true;
        this.tube18 = new ModelPart(this, 40, 32);
        this.tube18.addCuboid(-3F, -4F, 0.5F, 1, 1, 1, par1);
        this.tube18.setPivot(0F, 11F, 4F);
        this.tube18.setTextureSize(128, 64);
        this.tube18.mirror = true;
    }

    private void setRotation(ModelPart model, float y, float z) {
        model.pitch = 0.0F;
        model.yaw = y;
        model.roll = z;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw / 57.29577951308232F;
        this.head.pitch = headPitch / 57.29577951308232F;
        this.oxygenMask.yaw = headYaw / 57.29577951308232F;
        this.oxygenMask.pitch = headPitch / 57.29577951308232F;
        final float qPi = (float) Math.PI / 4F;
        this.leg1.roll = -qPi;
        this.leg2.roll = qPi;
        this.leg3.roll = -qPi * 0.74F;
        this.leg4.roll = qPi * 0.74F;
        this.leg5.roll = -qPi * 0.74F;
        this.leg6.roll = qPi * 0.74F;
        this.leg7.roll = -qPi;
        this.leg8.roll = qPi;
        final float var8 = -0.0F;
        final float var9 = 0.3926991F;
        this.leg1.yaw = var9 * 2.0F + var8;
        this.leg2.yaw = -var9 * 2.0F - var8;
        this.leg3.yaw = var9 * 1.0F + var8;
        this.leg4.yaw = -var9 * 1.0F - var8;
        this.leg5.yaw = -var9 * 1.0F + var8;
        this.leg6.yaw = var9 * 1.0F - var8;
        this.leg7.yaw = -var9 * 2.0F + var8;
        this.leg8.yaw = var9 * 2.0F - var8;
        final float var10 = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance;
        final float var11 = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance;
        final float var12 = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 1.5707963267948966F) * 0.4F) * limbDistance;
        final float var13 = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + 3F * 1.5707963267948966F) * 0.4F) * limbDistance;
        final float var14 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance;
        final float var15 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance;
        final float var16 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 1.5707963267948966F) * 0.4F) * limbDistance;
        final float var17 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + 3F * 1.5707963267948966F) * 0.4F) * limbDistance;
        this.leg1.yaw += var10;
        this.leg2.yaw += -var10;
        this.leg3.yaw += var11;
        this.leg4.yaw += -var11;
        this.leg5.yaw += var12;
        this.leg6.yaw += -var12;
        this.leg7.yaw += var13;
        this.leg8.yaw += -var13;
        this.leg1.roll += var14;
        this.leg2.roll += -var14;
        this.leg3.roll += var15;
        this.leg4.roll += -var15;
        this.leg5.roll += var16;
        this.leg6.roll += -var16;
        this.leg7.roll += var17;
        this.leg8.roll += -var17;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.rearEnd.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg8.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg6.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg4.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg2.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg7.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg5.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg3.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.leg1.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.oxygenMask.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tank1.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tank2.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube1.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube2.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube3.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube4.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube5.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube6.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube7.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube8.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube9.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube10.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube11.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube12.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube13.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube15.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube14.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube16.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube17.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.tube18.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}