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

import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.screen.EnergyStorageModuleScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EnergyStorageModuleBlockEntity extends ConfigurableMachineBlockEntity implements TickableBlockEntity {
    public static final int CHARGE_BATTERY_SLOT = 0;
    public static final int DRAIN_BATTERY_SLOT = 1;

    public EnergyStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.ENERGY_STORAGE_MODULE_TYPE);
        setStatus(MachineStatus.NULL);
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public int getEnergyCapacity() {
        return Galacticraft.configManager.get().energyStorageModuleStorageSize();
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.POWER_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return MachineStatus.NULL;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        return slot == CHARGE_BATTERY_SLOT ? EnergyUtils.IS_INSERTABLE : EnergyUtils.IS_EXTRACTABLE;
    }

    @Override
    protected int getBatteryTransferRate() {
        return 100;
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        return MachineStatus.NULL;
    }

    @Override
    public void tickWork() {
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(DRAIN_BATTERY_SLOT);
        this.attemptDrainPowerToStack(CHARGE_BATTERY_SLOT);
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new EnergyStorageModuleScreenHandler(syncId, player, this);
        return null;
    }
}
