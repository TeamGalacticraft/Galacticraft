/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerLevelAccessor;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.machine.Region;
import dev.galacticraft.mod.machine.SealerManager;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int SEAL_CHECK_TIME = 20;

    private final Set<BlockPos> breathablePositions = new HashSet<>();
    private final Set<BlockPos> watching = new HashSet<>();
    private int sealCheckTime;
    private boolean updateQueued = true;
    private boolean disabled = false;
    private boolean oxygenWorld = false;
    private boolean sealed = false;
    private boolean oxygenUnloaded = true;
    private final Region region;
    private int remainingCapacity;

    public static final int SEALING_POWER = 1024;

    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_SEALER, pos, state);
        this.region = Region.EMPTY;
        this.remainingCapacity = SEALING_POWER;
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        this.oxygenWorld = CelestialBody.getByDimension(world).map(body -> body.atmosphere().breathable()).orElse(true);
        if (!world.isClientSide) ((ServerLevelAccessor) world).addSealer(this, (ServerLevel) world);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.oxygenUnloaded = false;
        profiler.push("extract_resources");
        this.chargeFromStack(CHARGE_SLOT);
        this.takeFluidFromStack(OXYGEN_INPUT_SLOT, OXYGEN_TANK, Gases.OXYGEN);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        assert level != null;
        if (this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate())) {
            if (!this.fluidStorage().getSlot(OXYGEN_TANK).isEmpty()) {
                if (this.sealCheckTime > 0) this.sealCheckTime--;
                for (BlockPos sealedBlockPos : region.getPositions()) {
                    spawnParticlesAtCenter(level, sealedBlockPos, ParticleTypes.FLAME);
                }
                if (sealed)
                {
                    return GCMachineStatuses.SEALED;
                }
                return GCMachineStatuses.AREA_TOO_LARGE;
            } else {
                return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
            }
        } else {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
    }

    private void tryClearSeal(@NotNull ServerLevel world) {
        if (this.sealed) {
            for (BlockPos pos1 : this.breathablePositions) {
                world.setBreathable(pos1, false);
            }
            this.breathablePositions.clear();
            this.watching.clear();

            this.sealed = false;
        }
        this.updateQueued = true;
        this.sealCheckTime = 0;
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickDisabled(world, pos, state, profiler);
    }

    @Override
    public void setRemoved() {
        if (!this.level.isClientSide) {
            ((ServerLevelAccessor) this.level).removeSealer(this, (ServerLevel) this.level);
        }
        if (!this.oxygenUnloaded) {
            this.oxygenUnloaded = true;
            for (BlockPos pos : this.breathablePositions) {
                this.level.setBreathable(pos, false);
            }
        }
        this.breathablePositions.clear();
        this.watching.clear();

        super.setRemoved();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new MachineMenu<>(
                    syncId,
                    (ServerPlayer) player,
                    this
            );
        }
        return null;
    }

    public void enqueueUpdate(BlockPos pos, VoxelShape voxelShape) {
        if ((this.watching.contains(pos) && !Block.isShapeFullBlock(voxelShape)) || (this.breathablePositions.contains(pos) && !voxelShape.isEmpty())) {
            this.updateQueued = true;
        }
    }



    public void checkAndUpdateSealing(BlockPos changedPos, ServerLevel world)
    {
        if (Block.isShapeFullBlock(world.getBlockState(changedPos).getCollisionShape(world, changedPos))) {
            System.out.println("solid block");
            SealerManager.recalculateSealedRegion(this, world);
        }else{
            System.out.println("unsolid block");
        }
        //recalculate region
    }

    private void sealRegion() {
        System.out.println("SEALING REGION");
        sealed = true;
    }

    private void unsealRegion() {
        System.out.println("UNSEALING REGION");
        sealed = false;
    }

    private boolean canSealRegion() {
        //if region still has capacity
        return remainingCapacity > 0;
    }

    private void requestAdditionalSealingPower() {
        // Request sealing power from adjacent sealers or update sealing power
        // Implement logic to communicate with neighboring sealers
    }

    public static void spawnParticlesAtCenter(ServerLevel level, BlockPos pos, SimpleParticleType type) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            // Spawn the particle at the center
            level.sendParticles(type, centerX, centerY, centerZ, 1, 0, 0, 0, 0);

    }

    public void buildRegion(Queue<BlockPos> visitedQueue, Queue<BlockPos> regionQueue) {
        this.region.setPositions(new HashSet<>(visitedQueue));
        this.remainingCapacity = SEALING_POWER - this.region.getRegionSize();
        if (regionQueue.isEmpty())
        {
            this.sealRegion();
        }
    }

    public int getSealingPower() {
        return SEALING_POWER - this.region.getRegionSize();
    }

    public Region getRegion() {
        return this.region;
    }

    public void updateRegion(Set<BlockPos> blocks) {
        this.region.setPositions(blocks);
    }
}