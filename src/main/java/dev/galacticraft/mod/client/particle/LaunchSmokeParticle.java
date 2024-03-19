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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.particle.LaunchSmokeParticleOption;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LaunchSmokeParticle extends LaunchParticle {
    float smokeParticleScale;
    private final SpriteSet sprites;

    public LaunchSmokeParticle(ClientLevel level, double posX, double posY, double posZ, double motX, double motY, double motZ, float size, boolean launched, SpriteSet sprites) {
        super(level, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        this.xd *= 0.10000000149011612D;
        this.yd *= 0.10000000149011612D;
        this.zd *= 0.10000000149011612D;
        this.setSize(0.2F, 0.2F);
        this.xd += motX;
        this.yd += motY;
        this.zd += motZ;
        this.alpha = 1.0F;
        this.rCol = this.gCol = this.bCol = (float) (Math.random() * 0.30000001192092896D) + 0.6F;
        this.quadSize *= 0.75F;
        this.quadSize *= size * 3;
        this.smokeParticleScale = this.quadSize;
        this.sprites = sprites;

        if (launched) {
            this.lifetime = (int) (this.lifetime * size) + 10;
        } else {
            this.xd += level.random.nextDouble() / 2 - 0.25;
            this.yd += level.random.nextDouble() / 20;
            this.zd += level.random.nextDouble() / 2 - 0.25;
            this.lifetime = 30 + this.lifetime;
        }

        this.hasPhysics = true;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        float var8 = (this.age + partialTicks) / this.lifetime * 32.0F;

        if (var8 < 0.0F) {
            var8 = 0.0F;
        }

        if (var8 > 1.0F) {
            var8 = 1.0F;
        }

        this.quadSize = this.smokeParticleScale * var8;
        super.render(buffer, camera, partialTicks);

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
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<LaunchSmokeParticleOption> {
        @Override
        public Particle createParticle(LaunchSmokeParticleOption option, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LaunchSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, option.getScale(), option.isLaunched(), this.sprites);
        }
    }
}
