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
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
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
public class CircuitFabricatorBlockEntity extends RecipeMachineBlockEntity<Inventory, FabricationRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT_DIAMOND = 1;
    public static final int INPUT_SLOT_SILICON = 2;
    public static final int INPUT_SLOT_SILICON_2 = 3;
    public static final int INPUT_SLOT_REDSTONE = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private final Inventory craftingInv = new InventoryFixedWrapper(this.itemInv().getSubInv(INPUT_SLOT, INPUT_SLOT + 1)) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return getWrappedInventory().canPlayerUse(player);
        }
    };
    private final FixedItemInv outputInv = this.itemInv().getSubInv(OUTPUT_SLOT, OUTPUT_SLOT + 1);
    private final SimpleInventory predicateInv = new SimpleInventory(1);

    public Status status = Status.NOT_ENOUGH_RESOURCES;

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.CIRCUIT_FABRICATOR, pos, state, GalacticraftRecipe.FABRICATION_TYPE, FabricationRecipe::getProcessingTime);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 70);
        builder.addSlot(INPUT_SLOT_DIAMOND, SlotType.INPUT, ExactItemFilter.createFilter(Items.DIAMOND), 31, 15);
        builder.addSlot(INPUT_SLOT_SILICON, SlotType.INPUT, ExactItemFilter.createFilter(GalacticraftItem.RAW_SILICON), 62, 45);
        builder.addSlot(INPUT_SLOT_SILICON_2, SlotType.INPUT, ExactItemFilter.createFilter(GalacticraftItem.RAW_SILICON), 62, 63);
        builder.addSlot(INPUT_SLOT_REDSTONE, SlotType.INPUT, ExactItemFilter.createFilter(Items.REDSTONE), 107, 70);
        builder.addSlot(INPUT_SLOT, SlotType.INPUT, stack -> {
            this.predicateInv.setStack(0, stack);
            assert this.world != null;
            return this.world.getRecipeManager().getFirstMatch(this.recipeType(), this.predicateInv, this.world).isPresent();
        }, 134, 15);
        builder.addSlot(OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(152, 70));
        return builder;
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
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.recipe() == null || !this.canCraft(Simulation.SIMULATE)) return Status.NOT_ENOUGH_RESOURCES;
        if (!super.canCraft(this.recipe())) return Status.FULL;
        return Status.PROCESSING;
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
    protected boolean canCraft(@Nullable FabricationRecipe recipe) {
        return this.canCraft(Simulation.SIMULATE) && super.canCraft(recipe);
    }

    private boolean canCraft(Simulation simulation) {
        return !this.itemInv().extractStack(INPUT_SLOT_DIAMOND, this.getFilterForSlot(INPUT_SLOT_DIAMOND), ItemStack.EMPTY, 1, simulation).isEmpty()
                && !this.itemInv().extractStack(INPUT_SLOT_SILICON, this.getFilterForSlot(INPUT_SLOT_SILICON), ItemStack.EMPTY, 1, simulation).isEmpty()
                && !this.itemInv().extractStack(INPUT_SLOT_SILICON_2, this.getFilterForSlot(INPUT_SLOT_SILICON_2), ItemStack.EMPTY, 1, simulation).isEmpty()
                && !this.itemInv().extractStack(INPUT_SLOT_REDSTONE, this.getFilterForSlot(INPUT_SLOT_REDSTONE), ItemStack.EMPTY, 1, simulation).isEmpty();
    }

    @Override
    protected void craft(FabricationRecipe recipe) {
        assert this.canCraft(Simulation.ACTION);
        super.craft(recipe);
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.CIRCUIT_FABRICATOR_HANDLER, syncId, player.getInventory(), this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Fabricator is active and is processing.
         */
        PROCESSING(new TranslatableText("ui.galacticraft.machine.status.processing"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Fabricator output slot is full.
         */
        FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Fabricator does not have the required resources to function.
         */
        NOT_ENOUGH_RESOURCES(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * The fabricator has no energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.GRAY, StatusType.MISSING_ENERGY);

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