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

package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.MoonVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

@Environment(EnvType.CLIENT)
public class MoonVillagerModel extends VillagerModel<MoonVillagerEntity> {
    private final ModelPart hat;
    private final ModelPart hatRim;
    private final ModelPart jacket;

    public MoonVillagerModel(ModelPart root) {
        super(root);
        this.hat = root.getChild(PartNames.HEAD).getChild(PartNames.HAT);
        this.hatRim = this.hat.getChild(PartNames.HAT_RIM);
        this.jacket = root.getChild(PartNames.BODY).getChild(PartNames.JACKET);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = VillagerModel.createBodyModel();
        mesh.getRoot().getChild(PartNames.HEAD).addOrReplaceChild(
                Constant.ModelPartName.MOON_VILLAGER_BRAIN,
                CubeListBuilder.create().texOffs(0, 38).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MoonVillagerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.hat.visible = false;
        this.hatRim.visible = false;
        this.jacket.visible = false;
    }
}
