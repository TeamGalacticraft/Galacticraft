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

    public MeteorStructurePiece(MeteorConfiguration config, BlockPos origin, RandomSource random) {
        super(GCStructurePieceTypes.METEOR, 0, new BoundingBox(
                origin.getX() - config.radius(), origin.getY() - config.radius(), origin.getZ() - config.radius(),
                origin.getX() + config.radius(), origin.getY() + config.radius(), origin.getZ() + config.radius()
        ));
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
        // Your existing meteor generation code here!
        placeMeteor(gen, origin, randomSource, config);
    }

    public boolean placeMeteor(WorldGenLevel gen, BlockPos origin, RandomSource randomSource, MeteorConfiguration config) {
        int radius = config.radius();
        List<MeteorStructure.Shell> shells = config.shells();
        List<MeteorStructure.Core> core = config.core();

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

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z) + (randomSource.nextDouble() - 0.5); // roughness
                    if (dist > radius) continue;

                    for (MeteorStructure.ShellLayer layer : layers) {
                        if (dist >= layer.inner && dist <= layer.outer) {
                            BlockState block = layer.block.getState(randomSource, origin.offset(x, y, z));
                            gen.setBlock(origin.offset(x, y, z), block, 3);
                            break;
                        }
                    }
                }
            }
        }

        for (MeteorStructure.Core blockCount : core) {
            int count = blockCount.count();
            double spread = Math.sqrt(count) * 2.0;

            for (int i = 0; i < count; i++) {
                // Random spherical direction
                double theta = randomSource.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * randomSource.nextDouble() - 1); // uniform over sphere

                // Biased radial distance (dense near core)
                double r = Math.cbrt(randomSource.nextDouble()) * spread;

                // Convert spherical to cartesian offset
                int ox = (int) (r * Math.sin(phi) * Math.cos(theta));
                int oy = (int) (r * Math.sin(phi) * Math.sin(theta));
                int oz = (int) (r * Math.cos(phi));

                BlockPos p = origin.offset(ox, oy, oz);
                BlockState block = blockCount.block().getState(randomSource, p);
                gen.setBlock(p, block, 3);
            }
        }
        return true;
    }
}
