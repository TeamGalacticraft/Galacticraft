package dev.galacticraft.mod.world.gen.dungeon.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Carves all voxels in a Bitmask during postProcess.
 * NOTE: This serializes the voxel list; for huge dungeons consider splitting by chunk or switching
 * to a path-based piece to reduce NBT size.
 */
public class MaskCarvePiece extends StructurePiece {
    public static StructurePieceType TYPE;

    private List<Long> voxels; // packed BlockPos.asLong()

    public MaskCarvePiece(Bitmask mask) {
        super(TYPE, 0, computeBoundingBox(mask));
        this.voxels = toList(mask);
    }

    public MaskCarvePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, tag);
        long[] arr = tag.getLongArray("vox");
        this.voxels = new ArrayList<>(arr.length);
        for (long v : arr) this.voxels.add(v);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        long[] arr = new long[voxels.size()];
        for (int i = 0; i < voxels.size(); i++) arr[i] = voxels.get(i);
        tag.putLongArray("vox", arr);
    }

    @Override
    public void postProcess(WorldGenLevel level,
                            net.minecraft.world.level.StructureManager structureManager,
                            net.minecraft.world.level.chunk.ChunkGenerator chunkGenerator,
                            net.minecraft.util.RandomSource random,
                            BoundingBox box,
                            net.minecraft.world.level.ChunkPos chunkPos,
                            BlockPos pivot) {
        if (voxels == null || voxels.isEmpty()) return;
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;

        for (long packed : voxels) {
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;

            BlockPos p = new BlockPos(x, y, z);
            if (!this.boundingBox.isInside(p)) continue;
            if (!level.getBlockState(p).isAir()) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static BoundingBox computeBoundingBox(Bitmask mask) {
        final int[] minX = {Integer.MAX_VALUE};
        final int[] minY = { Integer.MAX_VALUE };
        final int[] minZ = { Integer.MAX_VALUE };
        final int[] maxX = {Integer.MIN_VALUE};
        final int[] maxY = { Integer.MIN_VALUE };
        final int[] maxZ = { Integer.MIN_VALUE };
        final int[] empty = new int[1]; // detect no voxels
        mask.forEachLong(packed -> {
            empty[0] = 1;
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            if (x < minX[0]) minX[0] = x; if (y < minY[0]) minY[0] = y; if (z < minZ[0]) minZ[0] = z;
            if (x > maxX[0]) maxX[0] = x; if (y > maxY[0]) maxY[0] = y; if (z > maxZ[0]) maxZ[0] = z;
        });
        if (empty[0] == 0) { // empty mask; avoid invalid box
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }
        return new BoundingBox(minX[0], minY[0], minZ[0], maxX[0], maxY[0], maxZ[0]);
    }

    private static List<Long> toList(Bitmask mask) {
        ArrayList<Long> out = new ArrayList<>(mask.size());
        mask.forEachLong(out::add);
        return out;
    }
}