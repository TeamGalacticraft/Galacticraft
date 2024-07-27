package dev.galacticraft.mod.world.ships;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class Restitutions {
    private static final HashMap<Block, Float> restitutions = new HashMap<>();
    static {
        restitutions.put(Blocks.DIRT, 0.25f);
        restitutions.put(Blocks.STONE, 0.2f);
        restitutions.put(Blocks.BRICKS, 0.2f);
        restitutions.put(Blocks.SLIME_BLOCK, 0.8f);
        restitutions.put(Blocks.HONEY_BLOCK, 1.0f);
        restitutions.put(Blocks.GOLD_BLOCK, 1.2f);
    }

    public static float get(BlockState initialBlock) {
        return restitutions.getOrDefault(initialBlock.getBlock(), 0.4f);
    }
}
