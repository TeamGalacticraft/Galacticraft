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

package dev.galacticraft.mod.world.gen.structure.meteor;

import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import dev.galacticraft.mod.world.gen.structure.meteor.MeteorStructure.MeteorConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

public class MeteorStructurePiece extends StructurePiece {
    private final MeteorConfiguration config;
    private final BlockPos origin;

    public MeteorStructurePiece(MeteorConfiguration config, BlockPos origin, int chunkX, int chunkZ, RandomSource random) {
        super(GCStructurePieceTypes.METEOR, 0,
                BoundingBox.fromCorners(
                        new BlockPos(chunkX << 4, origin.getY() - config.radius(), chunkZ << 4),
                        new BlockPos((chunkX << 4) + 15, origin.getY() + config.radius(), (chunkZ << 4) + 15)
                )
        );
        this.config = config;
        this.origin = origin;
    }

    public MeteorStructurePiece(CompoundTag tag) {
        super(GCStructurePieceTypes.METEOR, tag);
        this.config = MeteorConfiguration.CODEC.parse(NbtOps.INSTANCE, tag.get("MeteorConfig"))
                .resultOrPartial(System.err::println)
                .orElseThrow();
        this.origin = BlockPos.of(tag.getLong("Origin"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.put("MeteorConfig", MeteorConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, config)
                .resultOrPartial(System.err::println)
                .orElseThrow());
        tag.putLong("Origin", origin.asLong());
    }

    @Override
    public void postProcess(WorldGenLevel gen, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        placeMeteor(gen, origin, randomSource, config, chunkBox);
    }

    public boolean placeMeteor(WorldGenLevel gen, BlockPos origin, RandomSource randomSource, MeteorConfiguration config, BoundingBox chunkBox) {
        int radius = config.radius();
        List<MeteorStructure.Shell> shells = config.shells();
        List<MeteorStructure.Core> core = config.core();

        // Precompute shell layers
        double totalParts = shells.stream().mapToInt(MeteorStructure.Shell::parts).sum();
        List<MeteorStructure.ShellLayer> layers = new ArrayList<>();
        double innerRadius = radius;

        for (MeteorStructure.Shell shell : shells) {
            double shellThickness = (shell.parts() / totalParts) * radius;
            double outer = innerRadius;
            double inner = innerRadius - shellThickness;
            layers.add(new MeteorStructure.ShellLayer(shell.block(), inner, outer));
            innerRadius = inner;
        }

        // Only loop within the current chunk bounds (x/z), full vertical range
        for (int x = chunkBox.minX(); x <= chunkBox.maxX(); x++) {
            for (int y = origin.getY() - radius; y <= origin.getY() + radius; y++) {
                for (int z = chunkBox.minZ(); z <= chunkBox.maxZ(); z++) {
                    double dx = x - origin.getX();
                    double dy = y - origin.getY();
                    double dz = z - origin.getZ();
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz) + (randomSource.nextDouble() - 0.5); // natural roughness

                    if (dist > radius) continue;

                    BlockPos pos = new BlockPos(x, y, z);

                    for (MeteorStructure.ShellLayer layer : layers) {
                        if (dist >= layer.inner && dist <= layer.outer) {
                            BlockState block = layer.block.getState(randomSource, pos);
                            gen.setBlock(pos, block, 3);
                            break;
                        }
                    }
                }
            }
        }

        // Core block scatter
        for (MeteorStructure.Core blockCount : core) {
            int count = blockCount.count();
            double spread = Math.sqrt(count) * 2.0;

            for (int i = 0; i < count; i++) {
                // Random spherical direction
                double theta = randomSource.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * randomSource.nextDouble() - 1); // uniform over sphere

                // New biased radial distance to be denser near origin
                double r = spread * Math.pow(1.0 - Math.sqrt(randomSource.nextDouble()), 2.0);

                int ox = (int) (r * Math.sin(phi) * Math.cos(theta));
                int oy = (int) (r * Math.sin(phi) * Math.sin(theta));
                int oz = (int) (r * Math.cos(phi));

                BlockPos p = origin.offset(ox, oy, oz);
                if (chunkBox.isInside(p)) {
                    BlockState block = blockCount.block().getState(randomSource, p);
                    gen.setBlock(p, block, 3);
                }
            }
        }

        return true;
    }
}
