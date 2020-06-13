package com.hrznstudio.galacticraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class GCOreFeatureConfig implements FeatureConfig {
    public final OreTargetPredicate target;
    public final int size;
    public final BlockState state;

    public GCOreFeatureConfig(OreTargetPredicate target, BlockState state, int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("size"), ops.createInt(this.size), ops.createString("state"), BlockState.serialize(ops, this.state).getValue()))).merge(target.serialize(ops));
    }

    public static <T> GCOreFeatureConfig deserialize(Dynamic<T> dynamic) {
        int size = dynamic.get("size").asInt(0);
        BlockState state = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new GCOreFeatureConfig(OreTargetPredicate.deserialize(dynamic), state, size);
    }
    
    public static class OreTargetPredicate implements Predicate<Block> {
        private final Block[] blocks;
        
        public OreTargetPredicate(Block... blocks) {
            this.blocks = blocks;
        }

        @Override
        public boolean test(Block block) {
            for (Block block1 : blocks) {
                if (block == block1) {
                    return true;
                }
            }
            return false;
        }
        
        public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
            Map<T, T> map = new HashMap<>();
            map.put(ops.createString("blocks"), ops.createInt(blocks.length));
            for (int i = 0; i < blocks.length; i++) {
                map.put(ops.createString("block_" + i), ops.createString(Registry.BLOCK.getId(blocks[i]).toString()));
            }
            return new Dynamic<>(ops, ops.createMap(ImmutableMap.copyOf(map)));
        }
        
        public static <T> OreTargetPredicate deserialize(Dynamic<T> dynamic) {
            int size = dynamic.get("blocks").asInt(0);
            Block[] blocks = new Block[size];
            for (int i = 0; i < size; i++) {
                blocks[i] = Registry.BLOCK.get(new Identifier(dynamic.get("block_" + i).asString().orElse("minecraft:air")));
            }
            return new OreTargetPredicate(blocks);
        }
    }
}
