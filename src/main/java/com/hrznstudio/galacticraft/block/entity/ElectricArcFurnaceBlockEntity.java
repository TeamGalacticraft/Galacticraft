package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.component.SubInventoryComponent;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import net.fabricmc.fabric.api.registry.FuelRegistry;
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
import java.util.Optional;
import java.util.function.Predicate;

public class ElectricArcFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public int cookTime = 0;
    public int cookLength = 0;
    private final Inventory subInv = InventoryWrapper.of(new SubInventoryComponent(this.getInventory(), new int[]{INPUT_SLOT}));

    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;

    public ElectricArcFurnaceBlockEntity(BlockEntityType<? extends ElectricArcFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    public ElectricArcFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_ARC_FURNACE_TYPE);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().electricArcFurnaceEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot == INPUT_SLOT;
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) return EnergyUtils::isEnergyItem;
        if (slot == INPUT_SLOT) return (stack) -> world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), world).isPresent();
        return (slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2) ? Constants.Misc.alwaysTrue() : Constants.Misc.alwaysFalse();
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<SmeltingRecipe> recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world);
        if (!recipe.isPresent()) return Status.NOT_ENOUGH_ITEMS;
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (!this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1, 
                recipe.get().getOutput().copy(), ActionType.TEST), ActionType.TEST).isEmpty()) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.cookLength == 0) {
                SmeltingRecipe recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElseThrow(AssertionError::new);
                this.cookLength = (int) (recipe.getCookTime() * 0.8F);
                this.cookTime = 0;
            }
            if (this.cookTime++ >= this.cookLength) {
                SmeltingRecipe recipe = this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, subInv, this.world).orElseThrow(AssertionError::new);
                if (this.getInventory().takeStack(INPUT_SLOT, 1, ActionType.PERFORM).isEmpty()) return;
                this.cookTime = 0;
                this.cookLength = 0;
                if (this.world.getRecipeManager().getFirstMatch(RecipeType.BLASTING, subInv, this.world).isPresent()) this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1, recipe.getOutput().copy(), ActionType.PERFORM), ActionType.PERFORM);
                this.getInventory().insertStack(OUTPUT_SLOT_2, this.getInventory().insertStack(OUTPUT_SLOT_1,
                        recipe.getOutput().copy(), ActionType.PERFORM), ActionType.PERFORM);

            }
        } else {
            if (this.cookTime > 0) this.cookTime--;
        }
    }

    private enum Status implements MachineStatus {
        /**
         * The electric arc furnace is cooking/smelting items
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The output slot is full.
         */
        OUTPUT_FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

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
