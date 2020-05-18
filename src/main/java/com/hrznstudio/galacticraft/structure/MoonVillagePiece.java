package com.hrznstudio.galacticraft.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class MoonVillagePiece extends PoolStructurePiece {
    public MoonVillagePiece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox) {
        super(GalacticraftStructurePieceTypes.MOON_VILLAGE, structureManager, structurePoolElement, blockPos, i, blockRotation, blockBox);
    }

    public MoonVillagePiece(StructureManager structureManager, CompoundTag compoundTag) {
        super(structureManager, compoundTag, GalacticraftStructurePieceTypes.MOON_VILLAGE);
    }
}
