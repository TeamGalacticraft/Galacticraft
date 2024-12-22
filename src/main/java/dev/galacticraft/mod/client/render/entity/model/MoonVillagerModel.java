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

    public MoonVillagerModel(ModelPart modelPart) {
        super(modelPart);
        this.hat = modelPart.getChild("head").getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.jacket = modelPart.getChild("body").getChild("jacket");
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
    public void setupAnim(MoonVillagerEntity entityGoalInfo, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setupAnim(entityGoalInfo, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        this.hat.visible = false;
        this.hatRim.visible = false;
        this.jacket.visible = false;
    }
}
