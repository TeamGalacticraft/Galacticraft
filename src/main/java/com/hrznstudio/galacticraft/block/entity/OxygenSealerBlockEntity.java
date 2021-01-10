/*
 * Copyright (c) 2020 HRZN LTD
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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.WorldOxygenAccessor;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenSealerBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 1000).multiply(Fraction.ofWhole(10000));
    public static final int BATTERY_SLOT = 0;
    private final Set<BlockPos> set = new HashSet<>();
    public static final byte SEAL_CHECK_TIME = 5 * 20;
    private byte sealCheckTime;

    public OxygenSealerBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_SEALER_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public Fraction getFluidTankMaxCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
        sealCheckTime = SEAL_CHECK_TIME;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_INPUT);
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == BATTERY_SLOT) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        }
        return s -> false;
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (!set.isEmpty()) {
                for (BlockPos pos1 : set) {
                    ((WorldOxygenAccessor) world).setBreathable(pos1, false);
                }
                set.clear();
            }
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }

        attemptChargeFromStack(BATTERY_SLOT);
        if (this.getCapacitor().getCurrentEnergy() < getEnergyUsagePerTick()) {
            setStatus(Status.NOT_ENOUGH_ENERGY);
            return;
        } else if (this.getFluidTank().isEmpty()) {
            setStatus(Status.NOT_ENOUGH_OXYGEN);
            return;
        } else {
            if (!set.isEmpty()) {
                for (BlockPos pos1 : set) {
                    ((WorldOxygenAccessor) world).setBreathable(pos1, false);
                }
                set.clear();
            }
        }

        if (sealCheckTime > 0) {
            sealCheckTime--;
        }
        if (sealCheckTime == 0) {
            sealCheckTime = SEAL_CHECK_TIME;
            Direction[] directions = Direction.values();
            BlockPos pos = this.getPos();
            Queue<BlockPos> queue = new LinkedList<>();
            if (set.isEmpty() && ((WorldOxygenAccessor) world).isBreathable(pos.up())) {
                setStatus(Status.ALREADY_SEALED);
                return;
            }
            set.clear();
            {
                BlockPos pos1 = pos.offset(Direction.UP);
                BlockState state = world.getBlockState(pos1);
                if (state.isAir() || !Block.isFaceFullSquare(state.getCollisionShape(world, pos1), Direction.DOWN)) {
                    queue.add(pos1);
                    set.add(pos1);
                }
            }
            while (!queue.isEmpty()) {
                pos = queue.poll();

                for (Direction direction : directions) {
                    if (Block.isFaceFullSquare(world.getBlockState(pos).getCollisionShape(world, pos), direction)) continue;
                    BlockPos pos1 = pos.offset(direction);
                    BlockState state = world.getBlockState(pos1);
                    if (state.isAir() || !Block.isFaceFullSquare(state.getCollisionShape(world, pos1), direction.getOpposite())) {
                        if (!set.contains(pos1)) {
                            queue.add(pos1);
                            set.add(pos1);
                        }
                    }
                }
                if (set.size() >= 1000) {
                    setStatus(Status.AREA_TOO_LARGE);
                    return;
                }
            }
            for (BlockPos pos1 : set) {
                ((WorldOxygenAccessor) world).setBreathable(pos1, true);
            }
            setStatus(Status.SEALED);
        }
        if (getStatus() == Status.SEALED) {
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
            this.getFluidTank().takeFluid(0, Fraction.of(set.size(), 2000), ActionType.PERFORM);
        }
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCompressorEnergyConsumptionRate();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        for (BlockPos pos : set) {
            ((WorldOxygenAccessor) world).setBreathable(pos, false);
        }
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return true;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return true;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return volume.getFluid().isIn(GalacticraftTags.OXYGEN);
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        AREA_TOO_LARGE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.area_too_large"), Formatting.GOLD, StatusType.OTHER),
        ALREADY_SEALED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.already_sealed"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        SEALED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.sealed"), Formatting.GREEN, StatusType.WORKING);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
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