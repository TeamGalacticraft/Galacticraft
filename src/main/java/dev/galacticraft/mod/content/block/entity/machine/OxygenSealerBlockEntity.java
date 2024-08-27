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
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.ServerLevelAccessor;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int SEAL_CHECK_TIME = 20;

    private int sealCheckTime;
    private boolean oxygenWorld = false;
    private boolean outputBlocked;


    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_SEALER, pos, state);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        this.oxygenWorld = CelestialBody.getByDimension(world).map(body -> body.atmosphere().breathable()).orElse(true);
        this.outputBlocked = false;
        if (!world.isClientSide) ((ServerLevelAccessor) world).addSealer(this, (ServerLevel) world);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
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
                if (this.sealCheckTime == 0)
                {
                    this.sealCheckTime = 20;
                    if (this.outputBlocked)
                    {
                        return GCMachineStatuses.BLOCKED;
                    }
                    RenderOxygenSealedArea(SealerManager.INSTANCE.getInsideSealerGroupings(pos.above(), level.dimensionType()).getCalculatedArea(), level, ParticleTypes.FLAME);
                    RenderOxygenSealedArea(SealerManager.INSTANCE.getInsideSealerGroupings(pos.above(), level.dimensionType()).getUncalculatedArea(), level, ParticleTypes.DRAGON_BREATH);
                    //checks if the block above the sealer is sealed or not
                    if (SealerManager.INSTANCE.getInsideSealerGroupings(pos.above(), level.dimensionType()).getBreathable())
                    {
                        return GCMachineStatuses.SEALED;
                    }
                    return GCMachineStatuses.AREA_TOO_LARGE;
                }
                return this.getState().getStatus();
            } else {
                return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
            }
        } else {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
    }

    private void RenderOxygenSealedArea(Set<BlockPos> calculatedArea, @NotNull ServerLevel level, SimpleParticleType type) {
        for (BlockPos pos : calculatedArea)
        {
            spawnParticlesAtCenter(level, pos, type);
        }
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

    public static void spawnParticlesAtCenter(ServerLevel level, BlockPos pos, SimpleParticleType type) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            // Spawn the particle at the center
            level.sendParticles(type, centerX, centerY, centerZ, 1, 0, 0, 0, 0);

    }

    public void setBlocked(boolean b) {
        this.outputBlocked = b;
    }

    public boolean isBlocked() {
        return outputBlocked;
    }

    public int getSealingPower() {
        return 1024;
    }

    public int getInsideArea() {
        return SealerManager.INSTANCE.getInsideSealerGroupings(this.getBlockPos().above(), this.level.dimensionType()).getTotalInsideBlocks();
    }

    public int getOutsideArea() {
        return SealerManager.INSTANCE.getInsideSealerGroupings(this.getBlockPos().above(), this.level.dimensionType()).getTotalOutsideBlocks();
    }
}