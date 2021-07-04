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

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricFurnaceBlockEntity extends RecipeMachineBlockEntity<Inventory, SmeltingRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    
    private final @NotNull Inventory craftingInv = new InventoryFixedWrapper(this.itemInv().getSubInv(INPUT_SLOT, INPUT_SLOT + 1)) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return getWrappedInventory().canPlayerUse(player);
        }
    };
    private final @NotNull FixedItemInv outputInv = this.itemInv().getSubInv(OUTPUT_SLOT, OUTPUT_SLOT + 1);

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 7);
        builder.addSlot(INPUT_SLOT, SlotType.INPUT, stack -> this.getRecipe(RecipeType.SMELTING, new SimpleInventory(stack)).isPresent(), 56, 25);
        builder.addSlot(OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(109, 25));
        return builder;
    }

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ELECTRIC_FURNACE, pos, state, RecipeType.SMELTING, AbstractCookingRecipe::getCookTime);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.recipe() == null) return Status.NOT_ENOUGH_ITEMS;
        if (!this.canCraft(this.recipe())) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    public @NotNull FixedItemInv outputInv() {
        return this.outputInv;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.ELECTRIC_FURNACE_HANDLER, syncId, player.getInventory(), this);
        return null;
    }

    private enum Status implements MachineStatus {
        /**
         * The electric furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableText("ui.galacticraft.machine.status.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * There are no valid items to smelt/cook.
         */
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.GRAY, StatusType.MISSING_ITEMS),

        /**
         * The electric furnace has no more energy
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

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
