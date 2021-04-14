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
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.SideOption;
import dev.galacticraft.mod.api.block.entity.ConfigurableMachineBlockEntity;
import dev.galacticraft.mod.entity.GalacticraftBlockEntities;
import dev.galacticraft.mod.screen.ElectricFurnaceScreenHandler;
import dev.galacticraft.mod.util.EnergyUtils;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    
    public int cookTime = 0;
    public int cookLength = 0;
    private final Inventory craftingInv = new InventoryFixedWrapper(this.getInventory().getMappedInv(INPUT_SLOT)) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return getWrappedInventory().canPlayerUse(player);
        }
    };

    public ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    public ElectricFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_FURNACE_TYPE);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot == INPUT_SLOT;
    }

    @Override
    public int getInventorySize() {
        return 3;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) return EnergyUtils::isEnergyExtractable;
        if (slot == INPUT_SLOT) return stack -> this.getRecipe(RecipeType.SMELTING, new SimpleInventory(stack)).isPresent();
        return ConstantItemFilter.NOTHING;
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<SmeltingRecipe> recipe = this.getRecipe(RecipeType.SMELTING, craftingInv);
        if (!recipe.isPresent()) return Status.NOT_ENOUGH_ITEMS;
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (!this.canInsert(OUTPUT_SLOT, recipe.get())) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.cookLength == 0) {
                SmeltingRecipe recipe = this.getRecipe(RecipeType.SMELTING, craftingInv).orElseThrow(AssertionError::new);
                this.cookLength = (int) (recipe.getCookTime() * 0.8F);
                this.cookTime = 0;
            }
            if (this.cookTime++ >= this.cookLength) {
                SmeltingRecipe recipe = this.getRecipe(RecipeType.SMELTING, craftingInv).orElseThrow(AssertionError::new);
                if (this.getInventory().extractStack(INPUT_SLOT, null, ItemStack.EMPTY, 1, Simulation.ACTION).isEmpty()) return;
                this.cookTime = 0;
                this.cookLength = 0;
                this.getInventory().insertStack(OUTPUT_SLOT, recipe.getOutput().copy(), Simulation.ACTION);
            }

        } else {
            if (this.cookTime > 0) this.cookTime--;
        }
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new ElectricFurnaceScreenHandler(syncId, player, this);
        return null;
    }

    private enum Status implements MachineStatus {
        /**
         * The electric furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableText("ui.galacticraft.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * There are no valid items to smelt/cook.
         */
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft.machinestatus.not_enough_items"), Formatting.GRAY, StatusType.MISSING_ITEMS),

        /**
         * The electric furnace has no more energy
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

        private final Text name;
        private final StatusType type;

        Status(MutableText name, Formatting color, StatusType type) {
            this.type = type;
            this.name = name.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
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
