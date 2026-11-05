package dev.galacticraft.mod.client.particle;

import dev.galacticraft.mod.tag.GCFluidTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

@Environment(EnvType.CLIENT)
public class GCBubbleParticle extends TextureSheetParticle {
    private TagKey<Fluid> fluidTag;
    GCBubbleParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
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