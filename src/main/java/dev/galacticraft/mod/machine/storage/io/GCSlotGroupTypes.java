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

package dev.galacticraft.mod.machine.storage.io;

import dev.galacticraft.machinelib.api.storage.slot.SlotGroupType;
import dev.galacticraft.machinelib.impl.storage.slot.InputType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class GCSlotGroupTypes {
    public static final SlotGroupType ENERGY_TO_SELF = SlotGroupType.create(TextColor.fromRgb(0xdcdb41), Component.translatable("slot_type.galacticraft.energy_charge"), InputType.TRANSFER);
    public static final SlotGroupType ENERGY_TO_ITEM = SlotGroupType.create(TextColor.fromRgb(0xb5b41c), Component.translatable("slot_type.galacticraft.energy_drain"), InputType.TRANSFER);
    public static final SlotGroupType SOLID_FUEL = SlotGroupType.create(TextColor.fromRgb(0x1f1f1f), Component.translatable("slot_type.galacticraft.solid_fuel"), InputType.INPUT);
    public static final SlotGroupType COAL = SlotGroupType.create(TextColor.fromRgb(0x000000), Component.translatable("slot_type.galacticraft.coal"), InputType.INPUT);

    public static final SlotGroupType OXYGEN_TO_ITEM = SlotGroupType.create(TextColor.fromRgb(0x41dcd7), Component.translatable("slot_type.galacticraft.oxygen_tank_fill"), InputType.TRANSFER);
    public static final SlotGroupType OXYGEN_TO_SELF = SlotGroupType.create(TextColor.fromRgb(0x41aadc), Component.translatable("slot_type.galacticraft.oxygen_tank_drain"), InputType.TRANSFER);

    public static final SlotGroupType OIL_FROM_ITEM = SlotGroupType.create(TextColor.fromRgb(0x41dcd7), Component.translatable("slot_type.galacticraft.oil_fill"), InputType.TRANSFER);
    public static final SlotGroupType FUEL_TO_ITEM = SlotGroupType.create(TextColor.fromRgb(0x41aadc), Component.translatable("slot_type.galacticraft.fuel_drain"), InputType.TRANSFER);

    public static final SlotGroupType GENERIC_INPUT = SlotGroupType.create(TextColor.fromRgb(0x44d844), Component.translatable("slot_type.galacticraft.item_input"), InputType.INPUT);
    public static final SlotGroupType GENERIC_OUTPUT = SlotGroupType.create(TextColor.fromRgb(0xc32727), Component.translatable("slot_type.galacticraft.item_output"), InputType.OUTPUT);

    public static final SlotGroupType OIL_INPUT = SlotGroupType.create(TextColor.fromRgb(0x000000), Component.translatable("slot_type.galacticraft.oil_input"), InputType.INPUT);
    public static final SlotGroupType FUEL_INPUT = SlotGroupType.create(TextColor.fromRgb(0xc2c123), Component.translatable("slot_type.galacticraft.fuel_input"), InputType.INPUT);
    public static final SlotGroupType FUEL_OUTPUT = SlotGroupType.create(TextColor.fromRgb(0xc2c123), Component.translatable("slot_type.galacticraft.fuel_output"), InputType.OUTPUT);

    public static final SlotGroupType OXYGEN_INPUT = SlotGroupType.create(TextColor.fromRgb(0xdb2e4c), Component.translatable("slot_type.galacticraft.oxygen_input"), InputType.INPUT);
    public static final SlotGroupType OXYGEN_OUTPUT = SlotGroupType.create(TextColor.fromRgb(0x2edb5b), Component.translatable("slot_type.galacticraft.oxygen_output"), InputType.OUTPUT);
    public static final SlotGroupType OXYGEN_TANK = SlotGroupType.create(TextColor.fromRgb(0x2edb5b), Component.translatable("slot_type.galacticraft.oxygen_io"), InputType.STORAGE);
    public static final SlotGroupType DIAMOND_INPUT = SlotGroupType.create(TextColor.fromRgb(0xFF0000), Component.translatable("slot_type.galacticraft.diamond_input"), InputType.INPUT);
    public static final SlotGroupType SILICON_INPUT = SlotGroupType.create(TextColor.fromRgb(0xFF0000), Component.translatable("slot_type.galacticraft.silicon_input"), InputType.INPUT);
    public static final SlotGroupType REDSTONE_INPUT = SlotGroupType.create(TextColor.fromRgb(0xFF0000), Component.translatable("slot_type.galacticraft.redstone_input"), InputType.INPUT);
}
