/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineFluidStorage;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.display.TankDisplay;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.OxygenCollectorScreenHandler;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenCollectorBlockEntity extends MachineBlockEntity {
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_TANK = 0;

    public int collectionAmount = 0;
    private boolean oxygenWorld = false;

    public OxygenCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_COLLECTOR, pos, state);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = CelestialBody.getByDimension(world).orElse(null);
        this.oxygenWorld = body == null || body.atmosphere().breathable();
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create().addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62)).build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GalacticraftSlotTypes.OXYGEN_OUTPUT, MAX_OXYGEN, new TankDisplay(31, 8, 48), true)
                .build();
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
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
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.getProfiler().push("transfer");
        this.trySpreadFluids(world);

        if (this.fluidStorage().isFull(OXYGEN_TANK)) return GalacticraftMachineStatus.OXYGEN_TANK_FULL;
        world.getProfiler().popPush("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate()) {
                world.getProfiler().push("collect");
                this.collectionAmount = collectOxygen(world, pos);
                world.getProfiler().pop();
                if (this.collectionAmount > 0) {
                    this.fluidStorage().insert(OXYGEN_TANK, FluidVariant.of(Gases.OXYGEN), FluidUtil.bucketsToDroplets(this.collectionAmount), transaction);
                    transaction.commit();
                    return GalacticraftMachineStatus.COLLECTING;
                } else {
                    return GalacticraftMachineStatus.NOT_ENOUGH_OXYGEN;
                }
            } else {
                this.collectionAmount = 0;
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            world.getProfiler().pop();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenCollectorScreenHandler(syncId, player, this);
        return null;
    }
}