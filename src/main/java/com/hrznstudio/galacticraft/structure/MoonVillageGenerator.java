package com.hrznstudio.galacticraft.structure;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;

public class MoonVillageGenerator {
    public static void addPieces(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos pos, List<StructurePiece> pieces, ChunkRandom random) {
        MoonVillageData.init();
        StructurePoolBasedGenerator.addPieces(new Identifier(Constants.MOD_ID, "moon_village/town_centers"), 6, MoonVillageGenerator.Piece::new, chunkGenerator, structureManager, pos, pieces, random);
    }

    public static class Piece extends PoolStructurePiece {
        public Piece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox) {
            super(GalacticraftStructurePieceTypes.MOON_VILLAGE, structureManager, structurePoolElement, blockPos, i, blockRotation, blockBox);
        }

        public Piece(StructureManager structureManager, CompoundTag compoundTag) {
            super(structureManager, compoundTag, GalacticraftStructurePieceTypes.MOON_VILLAGE);
        }
    }
}