package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class TemplatePiece extends StructurePiece {
    private final ResourceLocation templateId;
    private final BlockPos origin;     // world min-corner where we want the template placed
    private final Rotation yRot;

    public TemplatePiece(RoomTemplateDef def, BlockPos origin, Rotation yRot, BoundingBox box) {
        super(StructurePieceType.JIGSAW, 0, box);
        this.templateId = def.id();
        this.origin = origin;
        this.yRot = yRot;
    }

    public TemplatePiece(CompoundTag tag) {
        super(StructurePieceType.JIGSAW, tag);
        this.templateId = ResourceLocation.parse(tag.getString("tpl"));
        this.origin = new BlockPos(tag.getInt("ox"), tag.getInt("oy"), tag.getInt("oz"));
        this.yRot = Rotation.valueOf(tag.getString("rot"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putString("tpl", templateId.toString());
        tag.putInt("ox", origin.getX());
        tag.putInt("oy", origin.getY());
        tag.putInt("oz", origin.getZ());
        tag.putString("rot", yRot.name());
    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource randomSource,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos unusedPivotFromCaller
    ) {
        var templateManager = level.getLevel().getServer().getStructureManager();
        StructureTemplate tpl = templateManager.getOrCreate(templateId);

        var pivot = boundingBox.getCenter().subtract(origin);

        var place = new StructurePlaceSettings()
                .setRotation(this.yRot)
                .setRotationPivot(pivot)  // pivot at template min-corner
                .setBoundingBox(box)
                .setKnownShape(true)
                .setIgnoreEntities(true);

        // Place the template so local (0,0,0) lands at 'origin'
        tpl.placeInWorld(level, this.origin, pivot, place, randomSource, 2);
    }
}