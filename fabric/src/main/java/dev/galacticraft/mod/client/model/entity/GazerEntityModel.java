/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import net.minecraft.world.entity.Entity;

public class GazerEntityModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart head;

    public GazerEntityModel(ModelPart root) {
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

        partDefinition.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 7.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(82, 32).mirror().addBox(-4.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(-0.01F)).mirror(false)
                .texOffs(87, 71).addBox(0.0F, -6.0F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        partDefinition.addOrReplaceChild(PartNames.LEFT_ARM, CubeListBuilder.create().texOffs(54, 59).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(58, 39).mirror().addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-6.0F, 13.0F, 3.0F));

        partDefinition.addOrReplaceChild(PartNames.RIGHT_ARM, CubeListBuilder.create().texOffs(54, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(58, 39).addBox(-2.0F, 8.0F, 2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 13.0F, 3.0F));

        partDefinition.addOrReplaceChild(PartNames.LEFT_LEG, CubeListBuilder.create().texOffs(58, 0).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-2.0F, 18.0F, 1.0F));

        partDefinition.addOrReplaceChild(PartNames.RIGHT_LEG, CubeListBuilder.create().texOffs(58, 0).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(2.0F, 18.0F, 2.0F));

        PartDefinition head = partDefinition.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(0, 39).addBox(-11.0F, -6.01F, -7.0F, 22.0F, 6.0F, 14.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 11.0F, 1.0F));

        head.addOrReplaceChild(PartNames.MOUTH, CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -23.0F, -14.0F, 22.0F, 25.0F, 14.0F, CubeDeformation.NONE)
                .texOffs(32, 59).addBox(-3.0F, -8.0F, -19.0F, 6.0F, 13.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -6.0F, 7.0F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(poseStack, vertices, light, overlay, color);
    }
}