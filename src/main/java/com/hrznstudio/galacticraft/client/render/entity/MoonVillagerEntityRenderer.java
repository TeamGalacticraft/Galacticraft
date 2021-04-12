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

package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.MoonVillagerEntityModel;
import com.hrznstudio.galacticraft.entity.MoonVillagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Environment(EnvType.CLIENT)
public class MoonVillagerEntityRenderer extends MobRenderer<MoonVillagerEntity, MoonVillagerEntityModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/moon_villager/moon_villager.png");

    public MoonVillagerEntityRenderer(EntityRenderDispatcher dispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(dispatcher, new MoonVillagerEntityModel(0.0F), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this));
        this.addLayer(new VillagerProfessionLayer<>(this, reloadableResourceManager, "villager"));
        this.addLayer(new CrossedArmsItemLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MoonVillagerEntity villagerEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(MoonVillagerEntity villagerEntity, PoseStack matrixStack, float f) {
        float g = 0.9375F;
        if (villagerEntity.isBaby()) {
            g = (float)((double)g * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        matrixStack.scale(g, g, g);
    }
}
