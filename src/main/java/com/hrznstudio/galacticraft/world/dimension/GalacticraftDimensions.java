/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSource;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGenerator;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDimensions {
    public static final RegistryKey<World> MOON = RegistryKey.of(Registry.DIMENSION, new Identifier(Constants.MOD_ID, "moon"));
    public static final DimensionType MOON_TYPE = new DimensionType(OptionalLong.empty(), true, false, false, true, false, false, false, false, false, true, 256, HorizontalVoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getId(), 0.1F) {
        @Override
        public int method_28531(long l) {
            double d = MathHelper.fractionalPart((double)l / 24000.0D - 0.25D);
            double e = 0.5D - Math.cos(d * 3.14159265358979323846D) / 2.0D;
            return (int) ((d * 2.0D + e) / 3.0D);
        }
    };

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(Constants.MOD_ID, "moon"), MoonChunkGenerator.CODEC);
        FabricDimensions.registerDefaultPlacer(MOON, GalacticraftDimensions::placeEntity);
    }

    public static void addGCDims(RegistryTracker.Modifiable registryTracker) {
        registryTracker.addDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(Constants.MOD_ID, "moon")), MOON_TYPE);
    }

    private static BlockPattern.TeleportTarget placeEntity(Entity entity, ServerWorld world, Direction direction, double v, double v1) {
        return new BlockPattern.TeleportTarget(new Vec3d(entity.getX(), 128, entity.getZ()), Vec3d.ZERO, 0);
    }

    public static SimpleRegistry<DimensionOptions> addGCDimOptions(long seed, SimpleRegistry<DimensionOptions> registry) {
        registry.add(RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier(Constants.MOD_ID, "moon")), new DimensionOptions(() -> MOON_TYPE, new MoonChunkGenerator(new MoonBiomeSource(seed, 4), seed)));
        registry.markLoaded(RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier(Constants.MOD_ID, "moon")));
        return registry;
    }
}
