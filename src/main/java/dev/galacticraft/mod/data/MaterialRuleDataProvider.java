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

package dev.galacticraft.mod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.ShiftedScaledNoiseThresholdConditionSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.Function;

public class MaterialRuleDataProvider {
    public static final Codec<SurfaceRules.ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION
            .byNameCodec()
            .dispatch(conditionSource -> conditionSource.codec().codec(), Function.identity());

    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> SHIFTED_SCALED_NOISE_CONDITION =
            ResourceKey.create(Registries.MATERIAL_CONDITION, Constant.id("shifted_scaled_noise_threshold"));

    public static MapCodec<? extends SurfaceRules.ConditionSource> bootstrap(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> registry) {
        return Registry.register(registry, "shifted_scaled_noise_threshold", ShiftedScaledNoiseThresholdConditionSource.CODEC.codec());
    }

    public static void register() {
        Registry.register(
                BuiltInRegistries.MATERIAL_CONDITION,
                MaterialRuleDataProvider.SHIFTED_SCALED_NOISE_CONDITION.location(),
                ShiftedScaledNoiseThresholdConditionSource.CODEC.codec()
        );
    }
}
