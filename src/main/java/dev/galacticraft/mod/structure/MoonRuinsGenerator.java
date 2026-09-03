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

package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoonRuinsGenerator {
    private static final int NO_LIMIT = Integer.MAX_VALUE;

    // Each template needs one DATA marker named "surface" at its intended ground plane.
    private static final List<RuinDefinition> PIECES = List.of(
            new RuinDefinition(Constant.id("moon_ruins/ruin_1"), 12, NO_LIMIT),
            new RuinDefinition(Constant.id("moon_ruins/ruin_2"), 2, 1),
            new RuinDefinition(Constant.id("moon_ruins/ruin_3"), 10, NO_LIMIT),
            new RuinDefinition(Constant.id("moon_ruins/ruin_4"), 8, NO_LIMIT),
            new RuinDefinition(Constant.id("moon_ruins/ruin_5"), 8, NO_LIMIT),
            new RuinDefinition(Constant.id("moon_ruins/ruin_6"), 6, NO_LIMIT),
            new RuinDefinition(Constant.id("moon_ruins/ruin_7"), 6, NO_LIMIT)
    );

    private static final int MIN_SURROUNDING_RUINS = 4;
    private static final int MAX_SURROUNDING_RUINS = 8;

    // Terrain blend tuning values.
    private static final int TERRAIN_BLEND_RADIUS = 8;
    private static final int TERRAIN_SAMPLE_RADIUS = 2;
    private static final double TERRAIN_SAMPLE_STRENGTH = 0.55D;
    private static final int TERRAIN_RELAX_PASSES = 2;

    private record RuinDefinition(ResourceLocation templateId, int weight, int maxPerCluster) {
        private RuinDefinition {
            if (weight <= 0) {
                throw new IllegalArgumentException("Ruin weight must be greater than zero");
            }

            if (maxPerCluster <= 0) {
                throw new IllegalArgumentException("Ruin maxPerCluster must be greater than zero");
            }
        }
    }

    private record SurfacePlacement(BlockPos templatePos, int surfaceY, BoundingBox structureBoundingBox) {
    }

    private static final class PlacementState {
        private final Map<ResourceLocation, Integer> counts = new HashMap<>();
        private final List<BoundingBox> occupiedStructureBoxes = new ArrayList<>();

        private int getCount(ResourceLocation templateId) {
            return this.counts.getOrDefault(templateId, 0);
        }

        private boolean canPlace(RuinDefinition definition) {
            return getCount(definition.templateId()) < definition.maxPerCluster();
        }

        private boolean intersectsExisting(BoundingBox box) {
            for (BoundingBox existing : this.occupiedStructureBoxes) {
                if (box.intersects(existing)) {
                    return true;
                }
            }

            return false;
        }

        private void register(RuinDefinition definition, BoundingBox box) {
            this.counts.merge(definition.templateId(), 1, Integer::sum);
            this.occupiedStructureBoxes.add(box);
        }
    }

    private static RuinDefinition getPiece(RandomSource random, PlacementState placementState) {
        int totalWeight = 0;

        for (RuinDefinition definition : PIECES) {
            if (placementState.canPlace(definition)) {
                totalWeight += definition.weight();
            }
        }

        if (totalWeight <= 0) {
            return null;
        }

        int roll = random.nextInt(totalWeight);

        for (RuinDefinition definition : PIECES) {
            if (!placementState.canPlace(definition)) {
                continue;
            }

            roll -= definition.weight();

            if (roll < 0) {
                return definition;
            }
        }

        throw new IllegalStateException("Failed to select an eligible Moon ruin");
    }

    public static void addPiecesToStructure(Structure.GenerationContext context, BlockPos pos, Rotation rotation, StructurePieceAccessor structurePiecesHolder, RandomSource random) {
        PlacementState placementState = new PlacementState();

        addPiece(context, pos, rotation, structurePiecesHolder, random, placementState);
        addSurroundingRuins(context, random, rotation, pos, structurePiecesHolder, placementState);
    }

    private static boolean addPiece(Structure.GenerationContext context, BlockPos pos, Rotation rotation, StructurePieceAccessor structurePiecesHolder, RandomSource random, PlacementState placementState) {
        RuinDefinition definition = getPiece(random, placementState);

        if (definition == null) {
            return false;
        }

        SurfacePlacement placement = alignToSurface(context, definition.templateId(), pos, rotation);

        if (placementState.intersectsExisting(placement.structureBoundingBox())) {
            return false;
        }

        structurePiecesHolder.addPiece(new Piece(context.structureTemplateManager(), definition.templateId(), placement.templatePos(), rotation, placement.surfaceY()));
        placementState.register(definition, placement.structureBoundingBox());

        return true;
    }

    private static SurfacePlacement alignToSurface(Structure.GenerationContext context, ResourceLocation templateId, BlockPos desiredPos, Rotation rotation) {
        StructureTemplateManager manager = context.structureTemplateManager();
        StructureTemplate template = manager.get(templateId).orElseThrow(() -> new IllegalStateException("Missing Moon ruin structure template: " + templateId));
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE);
        List<StructureTemplate.StructureBlockInfo> structureBlocks = template.filterBlocks(BlockPos.ZERO, settings, Blocks.STRUCTURE_BLOCK, true);
        BlockPos markerOffset = getSurfaceMarker(templateId, structureBlocks);
        BoundingBox footprint = template.getBoundingBox(settings, desiredPos);

        long totalHeight = 0L;
        int columnCount = 0;

        for (int x = footprint.minX(); x <= footprint.maxX(); ++x) {
            for (int z = footprint.minZ(); z <= footprint.maxZ(); ++z) {
                int height = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
                totalHeight += height;
                ++columnCount;
            }
        }

        if (columnCount == 0) {
            throw new IllegalStateException("Moon ruin " + templateId + " has an empty X/Z footprint");
        }

        int averageSurfaceY = (int) Math.round((double) totalHeight / (double) columnCount);
        int templateY = averageSurfaceY - markerOffset.getY();
        BlockPos alignedPos = new BlockPos(desiredPos.getX(), templateY, desiredPos.getZ());
        BoundingBox alignedBoundingBox = template.getBoundingBox(settings, alignedPos);

        return new SurfacePlacement(alignedPos, averageSurfaceY, alignedBoundingBox);
    }

    private static @NotNull BlockPos getSurfaceMarker(ResourceLocation templateId, List<StructureTemplate.StructureBlockInfo> structureBlocks) {
        StructureTemplate.StructureBlockInfo surfaceMarker = null;

        for (StructureTemplate.StructureBlockInfo info : structureBlocks) {
            CompoundTag nbt = info.nbt();

            if (nbt == null) {
                continue;
            }

            if ("surface".equals(nbt.getString("metadata"))) {
                if (surfaceMarker != null) {
                    throw new IllegalStateException("Moon ruin structure " + templateId + " contains more than one 'surface' marker");
                }

                surfaceMarker = info;
            }
        }

        if (surfaceMarker == null) {
            throw new IllegalStateException("Moon ruin structure " + templateId + " does not contain a 'surface' data marker");
        }

        return surfaceMarker.pos();
    }

    private static void addSurroundingRuins(Structure.GenerationContext context, RandomSource random, Rotation rotation, BlockPos pos, StructurePieceAccessor structurePiecesHolder, PlacementState placementState) {
        BlockPos centerPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        BlockPos oppositeCorner = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ZERO).offset(centerPos);
        BlockPos candidateOrigin = new BlockPos(Math.min(centerPos.getX(), oppositeCorner.getX()), centerPos.getY(), Math.min(centerPos.getZ(), oppositeCorner.getZ()));
        List<BlockPos> roomPositions = getRoomPositions(random, candidateOrigin);
        int targetRuinCount = Mth.nextInt(random, MIN_SURROUNDING_RUINS, MAX_SURROUNDING_RUINS);
        int placed = 0;

        while (placed < targetRuinCount && !roomPositions.isEmpty()) {
            int index = random.nextInt(roomPositions.size());
            BlockPos ruinPos = roomPositions.remove(index);
            Rotation ruinRotation = Rotation.getRandom(random);

            if (addPiece(context, ruinPos, ruinRotation, structurePiecesHolder, random, placementState)) {
                ++placed;
            }
        }
    }

    private static List<BlockPos> getRoomPositions(RandomSource random, BlockPos blockPos) {
        List<BlockPos> list = new ArrayList<>(8);

        list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, 16 + Mth.nextInt(random, 1, 7)));
        list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, Mth.nextInt(random, 1, 7)));
        list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, -16 + Mth.nextInt(random, 4, 8)));
        list.add(blockPos.offset(Mth.nextInt(random, 1, 7), 0, 16 + Mth.nextInt(random, 1, 7)));
        list.add(blockPos.offset(Mth.nextInt(random, 1, 7), 0, -16 + Mth.nextInt(random, 4, 6)));
        list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, 16 + Mth.nextInt(random, 3, 8)));
        list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, Mth.nextInt(random, 1, 7)));
        list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, -16 + Mth.nextInt(random, 4, 8)));

        return list;
    }

    public static class Piece extends TemplateStructurePiece {
        private final int surfaceY;

        public Piece(StructureTemplateManager structureManager, ResourceLocation template, BlockPos pos, Rotation rotation, int surfaceY) {
            super(GCStructurePieceTypes.MOON_RUINS_PIECE, 0, structureManager, template, template.toString(), createPlaceSettings(rotation), pos);
            this.surfaceY = surfaceY;
            expandBoundingBoxForTerrainBlend();
        }

        public Piece(StructureTemplateManager structureManager, CompoundTag nbt) {
            super(GCStructurePieceTypes.MOON_RUINS_PIECE, nbt, structureManager, identifier -> createPlaceSettings(Rotation.valueOf(nbt.getString("Rot"))));
            this.surfaceY = nbt.contains("SurfaceY") ? nbt.getInt("SurfaceY") : this.templatePosition.getY();
            expandBoundingBoxForTerrainBlend();
        }

        private static StructurePlaceSettings createPlaceSettings(Rotation rotation) {
            // Saved air must be placed so underground parts can clear terrain.
            return new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        }

        private void expandBoundingBoxForTerrainBlend() {
            // postProcess only runs for chunks touching this box, so include the terrain blend area.
            BoundingBox templateBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
            this.boundingBox = templateBox.inflatedBy(TERRAIN_BLEND_RADIUS, 0, TERRAIN_BLEND_RADIUS);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
            super.addAdditionalSaveData(context, nbt);
            nbt.putString("Rot", this.placeSettings.getRotation().name());
            nbt.putInt("SurfaceY", this.surfaceY);
        }

        @Override
        protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {
            switch (metadata) {
                case "surface" -> {
                }
                case "chest" -> placeChest(world, pos, random, false);
                case "treasure" -> placeChest(world, pos, random, true);
                case "monster" -> spawnMonster(world, pos, GCEntityTypes.EVOLVED_PILLAGER);
                case "monster_2" -> spawnMonster(world, pos, GCEntityTypes.EVOLVED_EVOKER);
                default -> {
                }
            }
        }

        private static void placeChest(ServerLevelAccessor world, BlockPos pos, RandomSource random, boolean treasure) {
            world.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, world.getFluidState(pos).is(FluidTags.WATER)), Block.UPDATE_CLIENTS);

            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof ChestBlockEntity chest) {
                if (treasure) {
                    // TODO: Add a dedicated treasure loot table.
                    chest.setLootTable(GCLootTables.BASIC_MOON_RUINS_CHEST, random.nextLong());
                } else {
                    chest.setLootTable(GCLootTables.BASIC_MOON_RUINS_CHEST, random.nextLong());
                }
            }
        }

        private static void spawnMonster(ServerLevelAccessor world, BlockPos pos, EntityType<?> entityType) {
            var entityUnknown = entityType.create(world.getLevel());

            if (entityUnknown instanceof Mob entity) {
                entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
                entity.setPersistenceRequired();
                entity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
                world.addFreshEntityWithPassengers(entity);
            }
        }

        private void blendTerrain(WorldGenLevel world, BoundingBox chunkBoundingBox) {
            BoundingBox structureBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
            BoundingBox blendBox = structureBox.inflatedBy(TERRAIN_BLEND_RADIUS, 0, TERRAIN_BLEND_RADIUS);

            int writeMinX = Math.max(blendBox.minX(), chunkBoundingBox.minX());
            int writeMaxX = Math.min(blendBox.maxX(), chunkBoundingBox.maxX());
            int writeMinZ = Math.max(blendBox.minZ(), chunkBoundingBox.minZ());
            int writeMaxZ = Math.min(blendBox.maxZ(), chunkBoundingBox.maxZ());

            if (writeMinX > writeMaxX || writeMinZ > writeMaxZ) {
                return;
            }

            int gridMinX = blendBox.minX();
            int gridMaxX = blendBox.maxX();
            int gridMinZ = blendBox.minZ();
            int gridMaxZ = blendBox.maxZ();
            int width = gridMaxX - gridMinX + 1;
            int depth = gridMaxZ - gridMinZ + 1;

            int[][] naturalHeights = new int[width][depth];

            // Snapshot natural terrain before changing any columns.
            for (int gx = 0; gx < width; ++gx) {
                int x = gridMinX + gx;

                for (int gz = 0; gz < depth; ++gz) {
                    int z = gridMinZ + gz;
                    naturalHeights[gx][gz] = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                }
            }

            double[][] targetHeights = new double[width][depth];

            for (int gx = 0; gx < width; ++gx) {
                int x = gridMinX + gx;

                for (int gz = 0; gz < depth; ++gz) {
                    int z = gridMinZ + gz;

                    if (isInsideFootprint(x, z, structureBox)) {
                        targetHeights[gx][gz] = this.surfaceY;
                        continue;
                    }

                    double distance = distanceFromFootprint(x, z, structureBox);

                    if (distance >= TERRAIN_BLEND_RADIUS) {
                        targetHeights[gx][gz] = naturalHeights[gx][gz];
                        continue;
                    }

                    double sampledTerrain = sampleSurroundingTerrainHeight(naturalHeights, gridMinX, gridMinZ, gx, gz, structureBox);
                    double rawNatural = naturalHeights[gx][gz];
                    double terrainReference = rawNatural + (sampledTerrain - rawNatural) * TERRAIN_SAMPLE_STRENGTH;
                    double t = Mth.clamp(distance / (double) TERRAIN_BLEND_RADIUS, 0.0D, 1.0D);
                    double blend = smootherStep(t);

                    targetHeights[gx][gz] = this.surfaceY + (terrainReference - this.surfaceY) * blend;
                }
            }

            // Soften the target field without moving the footprint or outer edge.
            for (int pass = 0; pass < TERRAIN_RELAX_PASSES; ++pass) {
                double[][] relaxed = new double[width][depth];

                for (int gx = 0; gx < width; ++gx) {
                    int x = gridMinX + gx;

                    for (int gz = 0; gz < depth; ++gz) {
                        int z = gridMinZ + gz;

                        if (isInsideFootprint(x, z, structureBox)) {
                            relaxed[gx][gz] = this.surfaceY;
                            continue;
                        }

                        double distance = distanceFromFootprint(x, z, structureBox);

                        if (distance >= TERRAIN_BLEND_RADIUS) {
                            relaxed[gx][gz] = naturalHeights[gx][gz];
                            continue;
                        }

                        double weightedSum = targetHeights[gx][gz] * 4.0D;
                        double totalWeight = 4.0D;

                        for (int ox = -1; ox <= 1; ++ox) {
                            int nx = gx + ox;

                            if (nx < 0 || nx >= width) {
                                continue;
                            }

                            for (int oz = -1; oz <= 1; ++oz) {
                                if (ox == 0 && oz == 0) {
                                    continue;
                                }

                                int nz = gz + oz;

                                if (nz < 0 || nz >= depth) {
                                    continue;
                                }

                                double weight = ox == 0 || oz == 0 ? 2.0D : 1.0D;
                                weightedSum += targetHeights[nx][nz] * weight;
                                totalWeight += weight;
                            }
                        }

                        double neighbourAverage = weightedSum / totalWeight;
                        relaxed[gx][gz] = targetHeights[gx][gz] * 0.55D + neighbourAverage * 0.45D;
                    }
                }

                targetHeights = relaxed;
            }

            for (int x = writeMinX; x <= writeMaxX; ++x) {
                int gx = x - gridMinX;

                for (int z = writeMinZ; z <= writeMaxZ; ++z) {
                    int gz = z - gridMinZ;
                    int naturalHeight = naturalHeights[gx][gz];
                    int targetHeight = (int) Math.round(targetHeights[gx][gz]);

                    shapeTerrainColumn(world, x, z, naturalHeight, targetHeight);
                }
            }
        }

        private static boolean isInsideFootprint(int x, int z, BoundingBox structureBox) {
            return x >= structureBox.minX() && x <= structureBox.maxX() && z >= structureBox.minZ() && z <= structureBox.maxZ();
        }

        private static double distanceFromFootprint(int x, int z, BoundingBox structureBox) {
            int dx = 0;

            if (x < structureBox.minX()) {
                dx = structureBox.minX() - x;
            } else if (x > structureBox.maxX()) {
                dx = x - structureBox.maxX();
            }

            int dz = 0;

            if (z < structureBox.minZ()) {
                dz = structureBox.minZ() - z;
            } else if (z > structureBox.maxZ()) {
                dz = z - structureBox.maxZ();
            }

            return Math.sqrt((double) dx * dx + (double) dz * dz);
        }

        private static double sampleSurroundingTerrainHeight(int[][] naturalHeights, int gridMinX, int gridMinZ, int centerGX, int centerGZ, BoundingBox structureBox) {
            int width = naturalHeights.length;
            int depth = naturalHeights[0].length;
            double weightedSum = 0.0D;
            double totalWeight = 0.0D;

            for (int ox = -TERRAIN_SAMPLE_RADIUS; ox <= TERRAIN_SAMPLE_RADIUS; ++ox) {
                int gx = centerGX + ox;

                if (gx < 0 || gx >= width) {
                    continue;
                }

                int sampleX = gridMinX + gx;

                for (int oz = -TERRAIN_SAMPLE_RADIUS; oz <= TERRAIN_SAMPLE_RADIUS; ++oz) {
                    int gz = centerGZ + oz;

                    if (gz < 0 || gz >= depth) {
                        continue;
                    }

                    int sampleZ = gridMinZ + gz;

                    if (isInsideFootprint(sampleX, sampleZ, structureBox)) {
                        continue;
                    }

                    int distanceSquared = ox * ox + oz * oz;
                    double weight = 1.0D / (1.0D + (double) distanceSquared);

                    weightedSum += naturalHeights[gx][gz] * weight;
                    totalWeight += weight;
                }
            }

            if (totalWeight <= 0.0D) {
                return naturalHeights[centerGX][centerGZ];
            }

            return weightedSum / totalWeight;
        }

        private static double smootherStep(double t) {
            t = Mth.clamp(t, 0.0D, 1.0D);
            return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
        }

        private static void shapeTerrainColumn(WorldGenLevel world, int x, int z, int currentHeight, int targetHeight) {
            int minBuildHeight = world.getMinBuildHeight();
            int maxBuildHeight = world.getMaxBuildHeight();

            // Heightmap values point to the first free block, so keep one valid block below them.
            targetHeight = Mth.clamp(targetHeight, minBuildHeight + 1, maxBuildHeight);
            currentHeight = Mth.clamp(currentHeight, minBuildHeight + 1, maxBuildHeight);

            if (currentHeight == targetHeight) {
                return;
            }

            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            cursor.set(x, currentHeight - 1, z);

            BlockState topState = world.getBlockState(cursor);

            if (topState.isAir()) {
                return;
            }

            BlockState fillerState = findSubsurfaceState(world, x, currentHeight - 2, z, topState);

            if (targetHeight > currentHeight) {
                cursor.set(x, currentHeight - 1, z);
                world.setBlock(cursor, fillerState, Block.UPDATE_CLIENTS);

                for (int y = currentHeight; y < targetHeight - 1; ++y) {
                    cursor.set(x, y, z);
                    world.setBlock(cursor, fillerState, Block.UPDATE_CLIENTS);
                }

                cursor.set(x, targetHeight - 1, z);
                world.setBlock(cursor, topState, Block.UPDATE_CLIENTS);
            } else {
                for (int y = currentHeight - 1; y >= targetHeight; --y) {
                    cursor.set(x, y, z);
                    world.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }

                cursor.set(x, targetHeight - 1, z);
                world.setBlock(cursor, topState, Block.UPDATE_CLIENTS);
            }
        }

        private static BlockState findSubsurfaceState(WorldGenLevel world, int x, int startY, int z, BlockState fallback) {
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            int minY = Math.max(world.getMinBuildHeight(), startY - 8);

            for (int y = startY; y >= minY; --y) {
                cursor.set(x, y, z);

                BlockState state = world.getBlockState(cursor);

                if (!state.isAir() && state.getFluidState().isEmpty()) {
                    return state;
                }
            }

            return fallback;
        }

        @Override
        public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
            // Blend first so the template's saved blocks and air are placed last.
            blendTerrain(world, boundingBox);
            super.postProcess(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
        }
    }
}
