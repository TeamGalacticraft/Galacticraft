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
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.screen.RecipeMachineScreenHandler;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotTypes;
import dev.galacticraft.mod.screen.GCScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricArcFurnaceBlockEntity extends RecipeMachineBlockEntity<Container, BlastingRecipe> {
    private final @NotNull Container craftingInv = this.itemStorage().subInv(INPUT_SLOT, 1);

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GCSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 61))
                .addSlot(GCSlotTypes.ITEM_INPUT, new ItemSlotDisplay(44, 35))
                .addSlot(GCSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(108, 35))
                .addSlot(GCSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(134, 35))
                .build();
    }

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE, pos, state, RecipeType.BLASTING);
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate(), context) != Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        return super.extractResourcesToWork(context);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull BlastingRecipe recipe, @NotNull TransactionContext transaction) {
        ItemStack output = recipe.getResultItem();
        ItemVariant variant = ItemVariant.of(output);
        long count = output.getCount() * 2L;
        count -= this.itemStorage().insert(OUTPUT_SLOT_1, variant, count, transaction);
        if (count == 0) {
            return true;
        } else {
            count -= this.itemStorage().insert(OUTPUT_SLOT_2, variant, count, transaction);
            return count == 0;
        }
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatus.ACTIVE;
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull BlastingRecipe recipe, @NotNull TransactionContext transaction) {
        return !this.itemStorage().extract(INPUT_SLOT, 1, transaction).isEmpty();
    }

    @Override
    protected int getProcessTime(@NotNull BlastingRecipe recipe) {
        return (int) (recipe.getCookingTime() * 0.9);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return RecipeMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GCScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER
            );
        }
        return null;
    }
}
