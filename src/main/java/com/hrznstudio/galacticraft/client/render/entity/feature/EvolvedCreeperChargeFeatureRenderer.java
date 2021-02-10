/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.client.model.entity.EvolvedCreeperEntityModel;
import com.hrznstudio.galacticraft.entity.EvolvedCreeperEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EvolvedCreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {

    private static final Identifier TEX = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final EvolvedCreeperEntityModel model = new EvolvedCreeperEntityModel(2.0F);

    public EvolvedCreeperChargeFeatureRenderer(FeatureRendererContext<EvolvedCreeperEntity, EvolvedCreeperEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.1F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return TEX;
    }

    @Override
    protected EntityModel<EvolvedCreeperEntity> getEnergySwirlModel() {
        return model;
    }
}
