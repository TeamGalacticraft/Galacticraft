package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.component.SubInventoryComponent;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ElectricFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public int cookTime = 0;
    public int maxCookTime = 0;
    private final Inventory subInv = InventoryWrapper.of(new SubInventoryComponent(this.getInventory(), new int[]{0}));

    public ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }
    public ElectricFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_FURNACE_TYPE);
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return slot == 1;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return slot == 0;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }

    @Override
    public int getInventorySize() {
        return 3;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return slot == 2 ? EnergyUtils::isEnergyItem : stack -> true;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    @Override
    public void tick() {
        if (world.isClient) return;
        if (disabled()) {
            this.idleEnergyDecrement(true);
            return;
        }
        this.attemptChargeFromStack(2);

        if (this.getCapacitor().getCurrentEnergy() >= this.getEnergyUsagePerTick()) {
            SmeltingRecipe recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElse(null);
            if (maxCookTime == 0) {
                if (recipe != null && canAcceptRecipeOutput(recipe)) {
                    if (canInsert(1, recipe.getOutput())) {
                        this.maxCookTime = (int) (recipe.getCookTime() * 0.85D); //15% faster?
                        setStatus(Status.ACTIVE);
                    } else {
                        setStatus(Status.FULL);
                    }
                } else {
                    setStatus(Status.NOT_ENOUGH_ITEMS);
                    maxCookTime = 0;
                    if (cookTime > 0) {
                        cookTime--;
                    }
                }
            } else {
                if (recipe != null && canAcceptRecipeOutput(recipe)) {
                    if (!world.isClient) this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
                    if (cookTime++ >= maxCookTime) {
                        this.getInventory().takeStack(0, 1, ActionType.PERFORM);
                        this.insert(1, recipe.getOutput().copy());
                        recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElse(null);
                        maxCookTime = 0;
                        cookTime = -1;
                        setStatus(Status.NOT_ENOUGH_ITEMS);
                        if (recipe != null) {
                            if (canInsert(1, recipe.getOutput())) {
                                this.maxCookTime = (int) (recipe.getCookTime() * 0.85F); //15% faster?
                                setStatus(Status.ACTIVE);
                            } else {
                                setStatus(Status.FULL);
                            }
                        }
                    }
                } else {
                    cookTime--;
                    maxCookTime = 0;
                }
            }
        } else {
            setStatus(Status.NOT_ENOUGH_ENERGY);
            maxCookTime = 0;
            if (cookTime > 0) {
                cookTime--;
            }
        }
    }

    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (!this.getInventory().getStack(0).isEmpty() && recipe != null) {
            ItemStack output = recipe.getOutput();
            if (output.isEmpty()) {
                return false;
            } else {
                ItemStack stack = this.getInventory().getStack(1);
                if (stack.isEmpty()) {
                    return true;
                } else if (!stack.isItemEqualIgnoreDamage(output)) {
                    return false;
                } else if (stack.getCount() < this.getMaxCountPerStack() && stack.getCount() < stack.getMaxCount()) {
                    return true;
                } else {
                    return stack.getCount() < output.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private enum Status implements MachineStatus {
        /**
         * The electric furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * There are no valid items to smelt/cook.
         */
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_items"), Formatting.GRAY, StatusType.MISSING_ITEMS),

        /**
         * The electric furnace has no more energy
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

        private final Text name;
        private final StatusType type;

        Status(MutableText name, Formatting color, StatusType type) {
            this.type = type;
            this.name = name.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
            return name;
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
