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
import com.hrznstudio.galacticraft.screen.ElectricArcFurnaceScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ElectricArcFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public int cookTime = 0;
    public int cookLength = 0;
    private final Container subInv = new InventoryFixedWrapper(this.getInventory().getMappedInv(INPUT_SLOT)) {
        @Override
        public boolean stillValid(Player player) {
            return getWrappedInventory().stillValid(player);
        }
    };

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    public ElectricArcFurnaceBlockEntity(BlockEntityType<? extends ElectricArcFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    public ElectricArcFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_ARC_FURNACE_TYPE);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().electricArcFurnaceEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot == INPUT_SLOT;
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) return EnergyUtils::isEnergyExtractable;
        if (slot == INPUT_SLOT) return (stack) -> level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).isPresent();
        return (slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2) ? ConstantItemFilter.ANYTHING : ConstantItemFilter.NOTHING;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<SmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, subInv, this.level);
        if (!recipe.isPresent()) return Status.NOT_ENOUGH_ITEMS;
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (!this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1, 
                recipe.get().getResultItem().copy(), Simulation.SIMULATE), Simulation.SIMULATE).isEmpty()) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.cookLength == 0) {
                SmeltingRecipe recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, subInv, this.level).orElseThrow(AssertionError::new);
                this.cookLength = (int) (recipe.getCookingTime() * 0.8F);
                this.cookTime = 0;
            }
            if (this.cookTime++ >= this.cookLength) {
                SmeltingRecipe recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, subInv, this.level).orElseThrow(AssertionError::new);
                if (this.getInventory().extractStack(INPUT_SLOT, null, ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
                this.cookTime = 0;
                this.cookLength = 0;
                if (this.level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, subInv, this.level).isPresent()) this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1, recipe.getResultItem().copy(), Simulation.ACTION), Simulation.ACTION);
                this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1,
                        recipe.getResultItem().copy(), Simulation.ACTION), Simulation.ACTION);

            }
        } else {
            if (this.cookTime > 0) this.cookTime--;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new ElectricArcFurnaceScreenHandler(syncId, player, this);
        return null;
    }

    private enum Status implements MachineStatus {
        /**
         * The electric arc furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.active"), ChatFormatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        OUTPUT_FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.full"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * There are no valid items to smelt/cook.
         */
        NOT_ENOUGH_ITEMS(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_items"), ChatFormatting.GRAY, StatusType.MISSING_ITEMS),

        /**
         * The electric arc furnace has no more energy
         */
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY);

        private final Component name;
        private final StatusType type;

        Status(MutableComponent name, ChatFormatting color, StatusType type) {
            this.type = type;
            this.name = name.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Component getName() {
            return name;
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
