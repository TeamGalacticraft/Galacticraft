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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.attribute.oxygen.EmptyOxygenTank;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.screen.OxygenDecompressorScreenHandler;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenDecompressorBlockEntity extends ConfigurableMachineBlockEntity implements TickableBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int CHARGE_SLOT = 0;
    public static final int TANK_SLOT = 1;

    public OxygenDecompressorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_DECOMPRESSOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 2;
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
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.FLUID_OUTPUT, SideOption.POWER_INPUT);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) {
            return EnergyUtils.IS_EXTRACTABLE;
        } else {
            return OxygenTankUtils::isOxygenTank;
        }
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
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
        OxygenTank tank = OxygenTankUtils.getOxygenTank(this.getInventory().getSlot(TANK_SLOT));
        if (tank == EmptyOxygenTank.NULL) return Status.NOT_ENOUGH_ITEMS;
        if (tank.getAmount() <= 0) return Status.EMPTY_CANISTER;
        if (this.isTankFull(0)) return Status.FULL;
        return Status.DECOMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.getInventory().getSlot(TANK_SLOT));
            OxygenTankUtils.insertLiquidOxygen(tank, this.getFluidTank().insertFluid(0, OxygenTankUtils.extractLiquidOxygen(tank, 1620 / (20 * 5)), Simulation.ACTION));
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().oxygenDecompressorEnergyConsumptionRate();
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
        return key -> GalacticraftTags.OXYGEN.contains(key.getRawFluid());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenDecompressorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_ITEMS(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_items"), ChatFormatting.RED, StatusType.MISSING_FLUIDS),
        EMPTY_CANISTER(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.empty_canister"), ChatFormatting.RED, StatusType.MISSING_FLUIDS),
        NOT_ENOUGH_OXYGEN(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.empty_canister"), ChatFormatting.RED, StatusType.MISSING_ITEMS),
        FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),
        DECOMPRESSING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.decompressing"), ChatFormatting.GREEN, StatusType.WORKING);

        private final Component text;
        private final StatusType type;

        Status(TranslatableComponent text, ChatFormatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static Status get(int index) {
            if (index < 0) return Status.values()[0];
            return Status.values()[index % Status.values().length];
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