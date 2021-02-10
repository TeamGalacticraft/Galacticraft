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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.FabricationRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import io.github.cottonmc.component.api.ActionType;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
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
public class CircuitFabricatorBlockEntity extends ConfigurableMachineBlockEntity {
    private static final Item[] MANDATORY_MATERIALS = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};
    public static final int MAX_PROGRESS = 300;

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT_DIAMOND = 1;
    public static final int INPUT_SLOT_SILICON = 2;
    public static final int INPUT_SLOT_SILICON_2 = 3;
    public static final int INPUT_SLOT_REDSTONE = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private static final Predicate<ItemStack>[] SLOT_FILTERS;

    static {
        //noinspection unchecked
        SLOT_FILTERS = new Predicate[6];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = stack -> stack.getItem() == MANDATORY_MATERIALS[0];
        SLOT_FILTERS[2] = stack -> stack.getItem() == MANDATORY_MATERIALS[1];
        SLOT_FILTERS[3] = stack -> stack.getItem() == MANDATORY_MATERIALS[2];
        SLOT_FILTERS[4] = stack -> stack.getItem() == MANDATORY_MATERIALS[3];
        SLOT_FILTERS[5] = Constants.Misc.alwaysTrue();
    }


    public Status status = Status.NOT_ENOUGH_RESOURCES;
    public int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public int getInventorySize() {
        return 7;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 5) return stack -> this.getRecipe(new SimpleInventory(stack)).isPresent();
        if (slot == 6) return Constants.Misc.alwaysTrue();

        return SLOT_FILTERS[slot];
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        for (int i = 1; i < 6; i++) {
            if (!this.getFilterForSlot(i).test(getInventory().getStack(i))) {
                return Status.NOT_ENOUGH_RESOURCES;
            }
        }
        Optional<FabricationRecipe> recipe = this.getRecipe(new SimpleInventory(getWrappedInventory().getStack(INPUT_SLOT)));
        if (recipe.isPresent() && this.canInsert(OUTPUT_SLOT, recipe.get().getOutput())) return Status.FULL;
        return Status.PROCESSING;
    }

    @Override
    public void tickWork() {
        if (!this.getStatus().getType().isActive()) {
            if (this.progress > 0) progress--;
            return;
        }
        this.progress++;
        if (this.progress >= this.getMaxProgress()) {
            if (!this.getInventory().takeStack(INPUT_SLOT_DIAMOND, 1, ActionType.PERFORM).isEmpty()) return;
            if (!this.getInventory().takeStack(INPUT_SLOT_SILICON, 1, ActionType.PERFORM).isEmpty()) return;
            if (!this.getInventory().takeStack(INPUT_SLOT_SILICON_2, 1, ActionType.PERFORM).isEmpty()) return;
            if (!this.getInventory().takeStack(INPUT_SLOT_REDSTONE, 1, ActionType.PERFORM).isEmpty()) return;
            if (!this.getInventory().takeStack(INPUT_SLOT, 1, ActionType.PERFORM).isEmpty()) return;
            this.progress = 0;
            this.getInventory().insertStack(OUTPUT_SLOT, this.getRecipe(new SimpleInventory(getWrappedInventory().getStack(INPUT_SLOT))).orElse(null).getOutput().copy(), ActionType.PERFORM);
        }
    }

    private Optional<FabricationRecipe> getRecipe(Inventory input) {
        if (this.world == null) return Optional.empty();
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.FABRICATION_TYPE, input, this.world);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return MAX_PROGRESS;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Progress", this.progress);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        progress = tag.getInt("Progress");
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().circuitFabricatorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot != CHARGE_SLOT && slot != OUTPUT_SLOT;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Fabricator is active and is processing.
         */
        PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.processing"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Fabricator output slot is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Fabricator does not have the required resources to function.
         */
        NOT_ENOUGH_RESOURCES(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * The fabricator has no energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.GRAY, StatusType.MISSING_ENERGY);

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