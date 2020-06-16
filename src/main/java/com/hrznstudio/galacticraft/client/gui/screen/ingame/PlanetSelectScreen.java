package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.TranslatableText;

public class PlanetSelectScreen extends Screen {

    public PlanetSelectScreen() {
        super(new TranslatableText(""));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        super.renderBackground(matrices);
        matrices.push();
        //rotation test code
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));
        renderPlanet(100, 100, matrices, GalacticraftBlocks.ALUMINUM_BLOCK.getDefaultState());
        matrices.pop();
    }

    public void renderPlanet(int x, int y, MatrixStack matrices, BlockState planet){
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.translatef((float)x, (float)y, 100.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(planet, matrices, immediate, 1000, 1);
        immediate.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableRescaleNormal();
    }


}
