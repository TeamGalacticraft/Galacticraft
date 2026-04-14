/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.model;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelEvolvedZombie extends ModelBiped
{

    ModelRenderer leftOxygenTank;
    ModelRenderer rightOxygenTank;
    ModelRenderer tubeRight2;
    ModelRenderer tubeLeft1;
    ModelRenderer tubeRight3;
    ModelRenderer tubeRight4;
    ModelRenderer tubeRight5;
    ModelRenderer tubeLeft6;
    ModelRenderer tubeRight7;
    ModelRenderer tubeRight1;
    ModelRenderer tubeLeft2;
    ModelRenderer tubeLeft3;
    ModelRenderer tubeLeft4;
    ModelRenderer tubeLeft5;
    ModelRenderer tubeLeft7;
    ModelRenderer tubeRight6;
    ModelRenderer tubeLeft8;
    ModelRenderer oxygenMask;
    private float saveGravity;
    private boolean renderGear;

    public ModelEvolvedZombie(boolean renderGear)
    {
        this(0.0F, false, renderGear);
    }

    public ModelEvolvedZombie(float par1, boolean halfSizeTexture, boolean renderGear)
    {
        this.textureWidth = halfSizeTexture ? 64 : 128;
        this.textureHeight = halfSizeTexture ? 32 : 64;
        this.renderGear = renderGear;
        final int par2 = 0;
        this.leftOxygenTank = new ModelRenderer(this, 56, 20);
        this.leftOxygenTank.addBox(-1.5F, 0F, -1.5F, 3, 7, 3, par1);
        this.leftOxygenTank.setRotationPoint(2F, 2F, 3.8F);
        this.leftOxygenTank.mirror = true;
        this.setRotation(this.leftOxygenTank, 0F, 0F, 0F);
        this.rightOxygenTank = new ModelRenderer(this, 56, 20);
        this.rightOxygenTank.addBox(-1.5F, 0F, -1.5F, 3, 7, 3, par1);
        this.rightOxygenTank.setRotationPoint(-2F, 2F, 3.8F);
        this.rightOxygenTank.mirror = true;
        this.setRotation(this.rightOxygenTank, 0F, 0F, 0F);
        this.tubeRight2 = new ModelRenderer(this, 56, 30);
        this.tubeRight2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight2.setRotationPoint(-2F, 2F, 6.8F);
        this.tubeRight2.mirror = true;
        this.setRotation(this.tubeRight2, 0F, 0F, 0F);
        this.tubeLeft1 = new ModelRenderer(this, 56, 30);
        this.tubeLeft1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft1.setRotationPoint(2F, 3F, 5.8F);
        this.tubeLeft1.mirror = true;
        this.setRotation(this.tubeLeft1, 0F, 0F, 0F);
        this.tubeRight3 = new ModelRenderer(this, 56, 30);
        this.tubeRight3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight3.setRotationPoint(-2F, 1F, 6.8F);
        this.tubeRight3.mirror = true;
        this.setRotation(this.tubeRight3, 0F, 0F, 0F);
        this.tubeRight4 = new ModelRenderer(this, 56, 30);
        this.tubeRight4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight4.setRotationPoint(-2F, 0F, 6.8F);
        this.tubeRight4.mirror = true;
        this.setRotation(this.tubeRight4, 0F, 0F, 0F);
        this.tubeRight5 = new ModelRenderer(this, 56, 30);
        this.tubeRight5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight5.setRotationPoint(-2F, -1F, 6.8F);
        this.tubeRight5.mirror = true;
        this.setRotation(this.tubeRight5, 0F, 0F, 0F);
        this.tubeLeft6 = new ModelRenderer(this, 56, 30);
        this.tubeLeft6.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft6.setRotationPoint(2F, -2F, 5.8F);
        this.tubeLeft6.mirror = true;
        this.setRotation(this.tubeLeft6, 0F, 0F, 0F);
        this.tubeRight7 = new ModelRenderer(this, 56, 30);
        this.tubeRight7.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight7.setRotationPoint(-2F, -3F, 4.8F);
        this.tubeRight7.mirror = true;
        this.setRotation(this.tubeRight7, 0F, 0F, 0F);
        this.tubeRight1 = new ModelRenderer(this, 56, 30);
        this.tubeRight1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight1.setRotationPoint(-2F, 3F, 5.8F);
        this.tubeRight1.mirror = true;
        this.setRotation(this.tubeRight1, 0F, 0F, 0F);
        this.tubeLeft2 = new ModelRenderer(this, 56, 30);
        this.tubeLeft2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft2.setRotationPoint(2F, 2F, 6.8F);
        this.tubeLeft2.mirror = true;
        this.setRotation(this.tubeLeft2, 0F, 0F, 0F);
        this.tubeLeft3 = new ModelRenderer(this, 56, 30);
        this.tubeLeft3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft3.setRotationPoint(2F, 1F, 6.8F);
        this.tubeLeft3.mirror = true;
        this.setRotation(this.tubeLeft3, 0F, 0F, 0F);
        this.tubeLeft4 = new ModelRenderer(this, 56, 30);
        this.tubeLeft4.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft4.setRotationPoint(2F, 0F, 6.8F);
        this.tubeLeft4.mirror = true;
        this.setRotation(this.tubeLeft4, 0F, 0F, 0F);
        this.tubeLeft5 = new ModelRenderer(this, 56, 30);
        this.tubeLeft5.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft5.setRotationPoint(2F, -1F, 6.8F);
        this.tubeLeft5.mirror = true;
        this.setRotation(this.tubeLeft5, 0F, 0F, 0F);
        this.tubeLeft7 = new ModelRenderer(this, 56, 30);
        this.tubeLeft7.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeLeft7.setRotationPoint(2F, -3F, 4.8F);
        this.tubeLeft7.mirror = true;
        this.setRotation(this.tubeLeft7, 0F, 0F, 0F);
        this.tubeRight6 = new ModelRenderer(this, 56, 30);
        this.tubeRight6.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, par1);
        this.tubeRight6.setRotationPoint(-2F, -2F, 5.8F);
        this.tubeRight6.mirror = true;
        this.setRotation(this.tubeRight6, 0F, 0F, 0F);
        this.tubeLeft8 = new ModelRenderer(this, 56, 30);
        this.tubeLeft8.addBox(0F, 0F, 0F, 1, 1, 1, par1);
        this.tubeLeft8.setRotationPoint(0F, -5F, 0F);
        this.tubeLeft8.mirror = true;
        this.setRotation(this.tubeLeft8, 0F, 0F, 0F);
        this.oxygenMask = new ModelRenderer(this, 56, 0);
        this.oxygenMask.addBox(-5F, -9F, -5F, 10, 10, 10, par1);
        this.oxygenMask.setRotationPoint(0F, 0F, 0F);
        this.oxygenMask.mirror = true;
        this.setRotation(this.oxygenMask, 0F, 0F, 0F);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, par1);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, par1);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, par1);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + par2, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, par1);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + par2, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + par2, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + par2, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.saveGravity = WorldUtil.getGravityFactor(entity);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        if (this.isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * f5, 0.0F);
            this.bipedHead.render(f5);
            if (this.renderGear)
            {
                this.oxygenMask.render(f5);
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F);
            if (this.renderGear)
            {
                this.leftOxygenTank.render(f5);
                this.rightOxygenTank.render(f5);
                this.tubeRight2.render(f5);
                this.tubeLeft1.render(f5);
                this.tubeRight3.render(f5);
                this.tubeRight4.render(f5);
                this.tubeRight5.render(f5);
                this.tubeLeft6.render(f5);
                this.tubeRight7.render(f5);
                this.tubeRight1.render(f5);
                this.tubeLeft2.render(f5);
                this.tubeLeft3.render(f5);
                this.tubeLeft4.render(f5);
                this.tubeLeft5.render(f5);
                this.tubeLeft7.render(f5);
                this.tubeRight6.render(f5);
                this.tubeLeft8.render(f5);
            }
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);
            GL11.glPopMatrix();
        } else
        {
            if (this.renderGear)
            {
                this.leftOxygenTank.render(f5);
                this.rightOxygenTank.render(f5);
                this.tubeRight2.render(f5);
                this.tubeLeft1.render(f5);
                this.tubeRight3.render(f5);
                this.tubeRight4.render(f5);
                this.tubeRight5.render(f5);
                this.tubeLeft6.render(f5);
                this.tubeRight7.render(f5);
                this.tubeRight1.render(f5);
                this.tubeLeft2.render(f5);
                this.tubeLeft3.render(f5);
                this.tubeLeft4.render(f5);
                this.tubeLeft5.render(f5);
                this.tubeLeft7.render(f5);
                this.tubeRight6.render(f5);
                this.tubeLeft8.render(f5);
                this.oxygenMask.render(f5);
            }
            this.bipedHead.render(f5);
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
    {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
        float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
        this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
        this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F);
        this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F);
        this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
        this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
        this.bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;

        copyModelAngles(this.bipedHead, this.oxygenMask);
    }
}
