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
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.screen.GCMenuTypes;
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
    private final @NotNull Container craftingInv;

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.ELECTRIC_ARC_FURNACE, pos, state, RecipeType.BLASTING);
        craftingInv = this.itemStorage().getCraftingView(GCSlotGroupTypes.GENERIC_INPUT);
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
            return RecipeMachineMenu.create(
                    syncId,
                    player,
                    this,
                    GCMenuTypes.ELECTRIC_ARC_FURNACE
            );
        }
        return null;
    }
}
