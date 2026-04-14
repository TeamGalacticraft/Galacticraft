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
import dev.galacticraft.mod.mixin.client.AnimalModelAgeableListModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
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
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MoonAnimalOxygenTanksRenderLayer<T extends Animal, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = Constant.id(Constant.GearTexture.OXYGEN_TANKS);

    public static final Geometry QUADRUPED = new Geometry(
        -4.0F, 0.0F, 1.0F, 2.0F,
        0.78F, 0.78F, 0.78F,
        0.0F, 0.0F, 0.0F,
        Mth.HALF_PI,
        true
    );
    public static final Geometry CHICKEN = new Geometry(
        -2.0F, -2.0F, -5.75F, 1.5F,
        0.5F, 0.5F, 0.5F,
        0.0F, -1.75F, 2.6F,
        Mth.HALF_PI,
        false
    );

    private final Geometry geometry;
    private final @Nullable ModelPart body;
    private final @Nullable ModelPart tanks;

    public MoonAnimalOxygenTanksRenderLayer(RenderLayerParent<T, M> context, Geometry geometry) {
        super(context);
        this.geometry = geometry;

        if (!(context.getModel() instanceof AnimalModelAgeableListModel model)) {
            this.body = null;
            this.tanks = null;
            return;
        }

        this.body = model.callGetBodyParts().iterator().next();

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        this.addTankPair(partDefinition, Constant.Item.SMALL_OXYGEN_TANK, geometry, 0, 0);
        this.addTankPair(partDefinition, Constant.Item.MEDIUM_OXYGEN_TANK, geometry, 16, 0);
        this.addTankPair(partDefinition, Constant.Item.LARGE_OXYGEN_TANK, geometry, 0, 16);
        this.addTankPair(partDefinition, Constant.Item.INFINITE_OXYGEN_TANK, geometry, 16, 16);
        this.tanks = partDefinition.bake(32, 32);
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
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (this.tanks == null || !entity.galacticraft$hasGear()) {
            return;
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity), true));
        this.renderTank(this.tanks, entity.galacticraft$tankSize(0) + "_left", matrices, vertexConsumer, light);
        if (this.geometry.renderSecondTank()) {
            this.renderTank(this.tanks, entity.galacticraft$tankSize(1) + "_right", matrices, vertexConsumer, light);
        }
    }

    private void renderTank(ModelPart tanksRoot, String tankName, PoseStack matrices, VertexConsumer vertexConsumer, int light) {
        if (!tanksRoot.hasChild(tankName)) {
            return;
        }

        ModelPart tank = tanksRoot.getChild(tankName);
        tank.copyFrom(this.body);
        tank.x += this.geometry.offsetX();
        tank.y += this.geometry.offsetY();
        tank.z += this.geometry.offsetZ();
        tank.xScale = this.geometry.scaleX();
        tank.yScale = this.geometry.scaleY();
        tank.zScale = this.geometry.scaleZ();
        tank.xRot = this.geometry.bodyXRot();
        tank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    public record Geometry(
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