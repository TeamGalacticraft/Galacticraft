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
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipe.CompressingRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
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
public class CompressorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    public int fuelTime;
    public int maxFuelTime;
    public int progress;

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.COMPRESSOR_TYPE);
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return false;
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    @Override
    public int getInventorySize() {
        return 11;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return AbstractFurnaceBlockEntity::canUseAsFuel;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public int getMaxEnergy() {
        return 0;
    }

    @Override
    public void tick() {
        if (this.disabled()) {
            return;
        }
        InventoryWrapper inv = new InventoryWrapper() {
            @Override
            public InventoryComponent getComponent() {
                return getInventory();
            }

            @Override
            public int size() {
                return 9;
            }
        };

        if (this.fuelTime <= 0) {
            ItemStack fuel = getInventory().getStack(FUEL_INPUT_SLOT);
            if (fuel.isEmpty()) {
                // Machine out of fuel and no fuel present.
                setStatus(Status.MISSING_FUEL);
                progress = 0;
                return;
            } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
                this.maxFuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().get(fuel.getItem());
                this.fuelTime = maxFuelTime;
                decrement(FUEL_INPUT_SLOT, 1);
                setStatus(Status.PROCESSING);
            } else {
                // Can't start processing any new materials anyway, don't waste fuel.
                setStatus(Status.OUTPUT_FULL);
                progress = 0;
                return;
            }
        }
        this.fuelTime--;


        if (getStatus() == Status.PROCESSING && !isValidRecipe(inv)) {
            setStatus(Status.INVALID_RECIPE);
        }

        if (getStatus() == Status.PROCESSING && isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
            ItemStack resultStack = getResultFromRecipeStack(inv);
            this.progress++;

            if (this.progress % 40 == 0 && this.progress > maxProgress / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.progress == maxProgress) {
                this.progress = 0;

                craftItem(resultStack);
            }
        }
    }

    private void craftItem(ItemStack craftingResult) {
        for (int i = 0; i < 9; i++) {
            decrement(i, 1);
        }
        insert(OUTPUT_SLOT, craftingResult);
    }

    private ItemStack getResultFromRecipeStack(Inventory inv) {
        // Once this method has been called, we have verified that either a shapeless or shaped recipe is present with isValidRecipe. Ignore the warning on getShapedRecipe(inv).get().
        return getRecipe(inv).orElseThrow(() -> new IllegalStateException("A recipe was not present when getResultFromRecipeStack was called. This should never happen, as isValidRecipe should have been called first. That would have prevented this.")).craft(inv);
    }

    private Optional<CompressingRecipe> getRecipe(Inventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, input, this.world);
    }

    protected boolean canPutStackInResultSlot(ItemStack stack) {
        return canInsert(OUTPUT_SLOT, stack);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public boolean isValidRecipe(Inventory input) {
        return getRecipe(input).isPresent();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("Progress", this.progress);

        tag.putInt("FuelTime", this.fuelTime);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.progress = tag.getInt("Progress");

        this.fuelTime = tag.getInt("FuelTime");

    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return false;
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
         * Compressor has no fuel.
         */
        MISSING_FUEL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.missing_fuel"), Formatting.RED, StatusType.MISSING_ENERGY);

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
