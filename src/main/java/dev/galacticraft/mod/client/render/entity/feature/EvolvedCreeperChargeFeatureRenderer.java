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

import dev.galacticraft.mod.client.model.entity.EvolvedCreeperEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GalacticraftEntityModelLayer;
import dev.galacticraft.mod.entity.EvolvedCreeperEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EvolvedCreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final EvolvedCreeperEntityModel model;

    public EvolvedCreeperChargeFeatureRenderer(FeatureRendererContext<EvolvedCreeperEntity, EvolvedCreeperEntityModel> context, EntityModelLoader loader) {
        super(context);
        this.model = new EvolvedCreeperEntityModel(loader.getModelPart(GalacticraftEntityModelLayer.EVOLVED_CREEPER_ARMOR), false);
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.1F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return TEXTURE;
    }

    @Override
    protected EntityModel<EvolvedCreeperEntity> getEnergySwirlModel() {
        return model;
    }
}
