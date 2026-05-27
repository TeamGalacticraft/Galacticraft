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

package dev.galacticraft.mod.client.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.MoonSheepEntity;
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MoonSheepOxygenTanksRenderLayer extends RenderLayer<MoonSheepEntity, SheepModel<MoonSheepEntity>> {
    private static final ResourceLocation TEXTURE = Constant.id(Constant.GearTexture.OXYGEN_TANKS);
    private static final Geometry WOOLY = new Geometry(-4.0F, 0.0F, 1.0F, 2.0F, 0.78F, 0.78F, 0.78F, 0.0F, 0.75F, 0.0F, Mth.HALF_PI, true);
    private static final Geometry SHEARED = new Geometry(-4.0F, 0.0F, 1.0F, 2.0F, 0.68F, 0.68F, 0.68F, 0.0F, 2.25F, -1.15F, Mth.HALF_PI, true);

    private final @Nullable ModelPart body;
    private final @Nullable ModelPart woolyTanks;
    private final @Nullable ModelPart shearedTanks;

    public MoonSheepOxygenTanksRenderLayer(RenderLayerParent<MoonSheepEntity, SheepModel<MoonSheepEntity>> context) {
        super(context);

        if (!(context.getModel() instanceof AnimalModelAgeableListModel model)) {
            this.body = null;
            this.woolyTanks = null;
            this.shearedTanks = null;
            return;
        }

        this.body = model.callGetBodyParts().iterator().next();
        this.woolyTanks = this.bakeTanks(WOOLY);
        this.shearedTanks = this.bakeTanks(SHEARED);
    }

    private ModelPart bakeTanks(Geometry geometry) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        this.addTankPair(partDefinition, Constant.Item.SMALL_OXYGEN_TANK, geometry, 0, 0);
        this.addTankPair(partDefinition, Constant.Item.MEDIUM_OXYGEN_TANK, geometry, 16, 0);
        this.addTankPair(partDefinition, Constant.Item.LARGE_OXYGEN_TANK, geometry, 0, 16);
        this.addTankPair(partDefinition, Constant.Item.INFINITE_OXYGEN_TANK, geometry, 16, 16);
        return partDefinition.bake(32, 32);
    }

    private void addTankPair(PartDefinition partDefinition, String tankName, Geometry geometry, int u, int v) {
        partDefinition.addOrReplaceChild(tankName + "_left",
                CubeListBuilder.create().texOffs(u, v)
                .addBox(geometry.leftX(), geometry.y(), geometry.z(), 4, 8, 4, CubeDeformation.NONE),
                PartPose.ZERO
        );
        partDefinition.addOrReplaceChild(tankName + "_right",
                CubeListBuilder.create().texOffs(u, v)
                .addBox(geometry.rightX(), geometry.y(), geometry.z(), 4, 8, 4, CubeDeformation.NONE),
                PartPose.ZERO
        );
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, MoonSheepEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ModelPart tanks = entity.isSheared() ? this.shearedTanks : this.woolyTanks;
        Geometry geometry = entity.isSheared() ? SHEARED : WOOLY;
        if (tanks == null || !entity.galacticraft$hasGear()) {
            return;
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        this.renderTank(tanks, entity.galacticraft$tankSize(0) + "_left", geometry, matrices, vertexConsumer, light);
        if (geometry.renderSecondTank()) {
            this.renderTank(tanks, entity.galacticraft$tankSize(1) + "_right", geometry, matrices, vertexConsumer, light);
        }
    }

    private void renderTank(ModelPart tanksRoot, String tankName, Geometry geometry, PoseStack matrices, VertexConsumer vertexConsumer, int light) {
        if (!tanksRoot.hasChild(tankName)) {
            return;
        }

        ModelPart tank = tanksRoot.getChild(tankName);
        tank.copyFrom(this.body);
        tank.x += geometry.offsetX();
        tank.y += geometry.offsetY();
        tank.z += geometry.offsetZ();
        tank.xScale = geometry.scaleX();
        tank.yScale = geometry.scaleY();
        tank.zScale = geometry.scaleZ();
        tank.xRot = geometry.bodyXRot();
        tank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected ResourceLocation getTextureLocation(MoonSheepEntity entity) {
        return TEXTURE;
    }

    private record Geometry(
            float leftX,
            float rightX,
            float y,
            float z,
            float scaleX,
            float scaleY,
            float scaleZ,
            float offsetX,
            float offsetY,
            float offsetZ,
            float bodyXRot,
            boolean renderSecondTank
    ) {
    }
}