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

package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GCOreFeatureConfig implements FeatureConfig {
    public static final Codec<GCOreFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Target.field_24898.fieldOf("target").forGetter((oreFeatureConfig) -> oreFeatureConfig.target), BlockState.CODEC.fieldOf("state").forGetter((oreFeatureConfig) -> oreFeatureConfig.state), Codec.INT.fieldOf("size").withDefault(0).forGetter((oreFeatureConfig) -> oreFeatureConfig.size)).apply(instance, GCOreFeatureConfig::new));

    public final GCOreFeatureConfig.Target target;
    public final int size;
    public final BlockState state;

    public GCOreFeatureConfig(GCOreFeatureConfig.Target target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }

    public enum Target implements StringIdentifiable {
        MOON("moon", (blockState) -> {
            if (blockState == null) {
                return false;
            } else {
                return blockState.isOf(GalacticraftBlocks.MOON_ROCK);
            }
        });

        public static Map<String, GCOreFeatureConfig.Target> nameMap = Arrays.stream(values()).collect(Collectors.toMap(GCOreFeatureConfig.Target::getName, (target) -> target));
        public static final Codec<GCOreFeatureConfig.Target> field_24898 = StringIdentifiable.method_28140(GCOreFeatureConfig.Target::values, GCOreFeatureConfig.Target::byName);
        public String name;
        public Predicate<BlockState> predicate;

        Target(String name, Predicate<BlockState> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        public static GCOreFeatureConfig.Target byName(String name) {
            return nameMap.get(name);
        }

        public String getName() {
            return this.name;
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        public String asString() {
            return this.name;
        }
    }
}