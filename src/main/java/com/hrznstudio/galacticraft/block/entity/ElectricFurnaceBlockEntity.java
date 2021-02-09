package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.component.SubInventoryComponent;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ElectricFurnaceBlockEntity extends ConfigurableMachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    
    public int cookTime = 0;
    public int cookLength = 0;
    private final Inventory craftingInv = InventoryWrapper.of(new SubInventoryComponent(this.getInventory(), new int[]{INPUT_SLOT}));

    public ElectricFurnaceBlockEntity(BlockEntityType<? extends ElectricFurnaceBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    public ElectricFurnaceBlockEntity() {
        this(GalacticraftBlockEntities.ELECTRIC_FURNACE_TYPE);
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().electricCompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtract(int slot) {
        return slot == OUTPUT_SLOT;
    }

    @Override
    public boolean canHopperInsert(int slot) {
        return slot == INPUT_SLOT;
    }

    @Override
    public int getInventorySize() {
        return 3;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == CHARGE_SLOT) return EnergyUtils::isEnergyItem;
        if (slot == INPUT_SLOT) return stack -> this.getRecipe(RecipeType.SMELTING, new SimpleInventory(stack)).isPresent();
        return Constants.Misc.alwaysTrue();
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        Optional<SmeltingRecipe> recipe = this.getRecipe(RecipeType.SMELTING, craftingInv);
        if (!recipe.isPresent()) return Status.NOT_ENOUGH_ITEMS;
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (!this.canInsert(OUTPUT_SLOT, recipe.get())) return Status.OUTPUT_FULL;
        return Status.ACTIVE;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            if (this.cookLength == 0) {
                SmeltingRecipe recipe = this.getRecipe(RecipeType.SMELTING, craftingInv).orElseThrow(AssertionError::new);
                this.cookLength = (int) (recipe.getCookTime() * 0.8F);
                this.cookTime = 0;
            }
            if (this.cookTime++ >= this.cookLength) {
                SmeltingRecipe recipe = this.getRecipe(RecipeType.SMELTING, craftingInv).orElseThrow(AssertionError::new);
                if (this.getInventory().takeStack(INPUT_SLOT, 1, ActionType.PERFORM).isEmpty()) return;
                this.cookTime = 0;
                this.cookLength = 0;
                this.getInventory().insertStack(OUTPUT_SLOT, recipe.getOutput().copy(), ActionType.PERFORM);
            }

        } else {
            if (this.cookTime > 0) this.cookTime--;
        }
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    private enum Status implements MachineStatus {
        /**
         * The electric furnace is cooking/smelting items
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
