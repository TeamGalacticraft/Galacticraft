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
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
public class ElectricCompressorBlockEntity extends RecipeMachineBlockEntity<Inventory, CompressingRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int OUTPUT_SLOT = 10;
    public static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;

    private final Inventory craftingInv = new InventoryFixedWrapper(this.itemInv().getSubInv(1, 10)) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return getWrappedInventory().canPlayerUse(player);
        }
    };

    private final FixedItemInv outputInv = this.itemInv().getSubInv(OUTPUT_SLOT, SECOND_OUTPUT_SLOT + 1);

    public ElectricCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ELECTRIC_COMPRESSOR, pos, state, GalacticraftRecipe.COMPRESSING_TYPE, CompressingRecipe::getTime);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                builder.addSlot(y * 3 + x + 1, SlotType.INPUT, ConstantItemFilter.ANYTHING, x * 18 + 33, y * 18 + 18);
            }
        }

        builder.addSlot(OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(152, 29));
        builder.addSlot(SECOND_OUTPUT_SLOT, SlotType.OUTPUT, ConstantItemFilter.ANYTHING, new MachineItemInv.OutputSlotFunction(152, 47));
        return builder;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.recipe() == null) return Status.INVALID_RECIPE;
        if (!this.canCraft(this.recipe())) return Status.OUTPUT_FULL;
        return Status.COMPRESSING;
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
    public void tickWork() {
        super.tickWork();
        if (this.getStatus().getType().isActive()) {
            if (this.progress() % (this.maxProgress() / 5) == 0 && this.progress() > this.maxProgress() / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    protected void craft(CompressingRecipe recipe) {
        super.craft(recipe);
        recipe = this.recipe();
        if (this.canCraft(recipe)) {
            super.craft(recipe);
        }
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
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.ELECTRIC_COMPRESSOR_HANDLER, syncId, player.getInventory(), this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {

        /**
         * Compressor is compressing items.
         */
        COMPRESSING(new TranslatableText("ui.galacticraft.machine.status.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Compressor has no valid recipe.
         */
        INVALID_RECIPE(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS),

        /**
         * Compressor has no valid recipe.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft.machine.status.output_full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * Compressor has no items to process.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

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