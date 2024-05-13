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
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.OxygenCollectorMenu;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OxygenCollectorBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);

    public int collectionAmount = 0;
    private boolean oxygenWorld = false;

    public OxygenCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_COLLECTOR, pos, state);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = CelestialBody.getByDimension(world).orElse(null);
        this.oxygenWorld = body == null || body.atmosphere().breathable();
    }

    private int collectOxygen(@NotNull ServerLevel world, @NotNull BlockPos pos) {
        if (!this.oxygenWorld) {
            int minX = pos.getX() - 5;
            int minY = pos.getY() - 5;
            int minZ = pos.getZ() - 5;
            int maxX = pos.getX() + 5;
            int maxY = pos.getY() + 5;
            int maxZ = pos.getZ() + 5;

            float leafBlocks = 0;

            for (BlockPos pos1 : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = world.getBlockState(pos1);
                if (state.isAir()) {
                    continue;
                }
                if (state.getBlock() instanceof LeavesBlock && !state.getValue(LeavesBlock.PERSISTENT)) {
                    leafBlocks++;
                } else if (state.getBlock() instanceof CropBlock) {
                    leafBlocks += 0.75F;
                }
            }

            if (leafBlocks < 2) return 0;

            double oxyCount = 20 * (leafBlocks / 14.0F);
            return (int) Math.ceil(oxyCount) / 20; //every tick
        }
        return 183 / 20;
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromStack(CHARGE_SLOT);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("transfer");
        this.trySpreadFluids(level, state);

        if (this.fluidStorage().getSlot(OXYGEN_TANK).isFull()) return GCMachineStatuses.OXYGEN_TANK_FULL;
        profiler.popPush("transaction");
        try {
            if (this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate())) {
                profiler.push("collect");
                this.collectionAmount = collectOxygen(level, pos);
                profiler.pop();
                if (this.collectionAmount > 0) {
                    this.energyStorage().extract(Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate());
                    this.fluidStorage().getSlot(OXYGEN_TANK).insert(Gases.OXYGEN, FluidUtil.bucketsToDroplets(this.collectionAmount));
                    return GCMachineStatuses.COLLECTING;
                } else {
                    return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
                }
            } else {
                this.collectionAmount = 0;
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            profiler.pop();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenCollectorMenu(syncId, (ServerPlayer) player, this);
        return null;
    }

    public int getCollectionAmount() {
        return this.collectionAmount;
    }
}