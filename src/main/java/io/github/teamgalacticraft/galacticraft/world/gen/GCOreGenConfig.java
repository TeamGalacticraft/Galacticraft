package io.github.teamgalacticraft.galacticraft.world.gen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GCOreGenConfig implements FeatureConfig {
    public final BlockState target;
    public final BlockState state;
    public final int size;


    public GCOreGenConfig(BlockState target, BlockState blockState, int size) {
        this.target = target;
        this.size = size;
        this.state = blockState;
    }

    public static GCOreGenConfig deserialize(Dynamic<?> dynamic) {
        int size = dynamic.get("size").asInt(0);
        BlockState target = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.STONE.getDefaultState());
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new GCOreGenConfig(target, blockState, size);
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return null;
    }
}
