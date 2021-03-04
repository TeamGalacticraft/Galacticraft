/*
 * Copyright (c) 2019-2021 HRZN LTD
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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.GCRecipeAccessor;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPartType;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.RocketEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipe.RocketAssemblerRecipe;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {
    private static final FullFixedItemInv EMPTY_INV = new FullFixedItemInv(0);
    public static final int SCHEMATIC_INPUT_SLOT = 0;
    public static final int ROCKET_OUTPUT_SLOT = 1;
    public static final int ENERGY_INPUT_SLOT = 2;

    private final FullFixedItemInv inventory = new FullFixedItemInv(3) {
        public ItemFilter getFilterForSlot(int slot) {
            if (slot == SCHEMATIC_INPUT_SLOT) {
                return (itemStack -> itemStack.getItem() == GalacticraftItems.ROCKET_SCHEMATIC);
            } else if (slot == ENERGY_INPUT_SLOT) {
                return (EnergyUtils::isEnergyExtractable);
            } else {
                return (stack) -> stack.getItem() == GalacticraftItems.ROCKET;
            }
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return this.getFilterForSlot(slot).matches(item);
        }
    };
    public RocketData data = RocketData.EMPTY;
    public Map<Identifier, RocketAssemblerRecipe> recipes = new HashMap<>();
    private final SimpleCapacitor energy = new SimpleCapacitor(DefaultEnergyType.INSTANCE, Galacticraft.configManager.get().machineEnergyStorageSize());
    private FullFixedItemInv extendedInv = EMPTY_INV;
    private float progress = 0.0F;
    public RocketEntity fakeEntity;
    private boolean ready = false;
    private boolean building = false;
    private boolean queuedUpdate = false;

    public RocketAssemblerBlockEntity() {
        super(GalacticraftBlockEntities.ROCKET_ASSEMBLER_TYPE);
        inventory.addListener((view, i, previous, current) -> {
            if (!world.isClient && i == SCHEMATIC_INPUT_SLOT) {
                schematicUpdate(previous, current);
                markDirty();
            }
        }, () -> {

        });
        
        fakeEntity = new RocketEntity(GalacticraftEntityTypes.ROCKET, null);
    }

    private void schematicUpdate(ItemStack prev, ItemStack current) {
        try {
            recipes.clear();
            for (Recipe<Inventory> recipe : ((GCRecipeAccessor) world.getRecipeManager()).getAllOfTypeGC(GalacticraftRecipes.ROCKET_ASSEMBLER_TYPE).values()) {
                if (recipe instanceof RocketAssemblerRecipe) {
                    recipes.put(((RocketAssemblerRecipe) recipe).getPartOutput(), ((RocketAssemblerRecipe) recipe));
                }
            }
        } catch (NullPointerException ex) {
            queuedUpdate = true;
            return;
        }

        if (prev.isEmpty() && current.isEmpty()) {
            return;
        }

        if (!current.isEmpty() && current.getItem() == GalacticraftItems.ROCKET_SCHEMATIC) {
            if (this.data.equals(RocketData.fromItem(current))) {
                return;
            }
            this.data = RocketData.fromItem(current);
        }

        for (int i = 0; i < extendedInv.getSlotCount(); i++) {
            ItemStack stack = extendedInv.getInvStack(i);
            extendedInv.setInvStack(i, ItemStack.EMPTY, Simulation.ACTION);
            ItemEntity entity = new ItemEntity(this.world, this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), stack);
            this.world.spawnEntity(entity);
        }

        if (current.isEmpty()) {
            this.data = RocketData.EMPTY;
            this.extendedInv = EMPTY_INV;
            return;
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = AddonRegistry.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack::isItemEqual); //damage matters
                }
            }
        }

        extendedInv = new FullFixedItemInv(slots) {
            @Override
            public ItemFilter getFilterForSlot(int slot) {
                return filters.get(slot);
            }

            @Override
            public boolean isItemValidForSlot(int slot, ItemStack item) {
                return this.getFilterForSlot(slot).matches(item);
            }

            @Override
            public int getMaxAmount(int slot, ItemStack stack) {
                if (data != RocketData.EMPTY) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = AddonRegistry.ROCKET_PARTS.getId(data.getPartForType(RocketPartType.values()[i]));
                            if (a + recipes.get(id).getInput().size() > slot) {
                                for (ItemStack ingredient : recipes.get(id).getInput()) {
                                    if (a == slot) {
                                        return ingredient.getCount();
                                    }
                                    a++;
                                }
                            } else {
                                a += recipes.get(id).getInput().size();
                            }
                        }
                    }
                }
                return 0;
            }
        };

        if (!data.isEmpty()) {
            for (RocketPart part : data.getParts()) {
                if (part != null) {
                    fakeEntity.setPart(part);
                }
            }
            fakeEntity.setColor(this.data.getRed(), this.data.getGreen(), this.data.getBlue(), this.data.getAlpha());
        }

        extendedInv.addListener((view) -> {
            boolean success = true;
            for (int i = 0; i < extendedInv.getSlotCount(); i++) {
                if (extendedInv.getFilterForSlot(i).matches(extendedInv.getInvStack(i)) &&
                        extendedInv.getMaxAmount(i, extendedInv.getInvStack(i)) == extendedInv.getInvStack(i).getCount()) {
                    continue;
                }
                success = false;
                break;
            }
            this.ready = success;
        }, () -> {});
    }

    public float getProgress() {
        return progress;
    }

    private void schematicUpdateFromTag() {
        recipes.clear();
        for (Recipe<Inventory> recipe : ((GCRecipeAccessor) world.getRecipeManager()).getAllOfTypeGC(GalacticraftRecipes.ROCKET_ASSEMBLER_TYPE).values()) {
            if (recipe instanceof RocketAssemblerRecipe) {
                recipes.put(((RocketAssemblerRecipe) recipe).getPartOutput(), ((RocketAssemblerRecipe) recipe));
            }
        }

        if (this.data.isEmpty() && this.inventory.getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty()) {
            return;
        }

        if ((!this.data.isEmpty() && inventory.getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty()) || (this.data.isEmpty() && !inventory.getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty())) {
            throw new RuntimeException("Error loading schematic!");
        }

        if (inventory.getInvStack(SCHEMATIC_INPUT_SLOT).getItem() == GalacticraftItems.ROCKET_SCHEMATIC) {
            if (!this.data.equals(RocketData.fromItem(inventory.getInvStack(SCHEMATIC_INPUT_SLOT)))) {
                schematicUpdate(data.toSchematic(new ItemStack(GalacticraftItems.ROCKET_SCHEMATIC)), inventory.getInvStack(SCHEMATIC_INPUT_SLOT));
                return;
            }
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = AddonRegistry.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack1 -> stack.getItem().equals(stack1.getItem()));
                }
            }
        }

        FullFixedItemInv inv = new FullFixedItemInv(slots) {
            @Override
            public ItemFilter getFilterForSlot(int slot) {
                return filters.get(slot);
            }

            @Override
            public boolean isItemValidForSlot(int slot, ItemStack item) {
                return this.getFilterForSlot(slot).matches(item);
            }

            @Override
            public int getMaxAmount(int slot, ItemStack stack) {
                if (data != RocketData.EMPTY) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = AddonRegistry.ROCKET_PARTS.getId(data.getPartForType(RocketPartType.values()[i]));
                            if (a + recipes.get(id).getInput().size() > slot) {
                                for (ItemStack ingredient : recipes.get(id).getInput()) {
                                    if (a == slot) {
                                        return ingredient.getCount();
                                    }
                                    a++;
                                }
                            } else {
                                a += recipes.get(id).getInput().size();
                            }
                        }
                    }
                }
                return -1;
            }
        };
        CompoundTag tag = new CompoundTag();
        extendedInv.toTag(tag);
        inv.fromTag(tag);


        extendedInv = inv;

        if (!data.isEmpty()) {
            for (RocketPart part : data.getParts()) {
                if (part != null) {
                    fakeEntity.setPart(part);
                }
            }
        }

        extendedInv.addListener((view, slot, prev, cur) -> {
            if (extendedInv.getSlotCount() > 0) {

                boolean success = true;
                for (int i = 0; i < extendedInv.getSlotCount(); i++) {
                    if (extendedInv.getFilterForSlot(i).matches(extendedInv.getInvStack(i)) &&
                            extendedInv.getMaxAmount(i, extendedInv.getInvStack(i)) == extendedInv.getInvStack(i).getCount()) {
                        continue;
                    }
                    success = false;
                    break;
                }
                this.ready = success;
            } else {
                this.ready = false;
            }
        }, () -> {});
    }

    public FullFixedItemInv getInventory() {
        return inventory;
    }

    public FullFixedItemInv getExtendedInv() {
        return extendedInv;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag compoundTag) {
        super.fromTag(state, compoundTag);
        this.inventory.fromTag(compoundTag);
        this.data = RocketData.fromTag(compoundTag.getCompound("data"));
        this.extendedInv = new FullFixedItemInv(compoundTag.getInt("slots"));
        this.extendedInv.fromTag(compoundTag);
        this.schematicUpdateFromTag();
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("data", data.toTag());
        compoundTag.putInt("slots", extendedInv.getSlotCount());
        inventory.toTag(compoundTag);
        extendedInv.toTag(compoundTag);
        return compoundTag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(GalacticraftBlocks.ROCKET_ASSEMBLER.getDefaultState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    @Override
    public void tick() {
        if (queuedUpdate) {
            queuedUpdate = false;
            this.schematicUpdateFromTag();
        }
        if (getEnergyAttribute().getEnergy() >= getEnergyAttribute().getMaxCapacity()) {
            return;
        }
        int neededEnergy = Math.min(50, getEnergyAttribute().getMaxCapacity() - getEnergyAttribute().getEnergy());
        if (EnergyUtils.isEnergyExtractable(this.getInventory().getSlot(ENERGY_INPUT_SLOT))) {
            int amountFailedToExtract = EnergyUtils.extractEnergy(this.getInventory().getSlot(ENERGY_INPUT_SLOT), neededEnergy, Simulation.ACTION);
            this.getEnergyAttribute().insert(DefaultEnergyType.INSTANCE, neededEnergy - amountFailedToExtract, Simulation.ACTION);
        }

        if (this.building) { //out of 600 ticks
            if (this.energy.getEnergy() >= 20) {
                this.energy.extract(DefaultEnergyType.INSTANCE, Galacticraft.configManager.get().rocketAssemblerEnergyConsumptionRate(), Simulation.ACTION);
            } else {
                this.building = false;
            }

            if (progress++ >= Galacticraft.configManager.get().rocketAssemblerProcessTime()) {
                this.building = false;
                this.progress = 0;
                for (int i = 0; i < extendedInv.getSlotCount(); i++) {
                    extendedInv.setInvStack(i, ItemStack.EMPTY, Simulation.ACTION);
                }
                ItemStack stack1 = new ItemStack(GalacticraftItems.ROCKET);
                stack1.setTag(data.toTag());
                this.inventory.setInvStack(ROCKET_OUTPUT_SLOT, stack1, Simulation.ACTION);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
            }
        }
    }

    public SimpleCapacitor getEnergyAttribute() {
        return energy;
    }

    public boolean ready() {
        return ready;
    }

    public boolean building() {
        return building;
    }

    public void startBuilding() {
        this.building = true;
    }
}
