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

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGenerator;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDimensions {
    public static RegistryKey<World> MOON;
//    public static final DimensionType MOON = FabricDimensionType.builder().skyLight(true).desiredRawId(30).factory(MoonDimension::new).defaultPlacer((entity, serverWorld, direction, v, v1) -> new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), new Vec3d(0, 100, 0), 0)).buildAndRegister(new Identifier(Constants.MOD_ID, "moon"));

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(Constants.MOD_ID, "moon"), MoonChunkGenerator.CODEC);

        MOON = RegistryKey.of(Registry.DIMENSION, new Identifier(Constants.MOD_ID, "moon"));

        FabricDimensions.registerDefaultPlacer(MOON, GalacticraftDimensions::placeEntity);
    }

    private static BlockPattern.TeleportTarget placeEntity(Entity entity, ServerWorld world, Direction direction, double v, double v1) {
        return new BlockPattern.TeleportTarget(new Vec3d(entity.getX(), 128, entity.getZ()), Vec3d.ZERO, 0);
    }
}
