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
import dev.galacticraft.mod.client.render.entity.feature.ModelTransformer;
import dev.galacticraft.mod.client.render.entity.feature.gear.OxygenMaskFeatureRenderer;
import dev.galacticraft.mod.item.OxygenMaskItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class PlayerOxygenMaskFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T> & ModelWithHead> extends OxygenMaskFeatureRenderer<T, M> {

    public PlayerOxygenMaskFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms) {
        super(context, extra, maskTransforms, null);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack slot4 = ((FullFixedItemInv)((GearInventoryProvider) entity).getGearInv()).getSlot(4).get();
        if (slot4.getItem() instanceof OxygenMaskItem) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
            matrices.push();
            this.getContextModel().getHead().rotate(matrices);
            this.maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            this.renderColor(matrices, vertexConsumer, light, ((OxygenMaskItem) slot4.getItem()).getColor());
            matrices.pop();
        }
    }
}
