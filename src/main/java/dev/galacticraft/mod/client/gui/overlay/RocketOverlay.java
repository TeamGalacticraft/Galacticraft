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

package dev.galacticraft.mod.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Displays the rocket's progress to the left of the screen
 */
public class RocketOverlay {
    private static final ResourceLocation TEXTURE = Constant.id("textures/gui/overworld_rocket_gui.png");
    private static ResourceLocation playerHead;

    /**
     * Get the player's spaceship height off ground
     *
     * @param player thePlayer
     * @return position of player's spaceship
     */
    protected static int getPlayerPositionY(Player player)
    {
        if (player.getVehicle() != null && player.getVehicle() instanceof RocketEntity rocket)
        {
            return (int) Math.floor(rocket.getY());
        }

        return (int) Math.floor(player.getY());
    }
    public static void onHudRender(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (playerHead == null) {
            playerHead = mc.getSkinManager().getInsecureSkin(mc.player.getGameProfile()).texture();
        }
        if (Minecraft.getInstance().player.getVehicle() instanceof RocketEntity rocketEntity) {
            final int height = mc.getWindow().getGuiScaledHeight();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);

            float var1 = 0F;
            float var2 = height / 2 - 170 / 2;
            float var3 = 0.0F;
            float var3b = 0.0F;
            float var4 = 0.0F;
            float var5 = 1.0F;
            float var6 = 1.0F;
            float var7 = 1.0F;
            float var8 = 1.0F;
            float sizeScale = 0.65F;

            final Tesselator tess = Tesselator.getInstance();
            BufferBuilder worldRenderer = tess.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            worldRenderer.vertex(var1 + 0, var2 + 242.0F * sizeScale, 0.0).uv((var3 + 0) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + 20.0F * sizeScale, var2 + 242.0F * sizeScale, 0.0).uv((var3 + var5) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + 20.0F * sizeScale, var2 + 0, 0.0).uv((var3 + var5) * var7, (var4 + 0) * var8).endVertex();
            worldRenderer.vertex(var1 + 0, var2 + 0, 0.0).uv((var3 + 0) * var7, (var4 + 0) * var8).endVertex();
            BufferUploader.drawWithShader(worldRenderer.end());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


            float headOffset = 5F;

            EntityRenderer<RocketEntity> spaceshipRender = (EntityRenderer<RocketEntity>) mc.getEntityRenderDispatcher().getRenderer(rocketEntity);

            final int y1 = height / 2 + 60 - (int) Math.floor(getPlayerPositionY(mc.player) / 10.5F);
            var1 = 2.5F;
            var2 = y1;
            var3 = 8;
            var3b = 40;
            var4 = 8;
            var5 = 8;
            var6 = 8;
            var7 = 1.0F / 64.0F;
            var8 = 1.0F / 64.0F;

            graphics.pose().pushPose();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().translate(var1 + 4, var2 + 6, 50F);
            graphics.pose().scale(5F, 5F, 5F);
            graphics.pose().mulPose(Axis.XP.rotationDegrees(180F));
            graphics.pose().mulPose(Axis.YP.rotationDegrees(90F));

            try {
                MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
                spaceshipRender.render(rocketEntity, rocketEntity.getYRot(), tickDelta, graphics.pose(), source, LightTexture.FULL_BRIGHT);
                source.endBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            RenderSystem.setShaderTexture(0, playerHead);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);

            graphics.pose().translate(0F, -12F + headOffset, 60F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            worldRenderer.vertex(var1 + 0, var2 + var6 - 10, 0.0).uv((var3 + 0) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + var5, var2 + var6 - 10, 0.0).uv((var3 + var5) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + var5, var2 - 10, 0.0).uv((var3 + var5) * var7, (var4 + 0) * var8).endVertex();
            worldRenderer.vertex(var1 + 0, var2 - 10, 0.0).uv((var3 + 0) * var7, (var4 + 0) * var8).endVertex();
            BufferUploader.drawWithShader(worldRenderer.end());

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            worldRenderer.vertex(var1 + 0, var2 + var6, 0.0).uv((var3b + 0) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + var5, var2 + var6, 0.0).uv((var3b + var5) * var7, (var4 + var6) * var8).endVertex();
            worldRenderer.vertex(var1 + var5, var2 + 0, 0.0).uv((var3b + var5) * var7, (var4 + 0) * var8).endVertex();
            worldRenderer.vertex(var1 + 0, var2 + 0, 0.0).uv((var3b + 0) * var7, (var4 + 0) * var8).endVertex();
            BufferUploader.drawWithShader(worldRenderer.end());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            graphics.pose().popPose();
        }
    }
}
