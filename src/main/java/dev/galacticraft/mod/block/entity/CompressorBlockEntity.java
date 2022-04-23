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
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.CompressorScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
public class CompressorBlockEntity extends RecipeMachineBlockEntity<Inventory, CompressingRecipe> {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;

    private final Inventory craftingInv = this.itemStorage().subInv(0, FUEL_INPUT_SLOT);
    public int fuelTime;
    public int fuelLength;

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        MachineItemStorage.Builder builder = MachineItemStorage.Builder.create();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(GalacticraftSlotTypes.ITEM_INPUT, new ItemSlotDisplay(x * 18 + 17, y * 18 + 17));
            }
        }
        return builder
                .addSlot(GalacticraftSlotTypes.SOLID_FUEL, new ItemSlotDisplay(83, 47))
                .addSlot(GalacticraftSlotTypes.ITEM_OUTPUT, new ItemSlotDisplay(143, 36))
                .build();
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GalacticraftMachineStatus.COMPRESSING;
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(@NotNull CompressingRecipe recipe, @NotNull TransactionContext context) {
        ItemStack output = recipe.getOutput();
        return this.itemStorage().insert(OUTPUT_SLOT, ItemVariant.of(output), output.getCount(), context) == output.getCount();
    }

    @Override
    protected boolean extractCraftingMaterials(@NotNull CompressingRecipe recipe, @NotNull TransactionContext context) {
        DefaultedList<ItemStack> remainder = recipe.getRemainder(this.craftingInv);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = remainder.get(i);
            this.itemStorage().extract(i, 1, context);
            if (stack != ItemStack.EMPTY) {
                if (this.itemStorage().getAmount(i) == 0) {
                    if (stack.getCount() != this.itemStorage().insert(i, ItemVariant.of(stack), stack.getCount(), context)) {
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
    protected void tickConstant(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        if (--this.fuelTime <= 0) {
            this.fuelLength = 0;
            this.fuelTime = 0;
        }
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this.getMaxProgress() > 0) {
            if (this.getProgress() % (this.getMaxProgress() / 8) == 0 && this.getProgress() > this.getMaxProgress() / 2) {
                world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

        return super.tick(world, pos, state);
    }

    @Override
    protected @Nullable MachineStatus extractResourcesToWork(@NotNull TransactionContext context) {
        if (this.fuelLength == 0) {
            ItemStack stack = this.itemStorage().extract(FUEL_INPUT_SLOT, 1, context);
            Integer integer = FuelRegistry.INSTANCE.get(stack.getItem());
            if (integer != null && integer > 0) {
                this.fuelTime = this.fuelLength = integer;
            }
        }
        return this.fuelLength == 0 ? GalacticraftMachineStatus.NO_FUEL : super.extractResourcesToWork(context);
    }

    @Override
    protected int getProcessTime(@NotNull CompressingRecipe recipe) {
        return recipe.getTime();
    }

    @Override
    public void writeNbt(@NotNull NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);
        this.fuelTime = nbt.getInt(Constant.Nbt.FUEL_TIME);
        this.fuelLength = nbt.getInt(Constant.Nbt.FUEL_LENGTH);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new CompressorScreenHandler(syncId, player, this);
        return null;
    }
}
