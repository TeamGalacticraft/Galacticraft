package dev.galacticraft.mod.world.gen.dungeon.piece;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.world.gen.dungeon.TemplatePiece;
import dev.galacticraft.mod.world.gen.dungeon.plan.DungeonPlan;
import dev.galacticraft.mod.world.gen.dungeon.util.Bitmask;
import dev.galacticraft.mod.world.gen.dungeon.util.MaskCarvePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class DeferredDungeonPiece extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final StructurePieceType TYPE = (ctx, tag) -> new DeferredDungeonPiece(ctx, tag);

    // The umbrella piece owns the async plan:
    private CompletableFuture<DungeonPlan> planFuture;

    // Once the plan completes the first time we’re called, we materialize child “helpers”
    // and reuse them for each chunk that intersects.
    private List<TemplatePiece> roomPieces;   // derived from plan
    private MaskCarvePiece corridorsPiece;    // derived from plan
    private boolean initializedFromPlan = false;

    // Empty ctor for deserialization
    public DeferredDungeonPiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, 0, BoundingBox.infinite());
        int ux0 = tag.getInt("ux0"), uy0 = tag.getInt("uy0"), uz0 = tag.getInt("uz0");
        int ux1 = tag.getInt("ux1"), uy1 = tag.getInt("uy1"), uz1 = tag.getInt("uz1");
        this.boundingBox = new BoundingBox(ux0, uy0, uz0, ux1, uy1, uz1);
    }

    public DeferredDungeonPiece(BoundingBox umbrella, CompletableFuture<DungeonPlan> planFuture) {
        super(TYPE, 0, umbrella);
        this.planFuture = planFuture;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putInt("ux0", this.getBoundingBox().minX());
        tag.putInt("uy0", this.getBoundingBox().minY());
        tag.putInt("uz0", this.getBoundingBox().minZ());
        tag.putInt("ux1", this.getBoundingBox().maxX());
        tag.putInt("uy1", this.getBoundingBox().maxY());
        tag.putInt("uz1", this.getBoundingBox().maxZ());
    }

    @Override
    public void postProcess(WorldGenLevel gen,
                            StructureManager structureManager,
                            ChunkGenerator chunkGenerator,
                            RandomSource randomSource,
                            BoundingBox chunkBox,
                            ChunkPos chunkPos,
                            BlockPos pivot) {
        try {
            if (planFuture == null) return;

            DungeonPlan plan = null;
            if (!planFuture.isDone()) {
                // Give the planner a tiny window to finish (prevents “just missed it” no-op)
                try {
                    plan = planFuture.get(150, java.util.concurrent.TimeUnit.MILLISECONDS);
                } catch (java.util.concurrent.TimeoutException te) {
                    // Not ready yet, skip this chunk; future chunks will try again.
                    return;
                }
            } else {
                plan = planFuture.getNow(null);
            }
            if (plan == null) return;

            ensureInitializedFromPlan(plan);

            // Place only what intersects this chunk
            for (TemplatePiece room : roomPieces) {
                BoundingBox bb = room.getBoundingBox();
                if (bb != null && bb.intersects(chunkBox)) {
                    room.postProcess(gen, structureManager, chunkGenerator, randomSource, chunkBox, chunkPos, pivot);
                }
            }
            if (corridorsPiece != null) {
                BoundingBox bb = corridorsPiece.getBoundingBox();
                if (bb != null && bb.intersects(chunkBox)) {
                    corridorsPiece.postProcess(gen, structureManager, chunkGenerator, randomSource, chunkBox, chunkPos, pivot);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("DeferredDungeonPiece placement error", ex);
        }
    }

    private void ensureInitializedFromPlan(DungeonPlan plan) {
        if (initializedFromPlan) return;

        this.roomPieces = new ArrayList<>(plan.rooms.size());
        for (DungeonPlan.PlannedRoom pr : plan.rooms) {
            this.roomPieces.add(new TemplatePiece(pr.def, pr.minCorner, pr.rotation));
        }
        this.corridorsPiece = new MaskCarvePiece(plan.corridors);

        this.initializedFromPlan = true;
    }
}