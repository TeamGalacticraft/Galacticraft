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

package dev.galacticraft.mod.world.gen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.structure.dungeon.DungeonPiecePlacement;
import dev.galacticraft.mod.structure.dungeon.LunarDungeonGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

public class LunarDungeonStructure extends Structure {
    public static final MapCodec<LunarDungeonStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    StructureSettings.CODEC
                            .fieldOf("config")
                            .forGetter(structure -> structure.settings)
            ).apply(instance, LunarDungeonStructure::new));

    private static final int HUB_DEPTH = 16;

    public LunarDungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(
            GenerationContext context
    ) {
        return onTopOfChunkCenter(
                context,
                Heightmap.Types.WORLD_SURFACE_WG,
                builder -> addPieces(
                        builder,
                        context
                )
        );
    }

    private static void addPieces(
            StructurePiecesBuilder builder,
            GenerationContext context
    ) {
        ChunkPos chunkPos = context.chunkPos();

        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();

        int surfaceY = context.chunkGenerator().getBaseHeight(
                centerX,
                centerZ,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        int hubY = surfaceY - HUB_DEPTH;

        if (hubY <= context.heightAccessor().getMinBuildHeight()) {
            return;
        }

        BlockPos hubCenter = new BlockPos(
                centerX,
                hubY,
                centerZ
        );

        RandomSource random = RandomSource.create(
                context.random().nextLong()
        );

        Optional<List<DungeonPiecePlacement>> generated =
                LunarDungeonGenerator.generate(
                        context.structureTemplateManager(),
                        hubCenter,
                        context.heightAccessor().getMinBuildHeight(),
                        context.heightAccessor().getMaxBuildHeight(),
                        random
                );

        if (generated.isEmpty()) {
            return;
        }

        List<DungeonPiecePlacement> placements =
                generated.get();

        if (placements.isEmpty()) {
            return;
        }

        LunarDungeonGenerator.addPieces(
                context.structureTemplateManager(),
                placements,
                builder
        );
    }

    @Override
    public StructureType<?> type() {
        return GCStructureTypes.LUNAR_DUNGEON;
    }
}