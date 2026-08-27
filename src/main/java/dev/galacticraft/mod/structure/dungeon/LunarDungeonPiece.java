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

package dev.galacticraft.mod.structure.dungeon;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCLootTables;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;

public class LunarDungeonPiece extends TemplateStructurePiece {
    public LunarDungeonPiece(
            StructureTemplateManager manager,
            ResourceLocation template,
            BlockPos position,
            Rotation rotation
    ) {
        super(
                GCStructurePieceTypes.LUNAR_DUNGEON_PIECE,
                0,
                manager,
                template,
                template.toString(),
                placementSettings(rotation),
                position
        );
    }

    public LunarDungeonPiece(
            StructureTemplateManager manager,
            CompoundTag nbt
    ) {
        super(
                GCStructurePieceTypes.LUNAR_DUNGEON_PIECE,
                nbt,
                manager,
                id -> placementSettings(
                        Rotation.valueOf(
                                nbt.getString("Rot")
                        )
                )
        );
    }

    private static StructurePlaceSettings placementSettings(
            Rotation rotation
    ) {
        return new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(rotation)
                .addProcessor(
                        BlockIgnoreProcessor.STRUCTURE_BLOCK
                );
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext context,
            CompoundTag nbt
    ) {
        super.addAdditionalSaveData(
                context,
                nbt
        );

        nbt.putString(
                "Rot",
                this.placeSettings
                        .getRotation()
                        .name()
        );
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox chunkBox,
            ChunkPos chunkPos,
            BlockPos pivot
    ) {
        super.postProcess(
                world,
                structureManager,
                chunkGenerator,
                random,
                chunkBox,
                chunkPos,
                pivot
        );
    }

    @Override
    protected void handleDataMarker(
            String metadata,
            BlockPos pos,
            ServerLevelAccessor world,
            RandomSource random,
            BoundingBox boundingBox
    ) {
        if (metadata.startsWith(
                DungeonConnector.PREFIX
        )) {
            world.setBlock(
                    pos,
                    Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_CLIENTS
            );

            return;
        }

        switch (metadata) {
            case "loot_basic" ->
                    placeLootChest(
                            world,
                            pos,
                            random,
                            GCLootTables.MOON_DUNGEON_BASIC_CHEST
                    );

            case "loot_rare" ->
                    placeLootChest(
                            world,
                            pos,
                            random,
                            GCLootTables.MOON_DUNGEON_RARE_CHEST
                    );

            case "loot_treasure" ->
                    placeLootChest(
                            world,
                            pos,
                            random,
                            GCLootTables.MOON_DUNGEON_TREASURE_CHEST
                    );

            default -> {
            }
        }
    }

    private static void placeLootChest(
            ServerLevelAccessor world,
            BlockPos pos,
            RandomSource random,
            ResourceKey<LootTable> lootTable
    ) {
        world.setBlock(
                pos,
                Blocks.CHEST
                        .defaultBlockState()
                        .setValue(
                                ChestBlock.WATERLOGGED,
                                world.getFluidState(pos)
                                        .is(
                                                FluidTags.WATER
                                        )
                        ),
                Block.UPDATE_CLIENTS
        );

        BlockEntity blockEntity =
                world.getBlockEntity(pos);

        if (blockEntity instanceof ChestBlockEntity chest) {
            chest.setLootTable(
                    lootTable,
                    random.nextLong()
            );
        }
    }
}