/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.machine.storage.io;

import dev.galacticraft.api.machine.storage.io.ResourceFlow;
import dev.galacticraft.api.machine.storage.io.ResourceType;
import dev.galacticraft.api.machine.storage.io.SlotType;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

public class GalacticraftSlotTypes {
    public static final SlotType<Item, ItemVariant> ENERGY_CHARGE = SlotType.create(new Identifier(Constant.MOD_ID, "energy_charge"), TextColor.fromRgb(0xdcdb41), Text.translatable("slot_type.galacticraft.energy_charge"), Constant.Filter.Item.CAN_EXTRACT_ENERGY, ResourceFlow.BOTH, ResourceType.ITEM);
    public static final SlotType<Item, ItemVariant> ENERGY_DRAIN = SlotType.create(new Identifier(Constant.MOD_ID, "energy_drain"), TextColor.fromRgb(0xb5b41c), Text.translatable("slot_type.galacticraft.energy_drain"), Constant.Filter.Item.CAN_INSERT_ENERGY, ResourceFlow.BOTH, ResourceType.ITEM);
    public static final SlotType<Item, ItemVariant> SOLID_FUEL = SlotType.create(new Identifier(Constant.MOD_ID, "solid_fuel"), TextColor.fromRgb(0x1f1f1f), Text.translatable("slot_type.galacticraft.solid_fuel"), v -> FuelRegistry.INSTANCE.get(v.getItem()) != null, ResourceFlow.BOTH, ResourceType.ITEM);

    public static final SlotType<Item, ItemVariant> OXYGEN_TANK_FILL = SlotType.create(new Identifier(Constant.MOD_ID, "oxygen_tank_fill"), TextColor.fromRgb(0x41dcd7), Text.translatable("slot_type.galacticraft.oxygen_tank_fill"), Constant.Filter.Item.CAN_EXTRACT_OXYGEN, ResourceFlow.BOTH, ResourceType.ITEM);
    public static final SlotType<Item, ItemVariant> OXYGEN_TANK_DRAIN = SlotType.create(new Identifier(Constant.MOD_ID, "oxygen_tank_drain"), TextColor.fromRgb(0x41aadc), Text.translatable("slot_type.galacticraft.oxygen_tank_drain"), Constant.Filter.Item.CAN_INSERT_OXYGEN, ResourceFlow.BOTH, ResourceType.ITEM);

    public static final SlotType<Item, ItemVariant> OIL_FILL = SlotType.create(new Identifier(Constant.MOD_ID, "oil_fill"), TextColor.fromRgb(0x41dcd7), Text.translatable("slot_type.galacticraft.oil_fill"), Constant.Filter.Item.CAN_EXTRACT_OIL, ResourceFlow.BOTH, ResourceType.ITEM);
    public static final SlotType<Item, ItemVariant> FUEL_DRAIN = SlotType.create(new Identifier(Constant.MOD_ID, "fuel_drain"), TextColor.fromRgb(0x41aadc), Text.translatable("slot_type.galacticraft.fuel_drain"), Constant.Filter.Item.CAN_INSERT_FUEL, ResourceFlow.BOTH, ResourceType.ITEM);

    public static final SlotType<Item, ItemVariant> ITEM_INPUT = SlotType.create(new Identifier(Constant.MOD_ID, "item_input"), TextColor.fromRgb(0x44d844), Text.translatable("slot_type.galacticraft.item_input"), Constant.Filter.any(), ResourceFlow.INPUT, ResourceType.ITEM);
    public static final SlotType<Item, ItemVariant> ITEM_OUTPUT = SlotType.create(new Identifier(Constant.MOD_ID, "item_output"), TextColor.fromRgb(0xc32727), Text.translatable("slot_type.galacticraft.item_output"), Constant.Filter.any(), ResourceFlow.OUTPUT, ResourceType.ITEM);

    public static final SlotType<Fluid, FluidVariant> OIL_INPUT = SlotType.create(new Identifier(Constant.MOD_ID, "oil_input"), TextColor.fromRgb(0x000000), Text.translatable("slot_type.galacticraft.oil_input"), Constant.Filter.Fluid.OIL, ResourceFlow.INPUT, ResourceType.FLUID);
    public static final SlotType<Fluid, FluidVariant> FUEL_OUTPUT = SlotType.create(new Identifier(Constant.MOD_ID, "fuel_output"), TextColor.fromRgb(0xc2c123), Text.translatable("slot_type.galacticraft.fuel_output"), Constant.Filter.Fluid.FUEL, ResourceFlow.OUTPUT, ResourceType.FLUID);

    public static final SlotType<Fluid, FluidVariant> FLUID_INPUT = SlotType.create(new Identifier(Constant.MOD_ID, "fluid_input"), TextColor.fromRgb(0xdb2e4c), Text.translatable("slot_type.galacticraft.fluid_input"), Constant.Filter.any(), ResourceFlow.INPUT, ResourceType.FLUID);
    public static final SlotType<Fluid, FluidVariant> FLUID_OUTPUT = SlotType.create(new Identifier(Constant.MOD_ID, "fluid_output"), TextColor.fromRgb(0x2edb5b), Text.translatable("slot_type.galacticraft.fluid_output"), Constant.Filter.any(), ResourceFlow.OUTPUT, ResourceType.FLUID);

    public static final SlotType<Fluid, FluidVariant> OXYGEN_INPUT = SlotType.create(new Identifier(Constant.MOD_ID, "oxygen_input"), TextColor.fromRgb(0xdb2e4c), Text.translatable("slot_type.galacticraft.oxygen_input"), Constant.Filter.Gas.OXYGEN, ResourceFlow.INPUT, ResourceType.FLUID);
    public static final SlotType<Fluid, FluidVariant> OXYGEN_OUTPUT = SlotType.create(new Identifier(Constant.MOD_ID, "oxygen_output"), TextColor.fromRgb(0x2edb5b), Text.translatable("slot_type.galacticraft.oxygen_output"), Constant.Filter.Gas.OXYGEN, ResourceFlow.OUTPUT, ResourceType.FLUID);
    public static final SlotType<Fluid, FluidVariant> OXYGEN_IO = SlotType.create(new Identifier(Constant.MOD_ID, "oxygen_io"), TextColor.fromRgb(0x2edb5b), Text.translatable("slot_type.galacticraft.oxygen_io"), Constant.Filter.Gas.OXYGEN, ResourceFlow.BOTH, ResourceType.FLUID);
}
