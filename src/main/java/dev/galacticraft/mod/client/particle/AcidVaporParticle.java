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

package dev.galacticraft.mod.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.particle.ScaleParticleType;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class AcidVaporParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    protected AcidVaporParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, float scale) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.xd *= 0.10000000149011612D;
        this.yd *= 0.10000000149011612D;
        this.zd *= 0.10000000149011612D;
        this.xd += xSpeed / 10.0F;
        this.yd += ySpeed;
        this.zd += zSpeed / 10.0F;
        this.rCol = (float) (Math.random() * 0.10000001192092896D + 0.8);
        this.gCol = rCol;
        this.bCol = (float) (Math.random() * 0.10000001192092896D);
        this.alpha = 1.0F;
        this.scale(scale);
        this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.lifetime = (int) ((float) this.lifetime * scale);
        this.hasPhysics = true;
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float f = (float) Math.pow(this.age / 11.0, 2.0F);
        f = Math.max(f, 0.1F);
        return this.quadSize * f;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        this.alpha = this.alpha * 0.994F;
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            setSpriteFromAge(this.sprites);
            this.yd -= 0.04;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1;
                this.zd *= 1.1;
            }

            this.xd *= 1.001D;
            this.yd *= this.friction;
            this.zd *= 1.001D;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ScaleParticleType> {
        @Override
        public Particle createParticle(ScaleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AcidVaporParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, type.scale());
        }
    }
}
