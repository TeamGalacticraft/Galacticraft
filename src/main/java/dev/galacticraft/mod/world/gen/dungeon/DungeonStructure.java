package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.gen.dungeon.config.DungeonConfig;
import dev.galacticraft.mod.world.gen.dungeon.pieces.EntranceAnchorPiece;
import dev.galacticraft.mod.world.gen.structure.GCStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

public class DungeonStructure extends Structure {
    // Codec
    public static final MapCodec<DungeonStructure> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(DungeonConfig.CODEC.fieldOf("config").forGetter(s -> s.config))
                    .apply(i, DungeonStructure::new)
    );
    // Logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ChunkPos originChunk;
    // Dungeon Config
    private final DungeonConfig config;

    // Constructor
    public DungeonStructure(DungeonConfig config) {
        super(new StructureSettings(HolderSet.empty()));
        this.config = config;
    }

    // Type return
    @Override
    public StructureType<?> type() {
        return GCStructureTypes.DUNGEON;
    }

    // Generator
    @Override
    @NotNull
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        // Initiate values
        ChunkPos chunkPos = ctx.chunkPos();
        originChunk = chunkPos;
        BlockPos center = new BlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());

        // Get surface block pos
        int surfaceY = ctx.chunkGenerator().getFirstFreeHeight(
                center.getX(), center.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                ctx.heightAccessor(),
                ctx.randomState()
        );
        BlockPos surface = new BlockPos(center.getX(), surfaceY, center.getZ());

        return Optional.of(new GenerationStub(
                surface,
                piecesBuilder -> {
                    piecesBuilder.addPiece(new EntranceAnchorPiece(
                            surface,
                            11,
                            22,
                            2.2,
                            2.0
                    ));
                }
        ));
    }

    @Override
    public void afterPlace(WorldGenLevel gen, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox box, ChunkPos chunkPos, PiecesContainer pieces) {
        if (chunkPos.equals(originChunk)) {
            BlockPos center = new BlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());
            int surfaceY = gen.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, center).getY();
            BlockPos surface = new BlockPos(center.getX(), surfaceY, center.getZ());

            DungeonBuilder builder = new DungeonBuilder(config, gen.getRandom());
            builder.build(gen, surface);
            super.afterPlace(gen, structureManager, chunkGenerator, randomSource, box, chunkPos, pieces);
        }
    }
}