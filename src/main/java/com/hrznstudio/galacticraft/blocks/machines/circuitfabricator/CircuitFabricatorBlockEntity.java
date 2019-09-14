/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.util.BlockOptionUtils;
import io.github.cottonmc.energy.api.EnergyAttribute;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

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
    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;
    public SideOption[] sideOptions = {SideOption.BLANK, SideOption.POWER_INPUT};
    public Map<Direction, SideOption> selectedOptions = BlockOptionUtils.getDefaultSideOptions();
    int progress;

    public CircuitFabricatorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        selectedOptions.put(Direction.SOUTH, SideOption.POWER_INPUT);
        // Stop automation from inserting into the output or extracting from the inputs.
        getLimitedInventory().getSubRule(1, 6).disallowExtraction();
        getLimitedInventory().getRule(6).filterInserts(ConstantItemFilter.NOTHING);
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
        if (world.isClient || !enabled()) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOption.POWER_INPUT)) {
                EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.getEnergyAttribute().setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, Simulation.ACTION));
                }
            }
        }
        attemptChargeFromStack(0);


        if (status == CircuitFabricatorStatus.IDLE) {
            //this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            this.progress = 0;
        }


        if (getEnergyAttribute().getCurrentEnergy() <= 0) {
            status = CircuitFabricatorStatus.INACTIVE;
        } else {
            status = CircuitFabricatorStatus.IDLE;
        }


        if (status == CircuitFabricatorStatus.INACTIVE) {
            //this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            this.progress = 0;
            return;
        }


        if (isValidRecipe(this.getInventory().getInvStack(5))) {
            if (canPutStackInResultSlot(getResultFromRecipeStack())) {
                this.status = CircuitFabricatorStatus.PROCESSING;
            }
        } else {
            if (this.status != CircuitFabricatorStatus.INACTIVE) {
                this.status = CircuitFabricatorStatus.IDLE;
            }
        }

        if (status == CircuitFabricatorStatus.PROCESSING) {
            ItemStack resultStack = getResultFromRecipeStack();
            if (this.progress < this.maxProgress) {
                ++progress;
                this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            } else {
                this.progress = 0;

                getInventory().getSlot(1).extract(1);
                getInventory().getSlot(2).extract(1);
                getInventory().getSlot(3).extract(1);
                getInventory().getSlot(4).extract(1);
                getInventory().getSlot(5).extract(1);
                getInventory().getSlot(6).insert(resultStack);
            }
        }
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        BasicInventory inv = new BasicInventory(getInventory().getInvStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        FabricationRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("No recipe present????"));
        return recipe.craft(inv);
    }

    private Optional<FabricationRecipe> getRecipe(BasicInventory input) {
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
        return getRecipe(new BasicInventory(input)).isPresent() && hasMandatoryMaterials();
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
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        progress = tag.getInt("Progress");
    }
}