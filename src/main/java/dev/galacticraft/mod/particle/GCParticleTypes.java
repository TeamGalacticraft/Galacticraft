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

package dev.galacticraft.mod.particle;

import dev.galacticraft.mod.Constant.Particle;
import dev.galacticraft.mod.content.GCRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class GCParticleTypes {
    public static final GCRegistry<ParticleType<?>> PARTICLES = new GCRegistry<>(BuiltInRegistries.PARTICLE_TYPE);

    public static final SimpleParticleType DRIPPING_CRUDE_OIL = PARTICLES.register(Particle.DRIPPING_CRUDE_OIL, FabricParticleTypes.simple());
    public static final SimpleParticleType FALLING_CRUDE_OIL = PARTICLES.register(Particle.FALLING_CRUDE_OIL, FabricParticleTypes.simple());
    public static final SimpleParticleType DRIPPING_FUEL = PARTICLES.register(Particle.DRIPPING_FUEL, FabricParticleTypes.simple());
    public static final SimpleParticleType FALLING_FUEL = PARTICLES.register(Particle.FALLING_FUEL, FabricParticleTypes.simple());
    public static final SimpleParticleType DRIPPING_SULFURIC_ACID = PARTICLES.register(Particle.DRIPPING_SULFURIC_ACID, FabricParticleTypes.simple());
    public static final SimpleParticleType FALLING_SULFURIC_ACID = PARTICLES.register(Particle.FALLING_SULFURIC_ACID, FabricParticleTypes.simple());

    public static final SimpleParticleType CRYOGENIC_PARTICLE = PARTICLES.register(Particle.CRYOGENIC_PARTICLE, FabricParticleTypes.simple());
    public static final SimpleParticleType LANDER_FLAME_PARTICLE = PARTICLES.register(Particle.LANDER_FLAME, FabricParticleTypes.simple());
    public static final SimpleParticleType SPARK_PARTICLE = PARTICLES.register(Particle.SPARK, FabricParticleTypes.simple());
    public static final SimpleParticleType SPLASH_VENUS = PARTICLES.register(Particle.SPLASH_VENUS, FabricParticleTypes.simple());
    public static final ParticleType<LaunchSmokeParticleOption> LAUNCH_SMOKE_PARTICLE = PARTICLES.register(Particle.LAUNCH_SMOKE, FabricParticleTypes.complex(false, LaunchSmokeParticleOption.CODEC, LaunchSmokeParticleOption.STREAM_CODEC));
    public static final ParticleType<EntityParticleOption> LAUNCH_FLAME = PARTICLES.register(Particle.LAUNCH_FLAME, FabricParticleTypes.complex(false, EntityParticleOption::codec, EntityParticleOption::streamCodec));
    public static final ParticleType<EntityParticleOption> LAUNCH_FLAME_LAUNCHED = PARTICLES.register(Particle.LAUNCH_FLAME_LAUNCHED, FabricParticleTypes.complex(false, EntityParticleOption::codec, EntityParticleOption::streamCodec));
    public static final ParticleType<ScaleParticleType> ACID_VAPOR_PARTICLE = PARTICLES.register(Particle.ACID_VAPOR_PARTICLE, FabricParticleTypes.complex(false, ScaleParticleType::codec, ScaleParticleType::streamCodec));

    public static void register() {}
}
