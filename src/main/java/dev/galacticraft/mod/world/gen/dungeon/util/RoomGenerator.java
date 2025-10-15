package dev.galacticraft.mod.world.gen.dungeon.util;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.dungeon.records.BlockData;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Pure data/logic holder for a single template placement.
 * - Rotates around the template center (not the lower corner).
 * - Bounding box is computed from RoomDef.size* and the requested rotation.
 * <p>
 * Contract:
 * position = desired world MIN corner of the ROTATED template’s AABB.
 */
public final class RoomGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();

    // --- immutable placement info ---
    private final ResourceLocation templateId;
    private final Rotation rotation;
    private final int sizeX, sizeY, sizeZ;

    /**
     * Requested world MIN corner of the rotated template AABB.
     */
    private final BlockPos worldMinAfterRotation;

    /**
     * Optional (debug/trace): the logical room id.
     */
    private final String roomId;

    /**
     * Cached world-space bounding box for this placement.
     */
    private final BoundingBox boundingBox;

    // ====== Constructors ======

    public RoomGenerator(RoomDef def, BlockPos position, Rotation rotation) {
        Objects.requireNonNull(def, "def");
        this.templateId = ResourceLocation.parse(def.template());
        this.rotation = Objects.requireNonNull(rotation, "rotation");
        this.sizeX = def.sizeX();
        this.sizeY = def.sizeY();
        this.sizeZ = def.sizeZ();
        this.worldMinAfterRotation = Objects.requireNonNull(position, "position");
        this.roomId = def.id();
        this.boundingBox = computeBoundingBox(sizeX, sizeY, sizeZ, worldMinAfterRotation, rotation);
    }

    // ====== API ======

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

    /**
     * Local-space AABB of template [0..sx-1,0..sy-1,0..sz-1] rotated around 'pivot'.
     */
    private static LocalAabb rotatedLocalAabb(int sx, int sy, int sz, Rotation rot, BlockPos pivot) {
        // corners of the unrotated template volume
        BlockPos[] corners = new BlockPos[]{
                new BlockPos(0, 0, 0),
                new BlockPos(sx - 1, 0, 0),
                new BlockPos(0, sy - 1, 0),
                new BlockPos(0, 0, sz - 1),
                new BlockPos(sx - 1, sy - 1, 0),
                new BlockPos(sx - 1, 0, sz - 1),
                new BlockPos(0, sy - 1, sz - 1),
                new BlockPos(sx - 1, sy - 1, sz - 1)
        };

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos c : corners) {
            BlockPos rc = StructureTemplate.transform(c, Mirror.NONE, rot, pivot);
            if (rc.getX() < minX) minX = rc.getX();
            if (rc.getY() < minY) minY = rc.getY();
            if (rc.getZ() < minZ) minZ = rc.getZ();
            if (rc.getX() > maxX) maxX = rc.getX();
            if (rc.getY() > maxY) maxY = rc.getY();
            if (rc.getZ() > maxZ) maxZ = rc.getZ();
        }

        BlockPos min = new BlockPos(minX, minY, minZ);
        BlockPos max = new BlockPos(maxX, maxY, maxZ);
        BlockPos size = max.subtract(min).offset(1, 1, 1);
        return new LocalAabb(min, size);
    }

    public ResourceLocation templateId() {
        return templateId;
    }

    public Rotation rotation() {
        return rotation;
    }

    public int sizeX() {
        return sizeX;
    }

    public int sizeY() {
        return sizeY;
    }

    public int sizeZ() {
        return sizeZ;
    }

    public BlockPos worldMinAfterRotation() {
        return worldMinAfterRotation;
    }

    public String roomId() {
        return roomId;
    }

    // ====== Bounding box math (center rotation) ======

    public BoundingBox boundingBox() {
        return boundingBox;
    }

    /**
     * Returns the blocks this placement would emit, grouped by SectionPos, with 12-bit packed local coords.
     * Uses {@link TemplateScanner#loadBlocks(ResourceLocation)} to get cached raw blocks/palette.
     */
    public HashMap<SectionPos, List<BlockData>> getBlocks(TemplateScanner scanner) {
        var result = new HashMap<SectionPos, List<BlockData>>();

        // Load cached raw blocks for this template
        var tb = scanner.loadBlocks(this.templateId);

        // Rotation pivot & origin must match the old postProcess() math
        BlockPos pivot = new BlockPos(sizeX / 2, sizeY / 2, sizeZ / 2);
        LocalAabb rotLocal = rotatedLocalAabb(sizeX, sizeY, sizeZ, this.rotation, pivot);
        BlockPos origin = this.worldMinAfterRotation.subtract(rotLocal.min());

        Mirror mirror = Mirror.NONE;
        Rotation rot = this.rotation;

        for (StructureTemplate.StructureBlockInfo info : tb.rawBlocks()) {
            // local -> rotated/mirrored -> world
            BlockPos rotatedLocal = StructureTemplate.transform(info.pos(), mirror, rot, pivot);
            BlockPos worldPos = rotatedLocal.offset(origin);

            // rotate/mirror state so facings match
            BlockState state = info.state();
            if (state.is(GCBlocks.DUNGEON_ENTRANCE_BLOCK) || state.is(GCBlocks.DUNGEON_EXIT_BLOCK)) {
                state = GCBlocks.OLIANT_NEST_BLOCK.defaultBlockState();
            }
            if (rot != Rotation.NONE) state = state.rotate(rot);

            SectionPos sec = SectionPos.of(worldPos);
            BlockData data = BlockData.ofWorld(sec, worldPos, state);
            result.computeIfAbsent(sec, k -> new java.util.ArrayList<>()).add(data);
        }
        return result;
    }

    // ====== tiny value class ======
    private record LocalAabb(BlockPos min, BlockPos size) {
        int sizeX() {
            return size.getX();
        }

        int sizeY() {
            return size.getY();
        }

        int sizeZ() {
            return size.getZ();
        }
    }
}