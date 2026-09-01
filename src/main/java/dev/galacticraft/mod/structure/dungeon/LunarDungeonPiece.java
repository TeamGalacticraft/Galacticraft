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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;

public class LunarDungeonPiece extends TemplateStructurePiece {
    private static final String SURFACE_ENTRANCE_MARKER = "surface_entrance";

    /*
     * The bottom of the shaft is effectively a 3x3 hole.
     *
     * Radius 1.5 includes:
     *
     * XXX
     * XXX
     * XXX
     */
    private static final double BOTTOM_SHAFT_RADIUS = 1.5D;

    /*
     * The surface opening is roughly 9 blocks across.
     */
    private static final double TOP_SHAFT_RADIUS = 4.5D;

    /*
     * Higher values cause the tunnel to remain narrow for longer as it
     * approaches the dungeon, while widening more quickly near the surface.
     */
    private static final double SHAFT_TAPER_EXPONENT = 2.0D;

    /*
     * Maximum horizontal block offset actually required for a 9-wide circle.
     */
    private static final int MAX_SHAFT_OFFSET = 4;

    /*
     * Used as a fallback when the centre column has already been carved and
     * its heightmap therefore no longer represents the original surface.
     */
    private static final int SURFACE_SAMPLE_DISTANCE = 6;

    private static final int UNSET_SURFACE_Y = Integer.MIN_VALUE;

    private int surfaceEntranceY = UNSET_SURFACE_Y;

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

        expandBoundingBoxForEntrance();
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

        if (nbt.contains("SurfaceEntranceY")) {
            this.surfaceEntranceY =
                    nbt.getInt("SurfaceEntranceY");
        }

        expandBoundingBoxForEntrance();
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

        if (this.surfaceEntranceY != UNSET_SURFACE_Y) {
            nbt.putInt(
                    "SurfaceEntranceY",
                    this.surfaceEntranceY
            );
        }
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
        /*
         * Find this before calling super.postProcess(), because vanilla puts
         * the current chunk bounding box into placeSettings during placement.
         *
         * findDataMarker() deliberately creates fresh placement settings so
         * the marker can always be found regardless of which chunk is
         * currently placing this structure piece.
         */
        BlockPos surfaceEntrance =
                findDataMarker(SURFACE_ENTRANCE_MARKER);

        if (surfaceEntrance != null
                && this.surfaceEntranceY == UNSET_SURFACE_Y) {

            this.surfaceEntranceY =
                    calculateSurfaceY(
                            world,
                            surfaceEntrance
                    );
        }

        /*
         * Place the normal room template first.
         *
         * The shaft is carved afterwards so Moon terrain or roof blocks cannot
         * refill the entrance opening.
         */
        super.postProcess(
                world,
                structureManager,
                chunkGenerator,
                random,
                chunkBox,
                chunkPos,
                pivot
        );

        if (surfaceEntrance == null) {
            return;
        }

        if (this.surfaceEntranceY
                <= surfaceEntrance.getY()) {
            return;
        }

        carveSurfaceEntrance(
                world,
                surfaceEntrance,
                this.surfaceEntranceY,
                chunkBox
        );

        /*
         * TemplateStructurePiece resets its own bounding box when
         * postProcess() runs, so restore the expanded shaft bounds.
         */
        expandBoundingBoxForEntrance(
                surfaceEntrance,
                this.surfaceEntranceY
        );
    }

    /**
     * Finds a DATA-mode structure block in this template and returns its
     * transformed world position.
     */
    private BlockPos findDataMarker(
            String metadata
    ) {
        /*
         * Fresh settings are important here because the normal placeSettings
         * may currently contain a bounding box for one particular chunk.
         */
        StructurePlaceSettings markerSettings =
                placementSettings(
                        this.placeSettings.getRotation()
                );

        for (StructureTemplate.StructureBlockInfo info
                : this.template.filterBlocks(
                this.templatePosition,
                markerSettings,
                Blocks.STRUCTURE_BLOCK
        )) {

            CompoundTag markerNbt = info.nbt();

            if (markerNbt == null) {
                continue;
            }

            if (!"DATA".equals(
                    markerNbt.getString("mode")
            )) {
                continue;
            }

            if (metadata.equals(
                    markerNbt.getString("metadata")
            )) {
                return info.pos();
            }
        }

        return null;
    }

    private static int calculateSurfaceY(
            WorldGenLevel world,
            BlockPos entrance
    ) {
        int centerSurface =
                world.getHeight(
                        Heightmap.Types.WORLD_SURFACE_WG,
                        entrance.getX(),
                        entrance.getZ()
                );

        if (centerSurface
                > entrance.getY() + 4) {
            return centerSurface;
        }

        int surfaceY = centerSurface;

        int[][] samples = {
                {SURFACE_SAMPLE_DISTANCE, 0},
                {-SURFACE_SAMPLE_DISTANCE, 0},
                {0, SURFACE_SAMPLE_DISTANCE},
                {0, -SURFACE_SAMPLE_DISTANCE},

                {SURFACE_SAMPLE_DISTANCE, SURFACE_SAMPLE_DISTANCE},
                {SURFACE_SAMPLE_DISTANCE, -SURFACE_SAMPLE_DISTANCE},
                {-SURFACE_SAMPLE_DISTANCE, SURFACE_SAMPLE_DISTANCE},
                {-SURFACE_SAMPLE_DISTANCE, -SURFACE_SAMPLE_DISTANCE}
        };

        for (int[] sample : samples) {
            int sampleY =
                    world.getHeight(
                            Heightmap.Types.WORLD_SURFACE_WG,
                            entrance.getX()
                                    + sample[0],
                            entrance.getZ()
                                    + sample[1]
                    );

            surfaceY =
                    Math.max(
                            surfaceY,
                            sampleY
                    );
        }

        return surfaceY;
    }

    private static void carveSurfaceEntrance(
            WorldGenLevel world,
            BlockPos entrance,
            int surfaceY,
            BoundingBox chunkBox
    ) {
        int bottomY = entrance.getY();

        int height =
                Math.max(
                        1,
                        surfaceY - bottomY
                );

        BlockPos.MutableBlockPos mutable =
                new BlockPos.MutableBlockPos();

        for (int y = bottomY;
             y <= surfaceY;
             y++) {

            /*
             * 0 = dungeon entrance
             * 1 = surface
             */
            double progress =
                    (y - bottomY)
                            / (double) height;

            /*
             * Quadratic widening:
             *
             * radius = 1.5 at entrance
             * radius = 4.5 at surface
             *
             * Because progress is squared, the radius remains near 1.5 for
             * much longer in the lower section and widens increasingly quickly
             * as it approaches the surface.
             */
            double taperedProgress =
                    Math.pow(
                            progress,
                            SHAFT_TAPER_EXPONENT
                    );

            double radius =
                    BOTTOM_SHAFT_RADIUS
                            + (
                            TOP_SHAFT_RADIUS
                                    - BOTTOM_SHAFT_RADIUS
                    ) * taperedProgress;

            double radiusSquared =
                    radius * radius;

            for (int dx = -MAX_SHAFT_OFFSET;
                 dx <= MAX_SHAFT_OFFSET;
                 dx++) {

                for (int dz = -MAX_SHAFT_OFFSET;
                     dz <= MAX_SHAFT_OFFSET;
                     dz++) {

                    double distanceSquared =
                            dx * dx
                                    + dz * dz;

                    if (distanceSquared
                            > radiusSquared) {
                        continue;
                    }

                    mutable.set(
                            entrance.getX() + dx,
                            y,
                            entrance.getZ() + dz
                    );

                    /*
                     * Only modify the chunk Minecraft is currently processing.
                     *
                     * The entrance piece's bounding box includes the whole
                     * shaft footprint, so neighbouring chunks will carve their
                     * own portion when their FEATURES stage runs.
                     */
                    if (!chunkBox.isInside(mutable)) {
                        continue;
                    }

                    world.setBlock(
                            mutable,
                            Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_CLIENTS
                    );
                }
            }
        }
    }

    private void expandBoundingBoxForEntrance() {
        BlockPos entrance =
                findDataMarker(
                        SURFACE_ENTRANCE_MARKER
                );

        if (entrance == null) {
            return;
        }

        int topY =
                this.surfaceEntranceY
                        == UNSET_SURFACE_Y
                        ? this.boundingBox.maxY()
                        : this.surfaceEntranceY;

        expandBoundingBoxForEntrance(
                entrance,
                topY
        );
    }

    private void expandBoundingBoxForEntrance(
            BlockPos entrance,
            int topY
    ) {
        this.boundingBox =
                new BoundingBox(
                        Math.min(
                                this.boundingBox.minX(),
                                entrance.getX()
                                        - MAX_SHAFT_OFFSET
                        ),
                        this.boundingBox.minY(),
                        Math.min(
                                this.boundingBox.minZ(),
                                entrance.getZ()
                                        - MAX_SHAFT_OFFSET
                        ),

                        Math.max(
                                this.boundingBox.maxX(),
                                entrance.getX()
                                        + MAX_SHAFT_OFFSET
                        ),
                        Math.max(
                                this.boundingBox.maxY(),
                                topY
                        ),
                        Math.max(
                                this.boundingBox.maxZ(),
                                entrance.getZ()
                                        + MAX_SHAFT_OFFSET
                        )
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

            case "treasure_chest",
                 "loot_treasure" ->
                    placeLootChest(
                            world,
                            pos,
                            random,
                            GCLootTables.MOON_DUNGEON_TREASURE_CHEST
                    );

            case SURFACE_ENTRANCE_MARKER -> {
            }

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