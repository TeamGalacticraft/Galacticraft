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