/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.machine.FoodCannerBlock;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.galacticraft.mod.Constant.FoodCanner.*;
import static dev.galacticraft.mod.content.item.GCItems.CANNED_FOOD;
import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;

public class FoodCannerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int GRID_START = 2;
    public static final int INPUT_LENGTH = 17;
    public static final int STORAGE_SLOT = 18;
    public static final int OUTPUT_SLOT = 19;

    private int progress = 0;
    private boolean transferringCan = false;
    private boolean transferringFood = false;
    private boolean ejectCan = false;
    private boolean hasCan = false;
    private boolean hadCan = false;
    private boolean[] rowsConsumed = {false, false, false, false};

    private static final StorageSpec SPEC = StorageSpec.of(
            newMachineStorageSpec(),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.foodCannerEnergyConsumptionRate() * 2,
                    0
            )
    );

    private static MachineItemStorage.Spec newMachineStorageSpec() {
        MachineItemStorage.Spec storage = MachineItemStorage.builder();
        storage.add(
                ItemResourceSlot.builder(TransferType.TRANSFER)
                        .pos(8, 67)
                        .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                        .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY))
        );
        storage.add(
                ItemResourceSlot.builder(TransferType.INPUT)
                        .pos(INPUT_X, INPUT_Y)
                        .filter(ResourceFilters.ofResource(EMPTY_CAN))
                        .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.FOOD_CAN))
        );
        for (ItemResourceSlot.Spec slot : createFoodSlots(GRID_X, GRID_Y, 4, 4)) {
            storage.add(slot);
        }
        storage.add(
                ItemResourceSlot.builder(TransferType.INTERNAL)
                        .pos(CURRENT_X, CURRENT_Y)
                        .capacity(1)
                        .filter(ResourceFilters.ofResource(EMPTY_CAN))
        );
        storage.add(
                ItemResourceSlot.builder(TransferType.OUTPUT)
                        .pos(OUTPUT_X, OUTPUT_Y)
        );
        return storage;
    }

    private static List<ItemResourceSlot.Spec> createFoodSlots(int xOffset, int yOffset, int columns, int rows) {
        List<ItemResourceSlot.Spec> slots = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                slots.add(ItemResourceSlot.builder(TransferType.INPUT)
                        .pos(xOffset + (x * 18), yOffset + (y * 18))
                        .filter((item, tag) -> CannedFoodItem.canAddToCan(item))
                );
            }
        }
        return slots;
    }

    public FoodCannerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.FOOD_CANNER, pos, state, SPEC);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("extract_resources");
        this.chargeFromSlot(CHARGE_SLOT);
        profiler.pop();

        this.hasCan = !this.inputSlotEmpty() || !this.storageSlotEmpty() || !this.outputSlotEmpty();
        if (this.hasCan != this.hadCan) {
            this.hadCan = this.hasCan;
            world.setBlock(this.worldPosition, state.setValue(FoodCannerBlock.CAN, this.hasCan), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.noEnergy()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        this.incrementProgress();
        if (this.storageSlotEmpty()) {
            this.ejectCan = false;
            if (this.inputSlotEmpty()) {
                this.reset(0);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            } else if (this.getProgress() < START_ROW_1) {
                this.transferringCan = true;
                return GCMachineStatuses.TRANSFERRING_CAN;
            } else if (this.getProgress() == START_ROW_1) {
                if (this.itemStorage().slot(INPUT_SLOT).extractOne() != null) {
                    this.itemStorage().slot(STORAGE_SLOT).insert(CANNED_FOOD, 1);
                }
                this.transferringCan = false;
            } else {
                this.transferringCan = false;
                this.transferringFood = false;
                this.reset(0);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
        } else if (this.ejectCan) {
            this.ejectCan = false;
            if (!this.transferringCan && !this.transferringFood) {
                ItemStack itemStack = this.currentCan();
                if (ItemStack.isSameItemSameComponents(itemStack, CANNED_FOOD.getDefaultInstance())) {
                    itemStack = EMPTY_CAN.getDefaultInstance();
                }
                if (!this.itemStorage().slot(OUTPUT_SLOT).canInsert(itemStack.getItem(), itemStack.getComponentsPatch(), 1)) {
                    return MachineStatuses.OUTPUT_FULL;
                }
                this.transferringCan = true;
                this.setProgress(TRANSFER_OUTPUT);
                return GCMachineStatuses.TRANSFERRING_CAN;
            }
        } else if (!this.transferringCan && !this.transferringFood) {
            ItemResourceSlot storage = this.itemStorage().slot(STORAGE_SLOT);
            ItemStack can = storage.getResource().getDefaultInstance();
            can.applyComponents(storage.getComponents());
            if (CannedFoodItem.isFull(can)) {
                if (!this.outputSlotEmpty()) {
                    this.transferringCan = false;
                    this.reset(0);
                    return MachineStatuses.OUTPUT_FULL;
                }
                this.transferringCan = true;
                this.setProgress(TRANSFER_OUTPUT);
                return GCMachineStatuses.TRANSFERRING_CAN;
            }
        }

        if (this.transferringCan || this.getProgress() == TRANSFER_OUTPUT) {
            if (this.getProgress() == TRANSFER_OUTPUT) {
                if (!this.transferFood()) {
                    this.transferringCan = false;
                    this.reset(START_ROW_1);
                    return GCMachineStatuses.NO_FOOD;
                } else if (!this.outputSlotEmpty()) {
                    this.transferringCan = false;
                    this.reset(0);
                    return MachineStatuses.OUTPUT_FULL;
                }
                this.transferringCan = true;
            } else if (this.getProgress() == MAX_PROGRESS) {
                this.transferringCan = false;
                this.reset(0);
                ItemStack itemStack = this.currentCan();
                if (ItemStack.isSameItemSameComponents(itemStack, CANNED_FOOD.getDefaultInstance())) {
                    itemStack = EMPTY_CAN.getDefaultInstance();
                }
                if (!this.itemStorage().slot(OUTPUT_SLOT).canInsert(itemStack.getItem(), itemStack.getComponentsPatch(), 1)) {
                    return MachineStatuses.OUTPUT_FULL;
                }
                this.itemStorage().slot(STORAGE_SLOT).extractOne();
                this.itemStorage().slot(OUTPUT_SLOT).insert(itemStack.getItem(), itemStack.getComponentsPatch(), 1);
            }

            return GCMachineStatuses.TRANSFERRING_CAN;
        }

        boolean[] nonEmptyRows = new boolean[4];
        boolean empty = true;
        for (int row = 0; row < 4; row++) {
            nonEmptyRows[row] = !this.isRowEmpty(row);
            empty &= !nonEmptyRows[row];
        }

        if (this.getProgress() == START_ROW_1) {
            this.transferringFood = !empty;
        }
        if (empty && !this.transferringFood) {
            this.reset(START_ROW_1);
            return GCMachineStatuses.NO_FOOD;
        }

        for (int row : ROW_ORDER) {
            if (this.getProgress() == ROW_PROGRESS[row]) {
                if (nonEmptyRows[row]) {
                    this.setRowConsumed(row, true);
                    return GCMachineStatuses.CANNING;
                } else {
                    switch (row) {
                        case 0:
                            this.setProgress(START_ROW_2);
                            break;
                        case 1:
                            if (this.getFirstRowConsumed()) {
                                this.setProgress(SKIP_ROW_2);
                            } else {
                                this.setProgress(START_ROW_4);
                            }
                            break;
                        case 2:
                            if (this.getFourthRowConsumed()) {
                                this.setProgress(SKIP_ROW_3);
                            } else if (this.getFirstRowConsumed() || this.getSecondRowConsumed()) {
                                this.setProgress(FINAL_PROGRESS);
                            }
                            break;
                        case 3:
                            this.setProgress(START_ROW_3);
                    }
                }
            }
        }

        return GCMachineStatuses.CANNING;
    }

    @Override
    public void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        this.hasCan = !this.inputSlotEmpty() || !this.storageSlotEmpty() || !this.outputSlotEmpty();
        this.hadCan = this.hasCan;
        super.updateActiveState(level, pos, state.setValue(FoodCannerBlock.CAN, this.hasCan), active);
    }

    private boolean inputSlotEmpty() {
        return this.itemStorage().slot(INPUT_SLOT).isEmpty();
    }

    private boolean storageSlotEmpty() {
        return this.itemStorage().slot(STORAGE_SLOT).isEmpty();
    }

    private boolean outputSlotEmpty() {
        return this.itemStorage().slot(OUTPUT_SLOT).isEmpty();
    }

    private boolean noEnergy() {
        return !this.energyStorage().canExtract(Galacticraft.CONFIG.foodCannerEnergyConsumptionRate());
    }

    private ItemStack currentCan() {
        ItemResourceSlot storage = this.itemStorage().slot(STORAGE_SLOT);
        if (storage.getResource() == null) return ItemStack.EMPTY;
        ItemStack can = storage.getResource().getDefaultInstance();
        can.applyComponents(storage.getComponents());
        return can;
    }

    private boolean transferFood() {
        this.transferringFood = false;
        ItemStack can = this.currentCan();
        this.updateSlots(CannedFoodItem.addToCanEvenly(this.getItems(), can));
        this.itemStorage().slot(STORAGE_SLOT).extractOne();
        this.itemStorage().slot(STORAGE_SLOT).insert(can.getItem(), can.getComponentsPatch(), 1);
        return CannedFoodItem.isFull(can);
    }

    private List<ItemStack> getItems() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            if (this.getRowConsumed(row)) {
                for (int col = 0; col < 4; col++) {
                    int slot = GRID_START + 4 * row + col;
                    if (!this.itemStorage().slot(slot).isEmpty()) {
                        ItemStack itemStack = this.itemStorage().getItem(slot);
                        itemStack.applyComponents(this.itemStorage().slot(slot).getComponents());
                        stacks.add(itemStack);
                    } else {
                        stacks.add(ItemStack.EMPTY);
                    }
                }
            }
        }
        return stacks;
    }

    private void updateSlots(List<ItemStack> stacks) {
        int i = 0;
        for (int row = 0; row < 4; row++) {
            if (this.getRowConsumed(row)) {
                for (int col = 0; col < 4; col++) {
                    int slot = GRID_START + 4 * row + col;
                    ItemStack stack = stacks.get(i);
                    this.itemStorage().slot(slot).extract(this.itemStorage().getItem(slot).getCount());
                    this.itemStorage().slot(slot).insert(stack.getItem(), stack.getComponentsPatch(), stack.getCount());
                    ++i;
                }
            }
        }
    }

    private boolean isRowEmpty(int row) {
        for (int col = 0; col < 4; col++) {
            if (!this.itemStorage().slot(GRID_START + 4 * row + col).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void incrementProgress() {
        if (this.isActive()) {
            this.progress += 1;
            this.energyStorage().extract(Galacticraft.CONFIG.foodCannerEnergyConsumptionRate());
        }
        if (this.progress > MAX_PROGRESS) {
            this.setProgress(0);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void reset(int progress) {
        this.setProgress(progress);
        for (int row = 0; row < 4; row++) {
            this.setRowConsumed(row, false);
        }
    }

    public void ejectCan() {
        this.ejectCan = true;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return this.getSecurity().hasAccess(player) ? new FoodCannerMenu(syncId, (ServerPlayer) player, this) : null;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.setProgress(tag.getInt(Constant.Nbt.PROGRESS));
        this.transferringCan = tag.getBoolean(Constant.Nbt.TRANSFERRING_CAN);
        this.transferringFood = tag.getBoolean(Constant.Nbt.TRANSFERRING_FOOD);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.PROGRESS, this.progress);
        tag.putBoolean(Constant.Nbt.TRANSFERRING_CAN, this.transferringCan);
        tag.putBoolean(Constant.Nbt.TRANSFERRING_FOOD, this.transferringFood);
    }

    public boolean getRowConsumed(int row) {
        return this.rowsConsumed[row];
    }

    public void setRowConsumed(int row, boolean value) {
        this.rowsConsumed[row] = value;
    }

    public boolean getFirstRowConsumed() {
        return this.rowsConsumed[0];
    }

    public boolean getSecondRowConsumed() {
        return this.rowsConsumed[1];
    }

    public boolean getThirdRowConsumed() {
        return this.rowsConsumed[2];
    }

    public boolean getFourthRowConsumed() {
        return this.rowsConsumed[3];
    }
}