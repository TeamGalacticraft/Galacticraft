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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.AbstractSolarPanelBlockEntity;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.menu.GCMenuTypes;
import dev.galacticraft.mod.menu.SolarPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AdvancedSolarPanelBlockEntity extends AbstractSolarPanelBlockEntity {
    private static final StorageSpec STORAGE_SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    0,
                    Galacticraft.CONFIG.solarPanelEnergyProductionRate() * 2
            )
    );

    public AdvancedSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ADVANCED_SOLAR_PANEL, pos, state, STORAGE_SPEC);
    }

    @Override
    public boolean followsSun() {
        return true;
    }

    @Override
    public boolean nightCollection() {
        return false;
    }

    @Override
    protected long calculateEnergyProduction(long time, double multiplier) {
        double cos = Math.cos(this.level.getSunAngle(1.0f));
        if (cos <= 0) return 0;
        if (cos <= 0.26761643317033024) {
            return (long) (Galacticraft.CONFIG.solarPanelEnergyProductionRate() * (cos / 0.26761643317033024) * multiplier);
        }
        return (long) (Galacticraft.CONFIG.solarPanelEnergyProductionRate() * multiplier);
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inventory, Player player) {
        return new SolarPanelMenu<>(GCMenuTypes.ADVANCED_SOLAR_PANEL, syncId, (ServerPlayer) player, this);
    }
}