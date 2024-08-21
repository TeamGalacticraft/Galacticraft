package dev.galacticraft.mod.machine;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.*;

import static dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity.spawnParticlesAtCenter;

public class SealerManager {
    //instance of sealer manager
    public static final SealerManager INSTANCE = new SealerManager();

    private SealerManager() {
    }

    private static HashMap<BlockPos, Set<OxygenSealerBlockEntity>> WALLS = new HashMap<>();

    private static Set<OxygenSealerBlockEntity> sealerTracker = new HashSet<>();
    private final List<BlockPos> changedBlocks = new ArrayList<>();
    private List<DimensionType> changedBlocksDimensions = new ArrayList<>();
    private Queue<OxygenSealerBlockEntity> queuedSealers = new LinkedList<>();

    public static void checkSealerQueue(ServerLevel world) {
        OxygenSealerBlockEntity[] sealers = INSTANCE.queuedSealers.toArray(new OxygenSealerBlockEntity[0]);
        for (int i = 0; i < sealers.length; i++) {
            OxygenSealerBlockEntity sealer = sealers[i];
            if (sealer != null && Objects.requireNonNull(sealer.getLevel()).dimension().equals(world.dimension())) {
                calculateRegion(sealer, world);
                INSTANCE.queuedSealers.remove(sealer);
            }
        }
    }

    public static void recalculateSealedRegion(OxygenSealerBlockEntity sealer, ServerLevel world) {
        System.out.println("Recalculating area");
        Set<BlockPos> blocks = sealer.getRegion().getPositions();
        Set<BlockPos> toRemove = new HashSet<>();
        blocks.forEach(pos -> {
            if (Block.isShapeFullBlock(world.getBlockState(pos).getCollisionShape(world, pos))) {
                toRemove.add(pos);
            }
        });
        toRemove.forEach(blocks::remove);
        sealer.updateRegion(blocks);
    }

    public void onBlockChange(BlockPos pos, BlockState newState, DimensionType dimension) {
        //add all changed blocks to a list for rechecking
        INSTANCE.changedBlocksDimensions.add(dimension);
        INSTANCE.changedBlocks.add(pos);
    }

    public static void processBlockChanges(ServerLevel world) {
        BlockPos[] changedBlocks = INSTANCE.changedBlocks.toArray(BlockPos[]::new);
        DimensionType[] changedBlocksDimensions = INSTANCE.changedBlocksDimensions.toArray(DimensionType[]::new);
        for (int i = 0; i < changedBlocks.length; i++) {
            BlockPos block = changedBlocks[i];
            if (block != null && changedBlocksDimensions[i].equals(world.dimensionType())) {
                OxygenSealerBlockEntity sealer = findSealerManagingRegion(block);
                if (sealer != null)
                {
                    sealer.checkAndUpdateSealing(block, world);
                    INSTANCE.changedBlocks.remove(block);
                    INSTANCE.changedBlocksDimensions.remove(changedBlocksDimensions[i]);
                }
            }
        }
    }

    private static OxygenSealerBlockEntity findSealerManagingRegion(BlockPos pos)
    {
        for (OxygenSealerBlockEntity sealer : sealerTracker) {
            if (sealer.getRegion().contains(pos)) {
                return sealer;
            }
        }
        return null;
    }

    public void addSealer(OxygenSealerBlockEntity sealer, ServerLevel world)
    {
        System.out.println("Adding sealer at " + sealer.getBlockPos());

        INSTANCE.queuedSealers.add(sealer);
    }

    public static void calculateQueuedRegions(MinecraftServer minecraftServer, ServerLevel world) {
        checkSealerQueue(world);
    }


    private static void calculateRegion(OxygenSealerBlockEntity sealer, ServerLevel world) {
        System.out.println("Calculating sealer region");
        Queue<BlockPos> regionQueue = new LinkedList<>();
        Queue<BlockPos> visitedQueue = new LinkedList<>();
        if (!Block.isShapeFullBlock(world.getBlockState(sealer.getBlockPos().above(1)).getCollisionShape(world, sealer.getBlockPos().above(1)))) {
            spawnParticlesAtCenter(world, sealer.getBlockPos().above(1), ParticleTypes.END_ROD);
            regionQueue.add(sealer.getBlockPos().above(1));
        }

        while (!regionQueue.isEmpty() && visitedQueue.size() < OxygenSealerBlockEntity.SEALING_POWER) {
            BlockPos currentPos = regionQueue.poll();

            for (BlockPos neighbour : neighbours(currentPos))
            {
                if (!visitedQueue.contains(neighbour) && !regionQueue.contains(neighbour))
                {
                    if (!Block.isShapeFullBlock(world.getBlockState(neighbour).getCollisionShape(world, neighbour)))
                    {
                        spawnParticlesAtCenter(world, neighbour, ParticleTypes.END_ROD);
                        regionQueue.add(neighbour);
                    }
                }
            }
            visitedQueue.add(currentPos);
        }
        sealer.buildRegion(visitedQueue, regionQueue);
        sealerTracker.add(sealer);
    }

    private static void spawnParticle(ServerLevel world, SimpleParticleType particle, BlockPos pos) {
        world.addParticle(particle, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
    }

    private static BlockPos[] neighbours(BlockPos currentPos) {
        return new BlockPos[]{currentPos.offset(Direction.UP.getNormal()),currentPos.offset(Direction.DOWN.getNormal()),currentPos.offset(Direction.NORTH.getNormal()),currentPos.offset(Direction.EAST.getNormal()),currentPos.offset(Direction.SOUTH.getNormal()),currentPos.offset(Direction.WEST.getNormal())};
    }

    public void markVisited(Queue<BlockPos> region, Queue<BlockPos> visited, BlockPos pos)
    {
        region.remove(pos);
        visited.add(pos);
    }

    public void removeSealer(OxygenSealerBlockEntity sealer, ServerLevel world)
    {
        System.out.println("Removing sealer at " + sealer.getBlockPos());
        sealerTracker.remove(sealer);
    }
}
