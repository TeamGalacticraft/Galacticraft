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
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.ELECTRIC_ARC_FURNACE, pos, state, RecipeType.BLASTING);
        this.craftingInv = this.itemStorage().getCraftingView(GCSlotGroupTypes.GENERIC_INPUT);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromStack(GCSlotGroupTypes.ENERGY_TO_SELF);
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected void outputStacks(@NotNull BlastingRecipe recipe) {
        ItemStack output = recipe.getResultItem(this.level.registryAccess());
        if (!output.isEmpty()) {
            this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).insert(output.getItem(), output.getTag(), output.getCount() * 2L);
        }
    }

    @Override
    protected boolean canOutputStacks(@NotNull BlastingRecipe recipe) {
        ItemStack output = recipe.getResultItem(this.level.registryAccess());
        return output.isEmpty() || this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).canInsert(output.getItem(), output.getTag(), output.getCount() * 2L);
    }

    @Override
    protected void extractCraftingMaterials(@NotNull BlastingRecipe recipe) {
        this.itemStorage().getSlot(GCSlotGroupTypes.GENERIC_INPUT).extractOne();
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatuses.ACTIVE;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate());
    }

    @Override
    protected int getProcessTime(@NotNull BlastingRecipe recipe) {
        return (int) (recipe.getCookingTime() * 0.9);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new RecipeMachineMenu<>(
                    syncId,
                    (ServerPlayer) player,
                    this
            );
        }
        return null;
    }
}
