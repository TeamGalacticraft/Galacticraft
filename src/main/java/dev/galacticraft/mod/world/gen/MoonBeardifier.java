/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.gen;

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.HashSet;
import java.util.Set;

/**
 * Moon-specific beardifier that keeps BEARD_THIN terrain fill under Moon villages
 * while clamping away the carve-above behavior that creates pits around buildings.
 */
public class MoonBeardifier extends Beardifier {
    public static final int BEARD_KERNEL_RADIUS = 12;
    private static final int BEARD_KERNEL_SIZE = 24;
    private static final float[] BEARD_KERNEL = Util.make(new float[13824], array -> {
        for (int i = 0; i < BEARD_KERNEL_SIZE; ++i) {
            for (int j = 0; j < BEARD_KERNEL_SIZE; ++j) {
                for (int k = 0; k < BEARD_KERNEL_SIZE; ++k) {
                    array[i * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + j * BEARD_KERNEL_SIZE + k] = (float) MoonBeardifier.computeBeardContribution(j - BEARD_KERNEL_RADIUS, k - BEARD_KERNEL_RADIUS, i - BEARD_KERNEL_RADIUS);
                }
            }
        }
    });

    private final ObjectListIterator<Beardifier.Rigid> pieceIterator;
    private final ObjectListIterator<JigsawJunction> junctionIterator;
    private final Set<BoundingBox> fillOnlyBoxes;

    public static MoonBeardifier forStructuresInChunk(StructureManager world, ChunkPos pos) {
        int minBlockX = pos.getMinBlockX();
        int minBlockZ = pos.getMinBlockZ();
        ObjectArrayList<Beardifier.Rigid> pieces = new ObjectArrayList<>(10);
        ObjectArrayList<JigsawJunction> junctions = new ObjectArrayList<>(32);
        Set<BoundingBox> fillOnlyBoxes = new HashSet<>();

        world.startsForStructure(pos, structure -> structure.terrainAdaptation() != TerrainAdjustment.NONE)
                .forEach(start -> {
                    TerrainAdjustment terrainAdjustment = start.getStructure().terrainAdaptation();
                    boolean fillOnly = GCStructures.Moon.VILLAGE.location().equals(world.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(start.getStructure()));

                    for (StructurePiece structurePiece : start.getPieces()) {
                        if (!structurePiece.isCloseToChunk(pos, BEARD_KERNEL_RADIUS)) {
                            continue;
                        }

                        if (structurePiece instanceof PoolElementStructurePiece poolElementStructurePiece) {
                            StructureTemplatePool.Projection projection = poolElementStructurePiece.getElement().getProjection();
                            if (projection == StructureTemplatePool.Projection.RIGID) {
                                BoundingBox box = poolElementStructurePiece.getBoundingBox();
                                pieces.add(new Beardifier.Rigid(box, terrainAdjustment, poolElementStructurePiece.getGroundLevelDelta()));
                                if (fillOnly) {
                                    fillOnlyBoxes.add(box);
                                }
                            }

                            for (JigsawJunction jigsawJunction : poolElementStructurePiece.getJunctions()) {
                                int sourceX = jigsawJunction.getSourceX();
                                int sourceZ = jigsawJunction.getSourceZ();
                                if (sourceX <= minBlockX - BEARD_KERNEL_RADIUS
                                        || sourceZ <= minBlockZ - BEARD_KERNEL_RADIUS
                                        || sourceX >= minBlockX + 15 + BEARD_KERNEL_RADIUS
                                        || sourceZ >= minBlockZ + 15 + BEARD_KERNEL_RADIUS) {
                                    continue;
                                }
                                junctions.add(jigsawJunction);
                            }
                            continue;
                        }

                        BoundingBox box = structurePiece.getBoundingBox();
                        pieces.add(new Beardifier.Rigid(box, terrainAdjustment, 0));
                        if (fillOnly) {
                            fillOnlyBoxes.add(box);
                        }
                    }
                });

        return new MoonBeardifier(pieces.iterator(), junctions.iterator(), fillOnlyBoxes);
    }

    @VisibleForTesting
    public MoonBeardifier(ObjectListIterator<Beardifier.Rigid> pieceIterator, ObjectListIterator<JigsawJunction> junctionIterator, Set<BoundingBox> fillOnlyBoxes) {
        super(pieceIterator, junctionIterator);
        this.pieceIterator = pieceIterator;
        this.junctionIterator = junctionIterator;
        this.fillOnlyBoxes = fillOnlyBoxes;
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        int blockX = context.blockX();
        int blockY = context.blockY();
        int blockZ = context.blockZ();
        double density = 0.0;

        while (this.pieceIterator.hasNext()) {
            Beardifier.Rigid rigid = this.pieceIterator.next();
            BoundingBox box = rigid.box();
            int groundLevelDelta = rigid.groundLevelDelta();
            int horizDistX = Math.max(0, Math.max(box.minX() - blockX, blockX - box.maxX()));
            int horizDistZ = Math.max(0, Math.max(box.minZ() - blockZ, blockZ - box.maxZ()));
            int adjustedMinY = box.minY() + groundLevelDelta;
            int p = blockY - adjustedMinY;
            int q = switch (rigid.terrainAdjustment()) {
                case NONE -> 0;
                case BURY, BEARD_THIN -> p;
                case BEARD_BOX -> Math.max(0, Math.max(adjustedMinY - blockY, blockY - box.maxY()));
                case ENCAPSULATE -> Math.max(0, Math.max(box.minY() - blockY, blockY - box.maxY()));
            };

            double contribution = switch (rigid.terrainAdjustment()) {
                case NONE -> 0.0;
                case BURY -> MoonBeardifier.getBuryContribution(horizDistX, (double) q / 2.0, horizDistZ);
                case BEARD_THIN, BEARD_BOX -> MoonBeardifier.getBeardContribution(horizDistX, q, horizDistZ, p) * 0.8;
                case ENCAPSULATE -> MoonBeardifier.getBuryContribution((double) horizDistX / 2.0, (double) q / 2.0, (double) horizDistZ / 2.0) * 0.8;
            };

            if (this.fillOnlyBoxes.contains(box) && contribution < 0.0) {
                contribution = 0.0;
            }

            density += contribution;
        }
        this.pieceIterator.back(Integer.MAX_VALUE);

        while (this.junctionIterator.hasNext()) {
            JigsawJunction junction = this.junctionIterator.next();
            int sourceX = blockX - junction.getSourceX();
            int sourceY = blockY - junction.getSourceGroundY();
            int sourceZ = blockZ - junction.getSourceZ();
            density += MoonBeardifier.getBeardContribution(sourceX, sourceY, sourceZ, sourceY) * 0.4;
        }
        this.junctionIterator.back(Integer.MAX_VALUE);

        return density;
    }

    @Override
    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    private static double getBuryContribution(double x, double y, double z) {
        double distance = Mth.length(x, y, z);
        return Mth.clampedMap(distance, 0.0, 6.0, 1.0, 0.0);
    }

    private static double getBeardContribution(int x, int y, int z, int yy) {
        int i = x + BEARD_KERNEL_RADIUS;
        int j = y + BEARD_KERNEL_RADIUS;
        int k = z + BEARD_KERNEL_RADIUS;
        if (!MoonBeardifier.isInKernelRange(i) || !MoonBeardifier.isInKernelRange(j) || !MoonBeardifier.isInKernelRange(k)) {
            return 0.0;
        }
        double d = (double) yy + 0.5;
        double e = Mth.lengthSquared((double) x, d, (double) z);
        double f = -d * Mth.fastInvSqrt(e / 2.0) / 2.0;
        return f * (double) BEARD_KERNEL[k * BEARD_KERNEL_SIZE * BEARD_KERNEL_SIZE + i * BEARD_KERNEL_SIZE + j];
    }

    private static boolean isInKernelRange(int value) {
        return value >= 0 && value < BEARD_KERNEL_SIZE;
    }

    private static double computeBeardContribution(int x, int y, int z) {
        return MoonBeardifier.computeBeardContribution(x, (double) y + 0.5, z);
    }

    private static double computeBeardContribution(int x, double y, int z) {
        double d = Mth.lengthSquared((double) x, y, (double) z);
        return Math.exp(-d / 16.0);
    }
}