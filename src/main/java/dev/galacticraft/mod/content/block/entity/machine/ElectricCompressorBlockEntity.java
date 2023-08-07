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
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricCompressorBlockEntity extends RecipeMachineBlockEntity<Container, CompressingRecipe> {
    private final Container craftingInv;

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.ELECTRIC_COMPRESSOR, pos, state, GCRecipes.COMPRESSING_TYPE);
        this.craftingInv = this.itemStorage().getCraftingView(GCSlotGroupTypes.GENERIC_INPUT);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromStack(GCSlotGroupTypes.ENERGY_TO_SELF);
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.getStatus().type().isActive() && this.getMaxProgress() > 0) {
            if (this.getProgress() % (this.getMaxProgress() / 5) == 0 && this.getProgress() > this.getMaxProgress() / 2) {
                world.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        return super.tick(world, pos, state, profiler);
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatuses.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate());
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected void outputStacks(@NotNull CompressingRecipe recipe) {
        this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).insertStack(recipe.getResultItem(this.level.registryAccess()));
    }

    @Override
    protected boolean canOutputStacks(@NotNull CompressingRecipe recipe) {
        return this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).canInsertStack(recipe.getResultItem(this.level.registryAccess()));
    }

    @Override
    protected void extractCraftingMaterials(@NotNull CompressingRecipe recipe) {
        NonNullList<ItemStack> remainders = recipe.getRemainingItems(this.craftingInv);
        SlotGroup<Item, ItemStack, ItemResourceSlot> group = this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_INPUT);

        for (int i = 0; i < 9; i++) {
            group.extract(i, 1);

            ItemStack remainder = remainders.get(i);
            if (!remainder.isEmpty()) {
                if (group.getAmount(i) == 0) {
                    group.insert(i, remainder.getItem(), remainder.getTag(), remainder.getCount());
                }
            }
        }
    }

    @Override
    protected int getProcessTime(@NotNull CompressingRecipe recipe) {
        return recipe.getTime();
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