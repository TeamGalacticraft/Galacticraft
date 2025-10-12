package dev.galacticraft.mod.world.gen.dungeon.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.slf4j.Logger;

public class DeferredCarvePiece extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Register this once in your mod init:
    // DeferredCarvePiece.TYPE = StructurePieceType.setSimpleCodec(DeferredCarvePiece::new);
    public static StructurePieceType TYPE;

    private BlockPos a;
    private BlockPos b;
    private int radius; // 1 = 1-wide; 2 = 3×3, etc.

    public DeferredCarvePiece(BlockPos a, BlockPos b, int radius) {
        super(TYPE, 0, BoundingBox.fromCorners(a, b));
        this.a = a.immutable();
        this.b = b.immutable();
        this.radius = Math.max(1, radius);
    }

    // Deserialization ctor
    public DeferredCarvePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        this(
                new BlockPos(tag.getInt("ax"), tag.getInt("ay"), tag.getInt("az")),
                new BlockPos(tag.getInt("bx"), tag.getInt("by"), tag.getInt("bz")),
                Math.max(1, tag.getInt("rad"))
        );
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("ax", a.getX()); tag.putInt("ay", a.getY()); tag.putInt("az", a.getZ());
        tag.putInt("bx", b.getX()); tag.putInt("by", b.getY()); tag.putInt("bz", b.getZ());
        tag.putInt("rad", radius);
    }

    @Override
    public void postProcess(WorldGenLevel level,
                            StructureManager structureManager,
                            ChunkGenerator chunkGenerator,
                            RandomSource random,
                            BoundingBox box,
                            ChunkPos chunkPos,
                            BlockPos pivot) {

        carveLine(level, a, b, box, radius);
    }

    // --- Carving helpers (integer DDA with square cross-section) ---

    private static void carveLine(WorldGenLevel level, BlockPos a, BlockPos b, BoundingBox limit, int radius) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        int dz = b.getZ() - a.getZ();
        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));

        if (steps == 0) {
            carveDisk(level, a, limit, radius);
            return;
        }

        double sx = dx / (double) steps;
        double sy = dy / (double) steps;
        double sz = dz / (double) steps;

        double x = a.getX();
        double y = a.getY();
        double z = a.getZ();

        for (int i = 0; i <= steps; i++) {
            BlockPos p = BlockPos.containing(Math.round(x), Math.round(y), Math.round(z));
            carveDisk(level, p, limit, radius);
            x += sx; y += sy; z += sz;
        }
    }

    private static void carveDisk(WorldGenLevel level, BlockPos center, BoundingBox limit, int radius) {
        int r = Math.max(1, radius);
        for (int dx = -r + 1; dx <= r - 1; dx++) {
            for (int dz = -r + 1; dz <= r - 1; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                if (!limit.isInside(p)) continue;
                // Only replace non-air to avoid extra work; change as desired
                if (!level.getBlockState(p).isAir()) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }
}