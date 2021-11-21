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

import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotSettings;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
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

    private final Inventory craftingInv = this.itemStorage().mapped(INPUT_SLOT);
    private final SimpleInventory predicateInv = new SimpleInventory(1);

    public Status status = Status.NOT_ENOUGH_RESOURCES;

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.CIRCUIT_FABRICATOR, pos, state, GalacticraftRecipe.FABRICATION_TYPE);
    }

    @Override
    protected MachineItemStorage.Builder createInventory(MachineItemStorage.Builder builder) {
        builder.addSlot(SlotSettings.Builder.create(8, 70, SlotType.CHARGE).filter(Constant.Filter.Item.CAN_EXTRACT_ENERGY).build());
        builder.addSlot(SlotSettings.Builder.create(31, 15, SlotType.INPUT).filter(ExactItemFilter.createFilter(Items.DIAMOND)).build());
        builder.addSlot(SlotSettings.Builder.create(62, 45, SlotType.INPUT).filter(ExactItemFilter.createFilter(GalacticraftItem.RAW_SILICON)).build());
        builder.addSlot(SlotSettings.Builder.create(62, 63, SlotType.INPUT).filter(ExactItemFilter.createFilter(GalacticraftItem.RAW_SILICON)).build());
        builder.addSlot(SlotSettings.Builder.create(107, 70, SlotType.INPUT).filter(ExactItemFilter.createFilter(Items.REDSTONE)).build());
        builder.addSlot(SlotSettings.Builder.create(134, 15, SlotType.INPUT).build());
        builder.addSlot(SlotSettings.Builder.create(152, 70, SlotType.OUTPUT).filter(stack -> {
            synchronized (this.predicateInv) {
                this.predicateInv.setStack(0, stack);
                assert this.world != null;
                return this.world.getRecipeManager().getFirstMatch(this.recipeType(), this.predicateInv, this.world).isPresent();
            }
        }).disableInput().build());
        return builder;
    }

    @Override
    public long energyExtractionRate() {
        return 0;
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
        FabricationRecipe validRecipe = this.findValidRecipe();
        if (validRecipe == null) return Status.NOT_ENOUGH_RESOURCES;
        if (!this.canOutput(validRecipe, null)) return Status.FULL;
        return Status.PROCESSING;
    }

    @Override
    public @NotNull Inventory craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean outputStacks(FabricationRecipe recipe, Transaction transaction) {
        return this.itemStorage().insertStack(OUTPUT_SLOT, recipe.getOutput().copy(), transaction).isEmpty();
    }

    @Override
    protected void extractCraftingMaterials(FabricationRecipe recipe, Transaction transaction) {
        if (this.itemStorage().getSlot(INPUT_SLOT_DIAMOND).extract(ItemVariant.of(Items.DIAMOND), 1, transaction) == 1) {
            if (this.itemStorage().getSlot(INPUT_SLOT_SILICON).extract(ItemVariant.of(GalacticraftItem.RAW_SILICON), 1, transaction) == 1) {
                if (this.itemStorage().getSlot(INPUT_SLOT_SILICON_2).extract(ItemVariant.of(GalacticraftItem.RAW_SILICON), 1, transaction) == 1) {
                    if (this.itemStorage().getSlot(INPUT_SLOT_REDSTONE).extract(ItemVariant.of(Items.REDSTONE), 1, transaction) == 1) {
                        transaction.commit();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected @Nullable FabricationRecipe findValidRecipe() {
        try (Transaction transaction = Transaction.openOuter()) {
            if (this.itemStorage().getSlot(INPUT_SLOT_DIAMOND).extract(ItemVariant.of(Items.DIAMOND), 1, transaction) == 1
                    && this.itemStorage().getSlot(INPUT_SLOT_SILICON).extract(ItemVariant.of(GalacticraftItem.RAW_SILICON), 1, transaction) == 1
                    && this.itemStorage().getSlot(INPUT_SLOT_SILICON_2).extract(ItemVariant.of(GalacticraftItem.RAW_SILICON), 1, transaction) == 1
                    && this.itemStorage().getSlot(INPUT_SLOT_REDSTONE).extract(ItemVariant.of(Items.REDSTONE), 1, transaction) == 1) {
                return super.findValidRecipe();
            }
        }
        return null;
    }

    @Override
    protected int getProcessTime(@NotNull FabricationRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    public long energyConsumption() {
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