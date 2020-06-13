/*
 * Copyright (c) 2019 HRZN LTD
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
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.FabricationRecipe;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
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
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, EnergyStorage {

    private static final Item[] mandatoryMaterials = new Item[]{Items.DIAMOND, GalacticraftItems.RAW_SILICON, GalacticraftItems.RAW_SILICON, Items.REDSTONE};
    private static final ItemFilter[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new ItemFilter[7];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = new ExactItemFilter(mandatoryMaterials[0]);
        SLOT_FILTERS[2] = new ExactItemFilter(mandatoryMaterials[1]);
        SLOT_FILTERS[3] = new ExactItemFilter(mandatoryMaterials[2]);
        SLOT_FILTERS[4] = new ExactItemFilter(mandatoryMaterials[3]);
        SLOT_FILTERS[5] = null;// This is filled in by #getFilterForSlot
        SLOT_FILTERS[6] = ConstantItemFilter.ANYTHING;
    }

    private final int maxProgress = 300;
    public CircuitFabricatorStatus status = CircuitFabricatorStatus.IDLE;
    public int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
        // Stop automation from inserting into the output or extracting from the inputs.
        getLimitedInventory().getSubRule(1, 6).disallowExtraction();
        getLimitedInventory().getRule(6).filterInserts(ConstantItemFilter.NOTHING);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public CircuitFabricatorStatus getStatusForTooltip() {
        return status;
    }

    @Override
    protected int getInvSize() {
        return 7;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
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


        if (getEnergyAttribute().getCurrentEnergy() <= 0) {
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


        if (isValidRecipe(this.getInventory().getInvStack(5))) {
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
                this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), Simulation.ACTION);
            } else {
                progress = 0;

                getInventory().getSlot(1).extract(1);
                getInventory().getSlot(2).extract(1);
                getInventory().getSlot(3).extract(1);
                getInventory().getSlot(4).extract(1);
                getInventory().getSlot(5).extract(1);
                getInventory().getSlot(6).insert(resultStack);
            }
        }

        trySpreadEnergy();
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        SimpleInventory inv = new SimpleInventory(getInventory().getInvStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        FabricationRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("Not a valid recipe."));
        return recipe.craft(inv);
    }

    private Optional<FabricationRecipe> getRecipe(SimpleInventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.FABRICATION_TYPE, input, this.world);
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        ItemStack leftover = getInventory().getSlot(6).attemptInsertion(itemStack, Simulation.SIMULATE);
        return leftover.isEmpty();
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
        return getInventory().getInvStack(1).getItem() == mandatoryMaterials[0] &&
                getInventory().getInvStack(2).getItem() == mandatoryMaterials[1] &&
                getInventory().getInvStack(3).getItem() == mandatoryMaterials[2] &&
                getInventory().getInvStack(4).getItem() == mandatoryMaterials[3];
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

    @Override
    public double getStored(EnergySide face) {
        /*if (world.getBlockState(pos).getBlock() instanceof WireConnectable) {
            if (((WireConnectable) world.getBlockState(pos).getBlock()).canWireConnect(world, ConfigurableElectricMachineBlock.energySideToDirection(face),  pos.offset(ConfigurableElectricMachineBlock.energySideToDirection(face)), pos) != WireNetwork.WireConnectionType.NONE) {
                return GalacticraftEnergy.convertToTR(this.getEnergyAttribute().getCurrentEnergy());
            }
        }*/
        return GalacticraftEnergy.convertToTR(this.getEnergyAttribute().getCurrentEnergy());
    }

    @Override
    public void setStored(double amount) {
        this.getEnergyAttribute().setCurrentEnergy(GalacticraftEnergy.convertFromTR(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return GalacticraftEnergy.convertToTR(getEnergyAttribute().getMaxEnergy());
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
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