/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.RecipeTestContainer;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.CompressorMenu;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CompressorBlockEntity extends RecipeMachineBlockEntity<Container, CompressingRecipe> {
    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOTS = 1;
    public static final int INPUT_LENGTH = 9;
    public static final int OUTPUT_SLOT = INPUT_SLOTS + INPUT_LENGTH;

    public int fuelTime;
    public int fuelLength;
    private long fuelSlotModification = -1;

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.COMPRESSOR, pos, state, GCRecipes.COMPRESSING_TYPE);
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatuses.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().getSlot(FUEL_SLOT);
            if (slot.getModifications() != this.fuelSlotModification) {
                this.fuelSlotModification = slot.getModifications();
                if (!slot.isEmpty() && FuelRegistry.INSTANCE.get(slot.getResource()) > 0) {
                    return null;
                }
            }
            return GCMachineStatuses.NO_FUEL;
        }

        return null;
    }

    @Override
    protected void extractResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().getSlot(FUEL_SLOT);
            if (!slot.isEmpty()) {
                ItemStack stack = new ItemStack(slot.getResource(), 1);
                stack.setTag(slot.getTag());
                int time = FuelRegistry.INSTANCE.get(stack.getItem());
                if (time > 0) {
                    if (slot.extractOne()) {
                        this.fuelTime = this.fuelLength = time;
                        ItemStack remainder = stack.getRecipeRemainder();
                        if (remainder != null && !remainder.isEmpty() && slot.isEmpty()) { // fixme
                            slot.insert(stack.getItem(), stack.getTag(), stack.getCount());
                        }
                    }
                }
            }
        }
    }

    @Override
    protected @NotNull Container createCraftingInv() {
        return RecipeTestContainer.create(this.itemStorage(), INPUT_SLOTS, INPUT_LENGTH);
    }

    @Override
    protected void outputStacks(@NotNull CompressingRecipe recipe) {
        ItemStack stack = recipe.getResultItem(this.level.registryAccess());
        this.itemStorage().getSlot(OUTPUT_SLOT).insert(stack.getItem(), stack.getTag(), stack.getCount());
    }

    @Override
    protected boolean canOutputStacks(@NotNull CompressingRecipe recipe) {
        ItemStack stack = recipe.getResultItem(this.level.registryAccess());
        return this.itemStorage().getSlot(OUTPUT_SLOT).canInsert(stack.getItem(), stack.getTag(), stack.getCount());
    }

    @Override
    protected void extractCraftingMaterials(@NotNull CompressingRecipe recipe) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(this.craftingInv);
        for (int i = INPUT_SLOTS; i < INPUT_SLOTS + INPUT_LENGTH; i++) {
            ItemResourceSlot slot = this.itemStorage().getSlot(i);
            ItemStack stack = remainder.get(i);
            slot.extractOne();
            if (stack != ItemStack.EMPTY) {
                if (slot.isEmpty()) {
                    slot.insert(stack.getItem(), stack.getTag(), stack.getCount());
                }
            }
        }
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
    protected int getProcessTime(@NotNull CompressingRecipe recipe) {
        return recipe.getTime();
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public int getFuelLength() {
        return fuelLength;
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
        if (this.getSecurity().hasAccess(player)) return new CompressorMenu(syncId, (ServerPlayer) player, this);
        return null;
    }
}
