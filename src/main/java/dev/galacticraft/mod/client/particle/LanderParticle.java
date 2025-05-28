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

package dev.galacticraft.mod.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class LanderParticle extends TextureSheetParticle {
    private float smokeParticleScale;
    private final SpriteSet sprites;

    protected LanderParticle(ClientLevel clientLevel, double x, double y, double z, double mX, double mY, double mZ, SpriteSet sprites) {
        super(clientLevel, x, y, z, mX, mY, mZ);
        this.xd *= 0.10000000149011612D;
        this.zd *= 0.10000000149011612D;
        this.xd += mX;
        this.yd = mY;
        this.zd += mZ;
        this.rCol = 200F / 255F;
        this.gCol = 200F / 255F;
        this.bCol = 200F / 255F + this.random.nextFloat() / 3;
        this.quadSize *= 8F * 1.0F;
        this.smokeParticleScale = this.quadSize;
        this.lifetime = (int) 5.0D;
        this.hasPhysics = true;
        this.sprites = sprites;
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.smokeParticleScale * Mth.clamp((this.age + partialTicks) / this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        super.render(vertexConsumer, camera, f);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(this.sprites);
        this.move(this.xd, this.yd, this.zd);

        this.gCol -= 0.09F;
        this.rCol -= 0.09F;

        if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
        }

        this.quadSize *= 0.95999998F;

        this.xd *= 0.9599999785423279D;
        this.yd *= 0.9599999785423279D;
        this.zd *= 0.9599999785423279D;
    }

    @Environment(EnvType.CLIENT)
    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new LanderParticle(clientLevel, d, e, f, g, h, i, this.sprites);
        }
    }
}
