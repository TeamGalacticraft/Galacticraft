package dev.galacticraft.mod.world.ships;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class Frictions {
    private static final HashMap<Block, Double> frictions = new HashMap<>();
    static {
        frictions.put(Blocks.DIRT, 1000d);
        frictions.put(Blocks.STONE, 1000d);
        frictions.put(Blocks.BRICKS, 1000d);
    }

    public static double get(BlockState initialBlock) {
        if (frictions.containsKey(initialBlock.getBlock()))
        {
            return frictions.get(initialBlock.getBlock());
        }else
        {
            return 1000;
        }
    }
}
