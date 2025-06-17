/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.util.concurrent.CompletableFuture;

/**
 * Displays the rocket's progress to the left of the screen
 */
public class RocketOverlay {
    /**
     * The skin of the player in the rocket.
     * Use one of the defaults if the skin cannot be found.
     */
    private static CompletableFuture<PlayerSkin> playerSkin;

    /**
     * Get the player's spaceship height off ground
     *
     * @param player thePlayer
     * @return position of player's spaceship
     */
    protected static int getPlayerPositionY(Player player) {
        if (player.getVehicle() != null && player.getVehicle() instanceof RocketEntity rocket) {
            return (int) Math.floor(rocket.getY());
        }

        return (int) Math.floor(player.getY());
    }

    public static void onHudRender(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.getVehicle() instanceof RocketEntity rocketEntity) {
            if (playerSkin == null) {
                playerSkin = mc.getSkinManager().getOrLoad(mc.player.getGameProfile());
            }

            Holder<CelestialBody<?, ?>> body = rocketEntity.level().galacticraft$getCelestialBody();
            final int height = mc.getWindow().getGuiScaledHeight();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, body != null ? body.value().display().rocketOverlay() : Constant.CelestialOverlay.EARTH);

            float scale = 0.65F;
            float x0 = 0.0F;
            float x1 = x0 + 20.0F * scale;
            float y0 = height / 2 - 170 / 2;
            float y1 = y0 + 242.0F * scale;
            float u0 = 0.0F;
            float u1 = u0 + 1.0F;
            float v0 = 0.0F;
            float v1 = v0 + 1.0F;

            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            buffer.addVertex(x0, y1, 0.0F).setUv(u0, v1)
                    .addVertex(x1, y1, 0.0F).setUv(u1, v1)
                    .addVertex(x1, y0, 0.0F).setUv(u1, v0)
                    .addVertex(x0, y0, 0.0F).setUv(u0, v0);
            BufferUploader.drawWithShader(buffer.buildOrThrow());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            EntityRenderer<RocketEntity> spaceshipRender = (EntityRenderer<RocketEntity>) mc.getEntityRenderDispatcher().getRenderer(rocketEntity);

            final int h = height / 2 + 60 - (int) Math.floor(getPlayerPositionY(mc.player) / 10.5F);
            scale = 1.0F / 8.0F;
            x0 = 2.5F;
            x1 = x0 + 8.0F;
            y0 = h - 10.0F;
            y1 = y0 + 8.0F;
            u0 = 5.0F * scale;
            u1 = u0 + scale;
            v0 = scale;
            v1 = v0 + scale;

            graphics.pose().pushPose();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().translate(x0 + 4.0F, y0 + 16.0F, 50.0F);
            graphics.pose().scale(5.0F, 5.0F, 5.0F);
            graphics.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
            graphics.pose().mulPose(Axis.YP.rotationDegrees(90.0F));

            try {
                MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
                spaceshipRender.render(rocketEntity, 0, 0, graphics.pose(), source, LightTexture.FULL_BRIGHT);
                source.endBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            RenderSystem.setShaderTexture(0, playerSkin.getNow(DefaultPlayerSkin.get(mc.player.getUUID())).texture());

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);

            graphics.pose().translate(0.0F, -7.0F, 60.0F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.addVertex(x0, y1, 0.0F).setUv(v0, v1)
                    .addVertex(x1, y1, 0.0F).setUv(v1, v1)
                    .addVertex(x1, y0, 0.0F).setUv(v1, v0)
                    .addVertex(x0, y0, 0.0F).setUv(v0, v0);
            BufferUploader.drawWithShader(buffer.buildOrThrow());

            if (mc.player.isModelPartShown(PlayerModelPart.HAT)) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buffer.addVertex(x0, y1, 0.0F).setUv(u0, v1)
                        .addVertex(x1, y1, 0.0F).setUv(u1, v1)
                        .addVertex(x1, y0, 0.0F).setUv(u1, v0)
                        .addVertex(x0, y0, 0.0F).setUv(u0, v0);
                BufferUploader.drawWithShader(buffer.buildOrThrow());
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            graphics.pose().popPose();
        }
    }
}
