/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.gametest.machine;

import dev.galacticraft.machinelib.api.gametest.RecipeGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.MachineTest;
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@TestSuite("electric_furnace")
public final class ElectricFurnaceTestSuite extends RecipeGameTest<SingleRecipeInput, SmeltingRecipe, ElectricFurnaceBlockEntity> {
    public ElectricFurnaceTestSuite() {
        super(GCBlocks.ELECTRIC_FURNACE, List.of(
                machine -> machine.energyStorage().setEnergy(Long.MAX_VALUE / 2),
                machine -> machine.itemStorage().slot(ElectricFurnaceBlockEntity.INPUT_SLOT).set(Items.PORKCHOP, 1)
        ), ElectricFurnaceBlockEntity.OUTPUT_SLOT, 200);
    }

    @MachineTest(workTime = 200)
    public Runnable storeExperience(ElectricFurnaceBlockEntity machine) {
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        machine.itemStorage().slot(ElectricFurnaceBlockEntity.INPUT_SLOT).set(Items.PORKCHOP, 1);
        return () -> Assertions.assertEquals(1, machine.getRecipesUsed().getInt(ResourceLocation.withDefaultNamespace("cooked_porkchop")), "Failed to store the experience of the crafted recipe!");
    }

    @MachineTest
    public Runnable saveStoredExperience(ElectricFurnaceBlockEntity machine) {
        ResourceLocation id = ResourceLocation.withDefaultNamespace("green_dye");
        machine.getRecipesUsed().put(id, 3);
        CompoundTag tag = machine.saveWithoutMetadata(machine.getLevel().registryAccess());
        machine.getRecipesUsed().clear();
        machine.loadWithComponents(tag, machine.getLevel().registryAccess());
        return () -> Assertions.assertEquals(3, machine.getRecipesUsed().getInt(id), "Failed to save the stored experience!");
    }

    @MachineTest
    public Runnable popExperience(ElectricFurnaceBlockEntity machine) {
        ServerLevel level = (ServerLevel) machine.getLevel();
        BlockPos pos = machine.getBlockPos();
        machine.getRecipesUsed().put(ResourceLocation.withDefaultNamespace("green_dye"), 10);
        machine.popExperience(level, Vec3.atCenterOf(pos));
        return () -> {
            int experience = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos).inflate(2.0D)).stream().mapToInt(ExperienceOrb::getValue).sum();
            Assertions.assertEquals(10, experience, "Failed to drop the stored experience!");
            Assertions.assertTrue(machine.getRecipesUsed().isEmpty(), "Failed to clear the stored experience!");
        };
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> functions = super.registerTests();
        functions.add(this.createChargeFromEnergyItemTest(ElectricFurnaceBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return functions;
    }
}
