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

package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
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

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@SuppressWarnings("PointlessArithmeticExpression")
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {
//    private static final Identifier EARTH_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/earth.png");
//    private static final Identifier SUN_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/sun.png");
//    @Shadow
//    @Final
//    private MinecraftClient client;
//    @Shadow
//    private ClientWorld world;
//    private final int starGLCallList = GL11.glGenLists(3);
//    private int glSkyList;
//    private int glSkyList2;
//
//    @Inject(at = @At("RETURN"), method = "<init>")
//    private void initGalacticraft(MinecraftClient client, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
//        RenderSystem.pushMatrix();
//        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
//        this.renderStarsGalacticraft();
//        GL11.glEndList();
//        RenderSystem.popMatrix();
//        final Tessellator tessellator = Tessellator.getInstance();
//        this.glSkyList = this.starGLCallList + 1;
//        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
//        final byte byte2 = 64;
//        final int i = 256 / byte2 + 2;
//        float f = 16F;
//        BufferBuilder buffer = tessellator.getBuffer();
//
//        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
//            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
//                buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
//                buffer.vertex(j + 0, f, l + 0).next();
//                buffer.vertex(j + byte2, f, l + 0).next();
//                buffer.vertex(j + byte2, f, l + byte2).next();
//                buffer.vertex(j + 0, f, l + byte2).next();
//                tessellator.draw();
//            }
//        }
//
//        GL11.glEndList();
//        this.glSkyList2 = this.starGLCallList + 2;
//        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
//        f = -16F;
//        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
//
//        for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
//            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
//                buffer.vertex(k + byte2, f, i1 + 0).next();
//                buffer.vertex(k + 0, f, i1 + 0).next();
//                buffer.vertex(k + 0, f, i1 + byte2).next();
//                buffer.vertex(k + byte2, f, i1 + byte2).next();
//            }
//        }
//
//        tessellator.draw();
//        GL11.glEndList();
//    }
//
//    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
//    private void renderGalacticraft(MatrixStack matrixStack, float f, CallbackInfo ci) {
//        if (this.world.getDimension().getType() == GalacticraftDimensions.MOON) {
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            RenderSystem.disableRescaleNormal();
//            RenderSystem.color3f(1F, 1F, 1F);
//            final Tessellator tessellator = Tessellator.getInstance();
//            RenderSystem.depthMask(false);
//            RenderSystem.enableFog();
//            RenderSystem.color3f(0, 0, 0);
//            GL11.glCallList(this.glSkyList);
//            RenderSystem.disableFog();
//            RenderSystem.disableAlphaTest();
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            RenderSystem.disableLighting();
//            float var10;
//            float var11;
//            float var12;
//
//            float var20 = 1.0F; //TESTING
//
//            matrixStack.push();
//            RenderSystem.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
//            RenderSystem.rotatef(this.world.getSkyAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(-19.0F, 0, 1.0F, 0);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, var20);
//            GL11.glCallList(this.starGLCallList);
//            matrixStack.pop();
//
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//            matrixStack.push();
//
//            matrixStack.pop();
//
//            matrixStack.push();
//
//            RenderSystem.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.0F); //TODO
//            RenderSystem.rotatef(this.world.getSkyAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F); //TODO
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.0F);
//            var12 = 20.0F / 3.5F;
//            BufferBuilder buffer = tessellator.getBuffer();
//            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
//            buffer.vertex(-var12, 99.9D, -var12).next();
//            buffer.vertex(var12, 99.9D, -var12).next();
//            buffer.vertex(var12, 99.9D, var12).next();
//            buffer.vertex(-var12, 99.9D, var12).next();
//            tessellator.draw();
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//            var12 = 15.0F;
//            client.getTextureManager().bindTexture(SUN_TEXTURE);
//            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
//            buffer.vertex(-var12, 100.0D, -var12).texture(0.0F, 0.0F).next();
//            buffer.vertex(var12, 100.0D, -var12).texture(1.0F, 0.0F).next();
//            buffer.vertex(var12, 100.0D, var12).texture(1.0F, 1.0F).next();
//            buffer.vertex(-var12, 100.0D, var12).texture(0.0F, 1.0F).next();
//            tessellator.draw();
//
//            matrixStack.pop();
//
//            matrixStack.push();
//
//            RenderSystem.disableBlend();
//
//            // EARTH:
//            var12 = 10.0F;
//            final float earthRotation = (float) (this.world.getSpawnPos().getZ() - client.player.getPos().z) * 0.01F;
//            matrixStack.scale(0.6F, 0.6F, 0.6F);
//            RenderSystem.rotatef(earthRotation, 1.0F, 0.0F, 0.0F);
//            RenderSystem.rotatef(200F, 1.0F, 0.0F, 0.0F);
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1F);
//
//            // Overworld texture is 48x48 in a 64x64 .png file
//            client.getTextureManager().bindTexture(EARTH_TEXTURE);
//
//            this.world.getMoonPhase();
//            buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
//            buffer.vertex(-var12, -100.0D, var12).texture(0F, 1.0F).next();
//            buffer.vertex(var12, -100.0D, var12).texture(1.0F, 1.0F).next();
//            buffer.vertex(var12, -100.0D, -var12).texture(1.0F, 0F).next();
//            buffer.vertex(-var12, -100.0D, -var12).texture(0F, 0F).next();
//            tessellator.draw();
//
//            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.disableBlend();
//            RenderSystem.enableAlphaTest();
//            RenderSystem.enableFog();
//            matrixStack.pop();
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
//            final double var25 = client.player.getPos().getY() - this.world.getHeight();
//
//            if (var25 < 0.0D) {
//                matrixStack.push();
//                matrixStack.translate(0.0F, 12.0F, 0.0F);
//                GL11.glCallList(this.glSkyList2);
//                matrixStack.pop();
//                var10 = 1.0F;
//                var11 = -((float) (var25 + 65.0D));
//                var12 = -var10;
//                buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
//                buffer.vertex(-var10, var11, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var11, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var11, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var11, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var11, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var11, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var11, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var11, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(-var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, var10).color(0, 0, 0, 1.0F).next();
//                buffer.vertex(var10, var12, -var10).color(0, 0, 0, 1.0F).next();
//                tessellator.draw();
//            }
//
//            RenderSystem.color3f(70F / 256F, 70F / 256F, 70F / 256F);
//
//            matrixStack.push();
//            matrixStack.translate(0.0F, -((float) (var25 - 16.0D)), 0.0F);
//            GL11.glCallList(this.glSkyList2);
//            matrixStack.pop();
//            RenderSystem.enableRescaleNormal();
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            RenderSystem.depthMask(true);
//            RenderSystem.enableColorMaterial();
//            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            RenderSystem.disableBlend();
//            ci.cancel();
//            //noinspection UnnecessaryReturnStatement
//            return;
//        }
//    }
//
//    private void renderStarsGalacticraft() {
//        Random random = new Random(10842L);
//
//        final Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferBuilder = tessellator.getBuffer();
//        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION);
//        for (int var3 = 0; var3 < 6000; ++var3) {
//            double var4 = random.nextFloat() * 2.0F - 1.0F;
//            double var6 = random.nextFloat() * 2.0F - 1.0F;
//            double var8 = random.nextFloat() * 2.0F - 1.0F;
//            final double var10 = 0.15F + random.nextFloat() * 0.1F;
//            double var12 = var4 * var4 + var6 * var6 + var8 * var8;
//
//            if (var12 < 1.0D && var12 > 0.01D) {
//                var12 = 1.0D / Math.sqrt(var12);
//                var4 *= var12;
//                var6 *= var12;
//                var8 *= var12;
//                final double var14 = var4 * 100.0D;
//                final double var16 = var6 * 100.0D;
//                final double var18 = var8 * 100.0D;
//                final double var20 = Math.atan2(var4, var8);
//                final double var22 = Math.sin(var20);
//                final double var24 = Math.cos(var20);
//                final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
//                final double var28 = Math.sin(var26);
//                final double var30 = Math.cos(var26);
//                final double var32 = random.nextDouble() * Math.PI * 2.0D;
//                final double var34 = Math.sin(var32);
//                final double var36 = Math.cos(var32);
//
//                for (int var38 = 0; var38 < 4; ++var38) {
//                    final double var39 = 0.0D;
//                    final double var41 = ((var38 & 2) - 1) * var10;
//                    final double var43 = ((var38 + 1 & 2) - 1) * var10;
//                    final double var47 = var41 * var36 - var43 * var34;
//                    final double var49 = var43 * var36 + var41 * var34;
//                    final double var53 = var47 * var28 + var39 * var30;
//                    final double var55 = var39 * var28 - var47 * var30;
//                    final double var57 = var55 * var22 - var49 * var24;
//                    final double var61 = var49 * var22 + var55 * var24;
//                    bufferBuilder.vertex(var14 + var57, var16 + var53, var18 + var61).next();
//                }
//            }
//        }
//        tessellator.draw();
//    }

}
