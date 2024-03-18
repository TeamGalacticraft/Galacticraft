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

package dev.galacticraft.mod.particle;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCParticleTypes {
    public static final SimpleParticleType DRIPPING_FUEL = FabricParticleTypes.simple();
    public static final SimpleParticleType FALLING_FUEL = FabricParticleTypes.simple();
    public static final SimpleParticleType DRIPPING_CRUDE_OIL = FabricParticleTypes.simple();
    public static final SimpleParticleType FALLING_CRUDE_OIL = FabricParticleTypes.simple();
    public static final SimpleParticleType CRYOGENIC_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType LANDER_FLAME_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SPARK_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType DRIPPING_SULFURIC_ACID = FabricParticleTypes.simple();
    public static final SimpleParticleType FALLING_SULFURIC_ACID = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.DRIPPING_CRUDE_OIL), DRIPPING_CRUDE_OIL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.FALLING_CRUDE_OIL), FALLING_CRUDE_OIL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.DRIPPING_FUEL), DRIPPING_FUEL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.FALLING_FUEL), FALLING_FUEL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.CRYOGENIC_PARTICLE), CRYOGENIC_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.LANDER_FLAME), LANDER_FLAME_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.SPARK), SPARK_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.DRIPPING_SULFURIC_ACID), DRIPPING_SULFURIC_ACID);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Constant.id(Constant.Particle.FALLING_SULFURIC_ACID), FALLING_SULFURIC_ACID);
    }
}
