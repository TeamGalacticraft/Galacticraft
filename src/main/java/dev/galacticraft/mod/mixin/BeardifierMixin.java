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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Modifies Beardifier to support "fill-only" terrain adjustment for Galacticraft structures.
 * Structures tagged as fill-only get terrain filled underneath (like BEARD_THIN) but no
 * carving above, preventing the terrain pits around buildings on slopes.
 */
@Mixin(Beardifier.class)
public abstract class BeardifierMixin {

    @Shadow
    @Final
    private ObjectListIterator<Beardifier.Rigid> pieceIterator;

    @Shadow
    @Final
    private ObjectListIterator<JigsawJunction> junctionIterator;

    @Unique
    private static final ResourceLocation MOON_VILLAGE_ID = Constant.id("moon_village");

    @Unique
    private final Set<BoundingBox> galacticraft$fillOnlyBoxes = new HashSet<>();

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"))
    private static void galacticraft$tagFillOnlyStructures(StructureManager world, ChunkPos pos, CallbackInfoReturnable<Beardifier> cir) {
        Beardifier beardifier = cir.getReturnValue();
        Set<BoundingBox> fillOnly = ((BeardifierMixin) (Object) beardifier).galacticraft$fillOnlyBoxes;

        world.startsForStructure(pos, structure -> structure.terrainAdaptation() != TerrainAdjustment.NONE)
                .forEach(start -> {
                    var structureKey = world.registryAccess()
                            .registryOrThrow(Registries.STRUCTURE)
                            .getKey(start.getStructure());
                    if (MOON_VILLAGE_ID.equals(structureKey)) {
                        for (var piece : start.getPieces()) {
                            if (piece.isCloseToChunk(pos, 12)) {
                                fillOnly.add(piece.getBoundingBox());
                            }
                        }
                    }
                });
    }

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void galacticraft$computeFillOnly(DensityFunction.FunctionContext context, CallbackInfoReturnable<Double> cir) {
        if (this.galacticraft$fillOnlyBoxes.isEmpty()) {
            return;
        }

        int blockX = context.blockX();
        int blockY = context.blockY();
        int blockZ = context.blockZ();
        double totalDensity = 0.0;

        while (this.pieceIterator.hasNext()) {
            Beardifier.Rigid rigid = this.pieceIterator.next();
            BoundingBox box = rigid.box();
            int groundLevelDelta = rigid.groundLevelDelta();
            int horizDistX = Math.max(0, Math.max(box.minX() - blockX, blockX - box.maxX()));
            int horizDistZ = Math.max(0, Math.max(box.minZ() - blockZ, blockZ - box.maxZ()));
            int adjustedMinY = box.minY() + groundLevelDelta;
            int p = blockY - adjustedMinY;

            double contribution;
            switch (rigid.terrainAdjustment()) {
                case NONE -> contribution = 0.0;
                case BURY -> contribution = galacticraft$getBuryContribution(horizDistX, (double) p / 2.0, horizDistZ);
                case BEARD_THIN -> {
                    contribution = galacticraft$getBeardContribution(horizDistX, p, horizDistZ, p) * 0.8;
                    if (galacticraft$fillOnlyBoxes.contains(box)) {
                        contribution = Math.max(0.0, contribution);
                    }
                }
                case BEARD_BOX -> {
                    int q = Math.max(0, Math.max(adjustedMinY - blockY, blockY - box.maxY()));
                    contribution = galacticraft$getBeardContribution(horizDistX, q, horizDistZ, p) * 0.8;
                    if (galacticraft$fillOnlyBoxes.contains(box)) {
                        contribution = Math.max(0.0, contribution);
                    }
                }
                case ENCAPSULATE -> {
                    int q = Math.max(0, Math.max(box.minY() - blockY, blockY - box.maxY()));
                    contribution = galacticraft$getBuryContribution((double) horizDistX / 2.0, (double) q / 2.0, (double) horizDistZ / 2.0) * 0.8;
                }
                default -> contribution = 0.0;
            }
            totalDensity += contribution;
        }
        this.pieceIterator.back(Integer.MAX_VALUE);

        while (this.junctionIterator.hasNext()) {
            JigsawJunction junction = this.junctionIterator.next();
            int dx = blockX - junction.getSourceX();
            int dy = blockY - junction.getSourceGroundY();
            int dz = blockZ - junction.getSourceZ();
            totalDensity += galacticraft$getBeardContribution(dx, dy, dz, dy) * 0.4;
        }
        this.junctionIterator.back(Integer.MAX_VALUE);

        cir.setReturnValue(totalDensity);
    }

    @Unique
    private static double galacticraft$getBuryContribution(double x, double y, double z) {
        double d = Mth.length(x, y, z);
        return Mth.clampedMap(d, 0.0, 6.0, 1.0, 0.0);
    }

    @Unique
    private static final int KERNEL_RADIUS = 12;
    @Unique
    private static final int KERNEL_SIZE = 24;

    @Unique
    private static double galacticraft$getBeardContribution(int x, int y, int z, int yy) {
        int i = x + KERNEL_RADIUS;
        int j = y + KERNEL_RADIUS;
        int k = z + KERNEL_RADIUS;
        if (i < 0 || i >= KERNEL_SIZE || j < 0 || j >= KERNEL_SIZE || k < 0 || k >= KERNEL_SIZE) {
            return 0.0;
        }
        double d = (double) yy + 0.5;
        double e = Mth.lengthSquared((double) x, d, (double) z);
        double f = -d * Mth.fastInvSqrt(e / 2.0) / 2.0;
        return f * (double) galacticraft$getBeardKernelValue(i, j, k);
    }

    @Unique
    private static float galacticraft$getBeardKernelValue(int i, int j, int k) {
        double d = Mth.lengthSquared((double) (j - KERNEL_RADIUS), (double) (k - KERNEL_RADIUS) + 0.5, (double) (i - KERNEL_RADIUS));
        return (float) Math.pow(Math.E, -d / 16.0);
    }
}
