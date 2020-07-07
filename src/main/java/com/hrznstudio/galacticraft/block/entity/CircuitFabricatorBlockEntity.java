/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.entity;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.FabricationRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import io.github.cottonmc.component.api.ActionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

    private static final Item[] mandatoryMaterials = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};
    private static final Predicate<ItemStack>[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new Predicate[7];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = stack -> !stack.isEmpty() && stack.getItem() == mandatoryMaterials[0];
        SLOT_FILTERS[2] = stack -> !stack.isEmpty() && stack.getItem() == mandatoryMaterials[1];
        SLOT_FILTERS[3] = stack -> !stack.isEmpty() && stack.getItem() == mandatoryMaterials[2];
        SLOT_FILTERS[4] = stack -> !stack.isEmpty() && stack.getItem() == mandatoryMaterials[3];
        SLOT_FILTERS[5] = stack -> true;// This is filled in by #getFilterForSlot
        SLOT_FILTERS[6] = stack -> true;
    }

    private final int maxProgress = 300;
    public CircuitFabricatorStatus status = CircuitFabricatorStatus.IDLE;
    public int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
    }

    @Override
    protected boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return super.canExtract(slot, stack, dir) && slot == 6; // no hopper extract from input + battery slots
    }

    @Override
    protected boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return super.canInsert(slot, stack, dir) && slot != 6;
    }

    @Override
    protected boolean canExtractEnergy() {
        return false;
    }

    @Override
    protected boolean canInsertEnergy() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public CircuitFabricatorStatus getStatusForTooltip() {
        return status;
    }

    @Override
    protected int getInventorySize() {
        return 7;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 5) {
            return this::isValidRecipe;
        }
        return SLOT_FILTERS[slot];
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }

        if (disabled()) {
            this.status = CircuitFabricatorStatus.OFF;
            idleEnergyDecrement(true);
            return;
        }
        attemptChargeFromStack(0);


        if (status == CircuitFabricatorStatus.IDLE) {
            idleEnergyDecrement(false);
            if (this.progress > 0) {
                progress--;
            }
        }


        if (getCapacitorComponent().getCurrentEnergy() <= 0) {
            status = CircuitFabricatorStatus.NOT_ENOUGH_ENERGY;
        } else {
            status = CircuitFabricatorStatus.IDLE;
        }


        if (status == CircuitFabricatorStatus.NOT_ENOUGH_ENERGY) {
            if (progress > 0) {
                this.progress--;
            }
            return;
        }


        if (isValidRecipe(this.getInventory().getStack(5))) {
            if (canPutStackInResultSlot(getResultFromRecipeStack())) {
                this.status = CircuitFabricatorStatus.PROCESSING;
            }
        } else {
            if (this.status != CircuitFabricatorStatus.NOT_ENOUGH_ENERGY) {
                this.status = CircuitFabricatorStatus.IDLE;
            }
        }

        if (status == CircuitFabricatorStatus.PROCESSING) {
            ItemStack resultStack = getResultFromRecipeStack();
            if (this.progress < this.maxProgress) {
                ++progress;
                this.getCapacitorComponent().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
            } else {
                progress = 0;

                ItemStack stack = getInventory().getStack(1);
                stack.decrement(1);
                getInventory().setStack(1, stack);
                stack = getInventory().getStack(2);
                stack.decrement(1);
                getInventory().setStack(2, stack);
                stack = getInventory().getStack(3);
                stack.decrement(1);
                getInventory().setStack(3, stack);
                stack = getInventory().getStack(4);
                stack.decrement(1);
                getInventory().setStack(4, stack);
                stack = getInventory().getStack(5);
                stack.decrement(1);
                getInventory().setStack(5, stack);

                getInventory().insertStack(6, resultStack, ActionType.PERFORM);
            }
        }

        trySpreadEnergy();
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        SimpleInventory inv = new SimpleInventory(getInventory().getStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        FabricationRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("Not a valid recipe."));
        return recipe.craft(inv);
    }

    private Optional<FabricationRecipe> getRecipe(SimpleInventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.FABRICATION_TYPE, input, this.world);
    }

    private boolean canPutStackInResultSlot(ItemStack stack) {
        return canInsert(6, stack);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    // This is just for testing
    private boolean isValidRecipe(ItemStack input) {
        // TODO check up on this
        return getRecipe(new SimpleInventory(input)).isPresent() && hasMandatoryMaterials();
//        return !input.isEmpty() && hasMandatoryMaterials();
    }

    private boolean hasMandatoryMaterials() {
        return getInventory().getStack(1).getItem() == mandatoryMaterials[0] &&
                getInventory().getStack(2).getItem() == mandatoryMaterials[1] &&
                getInventory().getStack(3).getItem() == mandatoryMaterials[2] &&
                getInventory().getStack(4).getItem() == mandatoryMaterials[3];
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
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().circuitFabricatorEnergyConsumptionRate();
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum CircuitFabricatorStatus implements MachineStatus {
        /**
         * Fabricator is active and is processing.
         */
        PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.processing"), Formatting.GREEN),

        /**
         * Fabricator is not processing.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD),

        /**
         * The fabricator has no energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.GRAY),

        /**
         * The fabricator has been switched off.
         */
        OFF(new TranslatableText("ui.galacticraft-rewoven.machinestatus.off"), Formatting.RED);


        private final Text text;

        CircuitFabricatorStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static CircuitFabricatorStatus get(int index) {
            switch (index) {
                case 0:
                    return PROCESSING;
                case 1:
                    return IDLE;
                case 3:
                    return OFF;
                default:
                    return NOT_ENOUGH_ENERGY;
            }
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}