/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.entity.EvolvedCreeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a> and made with the help of <a href="https://blockbench.net/https://blockbench.net/">Blockbench</a>
 */
@Environment(EnvType.CLIENT)
public class EvolvedCreeperEntityModel extends EntityModel<EvolvedCreeperEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart mask;
    private final ModelPart tank;
    private final ModelPart pipe;

    public EvolvedCreeperEntityModel(ModelPart root, boolean gear) {
        this.root = root;
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        if (gear) {
            this.mask = root.getChild(Constant.ModelPartName.OXYGEN_MASK);
            this.tank = root.getChild(Constant.ModelPartName.OXYGEN_TANK);
            this.pipe = root.getChild(Constant.ModelPartName.OXYGEN_PIPE);
        } else {
            this.mask = null;
            this.tank = null;
            this.pipe = null;
        }
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, 4.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, 4.0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-2.0F, 18.0F, -4.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(2.0F, 18.0F, -4.0F));
        if (dilation == Dilation.NONE) {
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_MASK, ModelPartBuilder.create().uv(0, 44).cuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_TANK, ModelPartBuilder.create().uv(0, 34).cuboid(-4.0F, 1.0F, 2.0F, 8, 6, 4, dilation), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
            modelPartData.addChild(Constant.ModelPartName.OXYGEN_PIPE, ModelPartBuilder.create().uv(40, 51).cuboid(-2.0F, -3.0F, 0.0F, 4, 5, 8, dilation), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
        }
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(EvolvedCreeperEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        if (this.mask != null) {
            this.mask.yaw = (float) (headYaw / (180.0F / Math.PI));
            this.mask.pitch = (float) (headPitch / (180.0F / Math.PI));
        }
        this.head.yaw = (float) (headYaw / (180.0F / Math.PI));
        this.head.pitch = (float) (headPitch / (180.0F / Math.PI));

        this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.child) {
            this.root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        } else {
            matrices.push();
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(0.0F, 1.0F, 0.0F);
            this.head.render(matrices, vertexConsumer, light, overlay);
            if (this.mask != null) this.mask.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.pop();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(0.0F, 1.52083F, 0.0F);
            this.body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            if (this.tank != null) this.tank.render(matrices, vertexConsumer, light, overlay);
            if (this.pipe != null) this.pipe.render(matrices, vertexConsumer, light, overlay);
            this.leftHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.leftFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightFrontLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.rightHindLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.pop();
        }
    }
}
