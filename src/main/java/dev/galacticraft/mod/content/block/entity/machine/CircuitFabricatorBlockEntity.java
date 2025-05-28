/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.vanilla.RecipeHelper;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CircuitFabricatorBlockEntity extends RecipeMachineBlockEntity<RecipeInput, FabricationRecipe> {
    public static final int CHARGE_SLOT = 0;
    public static final int DIAMOND_SLOT = 1;
    public static final int SILICON_SLOT_1 = 2;
    public static final int SILICON_SLOT_2 = 3;
    public static final int REDSTONE_SLOT = 4;
    public static final int INPUT_SLOT = 5;
    public static final int OUTPUT_SLOT = 6;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 72)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(31, 17)
                            .filter(ResourceFilters.itemTag(FabricationRecipe.DIAMOND_SLOT_TAG))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.DIAMOND)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(62, 47)
                            .filter(ResourceFilters.itemTag(FabricationRecipe.SILICON_SLOT_1_TAG))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.SILICON)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(62, 65)
                            .filter(ResourceFilters.itemTag(FabricationRecipe.SILICON_SLOT_2_TAG))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.SILICON)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(107, 72)
                            .filter(ResourceFilters.itemTag(FabricationRecipe.REDSTONE_SLOT_TAG))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.DUST)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(134, 17),
                    ItemResourceSlot.builder(TransferType.OUTPUT)
                            .pos(152, 72)
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.circuitFabricatorEnergyConsumptionRate() * 2,
                    0
            )
    );

    public CircuitFabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.CIRCUIT_FABRICATOR, pos, state, GCRecipes.FABRICATION_TYPE, SPEC);
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("charge");
        this.chargeFromSlot(CHARGE_SLOT);
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
        return this.itemStorage().slot(OUTPUT_SLOT).canInsert(output.getItem(), output.getComponentsPatch(), output.getCount());
    }

    @Override
    protected void outputStacks(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        ItemStack output = recipe.value().getResultItem(this.level.registryAccess());
        this.itemStorage().slot(OUTPUT_SLOT).insert(output.getItem(), output.getComponentsPatch(), output.getCount());
    }

    @Override
    protected @NotNull RecipeInput craftingInv() {
        return RecipeHelper.input(
                this.itemStorage().slot(DIAMOND_SLOT),
                this.itemStorage().slot(SILICON_SLOT_1),
                this.itemStorage().slot(SILICON_SLOT_2),
                this.itemStorage().slot(REDSTONE_SLOT),
                this.itemStorage().slot(INPUT_SLOT)
        );
    }

    @Override
    protected void extractCraftingMaterials(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        NonNullList<ItemStack> remainder = recipe.value().getRemainingItems(this.craftingInv());
        this.itemStorage().slot(DIAMOND_SLOT).extractOne();
        this.itemStorage().slot(SILICON_SLOT_1).extractOne();
        this.itemStorage().slot(SILICON_SLOT_2).extractOne();
        this.itemStorage().slot(REDSTONE_SLOT).extractOne();

        ItemResourceSlot input = this.itemStorage().slot(INPUT_SLOT);
        input.extractOne();

        if (input.isEmpty() && remainder.size() > 0) {
            ItemStack itemStack = remainder.get(0);
            if (!itemStack.isEmpty()) {
                input.insert(itemStack.getItem(), itemStack.getComponentsPatch(), itemStack.getCount());
            }
        }
    }

    @Override
    protected @NotNull MachineStatus workingStatus(RecipeHolder<FabricationRecipe> recipe) {
        return GCMachineStatuses.FABRICATING;
    }

    @Override
    public int getProcessingTime(@NotNull RecipeHolder<FabricationRecipe> recipe) {
        return recipe.value().getProcessingTime();
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new RecipeMachineMenu<>(
                GCMenuTypes.CIRCUIT_FABRICATOR,
                syncId,
                player,
                this
        );
    }
}
