package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.planetscreen.CelestialBodyDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PlanetSelectScreen extends Screen {

    private float earthRotation = 0;

    public static final List<CelestialBodyDisplay> celestialBodyDisplays = new ArrayList<>();

    public static final Identifier SKY = new Identifier(Constants.MOD_ID, "textures/planet_select_screen/stars.png");
    public String currentPlanet = "";

    public PlanetSelectScreen() {
        super(new TranslatableText("yes.yes.yes"));
    }

    @Override
    public void tick() {
        super.tick();
        earthRotation = earthRotation + 2F;

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        super.renderBackground(matrices);
        checkForPlanetInteraction(mouseX, mouseY);
        renderPlanets(matrices);
        textRenderer.draw(matrices, currentPlanet, 50, 10, 111111);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        Window window = MinecraftClient.getInstance().getWindow();
        MinecraftClient.getInstance().getTextureManager().bindTexture(SKY);
        drawTexture(matrices, 0, 0, 0, 0, window.getWidth(), window.getHeight());
    }

    public void renderPlanet(int x, int y, MatrixStack matrices, BlockState planet) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.translatef((float) x, (float) y, 100.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(planet, matrices, immediate, 10000, OverlayTexture.DEFAULT_UV);
        immediate.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public void rotate(MatrixStack stack, float angle) {
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
    }

    public void renderPlanets(MatrixStack matrices){

        for(CelestialBodyDisplay planet : celestialBodyDisplays){
            matrices.push();
            rotate(matrices, earthRotation);
            renderPlanet(planet.x, planet.y, matrices, planet.planetModel.getDefaultState());
            matrices.pop();
        }
    }

    public void checkForPlanetInteraction(int mouseX, int mouseY){
        for(CelestialBodyDisplay planet : celestialBodyDisplays){
            double width = 20;
            double height = 20;

            double y = planet.y - height;

            boolean colliding = mouseX >= (double)planet.x && mouseY >= y && mouseX < (planet.x + width) && mouseY < (y + height);
            if(colliding){
                this.currentPlanet = planet.name;
            }
        }
    }

    static {
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.SUN_BLOCK, 1, 0, 100, 100, "Sun"));
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.EARTH_BLOCK, 1, 0, 140, 140, "Earth"));
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.MOON_BLOCK, 1, 0 , 180, 180, "Moon"));
    }
}
