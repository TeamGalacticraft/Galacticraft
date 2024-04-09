/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LanderModel extends EntityModel<LanderEntity> {
    private final ModelPart feet;
    private final ModelPart legs;
    private final ModelPart head;

    public LanderModel(ModelPart root) {
        this.feet = root.getChild("feet");
        this.legs = root.getChild("legs");
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition feet = partdefinition.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(0, 52).addBox(-24.0F, -4.0F, 28.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(16.0F, -4.0F, 28.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        feet.addOrReplaceChild("foot_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0F, 0.0F, -32.0F, 0.0F, 3.1416F, 0.0F));

        feet.addOrReplaceChild("foot_r2", CubeListBuilder.create().texOffs(0, 52).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(20.0F, -6.0F, -32.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition legs = partdefinition.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        legs.addOrReplaceChild("cap_r1", CubeListBuilder.create().texOffs(200, 0).addBox(-4.0F, -32.1716F, -3.1716F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -12.1716F, -0.1716F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -12.1716F, -0.1716F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(20.0F, -4.0F, -32.0F, -0.7363F, -0.3035F, -0.3185F));

        legs.addOrReplaceChild("cap_r2", CubeListBuilder.create().texOffs(200, 0).addBox(-4.0F, -32.1716F, -3.1716F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -12.1716F, -0.1716F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -12.1716F, -0.1716F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0F, -4.0F, -32.0F, -0.7363F, 0.3035F, 0.3185F));

        legs.addOrReplaceChild("cap_r3", CubeListBuilder.create().texOffs(200, 0).addBox(-4.0F, -32.1716F, -4.8284F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -12.1716F, -1.8284F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -12.1716F, -1.8284F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0F, -4.0F, 32.0F, 0.7363F, -0.3035F, 0.3185F));

        legs.addOrReplaceChild("cap_r4", CubeListBuilder.create().texOffs(200, 0).addBox(-4.0F, -32.1716F, -4.8284F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.0F, -12.1716F, -1.8284F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -12.1716F, -1.8284F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(20.0F, -4.0F, 32.0F, 0.7363F, 0.3035F, -0.3185F));

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -66.0F, -14.0F, 12.0F, 14.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-18.0F, -26.0F, -22.0F, 36.0F, 8.0F, 44.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(-12.0F, -44.0F, -16.0F, 24.0F, 18.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(112, 54).addBox(-16.0F, -52.0F, -20.0F, 32.0F, 22.0F, 30.0F, new CubeDeformation(0.0F))
                .texOffs(116, 0).addBox(-16.0F, -56.0F, 14.0F, 32.0F, 26.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 128);
    }

    @Override
    public void setupAnim(LanderEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        feet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        legs.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
