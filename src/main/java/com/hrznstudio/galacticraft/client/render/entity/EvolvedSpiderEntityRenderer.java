/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.EvolvedSpiderModel;
import com.hrznstudio.galacticraft.client.render.entity.feature.EvolvedSpiderEyesFeatureRenderer;
import com.hrznstudio.galacticraft.entity.EvolvedSpiderEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvolvedSpiderEntityRenderer extends MobEntityRenderer<EvolvedSpiderEntity, EvolvedSpiderModel<EvolvedSpiderEntity>> {
    public static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/evolved/spider.png");

    public EvolvedSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvolvedSpiderModel<>(0.0F), 1.0F);
        this.addFeature(new EvolvedSpiderEyesFeatureRenderer<>(this));
    }

    @Override
    protected float getLyingAngle(EvolvedSpiderEntity spiderEntity) {
        return 180.0F;
    }

    @Override
    public Identifier getTexture(EvolvedSpiderEntity spiderEntity) {
        return TEXTURE;
    }
}
