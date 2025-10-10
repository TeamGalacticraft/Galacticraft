package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.ArrayList;
import java.util.List;

public class CorridorPiece extends StructurePiece {
    private final List<BlockPos> path = new ArrayList<>();
    private final int aperture; // kept as max(aAp, bAp) for bbox/compat

    private final BlockPos aPort;
    private final BlockPos bPort;
    private final net.minecraft.core.Direction aFacing;
    private final net.minecraft.core.Direction bFacing;

    private final int aAperture; // NEW: actual port A aperture
    private final int bAperture; // NEW: actual port B aperture
    private final boolean critical;

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    public CorridorPiece(List<BlockPos> path, int aperture, BoundingBox box,
                         BlockPos aPort, BlockPos bPort,
                         net.minecraft.core.Direction aFacing, net.minecraft.core.Direction bFacing,
                         int aAperture, int bAperture,
                         boolean critical) {
        super(StructurePieceType.JIGSAW, 0, box);
        this.path.addAll(path);
        this.aperture = Math.max(aperture, Math.max(aAperture, bAperture)); // bbox safety
        this.aPort = aPort;
        this.bPort = bPort;
        this.aFacing = aFacing;
        this.bFacing = bFacing;
        this.aAperture = Math.max(3, aAperture);
        this.bAperture = Math.max(3, bAperture);
        this.critical = critical;
    }

    public CorridorPiece(CompoundTag tag) {
        super(StructurePieceType.JIGSAW, tag);
        this.aperture = tag.getInt("aperture");
        int n = tag.getInt("pc");
        for (int i = 0; i < n; i++) {
            path.add(new BlockPos(tag.getInt("px" + i), tag.getInt("py" + i), tag.getInt("pz" + i)));
        }
        this.aPort = new BlockPos(tag.getInt("ax"), tag.getInt("ay"), tag.getInt("az"));
        this.bPort = new BlockPos(tag.getInt("bx"), tag.getInt("by"), tag.getInt("bz"));
        this.aFacing = net.minecraft.core.Direction.values()[tag.getInt("af")];
        this.bFacing = net.minecraft.core.Direction.values()[tag.getInt("bf")];
        this.critical = tag.getBoolean("crit");

        // NEW (default to 'aperture' if older saves don't have these keys)
        this.aAperture = tag.contains("aap") ? Math.max(3, tag.getInt("aap")) : this.aperture;
        this.bAperture = tag.contains("bap") ? Math.max(3, tag.getInt("bap")) : this.aperture;
    }

    private static void punchPortWith(WorldGenLevel level, BlockPos center, net.minecraft.core.Direction facing, int aperture, int depth, net.minecraft.world.level.block.Block block) {
        int half = (aperture - 1) / 2;
        var n = facing.getNormal();
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                for (int d = 0; d < depth; d++) {
                    BlockPos q = switch (facing.getAxis()) {
                        case X -> center.offset(n.getX() * d, i, j);
                        case Y -> center.offset(i, n.getY() * d, j);
                        case Z -> center.offset(i, j, n.getZ() * d);
                    };
                    level.setBlock(q, block.defaultBlockState(), 2);
                }
            }
        }
    }

    // REPLACE addAdditionalSaveData
    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("aperture", aperture);
        tag.putInt("pc", path.size());
        for (int i = 0; i < path.size(); i++) {
            var p = path.get(i);
            tag.putInt("px" + i, p.getX());
            tag.putInt("py" + i, p.getY());
            tag.putInt("pz" + i, p.getZ());
        }
        tag.putInt("ax", aPort.getX());
        tag.putInt("ay", aPort.getY());
        tag.putInt("az", aPort.getZ());
        tag.putInt("bx", bPort.getX());
        tag.putInt("by", bPort.getY());
        tag.putInt("bz", bPort.getZ());
        tag.putInt("af", aFacing.ordinal());
        tag.putInt("bf", bFacing.ordinal());
        tag.putBoolean("crit", critical);

        // NEW
        tag.putInt("aap", aAperture);
        tag.putInt("bap", bAperture);
    }

    // REPLACE postProcess
    @Override
    public void postProcess(WorldGenLevel level,
                            net.minecraft.world.level.StructureManager ignored,
                            ChunkGenerator chunkGenerator,
                            RandomSource randomSource,
                            BoundingBox box,
                            ChunkPos chunkPos,
                            BlockPos pivot) {

        var fill = this.critical ? net.minecraft.world.level.block.Blocks.COBWEB
                : net.minecraft.world.level.block.Blocks.AIR;

        int n = (this.path == null ? 0 : this.path.size());
        LOGGER.info("(Carve) start  nodes={} aAp={} bAp={} critical={} bbox=[({}, {}, {}) -> ({}, {}, {})]",
                n, aAperture, bAperture, critical,
                this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(),
                this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ());

        CorridorRouter.carveGradientWith(level, this.path, this.aAperture, this.bAperture, fill);

        punchPortWith(level, aPort, aFacing, aAperture, /*depth*/3, fill);
        punchPortWith(level, bPort, bFacing, bAperture, /*depth*/3, fill);

        LOGGER.info("(Carve) done   nodes={} aPort={}{} bPort={}{}",
                n, aPort, aFacing, bPort, bFacing);
    }
}