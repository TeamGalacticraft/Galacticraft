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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
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
        RandomSource random = ctx.random();
        ChunkPos chunkPos = ctx.chunkPos();
        BlockPos center = new BlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());

        // Get surface block pos
        int surfaceY = ctx.chunkGenerator().getFirstFreeHeight(
                center.getX(), center.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                ctx.heightAccessor(),
                ctx.randomState()
        );
        BlockPos surface = new BlockPos(center.getX(), surfaceY, center.getZ());

        // Start dungeon generation
        return Optional.of(new GenerationStub(
                surface,
                piecesBuilder -> {
                    LOGGER.info("Dungeon generating in chunk {} at position {}", chunkPos, surface);
                    DungeonBuilder builder = new DungeonBuilder(config, random);
                    piecesBuilder.addPiece(new EntranceAnchorPiece(
                            surface,
                            10,
                            22,
                            2.2,
                            2.0
                    ));
                    boolean ok = builder.build(ctx, piecesBuilder, surface);

                    if (ok) {
                        LOGGER.info("Dungeon finished generating at {}", surface);
                    } else {
                        LOGGER.error("Dungeon generation failed at {}", surface);
                    }
                }
        ));
    }
}