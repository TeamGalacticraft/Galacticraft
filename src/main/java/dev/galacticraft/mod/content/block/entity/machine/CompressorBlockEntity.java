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

import dev.galacticraft.machinelib.api.block.entity.BasicRecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.RecipeHelper;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.machine.CompressorBlock;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.CompressorMenu;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends BasicRecipeMachineBlockEntity<CraftingInput, CompressingRecipe> {
    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOTS = 1;
    public static final int INPUT_LENGTH = 9;
    public static final int OUTPUT_SLOT = INPUT_SLOTS + INPUT_LENGTH;

    public int fuelTime;
    public int fuelLength;

    private long fuelSlotModification = -1;
    private boolean hasFuel = false;
    private boolean lit = false;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.builder()
                    .add(ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(83, 47)
                            .filter((item, tag) -> {
                                Integer integer = FuelRegistry.INSTANCE.get(item);
                                return integer != null && integer > 0;
                            }))
                    .add3x3Grid(TransferType.INPUT, 17, 17)
                    .add(ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(143, 36))
    );

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.COMPRESSOR, pos, state, GCRecipes.COMPRESSING_TYPE, SPEC, INPUT_SLOTS, INPUT_LENGTH, OUTPUT_SLOT);
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<CompressingRecipe> recipe) {
        return GCMachineStatuses.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().slot(FUEL_SLOT);
            if (slot.getModifications() != this.fuelSlotModification) {
                this.fuelSlotModification = slot.getModifications();
                if (!slot.isEmpty()) {
                    Integer integer = FuelRegistry.INSTANCE.get(slot.getResource());
                    this.hasFuel = integer != null && integer > 0;
                } else {
                    this.hasFuel = false;
                }
            }
            if (!this.hasFuel) return GCMachineStatuses.NO_FUEL;
        }

        Level level = this.getLevel();
        BlockPos blockPos = this.getBlockPos();
        if (this.shouldExtinguish(level, blockPos, level.getBlockState(blockPos))) {
            return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
        }

        return null;
    }

    @Override
    protected void extractResourcesToWork() {
        if (this.fuelLength == 0) {
            ItemResourceSlot slot = this.itemStorage().slot(FUEL_SLOT);
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

        boolean lit = this.fuelLength > 0;
        if (this.lit != lit) {
            this.lit = lit;
            BlockState blockState = this.level.getBlockState(this.worldPosition)
                    .setValue(CompressorBlock.LIT, this.lit);
            this.level.setBlock(this.worldPosition, blockState, 2);
        }

    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("extinguish");
        if (this.fuelTime > 0 && this.shouldExtinguish(level, pos, state)) {
            this.fuelLength = 0;
            this.fuelTime = 0;
            RandomSource randomSource = level.getRandom();
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8F);

            return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
        }
        profiler.pop();

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
    public void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        this.lit = this.fuelLength > 0;
        super.updateActiveState(level, pos, state.setValue(CompressorBlock.LIT, this.lit), active);
    }

    private boolean shouldExtinguish(Level level, BlockPos pos, BlockState state) {
        return !level.galacticraft$isBreathable(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                && !level.galacticraft$isBreathable(pos);
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.fuelTime = tag.getInt(Constant.Nbt.FUEL_TIME);
        this.fuelLength = tag.getInt(Constant.Nbt.FUEL_LENGTH);

        this.lit = this.fuelLength > 0;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new CompressorMenu(syncId, player, this);
    }

    @Override
    protected CraftingInput craftingInv() {
        return RecipeHelper.craftingInput(3, 3, this.inputSlots.getSlots());
    }
}
