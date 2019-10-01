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

package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorStatus;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipes.ShapedCompressingRecipe;
import com.hrznstudio.galacticraft.recipes.ShapelessCompressingRecipe;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, WireConnectable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    public CompressorStatus status = CompressorStatus.INACTIVE;
    int progress;

    public ElectricCompressorBlockEntity() {
        super(GalacticraftBlockEntities.ELECTRIC_COMPRESSOR_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 12;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public int getMaxEnergy() {
        return ConfigurableElectricMachineBlockEntity.DEFAULT_MAX_ENERGY;
    }

    private int ticks = 0;
    public void tick() {
        if (!enabled() || world.isClient) {
            return;
        }

        InventoryFixedWrapper inv = new InventoryFixedWrapper(getInventory().getSubInv(0, 9)) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity var1) {
                return true;
            }
        };

        attemptChargeFromStack(FUEL_INPUT_SLOT);
        if (getEnergyAttribute().getCurrentEnergy() < 1) {
            status = CompressorStatus.INACTIVE;
        } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
            status = CompressorStatus.PROCESSING;
        } else {
            status = CompressorStatus.IDLE;
        }

        if (status == CompressorStatus.PROCESSING) {
            ItemStack resultStack = getResultFromRecipeStack(inv);
            this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 3, Simulation.ACTION);
            this.progress++;

            if (this.progress % 40 == 0 && this.progress > maxProgress / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.progress == maxProgress) {
                this.progress = 0;

                craftItem(resultStack);
            }
        } else if (status == CompressorStatus.INACTIVE) {
            if (progress > 0) {
                progress--;
            }
        } else {
            if (ticks++ % 2 == 0) {
                getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            }
            if (progress > 0) {
                progress--;
            }
        }

        trySpreadEnergy();
    }

    protected void craftItem(ItemStack craftingResult) {
        boolean canCraftTwo = true;

        for (int i = 0; i < 9; i++) {
            ItemStack item = getInventory().getInvStack(i);

            // If slot is not empty ( must be an ingredient if we've made it this far ), and there is less than 2 items in the slot, we cannot craft two.
            if (!item.isEmpty() && item.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            if (getInventory().getInvStack(OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount() || getInventory().getInvStack(SECOND_OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount()) {
                // There would be too many items in the output slot. Just craft one.
                canCraftTwo = false;
            }
        }

        for (int i = 0; i < 9; i++) {
            getInventory().getSlot(i).extract(canCraftTwo ? 2 : 1);
        }

        // <= because otherwise it loops only once and puts in only one slot
        for (int i = OUTPUT_SLOT; i <= SECOND_OUTPUT_SLOT; i++) {
            getInventory().getSlot(i).insert(craftingResult);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Energy", getEnergyAttribute().getCurrentEnergy());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        getEnergyAttribute().setCurrentEnergy(tag.getInt("Energy"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }


    public ItemStack getResultFromRecipeStack(Inventory inv) {
        // Once this method has been called, we have verified that either a shapeless or shaped recipe is present with isValidRecipe. Ignore the warning on getShapedRecipe(inv).get().

        Optional<ShapelessCompressingRecipe> shapelessRecipe = getShapelessRecipe(inv);
        if (shapelessRecipe.isPresent()) {
            return shapelessRecipe.get().craft(inv);
        }
        return getShapedRecipe(inv).orElseThrow(() -> new IllegalStateException("Neither a shapeless recipe or shaped recipe was present when getResultFromRecipeStack was called. This should never happen, as isValidRecipe should have been called first. That would have prevented this.")).craft(inv);
    }

    private Optional<ShapelessCompressingRecipe> getShapelessRecipe(Inventory input) {
        Optional<ShapelessCompressingRecipe> firstMatch = this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPELESS_COMPRESSING_TYPE, input, this.world);
        return firstMatch;
    }

    private Optional<ShapedCompressingRecipe> getShapedRecipe(Inventory input) {
        Optional<ShapedCompressingRecipe> firstMatch = this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPED_COMPRESSING_TYPE, input, this.world);
        return firstMatch;
    }

    protected boolean canPutStackInResultSlot(ItemStack itemStack) {
        return getInventory().getSlot(OUTPUT_SLOT).attemptInsertion(itemStack, Simulation.SIMULATE).isEmpty();
    }

    public boolean isValidRecipe(Inventory input) {
        Optional<ShapelessCompressingRecipe> shapelessRecipe = getShapelessRecipe(input);
        Optional<ShapedCompressingRecipe> shapedRecipe = getShapedRecipe(input);

        return shapelessRecipe.isPresent() || shapedRecipe.isPresent();
    }

}