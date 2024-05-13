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

import dev.galacticraft.machinelib.api.block.entity.BasicRecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.CraftingRecipeTestContainer;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends BasicRecipeMachineBlockEntity<CraftingContainer, CompressingRecipe> {
    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOTS = 1;
    public static final int INPUT_LENGTH = 9;
    public static final int OUTPUT_SLOT = INPUT_SLOTS + INPUT_LENGTH;

    public int fuelTime;
    public int fuelLength;

    private long fuelSlotModification = -1;
    private boolean hasFuel = false;

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.COMPRESSOR, pos, state, GCRecipes.COMPRESSING_TYPE, INPUT_SLOTS, INPUT_LENGTH, OUTPUT_SLOT);
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<CompressingRecipe> recipe) {
        return GCMachineStatuses.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().getSlot(FUEL_SLOT);
            if (slot.getModifications() != this.fuelSlotModification) {
                this.fuelSlotModification = slot.getModifications();
                if (!slot.isEmpty()) {
                    Integer integer = FuelRegistry.INSTANCE.get(slot.getResource());
                    this.hasFuel = integer != null && integer > 0;
                } else {
                    this.hasFuel = false;
                }
            }
            return this.hasFuel ? null : GCMachineStatuses.NO_FUEL;
        }

        return null;
    }

    @Override
    protected void extractResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().getSlot(FUEL_SLOT);
            if (!slot.isEmpty()) {
                Integer time = FuelRegistry.INSTANCE.get(slot.getResource());
                if (time > 0) {
                    if (slot.consumeOne() != null) {
                        this.fuelTime = this.fuelLength = time;
                    }
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
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        RecipeHolder<CompressingRecipe> recipe = this.getActiveRecipe();
        if (recipe != null && this.getState().isActive()) {
            int maxProgress = this.getProcessingTime(recipe);
            if (this.getProgress() % (maxProgress / 8) == 0 && this.getProgress() > maxProgress / 2) {
                level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        return super.tick(level, pos, state, profiler);
    }

    @Override
    public int getProcessingTime(@NotNull RecipeHolder<CompressingRecipe> recipe) {
        return recipe.value().getTime();
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

    @Override
    protected CraftingContainer createCraftingInv() {
        return CraftingRecipeTestContainer.create(3, 3, this.itemStorage(), this.inputSlots, this.inputSlotsLen);
    }
}
