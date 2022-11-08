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

import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class GCSlotGroups {
    public static final SlotGroup ENERGY_CHARGE = SlotGroup.create(TextColor.fromRgb(0xdcdb41), Component.translatable("slot_type.galacticraft.energy_charge"), false);
    public static final SlotGroup ENERGY_DRAIN = SlotGroup.create(TextColor.fromRgb(0xb5b41c), Component.translatable("slot_type.galacticraft.energy_drain"), false);
    public static final SlotGroup SOLID_FUEL = SlotGroup.create(TextColor.fromRgb(0x1f1f1f), Component.translatable("slot_type.galacticraft.solid_fuel"), true);

    public static final SlotGroup OXYGEN_TANK_FILL = SlotGroup.create(TextColor.fromRgb(0x41dcd7), Component.translatable("slot_type.galacticraft.oxygen_tank_fill"), false);
    public static final SlotGroup OXYGEN_TANK_DRAIN = SlotGroup.create(TextColor.fromRgb(0x41aadc), Component.translatable("slot_type.galacticraft.oxygen_tank_drain"), false);

    public static final SlotGroup OIL_FILL = SlotGroup.create(TextColor.fromRgb(0x41dcd7), Component.translatable("slot_type.galacticraft.oil_fill"), false);
    public static final SlotGroup FUEL_DRAIN = SlotGroup.create(TextColor.fromRgb(0x41aadc), Component.translatable("slot_type.galacticraft.fuel_drain"), false);

    public static final SlotGroup GENERIC_INPUT = SlotGroup.create(TextColor.fromRgb(0x44d844), Component.translatable("slot_type.galacticraft.item_input"), true);
    public static final SlotGroup GENERIC_OUTPUT = SlotGroup.create(TextColor.fromRgb(0xc32727), Component.translatable("slot_type.galacticraft.item_output"), true);

    public static final SlotGroup OIL_INPUT = SlotGroup.create(TextColor.fromRgb(0x000000), Component.translatable("slot_type.galacticraft.oil_input"), true);
    public static final SlotGroup FUEL_INPUT = SlotGroup.create(TextColor.fromRgb(0xc2c123), Component.translatable("slot_type.galacticraft.fuel_input"), true);
    public static final SlotGroup FUEL_OUTPUT = SlotGroup.create(TextColor.fromRgb(0xc2c123), Component.translatable("slot_type.galacticraft.fuel_output"), true);

    public static final SlotGroup OXYGEN_INPUT = SlotGroup.create(TextColor.fromRgb(0xdb2e4c), Component.translatable("slot_type.galacticraft.oxygen_input"), true);
    public static final SlotGroup OXYGEN_OUTPUT = SlotGroup.create(TextColor.fromRgb(0x2edb5b), Component.translatable("slot_type.galacticraft.oxygen_output"), true);
    public static final SlotGroup OXYGEN_TANK = SlotGroup.create(TextColor.fromRgb(0x2edb5b), Component.translatable("slot_type.galacticraft.oxygen_io"), true);
}
