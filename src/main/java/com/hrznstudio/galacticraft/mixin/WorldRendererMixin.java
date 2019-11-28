package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.math.compat.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@SuppressWarnings("PointlessArithmeticExpression")
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    private static final Identifier EARTH_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/earth.png");
    private static final Identifier SUN_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/sun.png");
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;
    private int starGLCallList = GlAllocationUtils.genLists(3);
    private int glSkyList;
    private int glSkyList2;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void initGalacticraft(MinecraftClient minecraftClient_1, CallbackInfo ci) {
        GlStateManager.pushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStarsGalacticraft();
        GL11.glEndList();
        GlStateManager.popMatrix();
        final Tessellator tessellator = Tessellator.getInstance();
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        final byte byte2 = 64;
        final int i = 256 / byte2 + 2;
        float f = 16F;
        BufferBuilder buffer = tessellator.getBufferBuilder();

        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
                buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
                buffer.vertex(j + 0, f, l + 0).next();
                buffer.vertex(j + byte2, f, l + 0).next();
                buffer.vertex(j + byte2, f, l + byte2).next();
                buffer.vertex(j + 0, f, l + byte2).next();
                tessellator.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        f = -16F;
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);

        for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
                buffer.vertex(k + byte2, f, i1 + 0).next();
                buffer.vertex(k + 0, f, i1 + 0).next();
                buffer.vertex(k + 0, f, i1 + byte2).next();
                buffer.vertex(k + byte2, f, i1 + byte2).next();
            }
        }

        tessellator.draw();
        GL11.glEndList();
    }

    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
    private void renderGalacticraft(float partialTicks, CallbackInfo callbackInfo) {
        if (this.world.dimension.getType() == GalacticraftDimensions.MOON) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.disableRescaleNormal();
            GlStateManager.color3f(1F, 1F, 1F);
            final Tessellator tessellator = Tessellator.getInstance();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color3f(0, 0, 0);
            GlStateManager.callList(this.glSkyList);
            GlStateManager.disableFog();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableLighting();
            float var10;
            float var11;
            float var12;

            float var20 = 1.0F; //TESTING

            if (var20 > 0.0F) {
                GlStateManager.pushMatrix();
                GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(this.world.getSkyAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(-19.0F, 0, 1.0F, 0);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, var20);
                GlStateManager.callList(this.starGLCallList);
                GlStateManager.popMatrix();
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.pushMatrix();

            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();

            GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.0F); //TODO
            GlStateManager.rotatef(this.world.getSkyAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F); //TODO
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.0F);
            var12 = 20.0F / 3.5F;
            BufferBuilder buffer = tessellator.getBufferBuilder();
            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
            buffer.vertex(-var12, 99.9D, -var12).next();
            buffer.vertex(var12, 99.9D, -var12).next();
            buffer.vertex(var12, 99.9D, var12).next();
            buffer.vertex(-var12, 99.9D, var12).next();
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            var12 = 15.0F;
            client.getTextureManager().bindTexture(SUN_TEXTURE);
            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
            buffer.vertex(-var12, 100.0D, -var12).texture(0.0D, 0.0D).next();
            buffer.vertex(var12, 100.0D, -var12).texture(1.0D, 0.0D).next();
            buffer.vertex(var12, 100.0D, var12).texture(1.0D, 1.0D).next();
            buffer.vertex(-var12, 100.0D, var12).texture(0.0D, 1.0D).next();
            tessellator.draw();

            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();

            GlStateManager.disableBlend();

            // EARTH:
            var12 = 10.0F;
            final float earthRotation = (float) (this.world.getSpawnPos().getZ() - client.player.z) * 0.01F;
            GlStateManager.scalef(0.6F, 0.6F, 0.6F);
            GlStateManager.rotatef(earthRotation, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(200F, 1.0F, 0.0F, 0.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1F);

            // Overworld texture is 48x48 in a 64x64 .png file
            client.getTextureManager().bindTexture(EARTH_TEXTURE);

            this.world.getMoonPhase();
            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
            buffer.vertex(-var12, -100.0D, var12).texture(0D, 1.0D).next();
            buffer.vertex(var12, -100.0D, var12).texture(1.0D, 1.0D).next();
            buffer.vertex(var12, -100.0D, -var12).texture(1.0D, 0D).next();
            buffer.vertex(-var12, -100.0D, -var12).texture(0D, 0D).next();
            tessellator.draw();

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.color3f(0.0F, 0.0F, 0.0F);
            final double var25 = client.player.getPos().getY() - this.world.getHorizonHeight();

            if (var25 < 0.0D) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0F, 12.0F, 0.0F);
                GlStateManager.callList(this.glSkyList2);
                GlStateManager.popMatrix();
                var10 = 1.0F;
                var11 = -((float) (var25 + 65.0D));
                var12 = -var10;
                buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
                buffer.vertex(-var10, var11, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var11, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var11, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var11, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var11, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var11, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var11, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var11, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
                tessellator.draw();
            }

            GlStateManager.color3f(70F / 256F, 70F / 256F, 70F / 256F);

            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, -((float) (var25 - 16.0D)), 0.0F);
            GlStateManager.callList(this.glSkyList2);
            GlStateManager.popMatrix();
            GlStateManager.enableRescaleNormal();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.depthMask(true);
            GlStateManager.enableColorMaterial();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            callbackInfo.cancel();
            return;
        }
    }

    private void renderStarsGalacticraft() {
        Random random = new Random(10842L);

        final Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION);
        for (int var3 = 0; var3 < 6000; ++var3) {
            double var4 = random.nextFloat() * 2.0F - 1.0F;
            double var6 = random.nextFloat() * 2.0F - 1.0F;
            double var8 = random.nextFloat() * 2.0F - 1.0F;
            final double var10 = 0.15F + random.nextFloat() * 0.1F;
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D) {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                final double var14 = var4 * 100.0D;
                final double var16 = var6 * 100.0D;
                final double var18 = var8 * 100.0D;
                final double var20 = Math.atan2(var4, var8);
                final double var22 = Math.sin(var20);
                final double var24 = Math.cos(var20);
                final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                final double var28 = Math.sin(var26);
                final double var30 = Math.cos(var26);
                final double var32 = random.nextDouble() * Math.PI * 2.0D;
                final double var34 = Math.sin(var32);
                final double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38) {
                    final double var39 = 0.0D;
                    final double var41 = ((var38 & 2) - 1) * var10;
                    final double var43 = ((var38 + 1 & 2) - 1) * var10;
                    final double var47 = var41 * var36 - var43 * var34;
                    final double var49 = var43 * var36 + var41 * var34;
                    final double var53 = var47 * var28 + var39 * var30;
                    final double var55 = var39 * var28 - var47 * var30;
                    final double var57 = var55 * var22 - var49 * var24;
                    final double var61 = var49 * var22 + var55 * var24;
                    bufferBuilder.vertex(var14 + var57, var16 + var53, var18 + var61).next();
                }
            }
        }
        tessellator.draw();
    }

}
