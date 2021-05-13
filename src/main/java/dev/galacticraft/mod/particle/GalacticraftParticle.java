/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftParticle {

    public static final DefaultParticleType DRIPPING_FUEL_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Constant.MOD_ID, Constant.Particle.DRIPPING_CRUDE_OIL_PARTICLE), FabricParticleTypes.simple());
    public static final DefaultParticleType DRIPPING_CRUDE_OIL_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Constant.MOD_ID, Constant.Particle.DRIPPING_FUEL_PARTICLE), FabricParticleTypes.simple());
    public static final DefaultParticleType DRIPPING_BACTERIAL_SLUDGE_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Constant.MOD_ID, Constant.Particle.DRIPPING_BACTERIAL_SLUDGE_PARTICLE), FabricParticleTypes.simple());

    public static void register() {
    }
}
