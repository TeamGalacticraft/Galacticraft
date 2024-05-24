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
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class DungeonConfiguration {
    public static final Codec<DungeonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("brickBlock").forGetter(DungeonConfiguration::getBrickBlock),
            Codec.INT.fieldOf("yPosition").forGetter(DungeonConfiguration::getYPosition),
            Codec.INT.fieldOf("hallwayLengthMin").forGetter(DungeonConfiguration::getHallwayLengthMin),
            Codec.INT.fieldOf("hallwayLengthMax").forGetter(DungeonConfiguration::getHallwayLengthMax),
            Codec.INT.fieldOf("hallwayHeight").forGetter(DungeonConfiguration::getHallwayHeight),
            Codec.INT.fieldOf("roomHeight").forGetter(DungeonConfiguration::getRoomHeight),
            BuiltInRegistries.STRUCTURE_PIECE.byNameCodec().fieldOf("bossRoom").forGetter(DungeonConfiguration::getBossRoom),
            BuiltInRegistries.STRUCTURE_PIECE.byNameCodec().fieldOf("treasureRoom").forGetter(DungeonConfiguration::getTreasureRoom)
    ).apply(instance, DungeonConfiguration::new));
    private BlockState brickBlock;
    private int yPosition;
    private int hallwayLengthMin;
    private int hallwayLengthMax;
    private int hallwayHeight;
    private int roomHeight;
    private StructurePieceType bossRoom;
    private StructurePieceType treasureRoom;

    public DungeonConfiguration() {
    }

    public DungeonConfiguration(BlockState brickBlock, int yPosition, int hallwayLengthMin, int hallwayLengthMax, int hallwayHeight, int roomHeight, StructurePieceType bossRoom, StructurePieceType treasureRoom) {
        this.brickBlock = brickBlock;
        this.yPosition = yPosition;
        this.hallwayLengthMin = hallwayLengthMin;
        this.hallwayLengthMax = hallwayLengthMax;
        this.hallwayHeight = hallwayHeight;
        this.roomHeight = roomHeight;
        this.bossRoom = bossRoom;
        this.treasureRoom = treasureRoom;
    }

    public CompoundTag write(CompoundTag tagCompound) {
        tagCompound.put("brickBlock", Util.getOrThrow(BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.brickBlock), RuntimeException::new));
        tagCompound.putInt("yPosition", this.yPosition);
        tagCompound.putInt("hallwayLengthMin", this.hallwayLengthMin);
        tagCompound.putInt("hallwayLengthMax", this.hallwayLengthMax);
        tagCompound.putInt("hallwayHeight", this.hallwayHeight);
        tagCompound.putInt("roomHeight", this.roomHeight);
        tagCompound.putString("bossRoom", BuiltInRegistries.STRUCTURE_PIECE.getKey(this.bossRoom).toString());
        tagCompound.putString("treasureRoom", BuiltInRegistries.STRUCTURE_PIECE.getKey(this.treasureRoom).toString());
        return tagCompound;
    }

    public void read(CompoundTag tagCompound) {
        try {
            this.brickBlock = Util.getOrThrow(BlockState.CODEC.decode(NbtOps.INSTANCE, tagCompound.get("brickBlock")), RuntimeException::new).getFirst();
            this.yPosition = tagCompound.getInt("yPosition");
            this.hallwayLengthMin = tagCompound.getInt("hallwayLengthMin");
            this.hallwayLengthMax = tagCompound.getInt("hallwayLengthMax");
            this.hallwayHeight = tagCompound.getInt("hallwayHeight");
            this.roomHeight = tagCompound.getInt("roomHeight");
            this.bossRoom = BuiltInRegistries.STRUCTURE_PIECE.get(ResourceLocation.tryParse(tagCompound.getString("bossRoom")));
            this.treasureRoom = BuiltInRegistries.STRUCTURE_PIECE.get(ResourceLocation.tryParse(tagCompound.getString("treasureRoom")));
        } catch (Exception e) {
            System.err.println("Failed to read dungeon configuration from NBT");
            e.printStackTrace();
        }
    }

    public BlockState getBrickBlock() {
        return brickBlock;
    }

    public int getYPosition() {
        return yPosition;
    }

    public int getHallwayLengthMin() {
        return hallwayLengthMin;
    }

    public int getHallwayLengthMax() {
        return hallwayLengthMax;
    }

    public int getHallwayHeight() {
        return hallwayHeight;
    }

    public int getRoomHeight() {
        return roomHeight;
    }

    public StructurePieceType getBossRoom() {
        return bossRoom;
    }

    public StructurePieceType getTreasureRoom() {
        return treasureRoom;
    }
}
