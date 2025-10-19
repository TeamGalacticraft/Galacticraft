/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
 * Contract:
 * position = desired world MIN corner of the ROTATED template’s AABB.
 * <p>
 * All rotation/translation math is delegated to PortGeom to keep it consistent.
 */
public final class RoomGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ResourceLocation templateId;
    private final Rotation rotation;
    private final int sizeX, sizeY, sizeZ;

    /**
     * Requested world MIN corner of the rotated template AABB.
     */
    private final BlockPos worldMinAfterRotation;

    private final String roomId;

    /**
     * Cached world-space bounding box for this placement (computed via PortGeom).
     */
    private final BoundingBox boundingBox;

    public RoomGenerator(RoomDef def, BlockPos position, Rotation rotation) {
        Objects.requireNonNull(def, "def");
        this.templateId = ResourceLocation.parse(def.template());
        this.rotation = Objects.requireNonNull(rotation, "rotation");
        this.sizeX = def.sizeX();
        this.sizeY = def.sizeY();
        this.sizeZ = def.sizeZ();
        this.worldMinAfterRotation = Objects.requireNonNull(position, "position");
        this.roomId = def.id();
        // unified bounding box computation
        this.boundingBox = PortGeom.computeBoundingBox(sizeX, sizeY, sizeZ, worldMinAfterRotation, rotation);
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

    public BoundingBox boundingBox() {
        return boundingBox;
    }

    /**
     * Returns blocks this placement would emit, grouped by SectionPos.
     * Rotation and translation use the same pivot/origin as PortGeom.
     */
    public HashMap<SectionPos, List<BlockData>> getBlocks(TemplateScanner scanner) {
        var result = new HashMap<SectionPos, List<BlockData>>();

        var tb = scanner.loadBlocks(this.templateId);

        // Precompute pivot & origin using unified helper
        var po = PortGeom.originForPlacedMin(sizeX, sizeY, sizeZ, rotation, worldMinAfterRotation);
        BlockPos pivot = po.pivot();
        BlockPos origin = po.origin();

        Mirror mirror = Mirror.NONE;
        Rotation rot = this.rotation;

        for (StructureTemplate.StructureBlockInfo info : tb.rawBlocks()) {
            // local -> rotated/mirrored -> world, with unified pivot/origin
            BlockPos rotatedLocal = StructureTemplate.transform(info.pos(), mirror, rot, pivot);
            BlockPos worldPos = rotatedLocal.offset(origin);

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
}