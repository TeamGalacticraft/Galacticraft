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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CryoFreezeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    float scaleStart;

    protected CryoFreezeParticle(ClientLevel clientLevel, double x, double y, double z, double mX, double mY, double mZ, SpriteSet spriteSet) {
        super(clientLevel, x, y, z, mX, mY, mZ);
        float f = 2.5F;
        this.xd *= 0.0;
        this.yd *= 0.10000000149011612;
        this.zd *= 0.0;
        this.xd += mX;
        this.yd += mY;
        this.zd += mZ;
        this.rCol = this.gCol = this.bCol = 1.0F - (float) (Math.random() * 0.30000001192092896D);
        this.rCol *= 0.8F;
        this.gCol *= 0.8F;
        this.quadSize *= 0.25F;
        this.quadSize *= f;
        this.scaleStart = this.quadSize;
        this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.3D));
        this.lifetime = (int) ((float) this.lifetime * f);
        this.hasPhysics = false;
        this.spriteSet = spriteSet;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return this.scaleStart * Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        setSpriteFromAge(this.spriteSet);
        this.move(0, this.yd, 0);
        this.yd *= 0.9599999785423279D;
        Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);

        if (player != null && this.y > player.getBoundingBox().minY) {
            this.y += (player.getBoundingBox().minY - this.y) * 0.2D;
            this.yd += (player.getDeltaMovement().y - this.yd) * 0.2D;
            this.setPos(this.x, this.y, this.z);
        }

        if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new CryoFreezeParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }
}
