/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Constant.EntityTexture;
import dev.galacticraft.mod.client.model.entity.EvolvedSkeletonBossModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.boss.SkeletonBoss;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class EvolvedSkeletonBossRenderer extends MobRenderer<SkeletonBoss, EvolvedSkeletonBossModel> {
    public static final ResourceLocation BOSS_TEXTURE = Constant.id(EntityTexture.SKELETON_BOSS);

    public EvolvedSkeletonBossRenderer(EntityRendererProvider.Context context) {
        super(context, new EvolvedSkeletonBossModel(context.bakeLayer(GCEntityModelLayer.SKELETON_BOSS)), 0.9F);
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonBoss entity) {
        return BOSS_TEXTURE;
    }
}
