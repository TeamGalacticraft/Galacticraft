package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.gen.structure.GCStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Ant-dungeon structure entrypoint.
 * - Finds a surface anchor at chunk center (WORLD_SURFACE_WG).
 * - Picks a reasonable underground center hint.
 * - Invokes DungeonGraphBuilder to emit room pieces + corridor pieces.
 * <p>
 * NOTE: Do not carve/place blocks here. Each StructurePiece handles block placement
 * in its own postProcess(...). Corridor “door punches” happen in CorridorPiece.
 */
public class DungeonStructure extends Structure {
    public static final MapCodec<DungeonStructure> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(DungeonConfig.CODEC.fieldOf("config").forGetter(s -> s.config))
                    .apply(i, DungeonStructure::new)
    );
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DungeonConfig config;

    public DungeonStructure(DungeonConfig config) {
        super(new StructureSettings(HolderSet.empty()));
        this.config = config;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    public StructureType<?> type() {
        return GCStructureTypes.DUNGEON;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        RandomSource rnd = ctx.random();
        ChunkPos cp = ctx.chunkPos();
        BlockPos seed = new BlockPos(cp.getMiddleBlockX(), 0, cp.getMiddleBlockZ());

        int surfaceY = ctx.chunkGenerator().getFirstFreeHeight(
                seed.getX(), seed.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                ctx.heightAccessor(),
                ctx.randomState()
        );
        BlockPos surface = new BlockPos(seed.getX(), surfaceY, seed.getZ());

        // Pick a target radius just to compute a center
        int targetRadius = rnd.nextIntBetweenInclusive(config.sphereRadiusMin(), config.sphereRadiusMax());
        BlockPos centerHint = surface.offset(0, -Math.max(20, targetRadius - 6), 0);

        LOGGER.info("[Dungeon] findGenerationPoint: chunk={} surface={} targetRadius={} centerHint={}",
                cp, surface, targetRadius, centerHint);

        return Optional.of(new GenerationStub(
                surface,
                piecesBuilder -> {
                    LOGGER.info("[Dungeon] GenerationStub: build start at surface={} ...", surface);
                    DungeonWorldBuilder builder = new DungeonWorldBuilder(config, rnd);
                    boolean ok = builder.build(ctx, piecesBuilder, surface, centerHint);

                    LOGGER.info("[Dungeon] GenerationStub: build result={}", ok);
                }
        ));
    }
}