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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.gen.structure.GCStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

public class MeteorStructure extends Structure {
    public static final MapCodec<MeteorStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StructureSettings.CODEC.fieldOf("config").forGetter(s -> s.settings),
            MeteorConfiguration.CODEC.fieldOf("meteor_configuration").forGetter(s -> s.configuration)
    ).apply(instance, MeteorStructure::new));

    private final MeteorConfiguration configuration;

    public MeteorStructure(StructureSettings settings, MeteorConfiguration configuration) {
        super(settings);
        this.configuration = configuration;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, piecesBuilder -> generateMeteor(piecesBuilder, context));
    }

    private void generateMeteor(StructurePiecesBuilder builder, GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();

        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();

        int surfaceY = context.chunkGenerator().getBaseHeight(
                x,
                z,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        //3 quarters buried
        int adjustedY = surfaceY - (this.configuration.radius / 2);
        BlockPos origin = new BlockPos(x, adjustedY, z);
        System.out.println("GENERATED METEOR AT HEIGHT: " + adjustedY);
        int minChunkX = (origin.getX() - this.configuration.radius) >> 4;
        int maxChunkX = (origin.getX() + this.configuration.radius) >> 4;
        int minChunkZ = (origin.getZ() - this.configuration.radius) >> 4;
        int maxChunkZ = (origin.getZ() + this.configuration.radius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                builder.addPiece(new MeteorStructurePiece(this.configuration, origin, chunkX, chunkZ, random));
            }
        }
    }

    @Override
    public StructureType<?> type() {
        return GCStructureTypes.METEOR;
    }

    public enum MeteorType {
        SHATTERED, RAIN, SINGULAR;

        public static final Codec<MeteorStructure.MeteorType> CODEC = Codec.STRING.xmap(
                name -> MeteorStructure.MeteorType.valueOf(name.toUpperCase()),
                MeteorStructure.MeteorType::name
        );
    }

    public record Core(BlockStateProvider block, int count) {
        public static final Codec<MeteorStructure.Core> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(MeteorStructure.Core::block),
                Codec.INT.fieldOf("count").forGetter(MeteorStructure.Core::count)
        ).apply(instance, MeteorStructure.Core::new));
    }

    public record Shell(BlockStateProvider block, int parts) {
        public static final Codec<MeteorStructure.Shell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(MeteorStructure.Shell::block),
                Codec.INT.fieldOf("parts").forGetter(MeteorStructure.Shell::parts)
        ).apply(instance, MeteorStructure.Shell::new));
    }

    public record MeteorConfiguration(
            boolean hasCrater,
            float meteorAngle,
            MeteorType meteorType,
            int radius,
            List<Core> core,
            List<Shell> shells,
            Optional<ResourceLocation> pool
    ) implements FeatureConfiguration {
        public static final Codec<MeteorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("has_crater").forGetter(MeteorConfiguration::hasCrater),
                Codec.FLOAT.fieldOf("meteor_angle").forGetter(MeteorConfiguration::meteorAngle),
                MeteorType.CODEC.fieldOf("meteor_type").forGetter(MeteorConfiguration::meteorType),
                Codec.INT.fieldOf("radius").forGetter(MeteorConfiguration::radius),
                Core.CODEC.listOf().fieldOf("core").forGetter(MeteorConfiguration::core),
                Shell.CODEC.listOf().fieldOf("shells").forGetter(MeteorConfiguration::shells),
                ResourceLocation.CODEC.optionalFieldOf("pool").forGetter(MeteorConfiguration::pool)
        ).apply(instance, MeteorConfiguration::new));
    }

    public static class ShellLayer {
        final BlockStateProvider block;
        final double inner;
        final double outer;

        ShellLayer(BlockStateProvider block, double inner, double outer) {
            this.block = block;
            this.inner = inner;
            this.outer = outer;
        }
    }
}
