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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.GCBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static dev.galacticraft.mod.world.gen.feature.MeteorFeature.MeteorType.SINGULAR;

public abstract class GCConfiguredFeature {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_LAKE = key("oil_lake");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_METEOR = key("small_meteor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEDIUM_METEOR = key("medium_meteor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_METEOR = key("large_meteor");

    public static final Feature<MeteorFeature.Configuration> METEOR_FEATURE = new MeteorFeature(MeteorFeature.Configuration.CODEC);

    public static void bootstrapRegistries(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(OIL_LAKE, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(GCBlocks.CRUDE_OIL), BlockStateProvider.simple(Blocks.STONE))));

        context.register(SMALL_METEOR, new ConfiguredFeature<>(METEOR_FEATURE, new MeteorFeature.Configuration(
                false,
                0,
                SINGULAR,
                3,
                List.of(new MeteorFeature.Center(BlockStateProvider.simple(GCBlocks.ASTEROID_ALUMINUM_ORE), 1)),
                List.of(new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.ASTEROID_ROCK), 1)),
                Optional.empty()
        )));

        context.register(MEDIUM_METEOR, new ConfiguredFeature<>(METEOR_FEATURE, new MeteorFeature.Configuration(
                false,
                0,
                SINGULAR,
                9,
                List.of(new MeteorFeature.Center(BlockStateProvider.simple(GCBlocks.ASTEROID_ALUMINUM_ORE), 9)),
                List.of(new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.ASTEROID_ROCK), 3), new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.ASTEROID_ROCK_2), 2)),
                Optional.empty()
        )));

        context.register(LARGE_METEOR, new ConfiguredFeature<>(METEOR_FEATURE, new MeteorFeature.Configuration(
                false,
                0,
                SINGULAR,
                27,
                List.of(new MeteorFeature.Center(BlockStateProvider.simple(GCBlocks.ASTEROID_ALUMINUM_ORE), 9), new MeteorFeature.Center(BlockStateProvider.simple(GCBlocks.ASTEROID_IRON_ORE), 9), new MeteorFeature.Center(BlockStateProvider.simple(GCBlocks.ASTEROID_SILICON_ORE), 9)),
                List.of(new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.ASTEROID_ROCK), 3), new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.ASTEROID_ROCK_2), 2), new MeteorFeature.Shell(BlockStateProvider.simple(GCBlocks.DENSE_ICE), 1)),
                Optional.empty()
        )));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return Constant.key(Registries.CONFIGURED_FEATURE, name);
    }
}
