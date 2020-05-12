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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.GCRecipeAccessor;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipe.RocketAssemblerRecipe;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
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
public class RocketAssemblerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable, EnergyAttributeProvider {

    public static final int SCHEMATIC_INPUT_SLOT = 0;
    public static final int ROCKET_OUTPUT_SLOT = 1;
    public static final int ENERGY_INPUT_SLOT = 2;

    private final FullFixedItemInv inventory = new FullFixedItemInv(3) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return getFilterForSlot(slot).matches(item);
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            if (slot == SCHEMATIC_INPUT_SLOT) {
                return (itemStack -> itemStack.getItem() == GalacticraftItems.ROCKET_SCHEMATIC);
            } else if (slot == ENERGY_INPUT_SLOT) {
                return (GalacticraftEnergy::isEnergyItem);
            } else {
                return (stack) -> stack.getItem() == GalacticraftItems.ROCKET;
            }
        }
    };
    public RocketData data = RocketData.EMPTY;
    public Map<Identifier, RocketAssemblerRecipe> recipes = new HashMap<>();
    private final SimpleEnergyAttribute energy = new SimpleEnergyAttribute(Galacticraft.configManager.get().machineEnergyStorageSize(), GalacticraftEnergy.GALACTICRAFT_JOULES);
    private FullFixedItemInv extendedInventory = new FullFixedItemInv(0) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return false;
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            return (item) -> false;
        }
    };
    private float progress = 0.0F;
    public RocketEntity fakeEntity;
    private boolean ready = false;
    private boolean building = false;
    private boolean queuedUpdate = false;

    public RocketAssemblerBlockEntity() {
        super(GalacticraftBlockEntities.ROCKET_ASSEMBLER_TYPE);
        inventory.addListener((inv, slot, previous, current) -> {
            if (slot == SCHEMATIC_INPUT_SLOT) {
                schematicUpdate(previous.copy(), current.copy());
            }
            markDirty();
        }, () -> {
        });
        fakeEntity = new RocketEntity(GalacticraftEntityTypes.ROCKET, world);
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

        for (int i = 0; i < extendedInventory.getSlotCount(); i++) {
            ItemStack stack = extendedInventory.getSlot(i).get().copy();
            extendedInventory.getSlot(i).set(ItemStack.EMPTY);
            ItemEntity entity = new ItemEntity(this.world, this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), stack);
            this.world.spawnEntity(entity);
        }

        if (current.isEmpty()) {
            this.data = RocketData.EMPTY;
            this.extendedInventory = new FullFixedItemInv(0) {
                @Override
                public boolean isItemValidForSlot(int slot, ItemStack item) {
                    return false;
                }

                @Override
                public ItemFilter getFilterForSlot(int slot) {
                    return (item) -> false;
                }
            };

            return;
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = Galacticraft.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack1 -> stack.getItem().equals(stack1.getItem()));
                }
            }
        }

        extendedInventory = new FullFixedItemInv(slots) {
            @Override
            public boolean isItemValidForSlot(int slot, ItemStack item) {
                return getFilterForSlot(slot).matches(item);
            }

            @Override
            public ItemFilter getFilterForSlot(int slot) {
                return filters.get(slot);
            }

            @Override
            public int getMaxAmount(int slot, ItemStack stack) {
                if (data != RocketData.EMPTY) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = Galacticraft.ROCKET_PARTS.getId(data.getPartForType(RocketPartType.values()[i]));
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
                fakeEntity.setPart(part);
            }
            fakeEntity.setColor(this.data.getRed(), this.data.getGreen(), this.data.getBlue(), this.data.getAlpha());
        }

        extendedInventory.addListener((inv, slot, previous, current1) -> {
            boolean success = true;
            for (int i = 0; i < extendedInventory.getSlotCount(); i++) {
                if (extendedInventory.getFilterForSlot(i).matches(extendedInventory.getInvStack(i)) &&
                        extendedInventory.getMaxAmount(i, extendedInventory.getInvStack(i)) == extendedInventory.getInvStack(i).getCount()) {
                    continue;
                }
                success = false;
                break;
            }
            this.ready = success;
        }, () -> {
        });
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

        if (this.data.isEmpty() && this.getInventory().getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty()) {
            return;
        }

        if ((!this.data.isEmpty() && inventory.getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty()) || (this.data.isEmpty() && !inventory.getInvStack(SCHEMATIC_INPUT_SLOT).isEmpty())) {
            throw new RuntimeException("Error loading schematic!");
        }

        if (inventory.getInvStack(SCHEMATIC_INPUT_SLOT).getItem() == GalacticraftItems.ROCKET_SCHEMATIC) {
            if (!this.data.equals(RocketData.fromItem(inventory.getInvStack(SCHEMATIC_INPUT_SLOT)))) {
                schematicUpdate(data.toSchematic(), inventory.getInvStack(SCHEMATIC_INPUT_SLOT));
                return;
            }
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = Galacticraft.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack1 -> stack.getItem().equals(stack1.getItem()));
                }
            }
        }

        FullFixedItemInv inv = new FullFixedItemInv(slots) {
            @Override
            public boolean isItemValidForSlot(int slot, ItemStack item) {
                return getFilterForSlot(slot).matches(item);
            }

            @Override
            public ItemFilter getFilterForSlot(int slot) {
                return filters.get(slot);
            }

            @Override
            public int getMaxAmount(int slot, ItemStack stack) {
                if (data != RocketData.EMPTY) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = Galacticraft.ROCKET_PARTS.getId(data.getPartForType(RocketPartType.values()[i]));
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

        inv.fromTag(extendedInventory.toTag());

        extendedInventory = inv;

        if (!data.isEmpty()) {
            for (RocketPart part : data.getParts()) {
                fakeEntity.setPart(part);
            }
        }

        extendedInventory.addListener((x, slot, previous, current1) -> {
            if (extendedInventory.getSlotCount() == 0) {

                boolean success = true;
                for (int i = 0; i < extendedInventory.getSlotCount(); i++) {
                    if (extendedInventory.getFilterForSlot(i).matches(extendedInventory.getInvStack(i)) &&
                            extendedInventory.getMaxAmount(i, extendedInventory.getInvStack(i)) == extendedInventory.getInvStack(i).getCount()) {
                        continue;
                    }
                    success = false;
                    break;
                }
                this.ready = success;
            } else {
                this.ready = false;
            }
        }, () -> {
        });
    }

    public FullFixedItemInv getInventory() {
        return inventory;
    }

    public FullFixedItemInv getExtendedInventory() {
        return extendedInventory;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag compoundTag) {
        super.fromTag(state, compoundTag);
        this.inventory.fromTag(compoundTag);
        this.data = RocketData.fromTag(compoundTag.getCompound("data"));
        this.extendedInventory = new FullFixedItemInv(compoundTag.getList("slots", new CompoundTag().getType()).size());
        this.extendedInventory.fromTag(compoundTag);
        this.schematicUpdateFromTag();
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        CompoundTag tag = super.toTag(compoundTag);
        compoundTag.put("data", data.toTag());
        inventory.toTag(compoundTag);
        extendedInventory.toTag(compoundTag);
        return tag;
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
        if (getEnergyAttribute().getCurrentEnergy() >= getEnergyAttribute().getMaxEnergy()) {
            return;
        }
        ItemStack stack = inventory.getInvStack(ENERGY_INPUT_SLOT).copy();
        int neededEnergy = Math.min(50, getEnergyAttribute().getMaxEnergy() - getEnergyAttribute().getCurrentEnergy());
        if (GalacticraftEnergy.isEnergyItem(stack)) {
            int amountFailedToExtract = GalacticraftEnergy.extractEnergy(stack, neededEnergy);
            this.getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, neededEnergy - amountFailedToExtract, Simulation.ACTION);
            inventory.forceSetInvStack(ENERGY_INPUT_SLOT, stack);
        }

        if (this.building) { //out of 600 ticks
            if (this.energy.getCurrentEnergy() >= 20) {
                this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, Galacticraft.configManager.get().rocketAssemblerEnergyConsumptionRate(), Simulation.ACTION);
            } else {
                this.building = false;
            }

            if (progress++ >= Galacticraft.configManager.get().rocketAssemblerProcessTime()) {
                this.building = false;
                this.progress = 0;
                for (int i = 0; i < extendedInventory.getSlotCount(); i++) {
                    extendedInventory.setInvStack(i, ItemStack.EMPTY, Simulation.ACTION);
                }
                ItemStack stack1 = new ItemStack(GalacticraftItems.ROCKET);
                stack1.setTag(data.toTag());
                this.getInventory().forceSetInvStack(ROCKET_OUTPUT_SLOT, stack1);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
            }
        }
    }

    @Override
    public EnergyAttribute getEnergyAttribute() {
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
