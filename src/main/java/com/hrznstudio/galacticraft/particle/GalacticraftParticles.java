/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.particle;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.particle.fluid.DrippingCrudeOilParticle;
import com.hrznstudio.galacticraft.particle.fluid.DrippingFuelParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftParticles {

    public static final DefaultParticleType DRIPPING_FUEL_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType DRIPPING_CRUDE_OIL_PARTICLE = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Constants.MOD_ID, Constants.Particles.DRIPPING_CRUDE_OIL_PARTICLE), DRIPPING_FUEL_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Constants.MOD_ID, Constants.Particles.DRIPPING_FUEL_PARTICLE), DRIPPING_CRUDE_OIL_PARTICLE);
        Galacticraft.logger.info("Registered particles!");
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ParticleFactoryRegistry.getInstance().register(DRIPPING_FUEL_PARTICLE, (effect1, world1, x1, y1, z1, velX1, velY1, velZ1) -> new DrippingFuelParticle(world1, x1, y1, z1, velX1, velY1, velZ1));
        ParticleFactoryRegistry.getInstance().register(DRIPPING_CRUDE_OIL_PARTICLE, (effect, world, x, y, z, velX, velY, velZ) -> new DrippingCrudeOilParticle(world, x, y, z, velX, velY, velZ));
    }
}
