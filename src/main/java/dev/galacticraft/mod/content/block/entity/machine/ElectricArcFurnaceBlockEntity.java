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
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElectricArcFurnaceBlockEntity extends BasicRecipeMachineBlockEntity<SingleRecipeInput, BlastingRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOTS = 2;
    public static final int OUTPUT_LENGTH = 2;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(44, 35),
                    ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(108, 35),
                    ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(134, 35)
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.electricArcFurnaceEnergyConsumptionRate() * 2,
                    0
            )
    );

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ELECTRIC_ARC_FURNACE, pos, state, RecipeType.BLASTING, SPEC, INPUT_SLOT, 1, OUTPUT_SLOTS, OUTPUT_LENGTH);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromSlot(CHARGE_SLOT);
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<BlastingRecipe> recipe) {
        return MachineStatuses.ACTIVE;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG.electricArcFurnaceEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extract(Galacticraft.CONFIG.electricArcFurnaceEnergyConsumptionRate());
    }

    @Override
    public int getProcessingTime(@NotNull RecipeHolder<BlastingRecipe> recipe) {
        return (int) (recipe.value().getCookingTime() * 0.9);
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new RecipeMachineMenu<>(
                GCMenuTypes.ELECTRIC_ARC_FURNACE,
                syncId,
                (ServerPlayer) player,
                this
        );
    }

    @Override
    protected SingleRecipeInput craftingInv() {
        assert this.inputSlots.size() == 1;
        return RecipeHelper.single(this.inputSlots.slot(0));
    }
}
