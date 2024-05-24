/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.structure.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.gen.structure.GCStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DungeonStructure extends Structure {
    public static final Codec<DungeonStructure> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            StructureSettings.CODEC.fieldOf("config").forGetter((moonRuinsStructure) -> moonRuinsStructure.settings),
            DungeonConfiguration.CODEC.fieldOf("dungeon_configuration").forGetter(dungeonStructure -> dungeonStructure.configuration)
    ).apply(instance, DungeonStructure::new));

    private final DungeonConfiguration configuration;

    public DungeonStructure(StructureSettings structureSettings, DungeonConfiguration configuration) {
        super(structureSettings);
        this.configuration = configuration;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, structurePiecesBuilder -> generatePieces(structurePiecesBuilder, context));
    }

    private void generatePieces(StructurePiecesBuilder collector, Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom worldgenRandom = context.random();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 90, chunkPos.getMinBlockZ());
//        Rotation rotation = Rotation.getRandom(worldgenRandom);
        DungeonStart startPiece = new DungeonStart(configuration, worldgenRandom, blockPos.getX() + 2, blockPos.getZ() + 2, 0);
        collector.addPiece(startPiece);
        startPiece.addChildren(startPiece, collector, worldgenRandom);
        List<StructurePiece> list = startPiece.attachedComponents;

        while (!list.isEmpty())
        {
            int i = worldgenRandom.nextInt(list.size());
            StructurePiece structurecomponent = list.remove(i);
            structurecomponent.addChildren(startPiece, collector, worldgenRandom);
        }

//        IglooPieces.addPieces(context.structureTemplateManager(), blockPos, rotation, collector, worldgenRandom);
    }

    public static long getDungeonPosForCoords(Level world, long worldSeed, int chunkX, int chunkZ, int spacing) {
        final int numChunks = spacing / 16;
        if (chunkX < 0) {
            chunkX -= numChunks - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= numChunks - 1;
        }

        int k = chunkX / numChunks;
        int l = chunkZ / numChunks;
        long seed = (long) k * 341873128712L + (long) l * 132897987541L + worldSeed + (long) (10387340 + world.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getId(world.dimensionType()));
        Random random = new Random();
        random.setSeed(seed);
        k = k * numChunks + random.nextInt(numChunks);
        l = l * numChunks + random.nextInt(numChunks);
        return (((long) k) << 32) + l;
    }

    @Override
    public StructureType<?> type() {
        return GCStructureTypes.MOON_DUNGEON;
    }
}
