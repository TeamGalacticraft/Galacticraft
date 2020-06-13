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

        public static final Codec<GCOreFeatureConfig.Target> field_24898 = StringIdentifiable.method_28140(GCOreFeatureConfig.Target::values, GCOreFeatureConfig.Target::byName);
        public static Map<String, GCOreFeatureConfig.Target> nameMap = Arrays.stream(values()).collect(Collectors.toMap(GCOreFeatureConfig.Target::getName, (target) -> target));
        public String name;
        public Predicate<BlockState> predicate;

        Target(String name, Predicate<BlockState> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public static GCOreFeatureConfig.Target byName(String name) {
            return nameMap.get(name);
        }

        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }

        public String asString() {
            return this.name;
        }
    }
}