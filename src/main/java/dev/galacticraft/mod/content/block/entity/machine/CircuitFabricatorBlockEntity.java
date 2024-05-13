/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.RecipeTestContainer;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CircuitFabricatorBlockEntity extends RecipeMachineBlockEntity<Container, FabricationRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int DIAMOND_SLOT = 1;
    public static final int SILICON_SLOT_1 = 2;
    public static final int SILICON_SLOT_2 = 3;
    public static final int REDSTONE_SLOT = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private final Container craftingInv;

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.CIRCUIT_FABRICATOR, pos, state, GCRecipes.FABRICATION_TYPE);

        this.craftingInv = RecipeTestContainer.create(this.itemStorage().getSlot(INPUT_SLOT));
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("charge");
        this.chargeFromStack(CHARGE_SLOT);
        profiler.pop();
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG.circuitFabricatorEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extractExact(Galacticraft.CONFIG.circuitFabricatorEnergyConsumptionRate());
    }

    @Override
    protected boolean canOutputStacks(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        ItemStack output = recipe.value().getResultItem(this.level.registryAccess());
        return this.itemStorage().getSlot(OUTPUT_SLOT).canInsert(output.getItem(), output.getTag(), output.getCount());
    }

    @Override
    protected @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected void outputStacks(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        ItemStack output = recipe.value().getResultItem(this.level.registryAccess());
        this.itemStorage().getSlot(OUTPUT_SLOT).insert(output.getItem(), output.getTag(), output.getCount());
    }

    @Override
    protected void extractCraftingMaterials(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        NonNullList<ItemStack> remainder = recipe.value().getRemainingItems(this.craftingInv);
        this.itemStorage().getSlot(DIAMOND_SLOT).extractOne();
        this.itemStorage().getSlot(SILICON_SLOT_1).extractOne();
        this.itemStorage().getSlot(SILICON_SLOT_2).extractOne();
        this.itemStorage().getSlot(REDSTONE_SLOT).extractOne();

        ItemResourceSlot input = this.itemStorage().getSlot(INPUT_SLOT);
        input.extractOne();

        if (input.isEmpty() && remainder.size() > 0) {
            ItemStack itemStack = remainder.get(0);
            if (!itemStack.isEmpty()) {
                input.insert(itemStack.getItem(), itemStack.getTag(), itemStack.getCount());
            }
        }
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<FabricationRecipe> recipe) {
        return GCMachineStatuses.FABRICATING;
    }

    @Override
    protected @Nullable RecipeHolder<FabricationRecipe> findValidRecipe(@NotNull Level world) {
        if (this.itemStorage().getSlot(DIAMOND_SLOT).contains(Items.DIAMOND)
                && this.itemStorage().getSlot(SILICON_SLOT_1).contains(GCItems.RAW_SILICON)
                && this.itemStorage().getSlot(SILICON_SLOT_2).contains(GCItems.RAW_SILICON)
                && this.itemStorage().getSlot(REDSTONE_SLOT).contains(Items.REDSTONE)) {
            return super.findValidRecipe(world);
        }

        return null;
    }

    @Override
    public int getProcessingTime(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        return recipe.value().getProcessingTime();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new RecipeMachineMenu<>(
                    syncId,
                    ((ServerPlayer) player),
                    this
            );
        }
        return null;
    }
}
