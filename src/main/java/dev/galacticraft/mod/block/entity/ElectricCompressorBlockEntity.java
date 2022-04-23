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
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricCompressorBlockEntity extends RecipeMachineBlockEntity<Inventory, CompressingRecipe> {
    public static final int CHARGE_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;

    private final Inventory craftingInv = this.itemStorage().subInv(CHARGE_SLOT);

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ELECTRIC_COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        MachineItemStorage.Builder builder = MachineItemStorage.Builder.create();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(GalacticraftSlotTypes.ITEM_INPUT, new ItemSlotDisplay(x * 18 + 30, y * 18 + 17));
            }
        }
        return builder.addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 61))
                .addSlot(GalacticraftSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(148, 22))
                .addSlot(GalacticraftSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(148, 48))
                .build();
    }

    @Override
    protected void tickConstant(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this.getStatus().getType().isActive() && this.getMaxProgress() > 0) {
            if (this.getProgress() % (this.getMaxProgress() / 5) == 0 && this.getProgress() > this.getMaxProgress() / 2) {
                world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        return super.tick(world, pos, state);
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GalacticraftMachineStatus.COMPRESSING;
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate(), context) != Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate()) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
        return super.extractResourcesToWork(context);
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull CompressingRecipe recipe, TransactionContext transaction) {
        ItemStack output = recipe.getOutput();
        ItemVariant variant = ItemVariant.of(output);
        long count = output.getCount() * 2;
        long outputted = this.itemStorage().insert(OUTPUT_SLOT, variant, count, transaction);
        if (outputted == count) return true;
        outputted += this.itemStorage().insert(SECOND_OUTPUT_SLOT, variant, count - outputted, transaction);
        return outputted == count;
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull CompressingRecipe recipe, TransactionContext transaction) {
        DefaultedList<ItemStack> remainder = recipe.getRemainder(this.craftingInv);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = remainder.get(i);
            this.itemStorage().extract(i, 1);

            if (!stack.isEmpty()) {
                if (this.itemStorage().getAmount(i) == 0) {
                    if (stack.getCount() == this.itemStorage().insert(i, ItemVariant.of(stack), stack.getCount())) {
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
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) {
            return RecipeMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GalacticraftScreenHandlerType.ELECTRIC_COMPRESSOR_HANDLER
            );
        }
        return null;
    }
}