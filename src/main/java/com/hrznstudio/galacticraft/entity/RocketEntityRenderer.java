package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.entity.moonvillager.T1RocketEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<T1RocketEntity> {

    public RocketEntityRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1);
    }

    @Override
    public void render(T1RocketEntity entity, double x, double y, double z, float f, float partialTickTime) {
        GlStateManager.pushMatrix();
        MinecraftClient client = MinecraftClient.getInstance();
        GlStateManager.translated(x, y, z);
        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        BakedModel model = client.getBlockRenderManager().getModel(GalacticraftBlocks.ROCKET_RENDER_BLOCK.getDefaultState());
        client.getBlockRenderManager().getModelRenderer().render(model, 1, 1, 1, 1);
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, f, partialTickTime);
    }

    @Override
    protected Identifier getTexture(T1RocketEntity var1) {
        return null;
    }
}