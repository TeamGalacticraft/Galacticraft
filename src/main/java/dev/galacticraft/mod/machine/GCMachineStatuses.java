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

package dev.galacticraft.mod.machine;

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.ChatFormatting;

public final class GCMachineStatuses {
    // Energy Generation
    public static final MachineStatus GENERATING = MachineStatus.create(Translations.MachineStatus.GENERATING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    // Energy Generation - Coal Generator
    public static final MachineStatus NO_FUEL = MachineStatus.create(Translations.MachineStatus.NO_FUEL, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus WARMING_UP = MachineStatus.create(Translations.MachineStatus.WARMING_UP, ChatFormatting.GOLD, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.create(Translations.MachineStatus.COOLING_DOWN, ChatFormatting.AQUA, MachineStatus.Type.PARTIALLY_WORKING);
    // Energy Generation - Solar Panels
    public static final MachineStatus PARTIALLY_GENERATING = MachineStatus.create(Translations.MachineStatus.PARTIALLY_GENERATING, ChatFormatting.YELLOW, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus NOT_GENERATING = MachineStatus.create(Translations.MachineStatus.NOT_GENERATING, ChatFormatting.DARK_GRAY, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus BLOCKED = MachineStatus.create(Translations.MachineStatus.BLOCKED, ChatFormatting.DARK_RED, MachineStatus.Type.OTHER);

    public static final MachineStatus FABRICATING = MachineStatus.create(Translations.MachineStatus.FABRICATING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.create(Translations.MachineStatus.COMPRESSING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus SMELTING = MachineStatus.create(Translations.MachineStatus.SMELTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);

    // Oxygen
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.create(Translations.MachineStatus.NOT_ENOUGH_OXYGEN, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    // Oxygen - Collector
    public static final MachineStatus COLLECTING = MachineStatus.create(Translations.MachineStatus.COLLECTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    // Oxygen - (De)compressor
    public static final MachineStatus COMPRESSING_OXYGEN = MachineStatus.create(Translations.MachineStatus.COMPRESSING_OXYGEN, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus DECOMPRESSING = MachineStatus.create(Translations.MachineStatus.DECOMPRESSING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.create(Translations.MachineStatus.MISSING_OXYGEN_TANK, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.create(Translations.MachineStatus.OXYGEN_TANK_FULL, ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.create(Translations.MachineStatus.EMPTY_OXYGEN_TANK, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    // Oxygen - Sealer
    public static final MachineStatus ALREADY_SEALED = MachineStatus.create(Translations.MachineStatus.ALREADY_SEALED, ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.create(Translations.MachineStatus.AREA_TOO_LARGE, ChatFormatting.RED, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus SEALED = MachineStatus.create(Translations.MachineStatus.SEALED, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    // Oxygen - Bubble Distributor
    public static final MachineStatus DISTRIBUTING = MachineStatus.create(Translations.MachineStatus.DISTRIBUTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);

    // Refinery
    public static final MachineStatus REFINING = MachineStatus.create(Translations.MachineStatus.REFINING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus MISSING_OIL = MachineStatus.create(Translations.MachineStatus.MISSING_OIL, ChatFormatting.RED, MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.create(Translations.MachineStatus.FUEL_TANK_FULL, ChatFormatting.GOLD, MachineStatus.Type.OTHER);

    // Fuel Loader
    public static final MachineStatus PREPARING = MachineStatus.create(Translations.MachineStatus.PREPARING, ChatFormatting.YELLOW, MachineStatus.Type.OTHER);
    public static final MachineStatus LOADING = MachineStatus.create(Translations.MachineStatus.LOADING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_FUEL = MachineStatus.create(Translations.MachineStatus.NOT_ENOUGH_FUEL, ChatFormatting.RED, MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus NO_ROCKET = MachineStatus.create(Translations.MachineStatus.NO_ROCKET, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ROCKET_IS_FULL = MachineStatus.create(Translations.MachineStatus.ROCKET_IS_FULL, ChatFormatting.GOLD, MachineStatus.Type.OUTPUT_FULL);

    // Food Canner
    public static final MachineStatus CANNING = MachineStatus.create(Translations.MachineStatus.CANNING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus TRANSFERRING_CAN = MachineStatus.create(Translations.MachineStatus.TRANSFERRING_CAN, ChatFormatting.GREEN, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus NO_FOOD = MachineStatus.create(Translations.MachineStatus.NO_FOOD, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus MISSING_EMPTY_CAN = MachineStatus.create(Translations.MachineStatus.MISSING_EMPTY_CAN, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);

    public static void register() {
    }
}
