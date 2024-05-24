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

package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.structure.dungeon.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Locale;

public class GCStructurePieceTypes {
    public static final GCRegistry<StructurePieceType> PIECES = new GCRegistry<>(BuiltInRegistries.STRUCTURE_PIECE);
    public static final StructurePieceType MOON_RUINS_PIECE = setTemplatePieceId(MoonRuinsGenerator.Piece::new, "moon_ruins_piece");
    public static final StructurePieceType DUNGEON_START = setPieceId(DungeonStart::new, "MoonDungeonStart");
    public static final StructurePieceType ROOM_ENTRANCE = setPieceId(RoomEntrance::new, "MoonDungeonEntranceRoom");
    public static final StructurePieceType CORRIDOR = setPieceId(Corridor::new, "MoonDungeonCorridor");
    public static final StructurePieceType EMPTY = setPieceId(RoomEmpty::new, "MoonDungeonEmptyRoom");
    public static final StructurePieceType ROOM_SPAWNER = setPieceId(RoomSpawner::new, "MoonDungeonSpawnerRoom");
    public static final StructurePieceType ROOM_BOSS = setGCPieceId(RoomBoss::new, RoomBoss::new, RoomBoss::new, "MoonDungeonBossRoom");
    public static final StructurePieceType ROOM_CHEST = setPieceId(RoomChest::new, "MoonDungeonChestRoom");
    public static final StructurePieceType ROOM_TREASURE = setGCPieceId(RoomTreasure::new, RoomTreasure::new, RoomTreasure::new, "MoonDungeonTreasureRoom");

    public static void register() {}

    private static StructurePieceType setFullContextPieceId(StructurePieceType type, String id) {
        return PIECES.register(id.toLowerCase(Locale.ROOT), type);
    }

    private static StructurePieceType setPieceId(StructurePieceType.ContextlessType type, String id) {
        return setFullContextPieceId(type, id);
    }

    private static StructurePieceType setTemplatePieceId(StructurePieceType.StructureTemplateType type, String id) {
        return setFullContextPieceId(type, id);
    }

    private static StructurePieceType setGCPieceId(StructurePieceType.ContextlessType type, GCRoomPiece piece, GCRoomPieceWithSize withSize , String id) {
        return setFullContextPieceId(new GCRoomPieceType() {
            @Override
            public SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, Direction direction, int genDepth) {
                return piece.create(configuration, random, blockPosX, blockPosZ, direction, genDepth);
            }

            @Override
            public SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth) {
                return withSize.create(configuration, random, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, direction, genDepth);
            }

            @Override
            public StructurePiece load(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
                return type.load(tag);
            }
        }, id);
    }

    public interface GCRoomPiece {
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, Direction direction, int genDepth);
    }

    public interface GCRoomPieceWithSize {
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth);
    }

    public interface GCRoomPieceType extends StructurePieceType {
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, Direction direction, int genDepth);
        SizedPiece create(DungeonConfiguration configuration, RandomSource random, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth);
    }
}
