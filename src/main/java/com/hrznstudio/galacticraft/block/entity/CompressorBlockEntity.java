/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.attribute.item.MachineItemInv;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipe.CompressingRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlockEntity extends MachineBlockEntity {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;

    private static final int MAX_PROGRESS = 200; // In ticks, 100/20 = 10 seconds
    private final Inventory craftingInv;
    public int fuelTime;
    public int fuelLength;
    public int progress;

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.COMPRESSOR_TYPE);
        this.craftingInv = new InventoryFixedWrapper(this.getInventory().getSubInv(0, 9)) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return getWrappedInventory().canPlayerUse(player);
            }
        };

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.getInventory().addSlot(y * 3 + x, SlotType.INPUT, ConstantItemFilter.ANYTHING, x * 18 + 19, y * 18 + 18);
            }
        }

        // Fuel slot
        this.getInventory().addSlot(FUEL_INPUT_SLOT, SlotType.FUEL_OUT, stack -> FuelRegistry.INSTANCE.get(stack.getItem()) != null, 3 * 18 + 1, 75);

        // Output slot
        this.getInventory().addSlot(OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(138, 38));
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
        Optional<CompressingRecipe> optional = this.getRecipe(this.craftingInv);
        if ((this.fuelLength > 0 || !this.getInventory().getInvStack(FUEL_INPUT_SLOT).isEmpty()) && optional.isPresent()) {
            if (this.canInsert(OUTPUT_SLOT, optional.get().getOutput())) {
                return Status.PROCESSING;
            } else {
                return Status.OUTPUT_FULL;
            }
        }
        if (!optional.isPresent()) return Status.INVALID_RECIPE;
        return Status.MISSING_FUEL;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.fuelLength == 0) {
                this.fuelLength = FuelRegistry.INSTANCE.get(this.getInventory().extractStack(FUEL_INPUT_SLOT, null, ItemStack.EMPTY, 1, Simulation.ACTION).getItem());
                this.fuelTime = this.fuelLength;
                if (this.fuelLength == 0) return;
            }
            if (this.fuelTime-- <= 0) {
                this.fuelLength = 0;
                this.fuelTime = 0;
            }
            if (this.progress++ >= this.getMaxProgress()) {
                this.progress = 0;
                this.craftItem(this.getRecipe(craftingInv).orElseThrow(AssertionError::new).getOutput().copy());
            }
            if (this.progress % 40 == 0 && this.progress > this.getMaxProgress() / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
            }
        }
    }

    private void craftItem(ItemStack craftingResult) {
        for (int i = 0; i < 9; i++) {
            this.decrement(i, 1);
        }
        this.insert(OUTPUT_SLOT, craftingResult);
    }

    private Optional<CompressingRecipe> getRecipe(Inventory input) {
        if (this.world == null) return Optional.empty();
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, input, this.world);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return CompressorBlockEntity.MAX_PROGRESS;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt(Constants.Nbt.PROGRESS, this.progress);
        tag.putInt(Constants.Nbt.FUEL_TIME, this.fuelTime);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.progress = tag.getInt(Constants.Nbt.PROGRESS);
        this.fuelTime = tag.getInt(Constants.Nbt.FUEL_TIME);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new CompressorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Compressor is compressing items.
         */
        PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machine.status.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableText("ui.galacticraft-rewoven.machine.status.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft-rewoven.machine.status.output_full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no fuel.
         */
        MISSING_FUEL(new TranslatableText("ui.galacticraft-rewoven.machine.status.missing_fuel"), Formatting.RED, StatusType.MISSING_ENERGY);

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
