package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.compat.SubInventoryComponent;
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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ElectricFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public int cookTime = 0;
    public int maxCookTime = 0;
    public ElectricFurnaceStatus status = ElectricFurnaceStatus.IDLE;
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
    public boolean canExtractOxygen(int tank) {
        return false;
    }

    @Override
    public boolean canInsertOxygen(int tank) {
        return false;
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
    public int getOxygenTankSize() {
        return 0;
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
                        status = ElectricFurnaceStatus.ACTIVE;
                    } else {
                        status = ElectricFurnaceStatus.FULL;
                    }
                } else {
                    status = ElectricFurnaceStatus.IDLE;
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
                        status = ElectricFurnaceStatus.IDLE;
                        if (recipe != null) {
                            if (canInsert(1, recipe.getOutput())) {
                                this.maxCookTime = (int) (recipe.getCookTime() * 0.85F); //15% faster?
                                status = ElectricFurnaceStatus.ACTIVE;
                            } else {
                                status = ElectricFurnaceStatus.FULL;
                            }
                        }
                    }
                } else {
                    cookTime--;
                    maxCookTime = 0;
                }
            }
        } else {
            status = ElectricFurnaceStatus.NOT_ENOUGH_ENERGY;
            maxCookTime = 0;
            if (cookTime > 0) {
                cookTime--;
            }
        }
    }

    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (!this.getInventory().getStack(0).isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.getOutput();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = this.getInventory().getStack(1);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < this.getMaxCountPerStack() && itemStack2.getCount() < itemStack2.getMaxCount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public MachineStatus getStatusForTooltip() {
        return status;
    }

    public enum ElectricFurnaceStatus implements MachineStatus {
        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active").setStyle(Style.EMPTY.withColor(Formatting.GREEN))),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(Style.EMPTY.withColor(Formatting.GOLD))),

        /**
         * The refinery is out of oil.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(Style.EMPTY.withColor(Formatting.GRAY))),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy").setStyle(Style.EMPTY.withColor(Formatting.RED)));

        private final Text text;

        ElectricFurnaceStatus(Text text) {
            this.text = text;
        }

        @Override
        public Text getText() {
            return this.text;
        }
    }
}
