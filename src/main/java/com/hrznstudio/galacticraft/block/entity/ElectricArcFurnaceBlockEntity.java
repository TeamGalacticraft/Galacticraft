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
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ElectricArcFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public int cookTime = 0;
    public int maxCookTime = 0;
    private final Inventory subInv = InventoryWrapper.of(new SubInventoryComponent(this.getInventory(), new int[]{INPUT_SLOT}));

    private static final int CHARGE_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT_1 = 2;
    private static final int OUTPUT_SLOT_2 = 3;

    public ElectricArcFurnaceBlockEntity(BlockEntityType<? extends ElectricArcFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    public ElectricArcFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_ARC_FURNACE_TYPE);
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
        return slot == 2;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return slot == 0 || slot == 1;
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
        return 4;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) return EnergyUtils::isEnergyItem;
        if (slot == INPUT_SLOT) return (stack) -> world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), world).isPresent();
        return (slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2) ? s -> true : s -> false;
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
        this.attemptChargeFromStack(CHARGE_SLOT);

        if (this.getCapacitor().getCurrentEnergy() >= this.getEnergyUsagePerTick()) {
            SmeltingRecipe recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElse(null);
            if (maxCookTime == 0) {
                if (recipe != null) {
                    if (canAcceptRecipeOutput(recipe)) {
                        this.maxCookTime = (int) (recipe.getCookTime() * 0.80D); //20% faster?
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
                        ItemStack out = new ItemStack(recipe.getOutput().getItem(), 1);
                        out.setTag(recipe.getOutput().getTag());
                        if (this.world.getRecipeManager().getFirstMatch(RecipeType.BLASTING, subInv, this.world).isPresent()) {
                            out.setCount(2);
                        }
                        this.getInventory().takeStack(INPUT_SLOT, 1, ActionType.PERFORM);
                        if (canInsert(OUTPUT_SLOT_1, out)) {
                            this.insert(OUTPUT_SLOT_1, out);
                        } else {
                            this.insert(OUTPUT_SLOT_2, out);
                        }
                        recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElse(null);
                        maxCookTime = 0;
                        cookTime = -1;
                        setStatus(Status.NOT_ENOUGH_ITEMS);
                        if (recipe != null && canAcceptRecipeOutput(recipe)) {
                            this.maxCookTime = (int) (recipe.getCookTime() * 0.85F); //15% faster?teh e
                            setStatus(Status.ACTIVE);
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
        if (!this.getInventory().getStack(INPUT_SLOT).isEmpty() && recipe != null) {
            ItemStack output = new ItemStack(recipe.getOutput().getItem(), 1);
            if (this.world.getRecipeManager().getFirstMatch(RecipeType.BLASTING, subInv, this.world).isPresent()) {
                output.setCount(2);
            }
            ItemStack stack = this.getInventory().getStack(OUTPUT_SLOT_1);
            ItemStack stack1 = this.getInventory().getStack(OUTPUT_SLOT_2);
            if (stack.isEmpty() || stack1.isEmpty()) {
                return true;
            } else if (stack.isItemEqual(output) && stack.getCount() + output.getCount() < this.getMaxCountPerStack() && stack.getCount() + output.getCount() <= stack.getMaxCount()) {
                return true;
            } else
                return stack1.isItemEqual(output) && stack1.getCount() + output.getCount() < this.getMaxCountPerStack() && stack1.getCount() + output.getCount() <= stack1.getMaxCount();
        }
        return false;
    }

    private enum Status implements MachineStatus {
        /**
         * The electric arc furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * There are no valid items to smelt/cook.
         */
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_items"), Formatting.GRAY, StatusType.MISSING_ITEMS),

        /**
         * The electric arc furnace has no more energy
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
