/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.tag.GCFluidTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

@Environment(EnvType.CLIENT)
public class GCBubbleParticle extends TextureSheetParticle {
    private TagKey<Fluid> fluidTag;
    GCBubbleParticle(ClientLevel clientLevel, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(clientLevel, d, e, f, g, h, i);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.xd = g * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.yd = h * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.zd = i * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
        this.lifetime = (int)((double)8.0F / (Math.random() * 0.8 + 0.2));
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.yd += 0.002;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double)0.85F;
            this.yd *= (double)0.85F;
            this.zd *= (double)0.85F;
            if (GCFluidTags.SWIMMABLE.stream().noneMatch(this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z))::is)) {
                this.remove();
            }
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            GCBubbleParticle bubbleParticle = new GCBubbleParticle(clientLevel, d, e, f, g, h, i);
            bubbleParticle.pickSprite(this.sprite);
            return bubbleParticle;
        }
    }
}