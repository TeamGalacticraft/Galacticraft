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

        // Rotate around template min-corner, not piece AABB center
        var place = new StructurePlaceSettings()
                .setRotation(this.yRot)
                .setRotationPivot(BlockPos.ZERO)   // ✅ correct pivot
                .setBoundingBox(box)
                .setKnownShape(true)
                .setIgnoreEntities(true);

        tpl.placeInWorld(level, this.origin, BlockPos.ZERO, place, randomSource, 2);


        var entranceId = dev.galacticraft.mod.Constant.id("dungeon_entrance_block");
        var exitId = dev.galacticraft.mod.Constant.id("dungeon_exit_block");

        var entranceBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(entranceId);
        var exitBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(exitId);

        var idSettings = new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings();
        java.util.List<StructureTemplate.StructureBlockInfo> eInfos = tpl.filterBlocks(BlockPos.ZERO, idSettings, entranceBlock);
        java.util.List<StructureTemplate.StructureBlockInfo> xInfos = tpl.filterBlocks(BlockPos.ZERO, idSettings, exitBlock);

        net.minecraft.world.level.block.Block purple = net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE;
        var size = tpl.getSize();

        for (var bi : eInfos) {
            BlockPos wp = Transforms.worldOfLocalMin(bi.pos(), this.origin, size, this.yRot);
            level.setBlock(wp, purple.defaultBlockState(), 2);
        }
        for (var bi : xInfos) {
            BlockPos wp = Transforms.worldOfLocalMin(bi.pos(), this.origin, size, this.yRot);
            level.setBlock(wp, purple.defaultBlockState(), 2);
        }
    }
}