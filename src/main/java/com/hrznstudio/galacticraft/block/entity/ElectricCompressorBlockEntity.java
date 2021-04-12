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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipe.CompressingRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.ElectricCompressorScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
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

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int CHARGE_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;
    private static final int MAX_PROGRESS = 200; // In ticks, 100/20 = 10 seconds

    private final Container craftingInv = new InventoryFixedWrapper(getInventory().getSubInv(0, 9)) {
        @Override
        public boolean stillValid(Player player) {
            return getWrappedInventory().stillValid(player);
        }
    };

    public int progress;

    public ElectricCompressorBlockEntity() {
        super(GalacticraftBlockEntities.ELECTRIC_COMPRESSOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 12;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return MAX_PROGRESS;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) {
            return EnergyUtils.IS_EXTRACTABLE;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<CompressingRecipe> optional = this.getRecipe(this.craftingInv);
        if (!optional.isPresent()) return Status.INVALID_RECIPE;
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (!this.getInventory().insertStack(SECOND_OUTPUT_SLOT,
                this.getInventory().insertStack(OUTPUT_SLOT, optional.get().getResultItem().copy(), Simulation.SIMULATE),
                Simulation.SIMULATE).isEmpty()) return Status.OUTPUT_FULL;
        return Status.COMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.progress++ >= this.getMaxProgress()) {
                this.craftItem(this.getRecipe(this.craftingInv).orElseThrow(AssertionError::new).getResultItem().copy());
                this.progress = 0;
            }
            if (this.progress % 40 == 0 && this.progress > this.getMaxProgress() / 2) {
                this.level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        } else {
            if (this.getProgress() > 0) {
                this.progress--;
            }
        }
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    protected void craftItem(ItemStack craftingResult) {
        boolean canCraftTwo = true;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = getInventory().getInvStack(i);
            if (!stack.isEmpty() && stack.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            ItemStack res = craftingResult.copy();
            res.setCount(res.getCount() * 2);
            res = this.getInventory().insertStack(OUTPUT_SLOT, res, Simulation.SIMULATE);
            res = this.getInventory().insertStack(SECOND_OUTPUT_SLOT, res, Simulation.SIMULATE);
            if (!res.isEmpty()) {
                canCraftTwo = false;
            }
        }
        if (canCraftTwo) {
            craftingResult = craftingResult.copy();
            craftingResult.setCount(craftingResult.getCount() * 2);
        }

        for (int i = 0; i < 9; i++) {
            this.decrement(i, canCraftTwo ? 2 : 1);
        }
        this.getInventory().insertStack(SECOND_OUTPUT_SLOT, this.getInventory().insertStack(OUTPUT_SLOT, craftingResult, Simulation.ACTION), Simulation.ACTION);
    }

    private Optional<CompressingRecipe> getRecipe(Container input) {
        if (this.level == null) return Optional.empty();
        return this.level.getRecipeManager().getRecipeFor(GalacticraftRecipes.COMPRESSING_TYPE, input, this.level);
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT || slot == SECOND_OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return !(slot == OUTPUT_SLOT || slot == SECOND_OUTPUT_SLOT);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new ElectricCompressorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {

        /**
         * Compressor is compressing items.
         */
        COMPRESSING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.active"), ChatFormatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_items"), ChatFormatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.output_full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no items to process.
         */
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY);

        private final Component text;
        private final StatusType type;

        Status(TranslatableComponent text, ChatFormatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
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