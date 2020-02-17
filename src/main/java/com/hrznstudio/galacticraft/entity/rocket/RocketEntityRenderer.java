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

package com.hrznstudio.galacticraft.entity.rocket;

import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    public RocketEntityRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1);
    }

    @Override
    public void render(RocketEntity entity, double x, double y, double z, float f, float partialTickTime) {
        super.render(entity, x, y, z, f, partialTickTime);
        GlStateManager.pushMatrix();

        MinecraftClient client = MinecraftClient.getInstance();
        GlStateManager.translated(x, y + 2.0D, z);

        if (entity.getStage() == LaunchStage.IGNITED) {
            GlStateManager.translated((entity.world.random.nextFloat() - 0.5F) * 0.12F, 0, (entity.world.random.nextFloat() - 0.5F) * 0.12F);
        }

        GlStateManager.rotatef((entity.yaw - 180.0F) * -1.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(entity.pitch, 1.0F, 0.0F, 0.0F);

        float float_7 = (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_TICKS) - partialTickTime;
        float float_8 = entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_STRENGTH) - partialTickTime;

        if (float_8 < 0.0F) {
            float_8 = 0.0F;
        }

        if (float_7 > 0.0F) {
            GlStateManager.rotatef(MathHelper.sin(float_7) * float_7 * float_8 / 10.0F * (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_SIDE), 1.0F, 0.0F, 0.0F);
        }

        float red = entity.getColor()[0] / 255F;
        float green = entity.getColor()[1] / 255F;
        float blue = entity.getColor()[2] / 255F;
        float alpha = entity.getColor()[3] / 255F;

        GlStateManager.pushTextureAttributes();

        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.translated(0.0D, -1.5, 0.0D);

        entity.getPartForType(RocketPartType.BOTTOM).preRender(entity);
        client.getBlockRenderManager().getModelRenderer().render(entity.getPartForType(RocketPartType.BOTTOM).getBlockToRender().getDefaultState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BOTTOM).getBlockToRender().getDefaultState()), alpha, red, green, blue);
        entity.getPartForType(RocketPartType.BOTTOM).postRender(entity);

        GlStateManager.translated(0.0D, .5, 0.0D);

        entity.getPartForType(RocketPartType.BOOSTER).preRender(entity);
        client.getBlockRenderManager().getModelRenderer().render(entity.getPartForType(RocketPartType.BOOSTER).getBlockToRender().getDefaultState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BOOSTER).getBlockToRender().getDefaultState()), alpha, red, green, blue);
        entity.getPartForType(RocketPartType.BOOSTER).postRender(entity);

        entity.getPartForType(RocketPartType.FIN).preRender(entity);
        client.getBlockRenderManager().getModelRenderer().render(entity.getPartForType(RocketPartType.FIN).getBlockToRender().getDefaultState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.FIN).getBlockToRender().getDefaultState()), alpha, red, green, blue);
        entity.getPartForType(RocketPartType.FIN).postRender(entity);

        GlStateManager.translated(0.0D, 1.0D, 0.0D);

        entity.getPartForType(RocketPartType.BODY).preRender(entity);
        client.getBlockRenderManager().getModelRenderer().render(entity.getPartForType(RocketPartType.BODY).getBlockToRender().getDefaultState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BODY).getBlockToRender().getDefaultState()), alpha, red, green, blue);
        entity.getPartForType(RocketPartType.BODY).postRender(entity);

        GlStateManager.translated(0.0D, 1.75, 0.0D);

        entity.getPartForType(RocketPartType.CONE).preRender(entity);
        client.getBlockRenderManager().getModelRenderer().render(entity.getPartForType(RocketPartType.CONE).getBlockToRender().getDefaultState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.CONE).getBlockToRender().getDefaultState()), alpha, red, green, blue);
        entity.getPartForType(RocketPartType.CONE).postRender(entity);

        GlStateManager.popMatrix();
    }

    @Override
    protected Identifier getTexture(RocketEntity var1) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}