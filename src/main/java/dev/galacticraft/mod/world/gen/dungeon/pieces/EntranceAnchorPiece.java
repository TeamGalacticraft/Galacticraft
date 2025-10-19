/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen.dungeon.pieces;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class EntranceAnchorPiece extends StructurePiece {
    private final BlockPos surface;
    private final int centerDepth;  // == 10
    private final double entryRadius;   // e.g. 24.0 at surface
    private final double falloffK;      // e.g. 2.2
    private final double accel;         // e.g. 2.0

    // NBT ctor
    public EntranceAnchorPiece(CompoundTag tag) {
        super(GCStructurePieceTypes.DUNGEON_ANCHOR, tag);
        this.surface = new BlockPos(tag.getInt("sx"), tag.getInt("sy"), tag.getInt("sz"));
        this.centerDepth = tag.getInt("centerDepth");
        this.entryRadius = tag.getDouble("entryRadius");
        this.boundingBox = makeBox(surface, centerDepth, entryRadius);
        this.falloffK = tag.getDouble("falloffK");
        this.accel = tag.getDouble("accel");
    }

    public EntranceAnchorPiece(BlockPos surface, int centerDepth, double entryRadius, double falloffK, double accel) {
        super(GCStructurePieceTypes.DUNGEON_ANCHOR, 0, makeBox(surface.offset(0, 30, 0), centerDepth + 30, entryRadius));
        this.surface = surface.offset(0, 30, 0);
        this.centerDepth = centerDepth + 30;
        this.entryRadius = entryRadius;
        this.falloffK = falloffK;
        this.accel = accel;
    }

    private static BoundingBox makeBox(BlockPos surface, int centerDepth, double entryRadius) {
        int radius = (int) Math.ceil(entryRadius) + 1;
        int minX = surface.getX() - radius;
        int maxX = surface.getX() + radius;
        int minZ = surface.getZ() - radius;
        int maxZ = surface.getZ() + radius;
        int minY = surface.getY() - centerDepth - 2;
        int maxY = surface.getY() + 6;
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("sx", surface.getX());
        tag.putInt("sy", surface.getY());
        tag.putInt("sz", surface.getZ());
        tag.putInt("centerDepth", centerDepth);
        tag.putDouble("entryRadius", entryRadius);
        tag.putDouble("falloffK", falloffK);
        tag.putDouble("accel", accel);
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pivot) {
        final int cx = surface.getX();
        final int cz = surface.getZ();

        final double entryRadius = this.entryRadius; // e.g., 18–30 (set in ctor)
        final int radius = (int) entryRadius;

        // -------- Pass 0: capture each column's "natural surface" block BEFORE carving --------
        final BlockState[][] surfaceTop = new BlockState[2 * radius + 1][2 * radius + 1];

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                final int lx = dx + radius;
                final int lz = dz + radius;
                final int x = cx + dx, z = cz + dz;

                // 1) Ask the heightmap for the worldgen surface height at (x,z)
                int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z);

                // 2) Take the block just below that (topmost terrain block)
                BlockState coat = GCBlocks.MOON_ROCK.defaultBlockState(); // sane fallback
                BlockPos p = new BlockPos(x, y - 1, z);
                if (this.boundingBox.isInside(p)) {
                    BlockState s = level.getBlockState(p);

                    // If that block isn't solid (plants/snow/fluid), walk down to find the first occluding, non-fluid block.
                    int minY = this.boundingBox.minY();
                    while ((s.isAir() || !s.getFluidState().isEmpty() || !s.canOcclude()) && p.getY() > minY) {
                        p = p.below();
                        s = level.getBlockState(p);
                    }
                    if (!s.isAir() && s.getFluidState().isEmpty() && s.canOcclude()) {
                        coat = s;
                    }
                }

                surfaceTop[lx][lz] = coat;
            }
        }

        final int minHalfExtent = 1;    // 1 => 3x3 minimum
        final double falloffK = this.falloffK;    // e.g., 2.0–3.0
        final double accel = this.accel;       // e.g., 1.6–2.5 ( >1 accelerates collapse )

        final int topY = surface.getY();
        final int bottomY = topY - centerDepth;

        // Loop depth-first so radius shrinks with depth
        for (int y = topY; y > bottomY; y--) {
            final double d = (topY - y) / (double) centerDepth;        // 0..1
            final double radY = entryRadius * Math.exp(-falloffK * Math.pow(d, accel));
            final int rBlock = (int) Math.ceil(radY);

            // Iterate only the square that can possibly be inside this radius
            for (int dx = -rBlock; dx <= rBlock; dx++) {
                for (int dz = -rBlock; dz <= rBlock; dz++) {
                    final int x = cx + dx;
                    final int z = cz + dz;
                    final BlockPos p = new BlockPos(x, y, z);
                    if (!this.boundingBox.isInside(p)) continue;

                    // Always guarantee a 3x3 shaft:
                    if (Math.max(Math.abs(dx), Math.abs(dz)) <= minHalfExtent) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        continue;
                    }

                    // Otherwise carve within the (round) aperture for this depth:
                    final double rEuclid = Math.sqrt((double) dx * dx + (double) dz * dz);
                    if (rEuclid <= radY) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        // -------- Pass 2: coat solid blocks that now touch air (except center 3×3) --------
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) continue; // skip center 3x3

                final int lx = dx + radius, lz = dz + radius;
                final BlockState coat = surfaceTop[lx][lz];
                if (coat == null) continue;

                final int x = cx + dx, z = cz + dz;

                // coat only the exposed faces in the funnel span
                for (int y = topY; y >= bottomY; y--) {
                    final BlockPos p = new BlockPos(x, y, z);
                    if (!this.boundingBox.isInside(p)) continue;

                    final BlockState s = level.getBlockState(p);
                    if (s.isAir() || !s.getFluidState().isEmpty()) continue;

                    boolean touchesAir =
                            level.getBlockState(p.above()).isAir() ||
                                    level.getBlockState(p.below()).isAir() ||
                                    level.getBlockState(p.north()).isAir() ||
                                    level.getBlockState(p.south()).isAir() ||
                                    level.getBlockState(p.east()).isAir() ||
                                    level.getBlockState(p.west()).isAir();

                    if (touchesAir) {
                        level.setBlock(p, coat, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }
}