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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipe.CompressingRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;

    private static final int MAX_PROGRESS = 200; // In ticks, 100/20 = 10 seconds
    private final Container craftingInv;
    public int fuelTime;
    public int fuelLength;
    public int progress;

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.COMPRESSOR_TYPE);
        this.craftingInv = new InventoryFixedWrapper(getInventory().getSubInv(0, 9)) {
            @Override
            public boolean stillValid(Player player) {
                return getWrappedInventory().stillValid(player);
            }
        };
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public int getInventorySize() {
        return 11;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return stack -> FuelRegistry.INSTANCE.get(stack.getItem()) != null;
        }
        return ConstantItemFilter.ANYTHING;
    }

    @Override
    public int getEnergyCapacity() {
        return 0;
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<CompressingRecipe> optional = this.getRecipe(this.craftingInv);
        if ((this.fuelLength > 0 || !this.getInventory().getInvStack(FUEL_INPUT_SLOT).isEmpty()) && optional.isPresent()) {
            if (this.canInsert(OUTPUT_SLOT, optional.get().getResultItem())) {
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
                this.craftItem(this.getRecipe(craftingInv).orElseThrow(AssertionError::new).getResultItem().copy());
            }
            if (this.progress % 40 == 0 && this.progress > this.getMaxProgress() / 2) {
                this.level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
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

    private Optional<CompressingRecipe> getRecipe(Container input) {
        if (this.level == null) return Optional.empty();
        return this.level.getRecipeManager().getRecipeFor(GalacticraftRecipes.COMPRESSING_TYPE, input, this.level);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return CompressorBlockEntity.MAX_PROGRESS;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Progress", this.progress);
        tag.putInt("FuelTime", this.fuelTime);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.progress = tag.getInt("Progress");
        this.fuelTime = tag.getInt("FuelTime");
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot < 9;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
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
        PROCESSING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.active"), ChatFormatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_items"), ChatFormatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.output_full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no fuel.
         */
        MISSING_FUEL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.missing_fuel"), ChatFormatting.RED, StatusType.MISSING_ENERGY);

        private final Component text;
        private final StatusType type;

        Status(TranslatableComponent text, ChatFormatting color, StatusType type) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
            this.type = type;
        }

        @Override
        public @NotNull Component getName() {
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
