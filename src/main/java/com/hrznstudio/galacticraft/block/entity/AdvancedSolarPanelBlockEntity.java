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

import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.screen.AdvancedSolarPanelScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
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

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AdvancedSolarPanelBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final int CHARGE_SLOT = 0;
    
    public AdvancedSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.ADVANCED_SOLAR_PANEL_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        return slot == CHARGE_SLOT ? EnergyUtils.IS_INSERTABLE : ConstantItemFilter.NOTHING;
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptDrainPowerToStack(CHARGE_SLOT);
    }

    @NotNull
    @Override
    public MachineStatus updateStatus() {
        if (getCapacitor().getEnergy() >= getCapacitor().getMaxCapacity()) {
            return Status.FULL;
        }

        if (!this.world.isDay()) {
            return Status.NIGHT;
        }

        byte panels = 0;
        for (int z = -1; z < 2; z++) {
            for (int y = -1; y < 2; y++) {
                if (this.world.isSkyVisible(pos.add(z, 2, y))) {
                    panels++;
                }
            }
        }

        if (panels == 0) {
            return Status.BLOCKED;
        } else if (panels == 9) {
            return Status.COLLECTING;
        } else {
            return Status.PARTIALLY_BLOCKED;
        }
    }

    @Override
    public void tickWork() {
    }

    @Override
    public int getEnergyGenerated() {
        if (this.getStatus().getType().isActive()) {
            double time = world.getTimeOfDay() % 24000;
            double multiplier = 0;
            if (time > 6000) time = 6000D - (time - 6000D);
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 2; y++) {
                    if (this.world.isSkyVisible(pos.add(z, 2, y))) {
                        multiplier++;
                    }
                }
            }
            multiplier /= 9;
            if (world.isRaining() || world.isThundering()) multiplier *= 0.5D;

            return (int) Math.min(this.getBaseEnergyGenerated(), (this.getBaseEnergyGenerated() * ((time) / 6000D) * multiplier) * 4);
        } else {
            return 0;
        }
    }

    @Override
    public int getBaseEnergyGenerated() {
        return Galacticraft.configManager.get().solarPanelEnergyProductionRate();
    }

    @Override
    public List<BlockFace> getLockedFaces() {
        return Collections.singletonList(BlockFace.TOP);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new AdvancedSolarPanelScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Solar panel is active and is generating energy.
         */
        COLLECTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.collecting"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Solar Panel can generate energy, but the buffer is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Solar Panel is generating energy, but less efficiently as it is blocked or raining.
         */
        PARTIALLY_BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.partially_blocked"), Formatting.DARK_AQUA, StatusType.PARTIALLY_WORKING),

        /**
         * Solar Panel is generating very little energy as it is night.
         */
        NIGHT(new TranslatableText("ui.galacticraft-rewoven.machinestatus.night"), Formatting.BLUE, StatusType.PARTIALLY_WORKING),

        /**
         * The sun is not visible.
         */
        BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.blocked"), Formatting.DARK_GRAY, StatusType.MISSING_RESOURCE);

        private final Text text;
        private final MachineStatus.StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
            this.type = type;
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