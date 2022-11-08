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
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GCMenuTypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricCompressorBlockEntity extends RecipeMachineBlockEntity<Container, CompressingRecipe> {
    public static final int CHARGE_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;

    private final Container craftingInv = this.itemStorage().subInv(CHARGE_SLOT);

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ELECTRIC_COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        MachineItemStorage.Builder builder = MachineItemStorage.Builder.create();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(GCSlotGroups.GENERIC_INPUT, Constant.Filter.any(), true, ItemSlotDisplay.create(x * 18 + 30, y * 18 + 17));
            }
        }
        return builder.addSlot(GCSlotGroups.ENERGY_CHARGE, Constant.Filter.Item.CAN_EXTRACT_ENERGY, true, ItemSlotDisplay.create(8, 61))
                .addSlot(GCSlotGroups.GENERIC_OUTPUT, Constant.Filter.any(), false, ItemSlotDisplay.create(148, 22))
                .addSlot(GCSlotGroups.GENERIC_OUTPUT, Constant.Filter.any(), false, ItemSlotDisplay.create(148, 48))
                .build();
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
        return GCMachineStatus.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate(), context) != Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        return super.extractResourcesToWork(context);
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull CompressingRecipe recipe, TransactionContext transaction) {
        ItemStack output = recipe.getResultItem();
        ItemVariant variant = ItemVariant.of(output);
        long count = output.getCount() * 2;
        long outputted = this.itemStorage().insert(OUTPUT_SLOT, variant, count, transaction);
        if (outputted == count) return true;
        outputted += this.itemStorage().insert(SECOND_OUTPUT_SLOT, variant, count - outputted, transaction);
        return outputted == count;
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull CompressingRecipe recipe, TransactionContext transaction) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(this.craftingInv);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = remainder.get(i);
            this.itemStorage().extract(i, 1, transaction);

            if (!stack.isEmpty()) {
                if (this.itemStorage().getAmount(i) == 0) {
                    if (stack.getCount() == this.itemStorage().insert(i, ItemVariant.of(stack), stack.getCount(), transaction)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected int getProcessTime(@NotNull CompressingRecipe recipe) {
        return recipe.getTime();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return RecipeMachineMenu.create(
                    syncId,
                    player,
                    this,
                    GCMenuTypes.ELECTRIC_COMPRESSOR_HANDLER
            );
        }
        return null;
    }
}