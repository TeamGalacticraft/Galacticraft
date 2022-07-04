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
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricArcFurnaceBlockEntity extends RecipeMachineBlockEntity<Inventory, BlastingRecipe> {
    private final @NotNull Inventory craftingInv = this.itemStorage().subInv(INPUT_SLOT, 1);

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 61))
                .addSlot(GalacticraftSlotTypes.ITEM_INPUT, new ItemSlotDisplay(44, 35))
                .addSlot(GalacticraftSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(108, 35))
                .addSlot(GalacticraftSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(134, 35))
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
    protected void tickConstant(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull BlastingRecipe recipe, @NotNull TransactionContext transaction) {
        ItemStack output = recipe.getOutput();
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
        return GalacticraftMachineStatus.ACTIVE;
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull BlastingRecipe recipe, @NotNull TransactionContext transaction) {
        return !this.itemStorage().extract(INPUT_SLOT, 1, transaction).isEmpty();
    }

    @Override
    protected int getProcessTime(@NotNull BlastingRecipe recipe) {
        return (int) (recipe.getCookTime() * 0.9);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) {
            return RecipeMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GalacticraftScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER
            );
        }
        return null;
    }
}
