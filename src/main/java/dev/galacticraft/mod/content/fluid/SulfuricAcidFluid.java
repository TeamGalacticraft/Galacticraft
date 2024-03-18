package dev.galacticraft.mod.content.fluid;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.particle.GCParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

/**
 * Sulfuric acid fluid
 */
public abstract class SulfuricAcidFluid extends BasicFluid {
    /**
     * Sulfuric acid fluid
     */
    public SulfuricAcidFluid() {
        super(false, true, 3, 2, 10, 100);
    }

    @Override
    public Fluid getFlowing() {
        return GCFluids.FLOWING_SULFURIC_ACID;
    }

    @Override
    public Fluid getSource() {
        return GCFluids.SULFURIC_ACID;
    }

    @Override
    public ParticleOptions getDripParticle() {
        return GCParticleTypes.DRIPPING_SULFURIC_ACID;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.WATER_AMBIENT);
    }

    @Override
    public Item getBucket() {
        return GCItems.SULFURIC_ACID_BUCKET;
    }

    @Override
    protected LiquidBlock getBlock() {
        return GCBlocks.SULFURIC_ACID;
    }

    public static class Still extends SulfuricAcidFluid {
        @Override
        public boolean isStill() {
            return true;
        }
    }

    public static class Flowing extends SulfuricAcidFluid {
        @Override
        public boolean isStill() {
            return false;
        }
    }
}