/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.CompressorScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CompressorBlockEntity extends RecipeMachineBlockEntity<Inventory, CompressingRecipe> {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;

    private final Inventory craftingInv = new InventoryFixedWrapper(this.itemInv().getSubInv(0, 9)) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return getWrappedInventory().canPlayerUse(player);
        }
    };
    private final FixedItemInv outputInv = this.itemInv().getSubInv(OUTPUT_SLOT, OUTPUT_SLOT + 1);
    public int fuelTime;
    public int fuelLength;

    public CompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE, CompressingRecipe::getTime);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(y * 3 + x, SlotType.INPUT, ConstantItemFilter.ANYTHING, x * 18 + 19, y * 18 + 18);
            }
        }
        builder.addSlot(FUEL_INPUT_SLOT, SlotType.FUEL_OUT, stack -> FuelRegistry.INSTANCE.get(stack.getItem()) != null, 79, 49);
        builder.addSlot(OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(138, 38));
        return builder;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public int getEnergyCapacity() {
        return 0;
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (this.recipe() == null) return Status.INVALID_RECIPE;
        if (!this.canCraft(this.recipe())) return Status.OUTPUT_FULL;
        if (this.fuelTime <= 0) return Status.MISSING_FUEL;
        return Status.PROCESSING;
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    public @NotNull FixedItemInv outputInv() {
        return this.outputInv;
    }

    @Override
    public void tickWork() {
        if (this.getStatus() == Status.MISSING_FUEL) {
            if (this.fuelLength == 0) {
                Integer integer = FuelRegistry.INSTANCE.get(this.itemInv().extractStack(FUEL_INPUT_SLOT, null, ItemStack.EMPTY, 1, Simulation.ACTION).getItem());
                if (integer != null) {
                    this.fuelTime = this.fuelLength = integer;
                }
            }
            this.setStatus(this.updateStatus());
        }
        if (--this.fuelTime <= 0) {
            this.fuelLength = 0;
            this.fuelTime = 0;
        }

        super.tickWork();
        if (this.getStatus().getType().isActive() && this.maxProgress() > 0) {
            if (this.progress() % (this.maxProgress() / 8) == 0 && this.progress() > this.maxProgress() / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    protected void craft(CompressingRecipe recipe) {
        super.craft(recipe);
        recipe = this.recipe();
        if (this.canCraft(recipe)) {
            super.craft(recipe);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.fuelTime = tag.getInt(Constant.Nbt.FUEL_TIME);
        this.fuelLength = tag.getInt(Constant.Nbt.FUEL_LENGTH);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new CompressorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Compressor is compressing items.
         */
        PROCESSING(new TranslatableText("ui.galacticraft.machine.status.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft.machine.status.output_full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no fuel.
         */
        MISSING_FUEL(new TranslatableText("ui.galacticraft.machine.status.missing_fuel"), Formatting.RED, StatusType.MISSING_ENERGY);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
            this.type = type;
        }

        @Override
        public @NotNull Text getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}
