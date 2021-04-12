/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.OxygenCollectorScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlockEntity extends ConfigurableMachineBlockEntity implements TickableBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int CHARGE_SLOT = 0;

    public int collectionAmount = 0;

    public OxygenCollectorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COLLECTOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        return EnergyUtils.IS_EXTRACTABLE;
    }

    private int collectOxygen() {
        Optional<CelestialBodyType> celestialBodyType = CelestialBodyType.getByDimType(level.dimension());

        if (celestialBodyType.isPresent()) {
            if (!celestialBodyType.get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
                int minX = this.worldPosition.getX() - 5;
                int minY = this.worldPosition.getY() - 5;
                int minZ = this.worldPosition.getZ() - 5;
                int maxX = this.worldPosition.getX() + 5;
                int maxY = this.worldPosition.getY() + 5;
                int maxZ = this.worldPosition.getZ() + 5;

                float leafBlocks = 0;

                for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.isAir()) {
                        continue;
                    }
                    if (blockState.getBlock() instanceof LeavesBlock && !blockState.getValue(LeavesBlock.PERSISTENT)) {
                        leafBlocks++;
                    } else if (blockState.getBlock() instanceof CropBlock) {
                        leafBlocks += 0.75F;
                    }
                }

                if (leafBlocks < 2) return 0;

                double oxyCount = 20 * (leafBlocks / 14.0F);
                return (int) Math.ceil(oxyCount) / 20; //every tick
            }
        }
        return 183 / 20;
    }

    private boolean canCollectOxygen() {
        Optional<CelestialBodyType> celestialBodyType = CelestialBodyType.getByDimType(level.dimension());

        if (celestialBodyType.isPresent()) {
            if (!celestialBodyType.get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
                int minX = this.worldPosition.getX() - 5;
                int minY = this.worldPosition.getY() - 5;
                int minZ = this.worldPosition.getZ() - 5;
                int maxX = this.worldPosition.getX() + 5;
                int maxY = this.worldPosition.getY() + 5;
                int maxZ = this.worldPosition.getZ() + 5;

                float leafBlocks = 0;

                for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.isAir()) {
                        continue;
                    }
                    if (blockState.getBlock() instanceof LeavesBlock && !blockState.getValue(LeavesBlock.PERSISTENT)) {
                        if (++leafBlocks >= 2) break;
                    } else if (blockState.getBlock() instanceof CropBlock) {
                        if ((leafBlocks += 0.75) >= 2) break;
                    }
                }
                return leafBlocks >= 2;
            }
        }
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
        this.trySpreadFluids(0);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.isTankFull(0)) return Status.FULL;
        if (!canCollectOxygen()) return Status.NOT_ENOUGH_LEAVES;
        return Status.COLLECTING;
    }

    @Override
    public void tickWork() {
        this.collectionAmount = 0;
        if (this.getStatus().getType().isActive()) {
            this.collectionAmount = collectOxygen();
            this.getFluidTank().insertFluid(0, FluidKeys.get(GalacticraftFluids.LIQUID_OXYGEN).withAmount(FluidAmount.of(collectionAmount, 100)), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().oxygenCollectorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return true;
    }

    @Override
    public boolean canPipeExtractFluid(int tank) {
        return true;
    }

    @Override
    public FluidFilter getFilterForTank(int tank) {
        return Constants.Misc.LOX_ONLY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenCollectorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        COLLECTING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.collecting"), ChatFormatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_LEAVES(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_leaves"), ChatFormatting.RED, StatusType.MISSING_RESOURCE),
        FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL);

        private final Component text;
        private final StatusType type;

        Status(TranslatableComponent text, ChatFormatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Component getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}