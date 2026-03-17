package dev.galacticraft.mod.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.block.entity.decoration.FlagBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {
    protected final ModelPart flag;

    public FlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GCEntityModelLayer.FLAG);
        this.flag = root.getChild("flag");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition flag = root.addOrReplaceChild("flag",
                CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 20, 40, 1),
                PartPose.rotation(0, 0, (float) Math.toRadians(90))
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(FlagBlockEntity flag, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.translate(0.4375, 2.0416666667, 0.479166667);

        matrices.mulPose(Axis.YP.rotation(-flag.getFacingRadians()));

        matrices.scale((float) 2 /3, (float) 2 /3, (float) 2 /3);
        renderPatterns(
                matrices, vertexConsumers, light, overlay, this.flag, ModelBakery.BANNER_BASE, flag.getBaseColor(), flag.getPatterns()
        );
        matrices.popPose();
    }

    public static void renderPatterns(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            ModelPart canvas,
            Material baseSprite,
            DyeColor color,
            BannerPatternLayers pattern
    ) {
        canvas.render(matrices, baseSprite.buffer(vertexConsumers, RenderType::entitySolid, false), light, overlay);
        renderPatternLayer(matrices, vertexConsumers, light, overlay, canvas, Sheets.BANNER_BASE, color);

        for (int i = 0; i < pattern.layers().size(); i++) {
            BannerPatternLayers.Layer layer = pattern.layers().get(i);
            Material material = Sheets.getBannerMaterial(layer.pattern());
            renderPatternLayer(matrices, vertexConsumers, light, overlay, canvas, material, layer.color());
        }
    }

    private static void renderPatternLayer(
            PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, ModelPart canvas, Material textureId, DyeColor color
    ) {
        int i = color.getTextureDiffuseColor();
        canvas.render(matrices, textureId.buffer(vertexConsumers, RenderType::entityNoOutline), light, overlay, i);
    }
}
