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
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.attribute.oxygen.EmptyOxygenTank;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.screen.OxygenCompressorScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCompressorBlockEntity extends MachineBlockEntity implements Tickable {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int CHARGE_SLOT = 0;

    public OxygenCompressorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COMPRESSOR_TYPE);
        this.getInventory().addSlot(SlotType.CHARGE, EnergyUtils.IS_EXTRACTABLE, 8, 62);
        this.getInventory().addSlot(SlotType.OXYGEN_TANK, OxygenTankUtils::isOxygenTank, 80, 27);
        this.getFluidTank().addSlot(SlotType.OXYGEN_IN, Constants.Filter.LOX_ONLY); //80, 27

    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.getFluidTank().getInvFluid(0).isEmpty()) return Status.NOT_ENOUGH_OXYGEN;
        OxygenTank tank = OxygenTankUtils.getOxygenTank(this.getInventory().getSlot(1));
        if (tank == EmptyOxygenTank.NULL) return Status.NOT_ENOUGH_ITEMS;
        if (tank.getCapacity() >= tank.getCapacity()) return Status.CONTAINER_FULL;
        return Status.COMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.getInventory().getSlot(1));
            this.getFluidTank().insertFluid(0, OxygenTankUtils.insertLiquidOxygen(tank, this.getFluidTank().attemptExtraction(Constants.Filter.LOX_ONLY, FluidAmount.of1620(1620 / 60), Simulation.ACTION)), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenCompressorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft-rewoven.machinestatus.missing_tank"), Formatting.RED, StatusType.MISSING_ITEMS),
        CONTAINER_FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        COMPRESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.compressing"), Formatting.GREEN, StatusType.WORKING);

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