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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotSettings;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;
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
public class ElectricArcFurnaceBlockEntity extends RecipeMachineBlockEntity<Inventory, BlastingRecipe> {
    private final @NotNull Inventory craftingInv = this.itemStorage().mapped(INPUT_SLOT);
    private final @NotNull SimpleInventory predicateInv = new SimpleInventory(1);

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    @Override
    protected MachineItemStorage.Builder createInventory(MachineItemStorage.Builder builder) {
        builder.addSlot(SlotSettings.Builder.create(8, 61, SlotType.CHARGE).filter(Constant.Filter.Item.CAN_EXTRACT_ENERGY).build());
        builder.addSlot(SlotSettings.Builder.create(44, 35, SlotType.INPUT).filter(stack -> {
            this.predicateInv.setStack(0, stack);
            return this.world.getRecipeManager().getFirstMatch(this.recipeType(), this.predicateInv, this.world).isPresent();
        }).build());
        builder.addSlot(SlotSettings.Builder.create(108, 35, SlotType.OUTPUT).disableInput().build());
        builder.addSlot(SlotSettings.Builder.create(134, 35, SlotType.OUTPUT).disableInput().build());
        return builder;
    }

    public ElectricArcFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE, pos, state, RecipeType.BLASTING);
    }

    @Override
    public long energyExtractionRate() {
        return 0;
    }

    @Override
    protected long energyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate();
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    protected void tickDisabled() {

    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        BlastingRecipe validRecipe = this.findValidRecipe();
        if (validRecipe == null) return Status.NOT_ENOUGH_ITEMS;
        if (!this.canOutput(validRecipe, null)) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(BlastingRecipe recipe, Transaction transaction) {
        ItemStack copy = recipe.getOutput().copy();
        copy.setCount(copy.getCount() * 2);
        ItemStack stack1 = this.itemStorage().insertStack(OUTPUT_SLOT_1, copy, transaction);
        if (stack1.isEmpty()) return true;
        stack1 = this.itemStorage().insertStack(OUTPUT_SLOT_2, stack1, transaction);
        if (stack1.isEmpty()) return true;
        transaction.abort();
        return false;
    }

    @Override
    protected void extractCraftingMaterials(BlastingRecipe recipe, Transaction transaction) {
        this.itemStorage().extractStack(INPUT_SLOT, 1, transaction);
    }

    @Override
    protected int getProcessTime(@NotNull BlastingRecipe recipe) {
        return (int) (recipe.getCookTime() * 0.8);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER, syncId, player.getInventory(), this);
        return null;
    }

    private enum Status implements MachineStatus {
        /**
         * The electric arc furnace is cooking/smelting items
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
         * The electric arc furnace has no more energy
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
