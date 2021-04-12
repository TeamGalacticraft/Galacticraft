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
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.FabricationRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.CircuitFabricatorScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int MAX_PROGRESS = 300;

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT_DIAMOND = 1;
    public static final int INPUT_SLOT_SILICON = 2;
    public static final int INPUT_SLOT_SILICON_2 = 3;
    public static final int INPUT_SLOT_REDSTONE = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private static final ItemFilter[] SLOT_FILTERS;
    private final Container recipeSlotInv = new InventoryFixedWrapper(this.getInventory().getMappedInv(INPUT_SLOT)) {
        @Override
        public boolean stillValid(Player player) {
            return getWrappedInventory().stillValid(player);
        }
    };

    static {
        SLOT_FILTERS = new ItemFilter[5];
        SLOT_FILTERS[CHARGE_SLOT] = EnergyUtils.IS_EXTRACTABLE;
        SLOT_FILTERS[INPUT_SLOT_DIAMOND] = stack -> stack.getItem() == Items.DIAMOND;
        SLOT_FILTERS[INPUT_SLOT_SILICON] = stack -> stack.getItem() == GalacticraftItems.RAW_SILICON;
        SLOT_FILTERS[INPUT_SLOT_SILICON_2] = stack -> stack.getItem() == GalacticraftItems.RAW_SILICON;
        SLOT_FILTERS[INPUT_SLOT_REDSTONE] = stack -> stack.getItem() == Items.REDSTONE;
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
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == INPUT_SLOT) return stack -> this.getRecipe(new SimpleContainer(stack)).isPresent();
        if (slot == OUTPUT_SLOT) return ConstantItemFilter.ANYTHING;

        return SLOT_FILTERS[slot];
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        for (int i = 1; i < 6; i++) {
            if (!this.getFilterForSlot(i).matches(getInventory().getInvStack(i))) {
                return Status.NOT_ENOUGH_RESOURCES;
            }
        }
        Optional<FabricationRecipe> recipe = this.getRecipe(recipeSlotInv);
        if (recipe.isPresent() && !this.canInsert(OUTPUT_SLOT, recipe.get().getResultItem())) return Status.FULL;
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
            if (this.getInventory().extractStack(INPUT_SLOT_DIAMOND, this.getFilterForSlot(INPUT_SLOT_DIAMOND), ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
            if (this.getInventory().extractStack(INPUT_SLOT_SILICON, this.getFilterForSlot(INPUT_SLOT_SILICON), ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
            if (this.getInventory().extractStack(INPUT_SLOT_SILICON_2, this.getFilterForSlot(INPUT_SLOT_SILICON_2), ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
            if (this.getInventory().extractStack(INPUT_SLOT_REDSTONE, this.getFilterForSlot(INPUT_SLOT_REDSTONE), ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
            if (this.getInventory().extractStack(INPUT_SLOT, this.getFilterForSlot(INPUT_SLOT), ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
            this.progress = 0;
            this.getInventory().insertStack(OUTPUT_SLOT, this.getRecipe(recipeSlotInv).orElseThrow(RuntimeException::new).getResultItem().copy(), Simulation.ACTION);
        }
    }

    private Optional<FabricationRecipe> getRecipe(Container input) {
        if (this.level == null) return Optional.empty();
        return this.level.getRecipeManager().getRecipeFor(GalacticraftRecipes.FABRICATION_TYPE, input, this.level);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return MAX_PROGRESS;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("Progress", this.progress);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new CircuitFabricatorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Fabricator is active and is processing.
         */
        PROCESSING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.processing"), ChatFormatting.GREEN, StatusType.WORKING),

        /**
         * Fabricator output slot is full.
         */
        FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Fabricator does not have the required resources to function.
         */
        NOT_ENOUGH_RESOURCES(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_items"), ChatFormatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * The fabricator has no energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.GRAY, StatusType.MISSING_ENERGY);

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