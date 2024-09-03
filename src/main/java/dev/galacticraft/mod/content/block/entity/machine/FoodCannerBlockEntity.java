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

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.network.s2c.BubbleUpdatePayload;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.galacticraft.mod.content.item.CannedFoodItem.*;

public class FoodCannerBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int STORAGE_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;
    //x0 -> x3 && y0 -> y3 x0 and y0 top left x3 and y3 bottom right
    public static final int FOOD_INPUT_SLOT_1 = 4;
    public static final int FOOD_INPUT_SLOT_2 = 5;
    public static final int FOOD_INPUT_SLOT_3 = 6;
    public static final int FOOD_INPUT_SLOT_4 = 7;
    public static final int FOOD_INPUT_SLOT_5 = 8;
    public static final int FOOD_INPUT_SLOT_6 = 9;
    public static final int FOOD_INPUT_SLOT_7 = 10;
    public static final int FOOD_INPUT_SLOT_8 = 11;
    public static final int FOOD_INPUT_SLOT_9 = 12;
    public static final int FOOD_INPUT_SLOT_10 = 13;
    public static final int FOOD_INPUT_SLOT_11 = 14;
    public static final int FOOD_INPUT_SLOT_12 = 15;
    public static final int FOOD_INPUT_SLOT_13 = 16;
    public static final int FOOD_INPUT_SLOT_14 = 17;
    public static final int FOOD_INPUT_SLOT_15 = 18;
    public static final int FOOD_INPUT_SLOT_16 = 19;

    private int progress = 1;

    private int firstRowConsumed = 0;
    private int secondRowConsumed = 0;
    private int thirdRowConsumed = 0;
    private int forthRowConsumed = 0;
    private final int maxProgress = 115;
    private boolean transferring_can = false;
    private boolean transferring_food = false;

    private ItemStack storage;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 67)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(62, 13)
                            .filter(ResourceFilters.ofResource(GCItems.EMPTY_CANNED_FOOD)),
                    ItemResourceSlot.builder(TransferType.STORAGE)
                            .pos(62, 40)
                            .capacity(1)
                            .filter(ResourceFilters.ofResource(GCItems.EMPTY_CANNED_FOOD)),
                    ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(62, 67),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(98, 13)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(116, 13)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(134, 13)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(152, 13)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(98, 31)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(116, 31)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(134, 31)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(152, 31)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(98, 49)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(116, 49)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(134, 49)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(152, 49)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(98, 67)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(116, 67)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(134, 67)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(152, 67)
                            .filter((item, tag) -> item != null && item.components().has(DataComponents.FOOD) && !(item instanceof CannedFoodItem))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
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
        if (noEnergy())
        {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        if (inputSlotEmpty())
        {
            if (!transferring_can && storageSlotEmpty())
            {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
        }
        if (storageSlotEmpty())
        {
            if (getProgress() == 1 && !transferring_can)
            {
                this.transferring_can = true;
                this.itemStorage().slot(INPUT_SLOT).extractOne();
                return GCMachineStatuses.TRANSFERRING_CAN;
            }
            if (getProgress() == 10)
            {
                if (transferring_can)
                {
                    if (transferring_food)
                    {
                        this.transferring_can = false;
                        this.itemStorage().slot(STORAGE_SLOT).insert(this.storage.getItem(), this.storage.getComponentsPatch(), 1);
                    }
                    else
                    {
                        this.transferring_can = false;
                        this.itemStorage().slot(STORAGE_SLOT).insert(GCItems.CANNED_FOOD, 1);
                        this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
                    }
                }
                else
                {
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
        if (progress < 107 && getSize(this.storage) == MAX_FOOD && !transferring_can && !transferring_food)
        {
            setProgress(107);
        }
        if (!(row0 || row1 || row2 || row3) && !transferring_can && !transferring_food)
        {
            if (storageContainsFood())
            {
                setProgress(107);
            }
            else
            {
                return GCMachineStatuses.NO_FOOD;
            }
        }
        if (getProgress() == 10)
        {
            transferring_food = false;
            if (row0)
            {
                if (getSize(this.storage) < MAX_FOOD)
                {
                    transferring_food = true;
                    setFirstRowConsumed(1);
                    List<ItemStack> leftover = addToCan(checkRow(0), this.storage);
                    clearRow(0, leftover);
                    return GCMachineStatuses.CANNING;
                }

            }
            else
            {
                setProgress(37);
            }
        }
        if (getProgress() == 37)
        {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food)
            {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row1)
            {
                if (getSize(this.storage) < MAX_FOOD)
                {
                    transferring_food = true;
                    setSecondRowConsumed(1);
                    List<ItemStack> leftover = addToCan(checkRow(1), this.storage);
                    clearRow(1, leftover);
                    return GCMachineStatuses.CANNING;
                }

            }
            else
            {
                if (getFirstRowConsumed() == 1)
                {
                    transferring_food = true;
                    setProgress(46);
                }
                else
                {
                    setProgress(53);
                }

            }
        }
        if (getProgress() == 53)
        {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food)
            {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row3)
            {
                if (getSize(this.storage) < MAX_FOOD)
                {
                    transferring_food = true;
                    setForthRowConsumed(1);
                    List<ItemStack> leftover = addToCan(checkRow(3), this.storage);
                    clearRow(3, leftover);
                    return GCMachineStatuses.CANNING;
                }

            }
            else
            {
                setProgress(79);
            }
        }
        if (getProgress() == 79)
        {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food)
            {
                setProgress(1);
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (row2)
            {
                if (getSize(this.storage) < MAX_FOOD)
                {
                    transferring_food = true;
                    setThirdRowConsumed(1);
                    List<ItemStack> leftover = addToCan(checkRow(2), this.storage);
                    clearRow(2, leftover);
                    return GCMachineStatuses.CANNING;
                }

            }
            else
            {
                if (getFirstRowConsumed() == 1 || getSecondRowConsumed() == 1)
                {
                    transferring_food = true;
                    setProgress(97);
                    if (getForthRowConsumed() == 1)
                    {
                        transferring_food = true;
                        setProgress(87);
                    }
                }else if (getForthRowConsumed() == 1)
                {
                    transferring_food = true;
                    setProgress(87);
                }else
                {
                    setProgress(107);
                }

            }
        }

        if (getProgress() == 107)
        {
            if (this.itemStorage().slot(STORAGE_SLOT).isEmpty() && transferring_food)
            {
                setProgress(1);
                resetConsumedRows();
                return GCMachineStatuses.MISSING_EMPTY_CAN;
            }
            transferring_food = false;
            if (outputFull())
            {
                if (storageSlotEmpty())
                {
                    this.storage = GCItems.CANNED_FOOD.getDefaultInstance();
                    resetConsumedRows();
                    setProgress(1);
                    return MachineStatuses.OUTPUT_FULL;
                }
                if (this.itemStorage().getItem(STORAGE_SLOT) != this.storage)
                {
                    this.itemStorage().slot(STORAGE_SLOT).extractOne();
                    this.itemStorage().slot(STORAGE_SLOT).insert(this.storage.getItem(), this.storage.getComponentsPatch(), 1);
                }
                if (getSize(this.storage) < MAX_FOOD && (row0 || row1 || row2 || row3))
                {
                    setProgress(10);
                    resetConsumedRows();
                    return MachineStatuses.OUTPUT_FULL  ;
                }
                return MachineStatuses.OUTPUT_FULL;
            }
            transferring_can = true;
            this.itemStorage().slot(STORAGE_SLOT).extractOne();
            return GCMachineStatuses.TRANSFERRING_CAN;
        }
        if (getProgress() == 115 && transferring_can)
        {
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
        setFirstRowConsumed(0);
        setSecondRowConsumed(0);
        setThirdRowConsumed(0);
        setForthRowConsumed(0);
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
        if (row == 0)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_1).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_1).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_2).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_2).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_3).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_3).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_4).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_4).getCount());
        }
        if (row == 1)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_5).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_5).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_6).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_6).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_7).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_7).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_8).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_8).getCount());
        }
        if (row == 2)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_9).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_9).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_10).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_10).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_11).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_11).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_12).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_12).getCount());
        }
        if (row == 3)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_13).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_13).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_14).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_14).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_15).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_15).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_16).extract(this.itemStorage().getItem(FOOD_INPUT_SLOT_16).getCount());
        }
    }
    private void clearRow(int row, List<ItemStack> stacks) {
        clearRow(row);
        if (row == 0)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_1).insert(stacks.get(0).getItem(), stacks.get(0).getComponentsPatch(), stacks.get(0).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_2).insert(stacks.get(1).getItem(), stacks.get(1).getComponentsPatch(), stacks.get(1).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_3).insert(stacks.get(2).getItem(), stacks.get(2).getComponentsPatch(), stacks.get(2).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_4).insert(stacks.get(3).getItem(), stacks.get(3).getComponentsPatch(), stacks.get(3).getCount());
        }
        if (row == 1)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_5).insert(stacks.get(0).getItem(), stacks.get(0).getComponentsPatch(), stacks.get(0).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_6).insert(stacks.get(1).getItem(), stacks.get(1).getComponentsPatch(), stacks.get(1).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_7).insert(stacks.get(2).getItem(), stacks.get(2).getComponentsPatch(), stacks.get(2).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_8).insert(stacks.get(3).getItem(), stacks.get(3).getComponentsPatch(), stacks.get(3).getCount());
        }
        if (row == 2)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_9).insert(stacks.get(0).getItem(), stacks.get(0).getComponentsPatch(), stacks.get(0).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_10).insert(stacks.get(1).getItem(), stacks.get(1).getComponentsPatch(), stacks.get(1).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_11).insert(stacks.get(2).getItem(), stacks.get(2).getComponentsPatch(), stacks.get(2).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_12).insert(stacks.get(3).getItem(), stacks.get(3).getComponentsPatch(), stacks.get(3).getCount());
        }
        if (row == 3)
        {
            this.itemStorage().slot(FOOD_INPUT_SLOT_13).insert(stacks.get(0).getItem(), stacks.get(0).getComponentsPatch(), stacks.get(0).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_14).insert(stacks.get(1).getItem(), stacks.get(1).getComponentsPatch(), stacks.get(1).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_15).insert(stacks.get(2).getItem(), stacks.get(2).getComponentsPatch(), stacks.get(2).getCount());
            this.itemStorage().slot(FOOD_INPUT_SLOT_16).insert(stacks.get(3).getItem(), stacks.get(3).getComponentsPatch(), stacks.get(3).getCount());
        }
    }

    private List<ItemStack> checkRow(int row)
    {
        List<ItemStack> stacks = new ArrayList<>();
        if (row == 0)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_1).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_1));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_2).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_2));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_3).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_3));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_4).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_4));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
        }
        if (row == 1)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_5).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_5));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_6).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_6));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_7).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_7));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_8).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_8));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
        }
        if (row == 2)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_9).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_9));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_10).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_10));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_11).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_11));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_12).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_12));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
        }
        if (row == 3)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_13).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_13));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_14).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_14));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_15).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_15));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_16).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_16));
            }else
            {
                stacks.add(Items.AIR.getDefaultInstance());
            }
        }
        return stacks;
    }

    private List<ItemStack> checkRowItems(int row)
    {
        List<ItemStack> stacks = new ArrayList<>();
        if (row == 0)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_1).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_1));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_2).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_2));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_3).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_3));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_4).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_4));
            }
        }
        if (row == 1)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_5).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_5));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_6).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_6));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_7).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_7));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_8).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_8));
            }
        }
        if (row == 2)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_9).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_9));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_10).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_10));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_11).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_11));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_12).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_12));
            }
        }
        if (row == 3)
        {
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_13).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_13));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_14).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_14));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_15).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_15));
            }
            if (!this.itemStorage().slot(FOOD_INPUT_SLOT_16).isEmpty())
            {
                stacks.add(this.itemStorage().getItem(FOOD_INPUT_SLOT_16));
            }
        }
        return stacks;
    }

    private void progress()
    {
        if (this.isActive())
        {
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
        if (this.getSecurity().hasAccess(player)) return new FoodCannerMenu(syncId, (ServerPlayer) player, this);
        return null;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        setProgress(tag.getInt(Constant.Nbt.PROGRESS));
        this.transferring_can = tag.getBoolean("TRANSFERRING_CAN");
        this.transferring_food = tag.getBoolean("TRANSFERRING_FOOD");
        ItemStack itemStack = GCItems.CANNED_FOOD.getDefaultInstance();
        itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(listTagToCompoundTag(tag.getList("STORAGE", ListTag.TAG_COMPOUND))));
        this.storage = itemStack;

    }


    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.PROGRESS, this.progress);
        tag.putBoolean("TRANSFERRING_CAN", this.transferring_can);
        tag.putBoolean("TRANSFERRING_FOOD", this.transferring_food);
        tag.put("STORAGE", compoundTagToListTag(Objects.requireNonNull(this.storage.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag()));
    }

    public static CompoundTag listTagToCompoundTag(ListTag listTag) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Items", listTag); // Store the ListTag under the key "Items"
        return compoundTag;
    }

    public static ListTag compoundTagToListTag(CompoundTag compoundTag){
        if (compoundTag != null) {
            return compoundTag.getList("Items", ListTag.TAG_COMPOUND); // Retrieve the ListTag stored under the key "Items"
        } else {
            return new ListTag(); // Return an empty ListTag if the key doesn't exist
        }
    }

    public int getFirstRowConsumed() {
        return firstRowConsumed;
    }
    public void setFirstRowConsumed(int value) {
        this.firstRowConsumed = value;
    }
    public int getSecondRowConsumed() {
        return secondRowConsumed;
    }
    public void setSecondRowConsumed(int value) {
        this.secondRowConsumed = value;
    }
    public int getThirdRowConsumed() {
        return thirdRowConsumed;
    }
    public void setThirdRowConsumed(int value) {
        this.thirdRowConsumed = value;
    }
    public int getForthRowConsumed() {
        return forthRowConsumed;
    }
    public void setForthRowConsumed(int value) {
        this.forthRowConsumed = value;
    }
}