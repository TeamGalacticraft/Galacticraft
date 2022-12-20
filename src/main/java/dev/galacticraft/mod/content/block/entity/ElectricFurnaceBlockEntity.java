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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.screen.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroups;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricFurnaceBlockEntity extends RecipeMachineBlockEntity<Container, SmeltingRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    
    private final @NotNull Container craftingInv = this.itemStorage().subInv(INPUT_SLOT, 1);

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GCSlotGroups.ENERGY_CHARGE, Constant.Filter.Item.CAN_EXTRACT_ENERGY, true, ItemSlotDisplay.create(8, 61))
                .addSlot(GCSlotGroups.GENERIC_INPUT, Constant.Filter.any(), true, ItemSlotDisplay.create(52, 35))
                .addSlot(GCSlotGroups.GENERIC_OUTPUT, Constant.Filter.any(), false, ItemSlotDisplay.create(113, 35))
                .build();
    }

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ELECTRIC_FURNACE, pos, state, RecipeType.SMELTING);
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    public boolean canExposedInsertEnergy() {
        return true;
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
    protected boolean outputStacks(@NotNull SmeltingRecipe recipe, TransactionContext transaction) {
        ItemStack output = recipe.getResultItem();
        return this.itemStorage().insert(OUTPUT_SLOT, ItemVariant.of(output), output.getCount(), transaction) == output.getCount();
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull SmeltingRecipe recipe, TransactionContext transaction) {
        return recipe.getIngredients().get(0).test(this.itemStorage().extract(INPUT_SLOT, 1, transaction));
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatus.ACTIVE;
    }

    @Override
    protected int getProcessTime(@NotNull SmeltingRecipe recipe) {
        return recipe.getCookingTime();
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate(), context) != Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        return super.extractResourcesToWork(context);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return RecipeMachineMenu.create(
                    syncId,
                    player,
                    this,
                    GCMenuTypes.ELECTRIC_FURNACE_HANDLER
            );
        }
        return null;
    }
}