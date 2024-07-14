package dev.galacticraft.mod.world.gen.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class MapGenAbandonedBase {
    public static void generateStructure(ServerLevel world, Random rand, BlockPos pos) {
        // Example: Simple 3x3x3 cube of stone
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    world.setBlock(pos.offset(x, y, z), Blocks.STONE.defaultBlockState(), 0);
                }
            }
        }
    }
}
