/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GreyEntityModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart head;

    public GreyEntityModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild(PartNames.BODY);
        this.leftArm = root.getChild(PartNames.LEFT_ARM);
        this.rightArm = root.getChild(PartNames.RIGHT_ARM);
        this.leftLeg = root.getChild(PartNames.LEFT_LEG);
        this.rightLeg = root.getChild(PartNames.RIGHT_LEG);
        this.head = root.getChild(PartNames.HEAD);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -15.0F, -2.0F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partDefinition.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create().texOffs(24, 16).mirror().addBox(-2.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-3.0F, 10.0F, 0.0F));
        partDefinition.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create().texOffs(24, 16).addBox(0.0F, -1.0F, 0.0F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 10.0F, 0.0F));
        partDefinition.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create().texOffs(22, 29).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-2.0F, 17.0F, 1.0F));
        partDefinition.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create().texOffs(22, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 17.0F, 1.0F));
        partDefinition.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -3.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Calculate the angle to swing the arms and legs based on the limb swing amount
        float armSwingAngle = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        float legSwingAngle = 1.4F * armSwingAngle;

        this.leftArm.xRot = armSwingAngle;
        this.rightArm.xRot = -armSwingAngle;
        this.leftLeg.xRot = legSwingAngle;
        this.rightLeg.xRot = -legSwingAngle;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}