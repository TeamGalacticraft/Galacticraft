/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.item.OxygenMaskItem;
import dev.galacticraft.mod.item.OxygenTankItem;
import dev.galacticraft.mod.item.SensorGlassesItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class PlayerSpaceGearFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends SpaceGearFeatureRenderer<T, M> {

    public PlayerSpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms) {
        super(context, extra, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms);
    }
    public PlayerSpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms, boolean isLeftOxygenTankEnabled, boolean isRightOxygenTankEnabled, boolean isOxygenMaskEnabled, boolean isSensorGlassesEnabled) {
        super(context, extra, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms, isLeftOxygenTankEnabled, isRightOxygenTankEnabled, isOxygenMaskEnabled, isSensorGlassesEnabled);
    }
    public PlayerSpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extraHelmet, float extraTankLeft, float extraTankRight, float extraSensorGlasses, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms) {
        super(context, extraHelmet, extraTankLeft, extraTankRight, extraSensorGlasses, pivotX, pivotY, pivotZ, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms);
    }
    public PlayerSpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extraHelmet, float extraLeftTank, float extraRightTank, float extraSensorGlasses, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms, boolean isOxygenMaskEnabled, boolean isLeftOxygenTankEnabled, boolean isRightOxygenTankEnabled, boolean isSensorGlassesEnabled) {
        super(context, extraHelmet, extraLeftTank, extraRightTank, extraSensorGlasses, pivotX, pivotY, pivotZ, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms, isOxygenMaskEnabled, isLeftOxygenTankEnabled, isRightOxygenTankEnabled, isSensorGlassesEnabled);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        FullFixedItemInv gearInv = (FullFixedItemInv) ((GearInventoryProvider) entity).getGearInv();
        this.setOxygenMaskEnabled(gearInv.getSlot(4).get().getItem() instanceof OxygenMaskItem);
        // todo: add each tank separately
        this.setLeftOxygenTankEnabled(gearInv.getSlot(7).get().getItem() instanceof OxygenTankItem);
        this.setRightOxygenTankEnabled(gearInv.getSlot(6).get().getItem() instanceof OxygenTankItem);
        this.setSensorGlassesEnabled(
                gearInv.getSlot(8).get().getItem() instanceof SensorGlassesItem ||
                gearInv.getSlot(9).get().getItem() instanceof SensorGlassesItem ||
                gearInv.getSlot(10).get().getItem() instanceof SensorGlassesItem ||
                gearInv.getSlot(11).get().getItem() instanceof SensorGlassesItem
        );
        super.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
    }
}
