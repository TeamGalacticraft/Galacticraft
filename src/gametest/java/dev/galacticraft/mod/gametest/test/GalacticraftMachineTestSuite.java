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

package dev.galacticraft.mod.gametest.test;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.misc.Reference;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.*;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.gametest.GalacticraftGameTest;
import dev.galacticraft.mod.item.BatteryItem;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class GalacticraftMachineTestSuite implements GalacticraftGameTest {
    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 372)
    public void coalGeneratorGenerationTest(TestContext context) {
        final var coalGeneratorPos = new BlockPos(0, 0, 0);
        context.setBlockState(coalGeneratorPos, GalacticraftBlock.COAL_GENERATOR);
        BlockEntity blockEntity = context.getBlockEntity(coalGeneratorPos);
        if (blockEntity instanceof CoalGeneratorBlockEntity coalGenerator) {
            coalGenerator.itemInv().setInvStack(CoalGeneratorBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                ItemStack invStack = coalGenerator.itemInvView().getInvStack(CoalGeneratorBlockEntity.FUEL_SLOT);
                if (!invStack.isEmpty()) {
                    context.throwPositionedException(String.format("Expected coal generator inventory to be empty but found %s!", invStack), coalGeneratorPos);
                }
                context.runAtTick(context.getTick() + 320 + 50 + 1, () -> {
                    context.addInstantFinalTask(() -> {
                        if (coalGenerator.getHeat() > 0) {
                            context.throwPositionedException("Expected coal generator to have cooled off!", coalGeneratorPos);
                        }
                        if (coalGenerator.capacitorView().getEnergy() != 26491) {
                            context.throwPositionedException(String.format("Expected coal generator to have 25006 energy! Found: %s", coalGenerator.capacitorView().getEnergy()), coalGeneratorPos);
                        }
                    });
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected coal generator but found %s instead!", blockEntity), coalGeneratorPos);
        }
    }

    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 303)
    public void circuitFabricatorFabricationTest(TestContext context) {
        final var circuitFabricatorPos = new BlockPos(0, 0, 0);
        context.setBlockState(circuitFabricatorPos, GalacticraftBlock.CIRCUIT_FABRICATOR);
        BlockEntity blockEntity = context.getBlockEntity(circuitFabricatorPos);
        if (blockEntity instanceof CircuitFabricatorBlockEntity circuitFabricator) {
            FixedItemInv inv = circuitFabricator.itemInv();
            inv.setInvStack(CircuitFabricatorBlockEntity.CHARGE_SLOT, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                if (circuitFabricator.capacitorView().getEnergy() == 0) {
                    context.throwPositionedException("Expected circuit fabricator to charge from battery but it did not!", circuitFabricatorPos);
                }

                inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT_DIAMOND, new ItemStack(Items.DIAMOND, 2), Simulation.ACTION);
                inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT_SILICON, new ItemStack(GalacticraftItem.RAW_SILICON, 2), Simulation.ACTION);
                inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT_SILICON_2, new ItemStack(GalacticraftItem.RAW_SILICON, 2), Simulation.ACTION);
                inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT_REDSTONE, new ItemStack(Items.REDSTONE, 2), Simulation.ACTION);
                inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT, new ItemStack(Items.REDSTONE_TORCH), Simulation.ACTION);

                context.runAtTick(context.getTick() + 300 + 1, () -> {
                    ItemStack output = inv.getInvStack(CircuitFabricatorBlockEntity.OUTPUT_SLOT);
                    if (output.getItem() != GalacticraftItem.BASIC_WAFER) {
                        context.throwPositionedException(String.format("Expected circuit fabricator to have made a wafer but found %s instead!", output), circuitFabricatorPos);
                    }
                    inv.setInvStack(CircuitFabricatorBlockEntity.INPUT_SLOT, new ItemStack(Items.REPEATER), Simulation.ACTION);
                    context.runAtTick(context.getTick() + 1, () -> context.addInstantFinalTask(() -> {
                        if (circuitFabricator.maxProgress() != 0) {
                            context.throwPositionedException("Expected circuit fabricator to be unable to craft as the output was full!", circuitFabricatorPos);
                        }
                    }));
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected circuit fabricator but found %s instead!", blockEntity), circuitFabricatorPos);
        }
    }


    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 203)
    public void compressorCompressingTest(TestContext context) {
        final var compressorPos = new BlockPos(0, 0, 0);
        context.setBlockState(compressorPos, GalacticraftBlock.COMPRESSOR);
        BlockEntity blockEntity = context.getBlockEntity(compressorPos);
        if (blockEntity instanceof CompressorBlockEntity compressor) {
            FixedItemInv inv = compressor.itemInv();
            inv.setInvStack(CompressorBlockEntity.FUEL_INPUT_SLOT, new ItemStack(Items.COAL), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                ItemStack fuelStack = inv.getInvStack(CompressorBlockEntity.FUEL_INPUT_SLOT);
                if (fuelStack.isEmpty()) {
                    context.throwPositionedException("Expected compressor to not use fuel while idle!", compressorPos);
                }
                inv.setInvStack(0, new ItemStack(Items.IRON_INGOT), Simulation.ACTION);
                inv.setInvStack(1, new ItemStack(Items.IRON_INGOT), Simulation.ACTION);

                context.runAtTick(context.getTick() + 200 + 1, () -> {
                    ItemStack output = inv.getInvStack(CompressorBlockEntity.OUTPUT_SLOT);
                    if (output.getItem() != GalacticraftItem.COMPRESSED_IRON) {
                        context.throwPositionedException(String.format("Expected compressor to have made compressed iron but found %s instead!", output), compressorPos);
                    }
                    inv.setInvStack(0, new ItemStack(GalacticraftItem.TIN[1]), Simulation.ACTION);
                    inv.setInvStack(1, new ItemStack(GalacticraftItem.TIN[1]), Simulation.ACTION);
                    context.runAtTick(context.getTick() + 1, () -> context.addInstantFinalTask(() -> {
                        if (compressor.maxProgress() != 0) {
                            context.throwPositionedException("Expected compressor to be unable to craft as the output was full!", compressorPos);
                        }
                    }));
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected compressor but found %s instead!", blockEntity), compressorPos);
        }
    }

    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 404)
    public void electricCompressorCompressingTest(TestContext context) {
        final var compressorPos = new BlockPos(0, 0, 0);
        context.setBlockState(compressorPos, GalacticraftBlock.ELECTRIC_COMPRESSOR);
        BlockEntity blockEntity = context.getBlockEntity(compressorPos);
        if (blockEntity instanceof ElectricCompressorBlockEntity compressor) {
            FixedItemInv inv = compressor.itemInv();
            inv.setInvStack(ElectricCompressorBlockEntity.CHARGE_SLOT, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                if (compressor.capacitorView().getEnergy() == 0) {
                    context.throwPositionedException("Expected electric compressor to charge from stack!", compressorPos);
                }
                inv.setInvStack(0, new ItemStack(Items.IRON_INGOT), Simulation.ACTION);
                inv.setInvStack(1, new ItemStack(Items.IRON_INGOT), Simulation.ACTION);

                context.runAtTick(context.getTick() + 200 + 1, () -> {
                    ItemStack output = inv.getInvStack(ElectricCompressorBlockEntity.OUTPUT_SLOT);
                    if (output.getItem() != GalacticraftItem.COMPRESSED_IRON) {
                        context.throwPositionedException(String.format("Expected electric compressor to have made compressed iron but found %s instead!", output), compressorPos);
                    }
                    inv.setInvStack(0, new ItemStack(GalacticraftItem.TIN[1]), Simulation.ACTION);
                    inv.setInvStack(1, new ItemStack(GalacticraftItem.TIN[1]), Simulation.ACTION);
                    context.runAtTick(context.getTick() + 200 + 1, () -> {
                        ItemStack output2 = inv.getInvStack(ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT);
                        if (output2.getItem() != GalacticraftItem.COMPRESSED_TIN) {
                            context.throwPositionedException(String.format("Expected electric compressor to have made compressed tin but found %s instead!", output), compressorPos);
                        }
                        inv.setInvStack(0, new ItemStack(GalacticraftItem.ALUMINUM[1]), Simulation.ACTION);
                        inv.setInvStack(1, new ItemStack(GalacticraftItem.ALUMINUM[1]), Simulation.ACTION);
                        context.runAtTick(context.getTick() + 1, () -> context.addInstantFinalTask(() -> {
                            if (compressor.maxProgress() != 0) {
                                context.throwPositionedException("Expected electric compressor to be unable to craft as the outputs were full!", compressorPos);
                            }
                        }));
                    });
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected electric compressor but found %s instead!", blockEntity), compressorPos);
        }
    }

    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 302)
    public void energyStorageModuleStorageTest(TestContext context) {
        final var energyStorageModulePos = new BlockPos(0, 0, 0);
        context.setBlockState(energyStorageModulePos, GalacticraftBlock.ENERGY_STORAGE_MODULE);
        BlockEntity blockEntity = context.getBlockEntity(energyStorageModulePos);
        if (blockEntity instanceof EnergyStorageModuleBlockEntity energyStorageModule) {
            FixedItemInv inv = energyStorageModule.itemInv();
            inv.setInvStack(EnergyStorageModuleBlockEntity.DRAIN_BATTERY_SLOT, new ItemStack(GalacticraftItem.BATTERY), Simulation.ACTION);
            Reference<ItemStack> slotRef = inv.getSlot(EnergyStorageModuleBlockEntity.DRAIN_BATTERY_SLOT);
            EnergyUtil.setEnergy(slotRef, BatteryItem.MAX_ENERGY);
            context.runAtTick(context.getTick() + 150 + 1, () -> {
                int energy = energyStorageModule.capacitorView().getEnergy();
                if (energy != 15000) {
                    context.throwPositionedException(String.format("Expected energy storage module to have 15000gJ but it had %s!", energy), energyStorageModulePos);
                }

                inv.setInvStack(EnergyStorageModuleBlockEntity.CHARGE_BATTERY_SLOT, new ItemStack(GalacticraftItem.BATTERY), Simulation.ACTION);
                Reference<ItemStack> slotRef1 = inv.getSlot(EnergyStorageModuleBlockEntity.CHARGE_BATTERY_SLOT);
                EnergyUtil.setEnergy(slotRef1, 0);

                context.runAtTick(context.getTick() + 150 + 1, () -> context.addInstantFinalTask(() -> {
                    int energy1 = energyStorageModule.capacitorView().getEnergy();
                    if (energy1 != 0) {
                        context.throwPositionedException(String.format("Expected energy storage module to have drained all of its power, but it had %s!", energy1), energyStorageModulePos);
                    }
                    int energy2 = EnergyUtil.getEnergy(inv.getInvStack(EnergyStorageModuleBlockEntity.CHARGE_BATTERY_SLOT));
                    if (energy2 != 15000) {
                        context.throwPositionedException(String.format("Energy was lost when transferring to items? Found %s of 15000 energy!", energy1), energyStorageModulePos);
                    }
                }));
            });
        } else {
            context.throwPositionedException(String.format("Expected energy storage module but found %s instead!", blockEntity), energyStorageModulePos);
        }
    }

    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 203)
    public void electricFurnaceSmeltingTest(TestContext context) {
        final var furnacePos = new BlockPos(0, 0, 0);
        context.setBlockState(furnacePos, GalacticraftBlock.ELECTRIC_FURNACE);
        BlockEntity blockEntity = context.getBlockEntity(furnacePos);
        if (blockEntity instanceof ElectricFurnaceBlockEntity furnace) {
            FixedItemInv inv = furnace.itemInv();
            inv.setInvStack(ElectricFurnaceBlockEntity.CHARGE_SLOT, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                if (furnace.capacitorView().getEnergy() == 0) {
                    context.throwPositionedException("Expected electric furnace to charge from stack!", furnacePos);
                }
                inv.setInvStack(ElectricFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.PORKCHOP), Simulation.ACTION);

                context.runAtTick(context.getTick() + 200 + 1, () -> {
                    ItemStack output = inv.getInvStack(ElectricFurnaceBlockEntity.OUTPUT_SLOT);
                    if (output.getItem() != Items.COOKED_PORKCHOP) {
                        context.throwPositionedException(String.format("Expected electric furnace to have made a cooked porkchop but found %s instead!", output), furnacePos);
                    }
                    inv.setInvStack(ElectricFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_COPPER), Simulation.ACTION);
                    context.runAtTick(context.getTick() + 1, () -> context.addInstantFinalTask(() -> {
                        if (furnace.maxProgress() != 0) {
                            context.throwPositionedException("Expected electric furnace to be unable to craft as the outputs were full!", furnacePos);
                        }
                    }));
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected electric furnace but found %s instead!", blockEntity), furnacePos);
        }
    }

    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 164)
    public void electricArcFurnaceBlastingTest(TestContext context) {
        final var arcFurnacePos = new BlockPos(0, 0, 0);
        context.setBlockState(arcFurnacePos, GalacticraftBlock.ELECTRIC_ARC_FURNACE);
        BlockEntity blockEntity = context.getBlockEntity(arcFurnacePos);
        if (blockEntity instanceof ElectricArcFurnaceBlockEntity arcFurnace) {
            FixedItemInv inv = arcFurnace.itemInv();
            inv.setInvStack(ElectricArcFurnaceBlockEntity.CHARGE_SLOT, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                if (arcFurnace.capacitorView().getEnergy() == 0) {
                    context.throwPositionedException("Expected electric arc furnace to charge from stack!", arcFurnacePos);
                }
                inv.setInvStack(ElectricArcFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_IRON), Simulation.ACTION);

                context.runAtTick(context.getTick() + 80 + 1, () -> {
                    ItemStack output = inv.getInvStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1);
                    if (output.getItem() != Items.IRON_INGOT) {
                        context.throwPositionedException(String.format("Expected electric arc furnace to have made an iron ingot but found %s instead!", output), arcFurnacePos);
                    }
                    inv.setInvStack(ElectricArcFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_COPPER), Simulation.ACTION);
                    context.runAtTick(context.getTick() + 80 + 1, () -> {
                        ItemStack output2 = inv.getInvStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_2);
                        if (output2.getItem() != Items.COPPER_INGOT) {
                            context.throwPositionedException(String.format("Expected electric arc furnace to have made a copper ingot but found %s instead!", output), arcFurnacePos);
                        }
                        inv.setInvStack(ElectricArcFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_GOLD), Simulation.ACTION);
                        context.runAtTick(context.getTick() + 1, () -> context.addInstantFinalTask(() -> {
                            if (arcFurnace.maxProgress() != 0) {
                                context.throwPositionedException("Expected electric arc furnace to be unable to craft as the outputs were full!", arcFurnacePos);
                            }
                        }));
                    });
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected electric arc furnace but found %s instead!", blockEntity), arcFurnacePos);
        }
    }


    @GameTest(structureName = SINGLE_BLOCK, tickLimit = 203)
    public void refineryRefiningTest(TestContext context) {
        final var refineryPos = new BlockPos(0, 0, 0);
        context.setBlockState(refineryPos, GalacticraftBlock.REFINERY);
        BlockEntity blockEntity = context.getBlockEntity(refineryPos);
        if (blockEntity instanceof RefineryBlockEntity refinery) {
            FixedItemInv inv = refinery.itemInv();
            inv.setInvStack(RefineryBlockEntity.FLUID_INPUT_SLOT, new ItemStack(GalacticraftItem.CRUDE_OIL_BUCKET), Simulation.ACTION);
            context.runAtTick(context.getTick() + 1, () -> {
                FluidVolume oil1 = refinery.fluidInv().getInvFluid(RefineryBlockEntity.OIL_TANK);
                if (oil1.amount().isLessThan(FluidAmount.ONE) || oil1.getRawFluid() != GalacticraftFluid.CRUDE_OIL) {
                    context.throwPositionedException(String.format("Expected refinery to accept 1B of crude oil but found a %s instead!", oil1), refineryPos);
                }
                ItemStack bucket = inv.getInvStack(RefineryBlockEntity.FLUID_INPUT_SLOT);
                if (bucket.getItem() != Items.BUCKET) {
                    context.throwPositionedException(String.format("Expected refinery to return a bucket from transaction but found a %s instead!", bucket), refineryPos);
                }
                inv.setInvStack(RefineryBlockEntity.CHARGE_SLOT, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
                context.runAtTick(context.getTick() + 1, () -> {
                    if (refinery.capacitorView().getEnergy() == 0) {
                        context.throwPositionedException("Expected refinery to charge from stack!", refineryPos);
                    }

                    context.runAtTick(context.getTick() + 200 + 1, () -> context.addInstantFinalTask(() -> {
                        FluidVolume oil = refinery.fluidInv().getInvFluid(RefineryBlockEntity.OIL_TANK);
                        FluidVolume fuel = refinery.fluidInv().getInvFluid(RefineryBlockEntity.FUEL_TANK);
                        if (!oil.isEmpty()) {
                            context.throwPositionedException(String.format("Expected refinery to refine all of the oil but found %s remaining!", oil), refineryPos);
                        }
                        if (fuel.amount().isLessThan(FluidAmount.ONE) || fuel.getRawFluid() != GalacticraftFluid.FUEL) {
                            context.throwPositionedException(String.format("Expected refinery to refine all of the oil into fuel but it only refined %s!", fuel), refineryPos);
                        }
                    }));
                });
            });
        } else {
            context.throwPositionedException(String.format("Expected refinery but found %s instead!", blockEntity), refineryPos);
        }
    }
}