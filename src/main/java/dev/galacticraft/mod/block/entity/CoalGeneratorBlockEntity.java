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

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.io.ResourceFlow;
import dev.galacticraft.api.machine.storage.io.ResourceType;
import dev.galacticraft.api.machine.storage.io.SlotType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.CoalGeneratorScreenHandler;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorBlockEntity extends MachineBlockEntity {
    @VisibleForTesting
    public static final Object2IntMap<Item> FUEL_MAP = Util.make(new Object2IntArrayMap<>(3), (map) -> {
        map.defaultReturnValue(0);
        map.put(Items.COAL_BLOCK, 320 * 10);
        map.put(Items.COAL, 320);
        map.put(Items.CHARCOAL, 310);
    });

    public static final int CHARGE_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    private static final SlotType<Item, ItemVariant> COAL_INPUT = SlotType.create(new Identifier(Constant.MOD_ID, "coal_input"), TextColor.fromRgb(0x000000), new TranslatableText("slot_type.galacticraft.coal_input"), v -> FUEL_MAP.containsKey(v.getItem()), ResourceFlow.INPUT, ResourceType.ITEM);

    private int fuelLength = 0;
    private int inventoryModCount = -1;
    private int fuelTime = 0;
    private double heat = 0.0d;

    /*
     * Energy stats:
     * T1 machine: uses 30gj/t
     * Coal Generator: generates 120gj/t (max heat)
     */

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.COAL_GENERATOR, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62))
                .addSlot(COAL_INPUT, new ItemSlotDisplay(71, 53))
                .build();
    }

    @Override
    protected void tickConstant() {
        super.tickConstant();
        if (this.fuelLength == 0) {
            if (this.heat > 0) {
                this.setHeat(Math.max(0, this.heat - 0.02d));
            }
        }
    }

    @Override
    public @NotNull MachineStatus tick() {
        this.world.getProfiler().push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            this.energyStorage().insert((long) (Galacticraft.CONFIG_MANAGER.get().coalGeneratorEnergyProductionRate() * this.heat), transaction);
        }
        this.attemptDrainPowerToStack(CHARGE_SLOT);
        this.trySpreadEnergy();
        this.world.getProfiler().swap("fuel_reset");
        if (this.fuelLength == 0) {
            this.consumeFuel();
            if (this.fuelLength == 0) {
                if (this.heat > 0) {
                    return GalacticraftMachineStatus.COOLING_DOWN;
                } else {
                    return GalacticraftMachineStatus.NO_FUEL;
                }
            }
        }
        this.world.getProfiler().swap("fuel_tick");
        if (this.fuelTime++ >= this.fuelLength) {
            this.fuelLength = 0;
            this.consumeFuel();
        }
        this.setHeat(Math.min(1, this.heat + 0.004));
        this.world.getProfiler().pop();

        if (this.energyStorage().isFull()) {
            return MachineStatuses.CAPACITOR_FULL;
        } else if (this.heat < 1.0) {
            return GalacticraftMachineStatus.WARMING_UP;
        } else {
            return GalacticraftMachineStatus.GENERATING;
        }
    }

    private void consumeFuel() {
        this.fuelTime = 0;
        this.fuelLength = 0;
        if (this.itemStorage().getModCount() != this.inventoryModCount) {
            this.inventoryModCount = this.itemStorage().getModCount();
            SingleSlotStorage<ItemVariant> slot = this.itemStorage().getSlot(FUEL_SLOT);
            try (Transaction transaction = Transaction.openOuter()) {
                this.fuelLength = FUEL_MAP.getInt(slot.getResource().toStack((int) slot.extract(slot.getResource(), 1, transaction)));
                transaction.commit();
            }
        }
    }

    public int getFuelLength() {
        return this.fuelLength;
    }

    public void setFuelLength(int fuelLength) {
        this.fuelLength = fuelLength;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new CoalGeneratorScreenHandler(syncId, player, this);
        return null;
    }

    public double getHeat() {
        return this.heat;
    }

    @VisibleForTesting
    public void setHeat(double heat) {
        this.heat = heat;
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public void setFuelTime(int value) {
        this.fuelTime = value;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.fuelLength = nbt.getInt(Constant.Nbt.FUEL_LENGTH);
        this.fuelTime = nbt.getInt(Constant.Nbt.FUEL_TIME);
        this.heat = nbt.getDouble(Constant.Nbt.HEAT);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putDouble(Constant.Nbt.HEAT, this.heat);
    }
}