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

public class OliGrubEntityModel<T extends Entity> extends EntityModel<T> {
    private static final String LEFT_MANDIBLE = "left_mandible";
    private static final String RIGHT_MANDIBLE = "right_mandible";
    private final ModelPart body;
    private final ModelPart leftMandible;
    private final ModelPart rightMandible;

    public OliGrubEntityModel(ModelPart root) {
        this.body = root.getChild(PartNames.BODY);
        this.leftMandible = root.getChild(LEFT_MANDIBLE);
        this.rightMandible = root.getChild(RIGHT_MANDIBLE);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 6.0F, 11.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partDefinition.addOrReplaceChild(LEFT_MANDIBLE, CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-1.0F, -1.0F, -4.0F, 3.0F, 2.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-3.0F, 23.0F, -3.0F));
        partDefinition.addOrReplaceChild(RIGHT_MANDIBLE, CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, -1.0F, -4.0F, 3.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 23.0F, -3.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int color) {
        this.body.render(poseStack, vertices, light, overlay);
        this.leftMandible.render(poseStack, vertices, light, overlay);
        this.rightMandible.render(poseStack, vertices, light, overlay);
    }
}