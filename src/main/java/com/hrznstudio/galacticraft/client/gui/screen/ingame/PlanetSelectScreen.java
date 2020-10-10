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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.planetscreen.CelestialBodyDisplay;
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
import net.minecraft.screen.PlayerScreenHandler;
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
    private final int tier;

    public PlanetSelectScreen(int tier) {
        super(new TranslatableText("yes.yes.yes"));
        this.tier = tier;
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
        MinecraftClient.getInstance().getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        MinecraftClient.getInstance().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        matrices.translate((float) x, (float) y, 1.0F);
        matrices.scale(1.0F, -1.0F, 1.0F);
        matrices.scale(16.0F, 16.0F, 16.0F);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(earthRotation));
        matrices.translate(-0.5, -0.5, -0.5);

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
