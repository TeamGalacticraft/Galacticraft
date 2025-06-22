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
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.galacticraft.mod.content.item.CannedFoodItem.*;

public class FoodCannerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int STORAGE_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;
    //x0 -> x3 && y0 -> y3 x0 and y0 top left x3 and y3 bottom right
    public static final int[][] ROWS = {
            {4, 5, 6, 7},
            {8, 9, 10, 11},
            {12, 13, 14, 15},
            {16, 17, 18, 19}
    };

    private int progress = 1;

    private boolean firstRowConsumed = false;
    private boolean secondRowConsumed = false;
    private boolean thirdRowConsumed = false;
    private boolean forthRowConsumed = false;
    private final int maxProgress = 115;
    private boolean transferring_can = false;
    private boolean transferring_food = false;

    private ItemStack storage;

    private static List<ItemResourceSlot.Spec> createFoodSlots(int xOffset, int yOffset, int columns, int rows) {
        List<ItemResourceSlot.Spec> slots = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                slots.add(ItemResourceSlot.builder(TransferType.INPUT)
                        .pos(xOffset + (x * 18), yOffset + (y * 18))
                        .filter((item, tag) -> CannedFoodItem.canAddToCan(item)));
            }
        }
        return slots;
    }

    private static MachineItemStorage.Spec newMachineStorageSpec() {
        MachineItemStorage.Spec storage = MachineItemStorage.builder();
        storage.add(
                ItemResourceSlot.builder(TransferType.TRANSFER)
                        .pos(8, 67)
                        .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                        .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)));
        storage.add(
                ItemResourceSlot.builder(TransferType.INPUT)
                        .pos(62, 13)
                        .filter(ResourceFilters.ofResource(GCItems.EMPTY_CAN))
                        .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.FOOD_CAN)));
        storage.add(
                ItemResourceSlot.builder(TransferType.STORAGE)
                        .pos(62, 40)
                        .capacity(1)
                        .filter(ResourceFilters.ofResource(GCItems.EMPTY_CAN)));
        storage.add(
                ItemResourceSlot.builder(TransferType.OUTPUT)
                        .pos(62, 67));
        for (ItemResourceSlot.Spec slot : createFoodSlots(98, 13, 4, 4)) {
            storage.add(slot);
        }
        return storage;
    }

    private static final StorageSpec SPEC = StorageSpec.of(
            newMachineStorageSpec(),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.foodCannerEnergyConsumptionRate() * 2,
                    0
            )
    );

    public FoodCannerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.FOOD_CANNER, pos, state, SPEC);
        this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("extract_resources");
        this.chargeFromSlot(CHARGE_SLOT);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        progress();
        if (noEnergy()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        if (inputSlotEmpty()) {
            if (!transferring_can && storageSlotEmpty()) {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
        }
        if (storageSlotEmpty()) {
            if (getProgress() == 1 && !transferring_can) {
                this.transferring_can = true;
                this.itemStorage().slot(INPUT_SLOT).extractOne();
                return GCMachineStatuses.TRANSFERRING_CAN;
            }
            if (getProgress() == 10) {
                if (transferring_can) {
                    if (transferring_food) {
                        this.transferring_can = false;
                        this.itemStorage().slot(STORAGE_SLOT).insert(this.storage.getItem(), this.storage.getComponentsPatch(), 1);
                    } else {
                        this.transferring_can = false;
                        this.itemStorage().slot(STORAGE_SLOT).insert(GCItems.CANNED_FOOD, 1);
                        this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
                    }
                } else {
                    setProgress(1);
                    resetConsumedRows();
                    return GCMachineStatuses.MISSING_EMPTY_CAN;
                }
            }
        }
        boolean row0 = !checkRowItems(0).isEmpty();
        boolean row1 = !checkRowItems(1).isEmpty();
        boolean row2 = !checkRowItems(2).isEmpty();
        boolean row3 = !checkRowItems(3).isEmpty();
        if (progress < 107 && getSize(this.storage) == MAX_FOOD && !transferring_can && !transferring_food) {
            setProgress(107);
        }
        if (!(row0 || row1 || row2 || row3) && !transferring_can && !transferring_food) {
            if (storageContainsFood()) {
                setProgress(107);
            } else {
                return GCMachineStatuses.NO_FOOD;
            }
        }
        if (getProgress() == 10) {
            transferring_food = false;
            if (row0) {
                if (getSize(this.storage) < MAX_FOOD) {
                    transferring_food = true;
                    setFirstRowConsumed(true);
                    List<ItemStack> leftover = addToCan(checkRow(0), this.storage);
                    clearRow(0, leftover);
                    return GCMachineStatuses.CANNING;
                }

            } else {
                setProgress(37);
            }
        }
        if (getProgress() == 37) {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food) {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row1) {
                if (getSize(this.storage) < MAX_FOOD) {
                    transferring_food = true;
                    setSecondRowConsumed(true);
                    List<ItemStack> leftover = addToCan(checkRow(1), this.storage);
                    clearRow(1, leftover);
                    return GCMachineStatuses.CANNING;
                }

            } else {
                if (getFirstRowConsumed()) {
                    transferring_food = true;
                    setProgress(46);
                } else {
                    setProgress(53);
                }

            }
        }
        if (getProgress() == 53) {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food) {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row3) {
                if (getSize(this.storage) < MAX_FOOD) {
                    transferring_food = true;
                    setForthRowConsumed(true);
                    List<ItemStack> leftover = addToCan(checkRow(3), this.storage);
                    clearRow(3, leftover);
                    return GCMachineStatuses.CANNING;
                }

            } else {
                setProgress(79);
            }
        }
        if (getProgress() == 79) {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food) {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row2) {
                if (getSize(this.storage) < MAX_FOOD) {
                    transferring_food = true;
                    setThirdRowConsumed(true);
                    List<ItemStack> leftover = addToCan(checkRow(2), this.storage);
                    clearRow(2, leftover);
                    return GCMachineStatuses.CANNING;
                }

            } else {
                if (getFirstRowConsumed() || getSecondRowConsumed()) {
                    transferring_food = true;
                    setProgress(97);
                    if (getForthRowConsumed()) {
                        transferring_food = true;
                        setProgress(87);
                    }
                } else if (getForthRowConsumed()) {
                    transferring_food = true;
                    setProgress(87);
                } else {
                    setProgress(107);
                }

            }
        }

        if (getProgress() == 107) {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food) {
                setProgress(1);
                resetConsumedRows();
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (outputFull()) {
                if (storageSlotEmpty()) {
                    this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
                    resetConsumedRows();
                    setProgress(1);
                    return MachineStatuses.OUTPUT_FULL;
                }
                if (this.itemStorage().getItem(STORAGE_SLOT) != this.storage) {
                    this.itemStorage().slot(STORAGE_SLOT).extractOne();
                    this.itemStorage().slot(STORAGE_SLOT).insert(this.storage.getItem(), this.storage.getComponentsPatch(), 1);
                }
                if (getSize(this.storage) < MAX_FOOD && (row0 || row1 || row2 || row3)) {
                    setProgress(10);
                    resetConsumedRows();
                    return MachineStatuses.OUTPUT_FULL;
                }
                return MachineStatuses.OUTPUT_FULL;
            }
            transferring_can = true;
            this.itemStorage().slot(STORAGE_SLOT).extractOne();
            return GCMachineStatuses.TRANSFERRING_CAN;
        }
        if (getProgress() == 115 && transferring_can) {
            transferring_can = false;
            this.itemStorage().slot(OUTPUT_SLOT).insert(this.storage.getItem(), this.storage.getComponentsPatch(), 1);
            this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
            resetConsumedRows();
            setProgress(1);
            return MachineStatuses.OUTPUT_FULL;
        }

        return GCMachineStatuses.CANNING;
    }

    private void resetConsumedRows() {
        setFirstRowConsumed(false);
        setSecondRowConsumed(false);
        setThirdRowConsumed(false);
        setForthRowConsumed(false);
    }

    private boolean storageContainsFood() {
        return !getContents(this.storage).isEmpty();
    }

    private boolean inputSlotEmpty() {
        return this.itemStorage().slot(INPUT_SLOT).isEmpty();
    }

    private boolean storageSlotEmpty() {
        return this.itemStorage().slot(STORAGE_SLOT).isEmpty();
    }

    private boolean noEnergy() {
        return !this.energyStorage().canExtract(Galacticraft.CONFIG.foodCannerEnergyConsumptionRate());
    }

    private boolean outputFull() {
        return this.itemStorage().slot(OUTPUT_SLOT).isFull();
    }

    private void clearRow(int row) {
        for (int slot : ROWS[row]) {
            this.itemStorage().slot(slot).extract(this.itemStorage().getItem(slot).getCount());
        }
    }

    private void clearRow(int row, List<ItemStack> stacks) {
        clearRow(row);
        for (int slot : ROWS[row]) {
            ItemStack stack = stacks.get(slot % 4);
            this.itemStorage().slot(slot).insert(stack.getItem(), stack.getComponentsPatch(), stack.getCount());
        }
    }

    private List<ItemStack> checkRow(int row) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot : ROWS[row]) {
            if (!this.itemStorage().slot(slot).isEmpty()) {
                stacks.add(this.itemStorage().getItem(slot));
            } else {
                stacks.add(Items.AIR.getDefaultInstance());
            }
        }
        return stacks;
    }

    private List<ItemStack> checkRowItems(int row) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot : ROWS[row]) {
            if (!this.itemStorage().slot(slot).isEmpty()) {
                stacks.add(this.itemStorage().getItem(slot));
            }
        }
        return stacks;
    }

    private void progress() {
        if (this.isActive()) {
            this.progress += 1;
        }
        if (this.progress > this.maxProgress) setProgress(1);
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return this.getSecurity().hasAccess(player) ? new FoodCannerMenu(syncId, (ServerPlayer) player, this) : null;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        setProgress(tag.getInt(Constant.Nbt.PROGRESS));
        this.transferring_can = tag.getBoolean(Constant.Nbt.TRANSFERRING_CAN);
        this.transferring_food = tag.getBoolean(Constant.Nbt.TRANSFERRING_FOOD);

        this.storage = ItemStack.parseOptional(lookup, tag.getCompound(Constant.Nbt.STORAGE));
    }


    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.PROGRESS, this.progress);
        tag.putBoolean(Constant.Nbt.TRANSFERRING_CAN, this.transferring_can);
        tag.putBoolean(Constant.Nbt.TRANSFERRING_FOOD, this.transferring_food);

        tag.put(Constant.Nbt.STORAGE, this.storage.save(lookup, new CompoundTag()));
    }

    public boolean getFirstRowConsumed() {
        return firstRowConsumed;
    }

    public void setFirstRowConsumed(boolean value) {
        this.firstRowConsumed = value;
    }

    public boolean getSecondRowConsumed() {
        return secondRowConsumed;
    }

    public void setSecondRowConsumed(boolean value) {
        this.secondRowConsumed = value;
    }

    public boolean getThirdRowConsumed() {
        return thirdRowConsumed;
    }

    public void setThirdRowConsumed(boolean value) {
        this.thirdRowConsumed = value;
    }

    public boolean getForthRowConsumed() {
        return forthRowConsumed;
    }

    public void setForthRowConsumed(boolean value) {
        this.forthRowConsumed = value;
    }
}