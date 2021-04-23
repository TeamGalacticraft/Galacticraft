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

package dev.galacticraft.mod.client.render.entity.feature.gear.player;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.client.render.entity.feature.gear.LeftOxygenTankFeatureRenderer;
import dev.galacticraft.mod.client.render.entity.feature.ModelTransformer;
import dev.galacticraft.mod.client.render.entity.feature.gear.OxygenTankTextureOffset;
import dev.galacticraft.mod.item.OxygenTankItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class PlayerLeftOxygenTankFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends LeftOxygenTankFeatureRenderer<T, M> {

    public PlayerLeftOxygenTankFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> leftTankTransforms) {
        super(context, extra, leftTankTransforms, OxygenTankTextureOffset.HEAVY_TANK);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack itemStack = ((FullFixedItemInv)((GearInventoryProvider) entity).getGearInv()).getSlot(7).get();
        if (itemStack.getItem() instanceof OxygenTankItem) {
            switch (((OxygenTankItem) itemStack.getItem()).getTankType()) {
                case SMALL:
                    this.textureType = OxygenTankTextureOffset.SMALL_TANK;
                    break;
                case MEDIUM:
                    this.textureType = OxygenTankTextureOffset.MEDIUM_TANK;
                    break;
                case HEAVY:
                    this.textureType = OxygenTankTextureOffset.HEAVY_TANK;
                    break;
                case INFINITE:
                    this.textureType = OxygenTankTextureOffset.INFINITE_TANK;
                    break;
            }
            if (itemStack.hasGlint()) {
                VertexConsumer vertexConsumerGlint = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(getTexture(entity)), false, itemStack.hasGlint());
                super.render(matrices, vertexConsumerGlint, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            } else {
                super.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            }
        }
    }
}
