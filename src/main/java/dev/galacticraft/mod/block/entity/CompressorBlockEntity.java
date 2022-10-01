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

import dev.galacticraft.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.StorageSlot;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.LongProperty;
import dev.galacticraft.mod.machine.storage.io.GCSlotTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.CompressorScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CompressorBlockEntity extends RecipeMachineBlockEntity<Container, CompressingRecipe> {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;

    private final Container craftingInv = this.itemStorage().subInv(0, FUEL_INPUT_SLOT);
    public int fuelTime;
    public int fuelLength;
    private LongProperty fuelSlotModification = LongProperty.create(-1);

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        MachineItemStorage.Builder builder = MachineItemStorage.Builder.create();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(GCSlotTypes.ITEM_INPUT, new ItemSlotDisplay(x * 18 + 17, y * 18 + 17));
            }
        }
        return builder
                .addSlot(GCSlotTypes.SOLID_FUEL, new ItemSlotDisplay(83, 47))
                .addSlot(GCSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(143, 36))
                .build();
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatus.COMPRESSING;
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull CompressingRecipe recipe, @NotNull TransactionContext context) {
        ItemStack output = recipe.getResultItem();
        return this.itemStorage().insert(OUTPUT_SLOT, ItemVariant.of(output), output.getCount(), context) == output.getCount();
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull CompressingRecipe recipe, @NotNull TransactionContext context) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(this.craftingInv);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = remainder.get(i);
            this.itemStorage().extract(i, 1, context);
            if (stack != ItemStack.EMPTY) {
                if (this.itemStorage().getAmount(i) == 0) {
                    if (stack.getCount() != this.itemStorage().insert(i, ItemVariant.of(stack), stack.getCount(), context)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        if (--this.fuelTime <= 0) {
            this.fuelLength = 0;
            this.fuelTime = 0;
        }
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.getMaxProgress() > 0) {
            if (this.getProgress() % (this.getMaxProgress() / 8) == 0 && this.getProgress() > this.getMaxProgress() / 2) {
                world.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

        return super.tick(world, pos, state, profiler);
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.fuelLength == 0) {
            StorageSlot<Item, ItemVariant, ItemStack> slot = this.itemStorage().getSlot(FUEL_INPUT_SLOT);
            if (slot.getModCountUnsafe() != this.fuelSlotModification.getValue()) {
                this.fuelSlotModification.setValue(slot.getModCountUnsafe(), context);
                ItemStack stack = this.itemStorage().extract(FUEL_INPUT_SLOT, 1, context);
                Item remainingItem = stack.getItem().getCraftingRemainingItem();
                Integer integer = FuelRegistry.INSTANCE.get(stack.getItem());
                if (integer != null && integer > 0) {
                    this.fuelTime = this.fuelLength = integer;
                    if (remainingItem != null && slot.getResource().isBlank()) {
                        slot.insert(ItemVariant.of(remainingItem), 1, context);
                    }
                }
            }
        }
        return this.fuelLength == 0 ? GCMachineStatus.NO_FUEL : super.extractResourcesToWork(context);
    }

    @Override
    protected int getProcessTime(@NotNull CompressingRecipe recipe) {
        return recipe.getTime();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.fuelTime = nbt.getInt(Constant.Nbt.FUEL_TIME);
        this.fuelLength = nbt.getInt(Constant.Nbt.FUEL_LENGTH);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new CompressorScreenHandler(syncId, player, this);
        return null;
    }
}
