/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.WorldOxygenAccessor;
import dev.galacticraft.mod.api.block.SideOption;
import dev.galacticraft.mod.api.block.entity.ConfigurableMachineBlockEntity;
import dev.galacticraft.mod.entity.GalacticraftBlockEntities;
import dev.galacticraft.mod.screen.OxygenSealerScreenHandler;
import dev.galacticraft.mod.util.EnergyUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenSealerBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
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
    public FluidAmount getFluidTankCapacity() {
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
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
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
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == BATTERY_SLOT) {
            return EnergyUtils.IS_EXTRACTABLE;
        }
        return ConstantItemFilter.NOTHING;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(BATTERY_SLOT);
        if (!world.isClient && this.getStatus().getType().isActive()) this.getFluidTank().extractFluid(0, Constants.Misc.LOX_ONLY, null, FluidAmount.of1620(set.size()), Simulation.ACTION);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.getFluidTank().getInvFluid(0).isEmpty()) return Status.NOT_ENOUGH_OXYGEN;
        return Status.SEALED;
    }

    @Override
    public void tickWork() {
        if (sealCheckTime > 0) {
            sealCheckTime--;
        }
        if (this.getStatus().getType().isActive()) {
            if (sealCheckTime == 0) {
                sealCheckTime = SEAL_CHECK_TIME;
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
                for (BlockPos pos1 : set) {
                    ((WorldOxygenAccessor) world).setBreathable(pos1, true);
                }
                setStatus(Status.SEALED);
            }
        }
    }

    @Override
    public void idleEnergyDecrement(boolean off) {
        super.idleEnergyDecrement(off);
        if (!set.isEmpty()) {
            for (BlockPos pos1 : set) {
                ((WorldOxygenAccessor) world).setBreathable(pos1, false);
            }
            set.clear();
        }
    }

    @Override
    protected int getBaseEnergyConsumption() {
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
    public boolean canHopperExtract(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return true;
    }

    @Override
    public boolean canPipeInsertFluid(int tank) {
        return true;
    }

    @Override
    public FluidFilter getFilterForTank(int tank) {
        return Constants.Misc.LOX_ONLY;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenSealerScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machinestatus.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        AREA_TOO_LARGE(new TranslatableText("ui.galacticraft.machinestatus.area_too_large"), Formatting.GOLD, StatusType.OTHER),
        ALREADY_SEALED(new TranslatableText("ui.galacticraft.machinestatus.already_sealed"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        SEALED(new TranslatableText("ui.galacticraft.machinestatus.sealed"), Formatting.GREEN, StatusType.WORKING);

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