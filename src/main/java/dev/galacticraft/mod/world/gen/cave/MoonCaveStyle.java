package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Describes the material/decorator identity of a planned Moon cave.
 */
public enum MoonCaveStyle {
    GLACIAL(
            Blocks.AIR.defaultBlockState(),
            Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
            Blocks.BLUE_WOOL.defaultBlockState(),
            Blocks.WHITE_WOOL.defaultBlockState(),
            Blocks.CYAN_WOOL.defaultBlockState()
    ),
    OLIVINE(
            Blocks.AIR.defaultBlockState(),
            GCBlocks.MOON_BASALT.defaultBlockState(),
            GCBlocks.OLIVINE_BLOCK.defaultBlockState(),
            GCBlocks.BUDDING_OLIVINE.defaultBlockState(),
            GCBlocks.OLIVINE_CLUSTER.defaultBlockState()
    ),
    CHEESE(
            Blocks.AIR.defaultBlockState(),
            Blocks.YELLOW_WOOL.defaultBlockState(),
            Blocks.ORANGE_WOOL.defaultBlockState(),
            Blocks.BROWN_WOOL.defaultBlockState(),
            Blocks.GOLD_BLOCK.defaultBlockState()
    );

    private final BlockState air;
    private final BlockState innerWall;
    private final BlockState outerWall;
    private final BlockState accent;
    private final BlockState spike;

    MoonCaveStyle(BlockState air, BlockState innerWall, BlockState outerWall, BlockState accent, BlockState spike) {
        this.air = air;
        this.innerWall = innerWall;
        this.outerWall = outerWall;
        this.accent = accent;
        this.spike = spike;
    }

    public BlockState air() {
        return this.air;
    }

    public BlockState innerWall() {
        return this.innerWall;
    }

    public BlockState outerWall() {
        return this.outerWall;
    }

    public BlockState accent() {
        return this.accent;
    }

    public BlockState spike() {
        return this.spike;
    }

    /**
     * Selects a cave style from the biome at the cave anchor.
     *
     * @param biome Biome holder.
     * @return Matching cave style, or null when this biome should not create planned Moon caves.
     */
    public static MoonCaveStyle fromBiome(Holder<Biome> biome) {
        if (biome.is(GCBiomes.Moon.GLACIAL_CAVERNS)) {
            return GLACIAL;
        }

        if (biome.is(GCBiomes.Moon.OLIVINE_CAVES)) {
            return OLIVINE;
        }

        if (biome.is(GCBiomes.Moon.CHEESE_CAVES)) {
            return CHEESE;
        }

        return null;
    }
}