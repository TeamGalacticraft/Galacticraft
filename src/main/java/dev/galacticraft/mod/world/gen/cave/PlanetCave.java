package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract class PlanetCave {
    private final ResourceLocation id;
    private final MoonCaveShapeType shapeType;
    private final MoonCaveShape shape;
    private final int weight;
    private final float spawnChance;
    private final int minAnchorY;
    private final int maxAnchorY;
    private final int minY;
    private final int maxY;
    private final BlockState air;
    private final BlockState innerWall;
    private final BlockState outerWall;
    private final BlockState accent;
    private final CaveTransitionConfig transitionConfig;

    protected PlanetCave(
            ResourceLocation id,
            MoonCaveShapeType shapeType,
            MoonCaveShape shape,
            int weight,
            float spawnChance,
            int minAnchorY,
            int maxAnchorY,
            int minY,
            int maxY,
            BlockState innerWall,
            BlockState outerWall,
            BlockState accent,
            CaveTransitionConfig transitionConfig
    ) {
        this.id = id;
        this.shapeType = shapeType;
        this.shape = shape;
        this.weight = weight;
        this.spawnChance = spawnChance;
        this.minAnchorY = minAnchorY;
        this.maxAnchorY = maxAnchorY;
        this.minY = minY;
        this.maxY = maxY;
        this.air = Blocks.AIR.defaultBlockState();
        this.innerWall = innerWall;
        this.outerWall = outerWall;
        this.accent = accent;
        this.transitionConfig = transitionConfig;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public MoonCaveShapeType shapeType() {
        return this.shapeType;
    }

    public MoonCaveShape shape() {
        return this.shape;
    }

    public int weight() {
        return this.weight;
    }

    public float spawnChance() {
        return this.spawnChance;
    }

    public int minAnchorY() {
        return this.minAnchorY;
    }

    public int maxAnchorY() {
        return this.maxAnchorY;
    }

    public int minY() {
        return this.minY;
    }

    public int maxY() {
        return this.maxY;
    }

    public CaveTransitionConfig transitionConfig() {
        return this.transitionConfig;
    }

    public abstract boolean matchesBiome(Holder<Biome> biome);

    public BlockState air(int x, int y, int z) {
        return this.air;
    }

    public BlockState innerWall(int x, int y, int z) {
        return this.innerWall;
    }

    public BlockState outerWall(int x, int y, int z) {
        return this.outerWall;
    }

    public BlockState accent(int x, int y, int z) {
        return this.accent;
    }

    public boolean canTransitionTo(PlanetCave other) {
        return other != null
                && this.transitionConfig.enabled()
                && other.transitionConfig.enabled()
                && this.shapeType == other.shapeType();
    }

    public void decorate(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            BlockPos pos,
            CaveSampleType type,
            int hash
    ) {
    }

    public BlockState surfaceBlock(int x, int y, int z, BlockState currentSurface) {
        return currentSurface;
    }

    public boolean paintsSurface() {
        return false;
    }
}