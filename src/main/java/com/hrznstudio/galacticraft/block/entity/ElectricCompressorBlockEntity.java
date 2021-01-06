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
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    public int progress;

    public ElectricCompressorBlockEntity() {
        super(GalacticraftBlockEntities.ELECTRIC_COMPRESSOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 12;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    public void tick() {
        if (disabled() || world.isClient) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }

        Inventory inv = new InventoryWrapper() {
            @Override
            public InventoryComponent getComponent() {
                return getInventory();
            }

            @Override
            public int size() {
                return 9;
            }
        };

        attemptChargeFromStack(FUEL_INPUT_SLOT);
        if (getCapacitor().getCurrentEnergy() < 1) {
            setStatus(Status.NOT_ENOUGH_ENERGY);
        } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
            setStatus(Status.PROCESSING);
        } else {
            if (isValidRecipe(inv)) setStatus(Status.OUTPUT_FULL);
            else setStatus(Status.INVALID_RECIPE);
        }

        if (getStatus() == Status.PROCESSING) {
            ItemStack resultStack = getResultFromRecipeStack(inv);
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
            this.progress++;

            if (this.progress % 40 == 0 && this.progress > maxProgress / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.progress == maxProgress) {
                this.progress = 0;

                craftItem(resultStack);
            }
        } else if (!getStatus().getType().isActive()) {
            if (progress > 0) {
                progress--;
            }
        } else {
            idleEnergyDecrement(false);
            if (progress > 0) {
                progress--;
            }
        }

        trySpreadEnergy();
    }

    protected void craftItem(ItemStack craftingResult) {
        boolean canCraftTwo = true;

        for (int i = 0; i < 9; i++) {
            ItemStack item = getInventory().getStack(i);

            // If slot is not empty ( must be an ingredient if we've made it this far ), and there is less than 2 items in the slot, we cannot craft two.
            if (!item.isEmpty() && item.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            if (getInventory().getStack(OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount() || getInventory().getStack(SECOND_OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount()) {
                // There would be too many items in the output slot. Just craft one.
                canCraftTwo = false;
            }
        }

        for (int i = 0; i < 9; i++) {
            decrement(i, canCraftTwo ? 2 : 1);
        }

        // <= because otherwise it loops only once and puts in only one slot
        for (int i = OUTPUT_SLOT; i <= SECOND_OUTPUT_SLOT; i++) {
            insert(i, craftingResult);
        }
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(this.getCachedState(), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }


    public ItemStack getResultFromRecipeStack(Inventory inv) {
        // Once this method has been called, we have verified that either a shapeless or shaped recipe is present with isValidRecipe. Ignore the warning on getShapedRecipe(inv).get().
        return getRecipe(inv).orElseThrow(() -> new IllegalStateException("A recipe was not present when getResultFromRecipeStack was called. This should never happen, as isValidRecipe should have been called first. That would have prevented this.")).craft(inv);
    }

    private Optional<CompressingRecipe> getRecipe(Inventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, input, this.world);
    }

    protected boolean canPutStackInResultSlot(ItemStack stack) {
        return canInsert(OUTPUT_SLOT, stack);
    }

    public boolean isValidRecipe(Inventory input) {
        Optional<CompressingRecipe> reciper = getRecipe(input);

        return reciper.isPresent();
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return slot == OUTPUT_SLOT || slot == SECOND_OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return !(slot == OUTPUT_SLOT || slot == SECOND_OUTPUT_SLOT);
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {

        /**
         * Compressor is compressing items.
         */
        PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

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