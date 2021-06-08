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
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.energy.impl.DefaultEnergyType;
import dev.galacticraft.energy.impl.SimpleCapacitor;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.RocketEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.recipe.RocketAssemblerRecipe;
import dev.galacticraft.mod.util.EnergyUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private static final FullFixedItemInv EMPTY_INV = new FullFixedItemInv(0);
    public static final int SCHEMATIC_INPUT_SLOT = 0;
    public static final int ROCKET_OUTPUT_SLOT = 1;
    public static final int ENERGY_INPUT_SLOT = 2;

    private final FullFixedItemInv inventory = new FullFixedItemInv(3) {
        public ItemFilter getFilterForSlot(int slot) {
            if (slot == SCHEMATIC_INPUT_SLOT) {
                return (itemStack -> itemStack.getItem() == GalacticraftItem.ROCKET_SCHEMATIC);
            } else if (slot == ENERGY_INPUT_SLOT) {
                return (EnergyUtil::isEnergyExtractable);
            } else {
                return (stack) -> stack.getItem() == GalacticraftItem.ROCKET;
            }
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return this.getFilterForSlot(slot).matches(item);
        }
    };
    public RocketData data = RocketData.empty();
    public Map<Identifier, RocketAssemblerRecipe> recipes = new HashMap<>();
    private final SimpleCapacitor energy = new SimpleCapacitor(DefaultEnergyType.INSTANCE, Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize());
    private FullFixedItemInv extendedInv = EMPTY_INV;
    private float progress = 0.0F;
    public RocketEntity fakeEntity;
    private boolean ready = false;
    private boolean building = false;
    private boolean queuedUpdate = false;

    public RocketAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ROCKET_ASSEMBLER_TYPE, pos, state);
        inventory.addListener((view, i, previous, current) -> {
            if (!world.isClient && i == SCHEMATIC_INPUT_SLOT) {
                schematicUpdate(previous, current);
                markDirty();
            }
        }, () -> {

        });
        
        fakeEntity = new RocketEntity(GalacticraftEntityType.ROCKET, null);
    }

    private void schematicUpdate(ItemStack prev, ItemStack current) {
        try {
            recipes.clear();
            for (Recipe<Inventory> recipe : world.getRecipeManager().getAllOfType(GalacticraftRecipe.ROCKET_ASSEMBLER_TYPE).values()) {
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

        if (!current.isEmpty() && current.getItem() == GalacticraftItem.ROCKET_SCHEMATIC) {
            if (this.data.equals(RocketData.fromTag(current.getTag(), this.world.getRegistryManager()))) {
                return;
            }
            this.data = RocketData.fromTag(current.getTag(), this.world.getRegistryManager());
        }

        for (int i = 0; i < extendedInv.getSlotCount(); i++) {
            ItemStack stack = extendedInv.getInvStack(i);
            extendedInv.setInvStack(i, ItemStack.EMPTY, Simulation.ACTION);
            ItemEntity entity = new ItemEntity(this.world, this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(), stack);
            this.world.spawnEntity(entity);
        }

        if (current.isEmpty()) {
            this.data = RocketData.empty();
            this.extendedInv = EMPTY_INV;
            return;
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = RocketPart.getId(this.world.getRegistryManager(), this.data.getPartForType(type));
                for (Ingredient ingredient : this.recipes.get(id).getInput().keySet()) {
                    filters.put(slots++, ingredient::test); //damage matters
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
                if (data != RocketData.empty()) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = RocketPart.getId(world.getRegistryManager(), data.getPartForType(RocketPartType.values()[i]));
                            if (a + recipes.get(id).getInput().size() > slot) {
                                for (int amount : recipes.get(id).getInput().values()) {
                                    if (a == slot) {
                                        return amount;
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
            for (RocketPart part : data.parts()) {
                if (part != null) {
                    fakeEntity.setPart(part);
                }
            }
            fakeEntity.setColor(this.data.color());
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
        for (Recipe<Inventory> recipe : world.getRecipeManager().getAllOfType(GalacticraftRecipe.ROCKET_ASSEMBLER_TYPE).values()) {
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

        if (inventory.getInvStack(SCHEMATIC_INPUT_SLOT).getItem() == GalacticraftItem.ROCKET_SCHEMATIC) {
            if (!this.data.equals(RocketData.fromTag(inventory.getInvStack(SCHEMATIC_INPUT_SLOT).copy().getOrCreateTag(), this.world.getRegistryManager()))) {
                ItemStack stack = new ItemStack(GalacticraftItem.ROCKET_SCHEMATIC);
                data.toTag(this.world.getRegistryManager(), stack.getOrCreateTag());
                schematicUpdate(stack, inventory.getInvStack(SCHEMATIC_INPUT_SLOT));
                return;
            }
        }

        int slots = 0;
        Map<Integer, ItemFilter> filters = new HashMap<>();
        RocketPartType[] values = RocketPartType.values();
        for (RocketPartType type : values) {
            if (this.data.getPartForType(type).hasRecipe()) {
                Identifier id = RocketPart.getId(world.getRegistryManager(), this.data.getPartForType(type));
                for (Ingredient ingredient : this.recipes.get(id).getInput().keySet()) {
                    filters.put(slots++, ingredient::test);
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
                if (data != RocketData.empty()) {
                    int a = 0;
                    for (int i = 0; i < RocketPartType.values().length; i++) {
                        if (data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                            Identifier id = RocketPart.getId(world.getRegistryManager(), data.getPartForType(RocketPartType.values()[i]));
                            if (a + recipes.get(id).getInput().size() > slot) {
                                for (int amount : recipes.get(id).getInput().values()) {
                                    if (a == slot) {
                                        return amount;
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
        NbtCompound tag = new NbtCompound();
        extendedInv.toTag(tag);
        inv.fromTag(tag);


        extendedInv = inv;

        if (!data.isEmpty()) {
            for (RocketPart part : data.parts()) {
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
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);
        this.inventory.fromTag(nbtCompound);
        this.data = RocketData.fromTag(nbtCompound.getCompound("data"), this.world.getRegistryManager());
        this.extendedInv = new FullFixedItemInv(nbtCompound.getInt("slots"));
        this.extendedInv.fromTag(nbtCompound);
        this.schematicUpdateFromTag();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);
        nbtCompound.put("data", data.toTag(this.world.getRegistryManager(), new NbtCompound()));
        nbtCompound.putInt("slots", extendedInv.getSlotCount());
        inventory.toTag(nbtCompound);
        extendedInv.toTag(nbtCompound);
        return nbtCompound;
    }

    @Override
    public void fromClientTag(NbtCompound nbtCompound) {
        this.readNbt(nbtCompound);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbtCompound) {
        return this.writeNbt(nbtCompound);
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
        if (EnergyUtil.isEnergyExtractable(this.getInventory().getSlot(ENERGY_INPUT_SLOT))) {
            int amountFailedToExtract = EnergyUtil.extractEnergy(this.getInventory().getSlot(ENERGY_INPUT_SLOT), neededEnergy, Simulation.ACTION);
            this.getEnergyAttribute().insert(DefaultEnergyType.INSTANCE, neededEnergy - amountFailedToExtract, Simulation.ACTION);
        }

        if (this.building) { //out of 600 ticks
            if (this.energy.getEnergy() >= 20) {
                this.energy.extract(DefaultEnergyType.INSTANCE, Galacticraft.CONFIG_MANAGER.get().rocketAssemblerEnergyConsumptionRate(), Simulation.ACTION);
            } else {
                this.building = false;
            }

            if (progress++ >= Galacticraft.CONFIG_MANAGER.get().rocketAssemblerProcessTime()) {
                this.building = false;
                this.progress = 0;
                for (int i = 0; i < extendedInv.getSlotCount(); i++) {
                    extendedInv.setInvStack(i, ItemStack.EMPTY, Simulation.ACTION);
                }
                ItemStack stack1 = new ItemStack(GalacticraftItem.ROCKET);
                data.toTag(this.world.getRegistryManager(), stack1.getOrCreateTag());
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
