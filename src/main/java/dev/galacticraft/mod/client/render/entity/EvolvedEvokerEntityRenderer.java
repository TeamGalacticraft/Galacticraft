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

package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.client.render.entity.feature.gear.LeftOxygenTankFeatureRenderer;
import dev.galacticraft.mod.client.render.entity.feature.gear.OxygenMaskFeatureRenderer;
import dev.galacticraft.mod.client.render.entity.feature.gear.OxygenTankTextureOffset;
import dev.galacticraft.mod.client.render.entity.feature.gear.RightOxygenTankFeatureRenderer;
import dev.galacticraft.mod.entity.EvolvedEvokerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EvokerEntityRenderer;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EvolvedEvokerEntityRenderer extends EvokerEntityRenderer<EvolvedEvokerEntity> {
    public EvolvedEvokerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.addFeature(new OxygenMaskFeatureRenderer<>(this, 1.0F,
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                    stack.translate(0.0F, 0.0F, 0.0F);
                    stack.scale(1.05f, 1.05f, 1.05f);
                }, null));
        this.addFeature(new LeftOxygenTankFeatureRenderer<>(this, 0.0F,
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}, OxygenTankTextureOffset.HEAVY_TANK));
        this.addFeature(new RightOxygenTankFeatureRenderer<>(this, 0.0F,
                (stack, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {}, OxygenTankTextureOffset.HEAVY_TANK));
    }
}
