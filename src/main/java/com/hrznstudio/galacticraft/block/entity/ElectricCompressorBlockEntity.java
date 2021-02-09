/*
 * Copyright (c) 2020 HRZN LTD
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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipe.CompressingRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int CHARGE_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;
    private static final int MAX_PROGRESS = 200; // In ticks, 100/20 = 10 seconds

    private final InventoryWrapper craftingInv = new InventoryWrapper() {
        @Override
        public InventoryComponent getComponent() {
            return getInventory();
        }

        @Override
        public int size() {
            return 9;
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
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
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
                this.getInventory().insertStack(OUTPUT_SLOT, optional.get().getOutput().copy(), ActionType.TEST),
                ActionType.TEST).isEmpty()) return Status.OUTPUT_FULL;
        return Status.COMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.progress++ >= this.getMaxProgress()) {
                this.craftItem(this.getRecipe(this.craftingInv).orElseThrow(AssertionError::new).getOutput().copy());
                this.progress = 0;
            }
            if (this.progress % 40 == 0 && this.progress > this.getMaxProgress() / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
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
            ItemStack stack = getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            ItemStack res = craftingResult.copy();
            res.setCount(res.getCount() * 2);
            res = this.getInventory().insertStack(OUTPUT_SLOT, res, ActionType.TEST);
            res = this.getInventory().insertStack(SECOND_OUTPUT_SLOT, res, ActionType.TEST);
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
        this.getInventory().insertStack(SECOND_OUTPUT_SLOT, this.getInventory().insertStack(OUTPUT_SLOT, craftingResult, ActionType.PERFORM), ActionType.PERFORM);
    }

    private Optional<CompressingRecipe> getRecipe(Inventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, input, this.world);
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

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {

        /**
         * Compressor is compressing items.
         */
        COMPRESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.output_full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no items to process.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
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