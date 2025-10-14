package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * Places a single structure template.
 * - Rotates around the template center (not the lower corner).
 * - Bounding box is computed from RoomDef.size* and the requested rotation.
 *
 * Contract:
 *   position = desired world MIN corner of the ROTATED template’s AABB.
 */
public final class TemplatePiece extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();

    // --- immutable placement info ---
    private final ResourceLocation templateId;
    private final Rotation rotation;
    private final int sizeX, sizeY, sizeZ;

    // We keep the requested world min for the rotated template
    private final BlockPos worldMinAfterRotation;

    // Optional (debug/trace): the logical room id
    private final String roomId;

    // ====== Constructors ======

    public TemplatePiece(RoomDef def, BlockPos position, Rotation rotation) {
        // Use a generic piece type; replace with your own if you have one (e.g., StructurePieceType.JIGSAW).
        super(StructurePieceType.JIGSAW, 0, computeBoundingBox(def, position, rotation));
        Objects.requireNonNull(def, "def");
        this.templateId = ResourceLocation.parse(def.template());
        this.rotation = Objects.requireNonNull(rotation, "rotation");
        this.sizeX = def.sizeX();
        this.sizeY = def.sizeY();
        this.sizeZ = def.sizeZ();
        this.worldMinAfterRotation = Objects.requireNonNull(position, "position");
        this.roomId = def.id();
    }

    /** NBT load (required by the structure system). */
    public TemplatePiece(CompoundTag tag) {
        super(StructurePieceType.JIGSAW, tag);
        this.templateId = ResourceLocation.parse(tag.getString("tpl"));
        this.rotation = Rotation.valueOf(tag.getString("rot"));

        this.sizeX = tag.getInt("sx");
        this.sizeY = tag.getInt("sy");
        this.sizeZ = tag.getInt("sz");

        int wx = tag.getInt("wx");
        int wy = tag.getInt("wy");
        int wz = tag.getInt("wz");
        this.worldMinAfterRotation = new BlockPos(wx, wy, wz);

        this.roomId = tag.contains("roomId") ? tag.getString("roomId") : "";

        // Recompute bounding box from persisted size + rotation + worldMinAfterRotation
        this.boundingBox = computeBoundingBox(sizeX, sizeY, sizeZ, worldMinAfterRotation, rotation);
    }

    // ====== Serialization ======

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putString("tpl", this.templateId.toString());
        tag.putString("rot", this.rotation.name());
        tag.putInt("sx", this.sizeX);
        tag.putInt("sy", this.sizeY);
        tag.putInt("sz", this.sizeZ);
        tag.putInt("wx", this.worldMinAfterRotation.getX());
        tag.putInt("wy", this.worldMinAfterRotation.getY());
        tag.putInt("wz", this.worldMinAfterRotation.getZ());
        if (this.roomId != null && !this.roomId.isBlank()) tag.putString("roomId", this.roomId);
    }

    // ====== Placement ======

    @Override
    public void postProcess(WorldGenLevel level,
                            StructureManager structureManager,
                            ChunkGenerator chunkGenerator,
                            RandomSource randomSource,
                            BoundingBox chunkBox,
                            ChunkPos chunkPos,
                            BlockPos pivotIgnored) {

        // Use the StructureManager provided by worldgen (server may be null here!)
        StructureTemplateManager tm =
                Objects.requireNonNull(level.getServer()).getStructureManager();
        StructureTemplate tpl = tm.getOrCreate(this.templateId);

        BlockPos pivot = new BlockPos(sizeX / 2, sizeY / 2, sizeZ / 2);
        LocalAabb rotLocal = rotatedLocalAabb(sizeX, sizeY, sizeZ, rotation, pivot);

        BlockPos origin = this.worldMinAfterRotation.subtract(rotLocal.min());

        // IMPORTANT: clip strictly to the currently generating chunk
        StructurePlaceSettings place = new StructurePlaceSettings()
                .setRotation(this.rotation)
                .setRotationPivot(pivot)
                .setBoundingBox(chunkBox)  // clip to this chunk
                .setKnownShape(true)
                .setIgnoreEntities(true);

        tpl.placeInWorld(level, origin, origin, place, randomSource, 2);
    }

    // ====== Bounding box math (center rotation) ======

    private static BoundingBox computeBoundingBox(RoomDef def, BlockPos worldMinAfterRotation, Rotation rot) {
        return computeBoundingBox(def.sizeX(), def.sizeY(), def.sizeZ(), worldMinAfterRotation, rot);
    }

    private static BoundingBox computeBoundingBox(int sx, int sy, int sz, BlockPos worldMinAfterRotation, Rotation rot) {
        BlockPos pivot = new BlockPos(sx / 2, sy / 2, sz / 2);
        LocalAabb rotLocal = rotatedLocalAabb(sx, sy, sz, rot, pivot);

        BlockPos minW = worldMinAfterRotation;
        BlockPos maxW = worldMinAfterRotation.offset(
                rotLocal.sizeX() - 1,
                rotLocal.sizeY() - 1,
                rotLocal.sizeZ() - 1
        );
        return BoundingBox.fromCorners(minW, maxW);
    }

    /** Local-space AABB of template [0..sx-1,0..sy-1,0..sz-1] rotated around 'pivot'. */
    private static LocalAabb rotatedLocalAabb(int sx, int sy, int sz, Rotation rot, BlockPos pivot) {
        // corners of the unrotated template volume
        BlockPos[] corners = new BlockPos[] {
                new BlockPos(0,     0,     0),
                new BlockPos(sx-1,  0,     0),
                new BlockPos(0,     sy-1,  0),
                new BlockPos(0,     0,     sz-1),
                new BlockPos(sx-1,  sy-1,  0),
                new BlockPos(sx-1,  0,     sz-1),
                new BlockPos(0,     sy-1,  sz-1),
                new BlockPos(sx-1,  sy-1,  sz-1)
        };

        // Rotate each corner around pivot using the same math that StructureTemplate uses internally.
        // We can leverage StructureTemplate.transform(...) by passing Mirror.NONE.
        int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE,minZ=Integer.MAX_VALUE;
        int maxX=Integer.MIN_VALUE,maxY=Integer.MIN_VALUE,maxZ=Integer.MIN_VALUE;

        for (BlockPos c : corners) {
            BlockPos rc = StructureTemplate.transform(c, net.minecraft.world.level.block.Mirror.NONE, rot, pivot);
            if (rc.getX() < minX) minX = rc.getX();
            if (rc.getY() < minY) minY = rc.getY();
            if (rc.getZ() < minZ) minZ = rc.getZ();
            if (rc.getX() > maxX) maxX = rc.getX();
            if (rc.getY() > maxY) maxY = rc.getY();
            if (rc.getZ() > maxZ) maxZ = rc.getZ();
        }

        BlockPos min = new BlockPos(minX, minY, minZ);
        BlockPos max = new BlockPos(maxX, maxY, maxZ);
        BlockPos size = max.subtract(min).offset(1,1,1);
        return new LocalAabb(min, size);
    }

    // ====== tiny value class ======
    private record LocalAabb(BlockPos min, BlockPos size) {
        int sizeX() { return size.getX(); }
        int sizeY() { return size.getY(); }
        int sizeZ() { return size.getZ(); }
    }
}