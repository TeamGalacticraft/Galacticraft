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
import dev.galacticraft.machinelib.api.block.entity.BasicRecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.RecipeHelper;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElectricCompressorBlockEntity extends BasicRecipeMachineBlockEntity<CraftingInput, CompressingRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOTS = 1;
    public static final int INPUT_LENGTH = 9;
    public static final int OUTPUT_SLOTS = INPUT_SLOTS + INPUT_LENGTH;
    public static final int OUTPUT_LENGTH = 2;

    public static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.builder()
                    .add(ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY))
                    )
                    .add3x3Grid(TransferType.INPUT, 30, 17)
                    .add(ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(148, 22)
                    )
                    .add(ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(148, 48)
                    ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.electricCompressorEnergyConsumptionRate() * 2,
                    0
            )
    );

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ELECTRIC_COMPRESSOR, pos, state, GCRecipes.COMPRESSING_TYPE, SPEC, INPUT_SLOTS, INPUT_LENGTH, OUTPUT_SLOTS, OUTPUT_LENGTH);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromSlot(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        RecipeHolder<CompressingRecipe> recipe = this.getActiveRecipe();
        if (recipe != null && this.getState().isActive()) {
            int maxProgress = this.getProcessingTime(recipe);
            if (this.getProgress() % (maxProgress / 5) == 0 && this.getProgress() > maxProgress / 2) {
                level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        return super.tick(level, pos, state, profiler);
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<CompressingRecipe> recipe) {
        return GCMachineStatuses.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG.electricCompressorEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extract(Galacticraft.CONFIG.electricCompressorEnergyConsumptionRate());
    }

    @Override
    public int getProcessingTime(@NotNull RecipeHolder<CompressingRecipe> recipe) {
        return recipe.value().getTime();
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new RecipeMachineMenu<>(
                GCMenuTypes.ELECTRIC_COMPRESSOR,
                syncId,
                player,
                this
        );
    }

    @Override
    protected CraftingInput craftingInv() {
        return RecipeHelper.craftingInput(3, 3, this.inputSlots.getSlots());
    }
}