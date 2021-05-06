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
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.item.GalacticraftItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.CircuitFabricatorScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CircuitFabricatorBlockEntity extends MachineBlockEntity {
    public static final int MAX_PROGRESS = 300;

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT_DIAMOND = 1;
    public static final int INPUT_SLOT_SILICON = 2;
    public static final int INPUT_SLOT_SILICON_2 = 3;
    public static final int INPUT_SLOT_REDSTONE = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private final Inventory recipeSlotInv;

    public Status status = Status.NOT_ENOUGH_RESOURCES;
    public int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntityType.CIRCUIT_FABRICATOR);
        this.recipeSlotInv = new InventoryFixedWrapper(this.getInventory().getMappedInv(INPUT_SLOT)) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return getWrappedInventory().canPlayerUse(player);
            }
        };
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 70);
        builder.addSlot(INPUT_SLOT_DIAMOND, SlotType.INPUT, ExactItemFilter.createFilter(Items.DIAMOND), 31, 15);
        builder.addSlot(INPUT_SLOT_SILICON, SlotType.INPUT, ExactItemFilter.createFilter(GalacticraftItems.RAW_SILICON), 62, 45);
        builder.addSlot(INPUT_SLOT_SILICON_2, SlotType.INPUT, ExactItemFilter.createFilter(GalacticraftItems.RAW_SILICON), 62, 63);
        builder.addSlot(INPUT_SLOT_REDSTONE, SlotType.INPUT, ExactItemFilter.createFilter(Items.REDSTONE), 107, 70);
        builder.addSlot(INPUT_SLOT, SlotType.INPUT, stack -> this.getRecipe(new SimpleInventory(stack)).isPresent(), 134, 15);
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
        for (int i = 1; i < 6; i++) {
            if (!this.getFilterForSlot(i).matches(getInventory().getInvStack(i))) {
                return Status.NOT_ENOUGH_RESOURCES;
            }
        }
        Optional<FabricationRecipe> recipe = this.getRecipe(recipeSlotInv);
        if (recipe.isPresent() && !this.canInsert(OUTPUT_SLOT, recipe.get().getOutput())) return Status.FULL;
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
            this.getInventory().insertStack(OUTPUT_SLOT, this.getRecipe(recipeSlotInv).orElseThrow(RuntimeException::new).getOutput().copy(), Simulation.ACTION);
        }
    }

    private Optional<FabricationRecipe> getRecipe(Inventory input) {
        if (this.world == null) return Optional.empty();
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipe.FABRICATION_TYPE, input, this.world);
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
        tag.putInt(Constant.Nbt.PROGRESS, this.progress);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.progress = tag.getInt(Constant.Nbt.PROGRESS);
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new CircuitFabricatorScreenHandler(syncId, player, this);
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