/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.entity.rocket.tier1;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Tier1RocketEntityRenderer extends EntityRenderer<Tier1RocketEntity> {

    public Tier1RocketEntityRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1);
    }

    @Override
    public void render(Tier1RocketEntity entity, double x, double y, double z, float f, float partialTickTime) {
        GlStateManager.pushMatrix();
        MinecraftClient client = MinecraftClient.getInstance();
        GlStateManager.translated(x, y + 2.0D, z);
        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        client.getBlockRenderManager().getModelRenderer().render(GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK.getDefaultState(), client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_BODY_RENDER_BLOCK.getDefaultState()), entity.getColor()[0], entity.getColor()[1], entity.getColor()[2], entity.getColor()[3]);
        GlStateManager.translated(0.0D, -0.5D + -0.5D, 0.0D);
        client.getBlockRenderManager().getModelRenderer().render(GalacticraftBlocks.ROCKET_THRUSTER_TIER_1_RENDER_BLOCK.getDefaultState(), client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_THRUSTER_TIER_1_RENDER_BLOCK.getDefaultState()), 1, 1, 1, 1);
        client.getBlockRenderManager().getModelRenderer().render(GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK.getDefaultState(), client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_FINS_RENDER_BLOCK.getDefaultState()), entity.getColor()[0], entity.getColor()[1], entity.getColor()[2], entity.getColor()[3]);
        GlStateManager.translated(0.0D, -1.0D + 0.5D, 0.0D);
        client.getBlockRenderManager().getModelRenderer().render(GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK.getDefaultState(), client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_BOTTOM_RENDER_BLOCK.getDefaultState()), entity.getColor()[0], entity.getColor()[1], entity.getColor()[2], entity.getColor()[3]);
        GlStateManager.translated(0.0D, 1.5D + 1.75D, 0.0D);
        client.getBlockRenderManager().getModelRenderer().render(GalacticraftBlocks.ROCKET_TOP_BASIC_RENDER_BLOCK.getDefaultState(), client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_TOP_BASIC_RENDER_BLOCK.getDefaultState()), entity.getColor()[0], entity.getColor()[1], entity.getColor()[2], entity.getColor()[3]);
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, f, partialTickTime);
    }

    @Override
    protected Identifier getTexture(Tier1RocketEntity var1) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}