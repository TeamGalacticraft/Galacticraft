package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.planetscreen.CelestialBodyDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlanetSelectScreen extends Screen {

    public static final List<CelestialBodyDisplay> celestialBodyDisplays = new ArrayList<>();
    public static final Identifier SKY = new Identifier(Constants.MOD_ID, "textures/planet_select_screen/stars.png");

    static {
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.SUN_BLOCK, 1, 0, 200, 130, "Sun", null));
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.EARTH_BLOCK, 1, 0, 140, 140, "Earth", new Identifier("overworld")));
        celestialBodyDisplays.add(new CelestialBodyDisplay(GalacticraftBlocks.MOON_BLOCK, 1, 0, 180, 180, "Moon", new Identifier("galacticraft-rewoven", "moon")));
    }

    public CelestialBodyDisplay currentPlanet = null;
    private float earthRotation = 0;

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
        renderBackground(matrices);
        checkForPlanetInteraction(mouseX, mouseY);
        renderPlanets(matrices);
        if(currentPlanet != null){
            textRenderer.draw(matrices, currentPlanet.name, 50, 10, 111111);
        }
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
        matrices.translate((float) x, (float) y, 100.0F);
        matrices.scale(1.0F, -1.0F, 1.0F);
        matrices.scale(16.0F, 16.0F, 16.0F);

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(planet, matrices, immediate, 10000, OverlayTexture.DEFAULT_UV);
        immediate.draw();
    }

    public void rotate(MatrixStack stack, float angle, float pivotX, float pivotY) {
        stack.translate(-pivotX, -pivotY, 0);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
        stack.translate(pivotX, pivotY, 0);
    }

    public void renderPlanets(MatrixStack matrices) {

        for (CelestialBodyDisplay planet : celestialBodyDisplays) {
            matrices.push();
            rotate(matrices, earthRotation, planet.x, planet.y);
            renderPlanet(planet.x, planet.y, matrices, planet.planetModel.getDefaultState());
            matrices.pop();
        }
    }

    public void checkForPlanetInteraction(int mouseX, int mouseY) {
        boolean somethingHappened = false;
        for (CelestialBodyDisplay planet : celestialBodyDisplays) {
            double width = 20;
            double height = 20;

            double y = planet.y - height;

            boolean colliding = mouseX >= (double) planet.x && mouseY >= y && mouseX < (planet.x + width) && mouseY < (y + height);
            if (colliding) {
                somethingHappened = true;
                this.currentPlanet = planet;
            }
        }
        if (!somethingHappened) {
            this.currentPlanet = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT){
            if(currentPlanet != null) {
                if(currentPlanet.dimension == null) return false;
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeIdentifier(currentPlanet.dimension);
                ClientSidePacketRegistry.INSTANCE.sendToServer(new Identifier(Constants.MOD_ID, "dimension_teleport"), passedData);
            }
        }
        return true;
    }

}
