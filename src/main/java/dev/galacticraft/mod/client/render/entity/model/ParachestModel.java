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

package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.ParachestEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ParachestModel extends HierarchicalModel<ParachestEntity> {
    
    private final ModelPart root;
    public ModelPart[] parachute = new ModelPart[3];
    public ModelPart[] parachuteStrings = new ModelPart[4];
    
    public ParachestModel(ModelPart parachute) {
        this.root = parachute;
        this.parachute[0] = parachute.getChild("parachute_0");
        this.parachute[1] = parachute.getChild("parachute_1");
        this.parachute[2] = parachute.getChild("parachute_2");
        this.parachuteStrings[0] = parachute.getChild("parachute_string_0");
        this.parachuteStrings[1] = parachute.getChild("parachute_string_1");
        this.parachuteStrings[2] = parachute.getChild("parachute_string_2");
        this.parachuteStrings[3] = parachute.getChild("parachute_string_3");
    }

    public static LayerDefinition createParachuteLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition[] parachute = new PartDefinition[3];
        PartDefinition[] parachuteStrings = new PartDefinition[4];
        parachute[0] = partDefinition.addOrReplaceChild("parachute_0", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40), PartPose.offset(15.0F, 4.0F, 0.0F));
        parachute[1] = partDefinition.addOrReplaceChild("parachute_1", CubeListBuilder.create().texOffs(0, 42).addBox(-20.0F, -45.0F, -20.0F, 40, 2, 40), PartPose.offset(0.0F, 0.0F, 0.0F));
        parachute[2] = partDefinition.addOrReplaceChild("parachute_2", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40), PartPose.offset(11F, -11, 0.0F));

        parachuteStrings[0] = partDefinition.addOrReplaceChild("parachute_string_0", CubeListBuilder.create().texOffs(100, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1), PartPose.offset(0.0F, 0.0F, 0.0F));
        parachuteStrings[1] = partDefinition.addOrReplaceChild("parachute_string_1", CubeListBuilder.create().texOffs(100, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1), PartPose.offset(0.0F, 0.0F, 0.0F));
        parachuteStrings[2] = partDefinition.addOrReplaceChild("parachute_string_2", CubeListBuilder.create().texOffs(100, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1), PartPose.offset(0.0F, 0.0F, 0.0F));
        parachuteStrings[3] = partDefinition.addOrReplaceChild("parachute_string_3", CubeListBuilder.create().texOffs(100, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 256, 256);
    }

    @Override
    public void setupAnim(ParachestEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.parachute[0].yRot = (float) (0 / Mth.RAD_TO_DEG);
        this.parachute[2].yRot = (float) -(0 / Mth.RAD_TO_DEG);
        this.parachuteStrings[0].yRot = (float) (0 / Mth.RAD_TO_DEG);
        this.parachuteStrings[1].yRot = (float) (0 / Mth.RAD_TO_DEG);
        this.parachuteStrings[2].yRot = (float) -(0 / Mth.RAD_TO_DEG);
        this.parachuteStrings[3].yRot = (float) -(0 / Mth.RAD_TO_DEG);

        this.parachute[0].setPos(-5.85F, -11.0F, 2.0F);
        this.parachute[1].setPos(9F, -7F, 2.0F);
        this.parachute[2].setPos(-2.15F, 4.0F, 2.0F);
        this.parachute[0].zRot = (float) (210F / Mth.RAD_TO_DEG);
        this.parachute[1].zRot = (float) (180F / Mth.RAD_TO_DEG);
        this.parachute[2].zRot = (float) -(210F / Mth.RAD_TO_DEG);
        this.parachuteStrings[0].zRot = (float) ((155F + 180F) / Mth.RAD_TO_DEG);
        this.parachuteStrings[0].xRot = (float) (23F / Mth.RAD_TO_DEG);
        this.parachuteStrings[0].setPos(9.0F, 3.0F, 2.0F);
        this.parachuteStrings[1].zRot = (float) ((155F + 180F) / Mth.RAD_TO_DEG);
        this.parachuteStrings[1].xRot = (float) -(23F / Mth.RAD_TO_DEG);
        this.parachuteStrings[1].setPos(9.0F, 3.0F, 2.0F);

        this.parachuteStrings[2].zRot = (float) -((155F + 180F) / Mth.RAD_TO_DEG);
        this.parachuteStrings[2].xRot = (float) (23F / Mth.RAD_TO_DEG);
        this.parachuteStrings[2].setPos(9.0F, 3.0F, 2.0F);
        this.parachuteStrings[3].zRot = (float) -((155F + 180F) / Mth.RAD_TO_DEG);
        this.parachuteStrings[3].xRot = (float) -(23F / Mth.RAD_TO_DEG);
        this.parachuteStrings[3].setPos(9.0F, 3.0F, 2.0F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
