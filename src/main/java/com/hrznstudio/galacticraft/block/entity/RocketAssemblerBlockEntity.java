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

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.GCRecipeAccessor;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPartType;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipe.RocketAssemblerRecipe;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
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
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    public static final int SCHEMATIC_INPUT_SLOT = 0;
    public static final int ROCKET_OUTPUT_SLOT = 1;
    public static final int ENERGY_INPUT_SLOT = 2;

    private final SimpleInventoryComponent inventory = new SimpleInventoryComponent(3) {
        @Override
        public boolean isAcceptableStack(int slot, ItemStack item) {
            return getFilterForSlot(slot).test(item);
        }

        public Predicate<ItemStack> getFilterForSlot(int slot) {
            if (slot == SCHEMATIC_INPUT_SLOT) {
                return (itemStack -> itemStack.getItem() == GalacticraftItems.ROCKET_SCHEMATIC);
            } else if (slot == ENERGY_INPUT_SLOT) {
                return (EnergyUtils::isEnergyItem);
            } else {
                return (stack) -> stack.getItem() == GalacticraftItems.ROCKET;
            }
        }
    };
    public RocketData data = RocketData.EMPTY;
    public Map<Identifier, RocketAssemblerRecipe> recipes = new HashMap<>();
    private final SimpleCapacitorComponent energy = new SimpleCapacitorComponent(Galacticraft.configManager.get().machineEnergyStorageSize(), GalacticraftEnergy.GALACTICRAFT_JOULES);
    private SimpleInventoryComponent extendedInventory = new SimpleInventoryComponent(0) {
        @Override
        public boolean isAcceptableStack(int slot, ItemStack item) {
            return false;
        }
    };
    private float progress = 0.0F;
    public RocketEntity fakeEntity;
    private boolean ready = false;
    private boolean building = false;
    private boolean queuedUpdate = false;
    private ItemStack previous = ItemStack.EMPTY;

    public RocketAssemblerBlockEntity() {
        super(GalacticraftBlockEntities.ROCKET_ASSEMBLER_TYPE);
        inventory.listen(() -> {
            if (!world.isClient) {
                if (!inventory.getStack(SCHEMATIC_INPUT_SLOT).getOrCreateTag().equals(previous.getOrCreateTag())) {
                    schematicUpdate(previous.copy(), inventory.getStack(SCHEMATIC_INPUT_SLOT));
                }
                markDirty();
                previous = inventory.getStack(SCHEMATIC_INPUT_SLOT).copy();
            }
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

        for (int i = 0; i < extendedInventory.getSize(); i++) {
            ItemStack stack = extendedInventory.getStack(i);
            extendedInventory.setStack(i, ItemStack.EMPTY);
            ItemEntity entity = new ItemEntity(this.world, this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), stack);
            this.world.spawnEntity(entity);
        }

        if (current.isEmpty()) {
            this.data = RocketData.EMPTY;
            this.extendedInventory = new SimpleInventoryComponent(0) {
                @Override
                public boolean isAcceptableStack(int slot, ItemStack item) {
                    return false;
                }
            };

            return;
        }

        int slots = 0;
        Map<Integer, Predicate<ItemStack>> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = Galacticraft.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack::isItemEqual); //damage matters
                }
            }
        }

        extendedInventory = new SimpleInventoryComponent(slots) {
            @Override
            public boolean isAcceptableStack(int slot, ItemStack item) {
                return filters.get(slot).test(item);
            }

            @Override
            public int getMaxStackSize(int slot) { //ignore default 
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
                if (part != null) {
                    fakeEntity.setPart(part);
                }
            }
            fakeEntity.setColor(this.data.getRed(), this.data.getGreen(), this.data.getBlue(), this.data.getAlpha());
        }

        extendedInventory.listen(() -> {
            boolean success = true;
            for (int i = 0; i < extendedInventory.getSize(); i++) {
                if (extendedInventory.isAcceptableStack(i, extendedInventory.getStack(i)) &&
                        extendedInventory.getMaxStackSize(i) == extendedInventory.getStack(i).getCount()) {
                    continue;
                }
                success = false;
                break;
            }
            this.ready = success;
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

        if (this.data.isEmpty() && this.inventory.getStack(SCHEMATIC_INPUT_SLOT).isEmpty()) {
            return;
        }

        if ((!this.data.isEmpty() && inventory.getStack(SCHEMATIC_INPUT_SLOT).isEmpty()) || (this.data.isEmpty() && !inventory.getStack(SCHEMATIC_INPUT_SLOT).isEmpty())) {
            throw new RuntimeException("Error loading schematic!");
        }

        if (inventory.getStack(SCHEMATIC_INPUT_SLOT).getItem() == GalacticraftItems.ROCKET_SCHEMATIC) {
            if (!this.data.equals(RocketData.fromItem(inventory.getStack(SCHEMATIC_INPUT_SLOT)))) {
                schematicUpdate(data.toSchematic(), inventory.getStack(SCHEMATIC_INPUT_SLOT));
                return;
            }
        }

        int slots = 0;
        Map<Integer, Predicate<ItemStack>> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = Galacticraft.ROCKET_PARTS.getId(this.data.getPartForType(type));
                for (ItemStack stack : this.recipes.get(id).getInput()) {
                    filters.put(slots++, stack1 -> stack.getItem().equals(stack1.getItem()));
                }
            }
        }

        SimpleInventoryComponent inv = new SimpleInventoryComponent(slots) {
            @Override
            public boolean isAcceptableStack(int slot, ItemStack stack) {
                return filters.get(slot).test(stack);
            }

            @Override
            public int getMaxStackSize(int slot) {
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

        inv.fromTag(extendedInventory.toTag(new CompoundTag()));

        extendedInventory = inv;

        if (!data.isEmpty()) {
            for (RocketPart part : data.getParts()) {
                if (part != null) {
                    fakeEntity.setPart(part);
                }
            }
        }

        extendedInventory.listen(() -> {
            if (extendedInventory.getSize() == 0) {

                boolean success = true;
                for (int i = 0; i < extendedInventory.getSize(); i++) {
                    if (extendedInventory.isAcceptableStack(i, extendedInventory.getStack(i)) &&
                            extendedInventory.getMaxStackSize(i) == extendedInventory.getStack(i).getCount()) {
                        continue;
                    }
                    success = false;
                    break;
                }
                this.ready = success;
            } else {
                this.ready = false;
            }
        });
    }

    public SimpleInventoryComponent getInventory() {
        return inventory;
    }

    public SimpleInventoryComponent getExtendedInventory() {
        return extendedInventory;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag compoundTag) {
        super.fromTag(state, compoundTag);
        this.inventory.fromTag(compoundTag);
        this.data = RocketData.fromTag(compoundTag.getCompound("data"));
        this.extendedInventory = new SimpleInventoryComponent(compoundTag.getInt("slots"));
        this.extendedInventory.fromTag(compoundTag);
        this.schematicUpdateFromTag();
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("data", data.toTag());
        compoundTag.putInt("slots", extendedInventory.getSize());
        inventory.toTag(compoundTag);
        extendedInventory.toTag(compoundTag);
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
        if (getEnergyAttribute().getCurrentEnergy() >= getEnergyAttribute().getMaxEnergy()) {
            return;
        }
        ItemStack stack = inventory.getStack(ENERGY_INPUT_SLOT).copy();
        int neededEnergy = Math.min(50, getEnergyAttribute().getMaxEnergy() - getEnergyAttribute().getCurrentEnergy());
        if (EnergyUtils.isEnergyItem(stack)) {
            int amountFailedToExtract = EnergyUtils.extractEnergy(stack, neededEnergy, ActionType.PERFORM);
            this.getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, neededEnergy - amountFailedToExtract, ActionType.PERFORM);
            inventory.setStack(ENERGY_INPUT_SLOT, stack);
        }

        if (this.building) { //out of 600 ticks
            if (this.energy.getCurrentEnergy() >= 20) {
                this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, Galacticraft.configManager.get().rocketAssemblerEnergyConsumptionRate(), ActionType.PERFORM);
            } else {
                this.building = false;
            }

            if (progress++ >= Galacticraft.configManager.get().rocketAssemblerProcessTime()) {
                this.building = false;
                this.progress = 0;
                for (int i = 0; i < extendedInventory.getSize(); i++) {
                    extendedInventory.setStack(i, ItemStack.EMPTY);
                }
                ItemStack stack1 = new ItemStack(GalacticraftItems.ROCKET);
                stack1.setTag(data.toTag());
                this.inventory.setStack(ROCKET_OUTPUT_SLOT, stack1);
            }
        } else {
            if (this.progress > 0) {
                this.progress--;
            }
        }
    }

    public SimpleCapacitorComponent getEnergyAttribute() {
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
