/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CircuitFabricatorBlockEntity extends RecipeMachineBlockEntity<Container, FabricationRecipe> {
    private final Container craftingInv;

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.CIRCUIT_FABRICATOR, pos, state, GalacticraftRecipe.FABRICATION_TYPE);
        this.craftingInv = this.itemStorage().getCraftingView(GCSlotGroupTypes.GENERIC_INPUT);
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("charge");
        this.attemptChargeFromStack(GCSlotGroupTypes.ENERGY_TO_SELF);
        profiler.pop();
    }

    @Override
    protected @Nullable MachineStatus hasResourcesToWork() {
        return this.energyStorage().canExtract(Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate()) ? null : MachineStatuses.NOT_ENOUGH_ENERGY;
    }

    @Override
    protected void extractResourcesToWork() {
        this.energyStorage().extractExact(Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate());
    }

    @Override
    public @NotNull Container craftingInv() {
        return this.craftingInv;
    }

    @Override
    protected boolean canOutputStacks(@NotNull FabricationRecipe recipe) {
        ItemStack output = recipe.getResultItem();
        return this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).canInsert(output.getItem(), output.getTag(), output.getCount());
    }

    @Override
    protected void outputStacks(@NotNull FabricationRecipe recipe) {
        ItemStack output = recipe.getResultItem();
        this.itemStorage().getGroup(GCSlotGroupTypes.GENERIC_OUTPUT).insert(output.getItem(), output.getTag(), output.getCount());
    }

    @Override
    protected void extractCraftingMaterials(@NotNull FabricationRecipe recipe) {
        NonNullList<ItemStack> remainder = recipe.getRemainingItems(this.craftingInv());
        this.itemStorage().getSlot(GCSlotGroupTypes.DIAMOND_INPUT).extractOne();
        SlotGroup<Item, ItemStack, ItemResourceSlot> siliconGroup = this.itemStorage().getGroup(GCSlotGroupTypes.SILICON_INPUT);
        siliconGroup.getSlot(0).extractOne();
        siliconGroup.getSlot(1).extractOne();
        this.itemStorage().getSlot(GCSlotGroupTypes.REDSTONE_INPUT).extractOne();
        this.itemStorage().getSlot(GCSlotGroupTypes.REDSTONE_INPUT).extractOne();

        ItemResourceSlot input = this.itemStorage().getSlot(GCSlotGroupTypes.GENERIC_INPUT);
        input.extractOne();

        if (input.isEmpty() && remainder.size() > 0) {
            ItemStack itemStack = remainder.get(0);
            if (!itemStack.isEmpty()) {
                input.insert(itemStack.getItem(), itemStack.getTag(), itemStack.getCount());
            }
        }
    }

    @Override
    protected @NotNull MachineStatus workingStatus() {
        return GCMachineStatus.FABRICATING;
    }

    @Override
    protected @Nullable FabricationRecipe findValidRecipe(@NotNull Level world) {
            if (this.itemStorage().getSlot(GCSlotGroupTypes.DIAMOND_INPUT).contains(Items.DIAMOND)
                    && this.itemStorage().getGroup(GCSlotGroupTypes.SILICON_INPUT).getSlot(0).contains(GCItem.RAW_SILICON)
                    && this.itemStorage().getGroup(GCSlotGroupTypes.SILICON_INPUT).getSlot(1).contains(GCItem.RAW_SILICON)
                    && this.itemStorage().getSlot(GCSlotGroupTypes.REDSTONE_INPUT).contains(Items.REDSTONE)) {
                return super.findValidRecipe(world);
            }

        return null;
    }

    @Override
    protected int getProcessTime(@NotNull FabricationRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new RecipeMachineMenu<>(
                    syncId,
                    ((ServerPlayer) player),
                    this,
                    GCMachineTypes.CIRCUIT_FABRICATOR
            );
        }
        return null;
    }
}
