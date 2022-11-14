/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.EvolvedCreeperEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EvolvedCreeperChargeRenderLayer extends EnergySwirlLayer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final EvolvedCreeperEntityModel model;

    public EvolvedCreeperChargeRenderLayer(RenderLayerParent<EvolvedCreeperEntity, EvolvedCreeperEntityModel> context, EntityModelSet loader) {
        super(context);
        this.model = new EvolvedCreeperEntityModel(loader.bakeLayer(GCEntityModelLayer.EVOLVED_CREEPER_ARMOR), false);
    }

    @Override
    protected float xOffset(float partialAge) {
        return partialAge * 0.1F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    protected EntityModel<EvolvedCreeperEntity> model() {
        return model;
    }
}
