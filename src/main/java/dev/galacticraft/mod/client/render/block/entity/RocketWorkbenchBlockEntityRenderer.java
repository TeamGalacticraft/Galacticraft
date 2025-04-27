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

package dev.galacticraft.mod.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.content.block.special.RocketWorkbench;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RocketWorkbenchBlockEntityRenderer implements BlockEntityRenderer<RocketWorkbenchBlockEntity> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/model/rocket_workbench.png");
    private final ModelPart top;
    private final ModelPart plierArm, plierSmallArm;
    private final ModelPart drillArm, drillTool;
    private final ModelPart flashlightArm, flashlight;
    private final ModelPart display;

    public RocketWorkbenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GCEntityModelLayer.ROCKET_WORKBENCH);
        this.top = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_TOP);
        this.plierArm = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_PLIER_TOOL_ARM);
        this.plierSmallArm = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_PLIER_TOOL_SMALL_ARM);
        this.drillArm = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_DRILL_TOOL_SMALL_ARM);
        this.drillTool = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_DRILL_TOOL_DRILL);
        this.flashlightArm = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_FLASHLIGHT_HOLDER);
        this.flashlight = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_FLASHLIGHT_HANDLE);
        this.display = root.getChild(Constant.ModelPartName.ROCKET_WORKBENCH_DISPLAY);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_TOP, CubeListBuilder.create().texOffs(0, 32).addBox(2.0F, -4.0F, -14.0F, 12, 4, 12, CubeDeformation.NONE), PartPose.rotation(Mth.PI, 0.0F, 0.0F));

        modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_PLIER_TOOL_ARM, CubeListBuilder.create().texOffs(48, 32).addBox(-2.0F, -15.0F, -4.0F, 4, 16, 4, CubeDeformation.NONE), PartPose.rotation(157.5F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition plierSmallArm = modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_PLIER_TOOL_SMALL_ARM, CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, -12.0F, -1.0F, 2, 13, 2, CubeDeformation.NONE), PartPose.rotation(Mth.PI, 0.0F, 0.0F));
        plierSmallArm.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_PLIER_TOOL_PLIERS, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 12.01F, 4, 4, 3, CubeDeformation.NONE), PartPose.rotation(Mth.HALF_PI, Mth.PI, 0.0F));

        PartDefinition drillArm = modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_DRILL_TOOL_SMALL_ARM, CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2, CubeDeformation.NONE), PartPose.rotation(202.5F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
        PartDefinition drillTool = modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_DRILL_TOOL_DRILL, CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.0F, -2.0F, 4, 8, 4, CubeDeformation.NONE), PartPose.rotation(Mth.HALF_PI, Mth.PI, 0.0F));
        drillTool.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_DRILL_TOOL_DRILL_BIT, CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, 6.0F, -0.5F, 0, 4, 1, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition flashlightArm = modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_FLASHLIGHT_HOLDER, CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -14.0F, -0.5F, 1, 14, 1, CubeDeformation.NONE), PartPose.rotation(0.0F, 0.0F, 202.5F * Mth.DEG_TO_RAD));
        PartDefinition flashlight = modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_FLASHLIGHT_HANDLE, CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -1.0F, -1.0F, 4, 2, 2, CubeDeformation.NONE), PartPose.rotation(0.0F, 0.0F, Mth.PI));
        flashlight.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_FLASHLIGHT_LIGHT, CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -2.0F, -2.0F, 2, 4, 4, CubeDeformation.NONE), PartPose.ZERO);

        modelPartData.addOrReplaceChild(Constant.ModelPartName.ROCKET_WORKBENCH_DISPLAY, CubeListBuilder.create().texOffs(16, 48).addBox(0.0F, -6.0F, -4.0F, 1, 6, 8, CubeDeformation.NONE), PartPose.rotation(0.0F, 0.0F, 202.5F * Mth.DEG_TO_RAD));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void render(RocketWorkbenchBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().relative(Direction.UP, 1));
        Direction facing = blockEntity.getBlockState().getValue(RocketWorkbench.FACING);

        matrices.pushPose();
        matrices.translate(0.0F, 1.0F, 0.0F);
        matrices.rotateAround(Axis.YP.rotationDegrees(90.0F - facing.toYRot()), 0.5F, 0.0F, 0.5F);
        RenderSystem.setShaderTexture(0, RocketWorkbenchBlockEntityRenderer.TEXTURE);
        this.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay);
        matrices.popPose();
    }

    public void render(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
        this.top.render(matrices, vertexConsumer, light, overlay);

        matrices.pushPose();
        matrices.translate(0.5F, 0.0F, 0.0F);
        this.plierArm.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(40.0F));
        matrices.translate(0.5F, 0.55F, -0.69F);
        this.plierSmallArm.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0.5F, 0.0F, 0.875F);
        this.drillArm.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0.5F, 0.875F, 1.25F);
        this.drillTool.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0.125F, 0.0F, 0.5F);
        this.flashlightArm.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(-20.0F));
        matrices.translate(-0.4375F, 0.75F, 0.5F);
        this.flashlight.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(1.0F, 0.0F, 0.5F);
        this.display.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();
    }
}
