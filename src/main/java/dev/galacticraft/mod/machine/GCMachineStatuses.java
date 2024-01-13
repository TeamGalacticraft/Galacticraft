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

package dev.galacticraft.mod.machine;

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import net.minecraft.ChatFormatting;

public final class GCMachineStatuses {
    public static final MachineStatus COLLECTING = MachineStatus.create("ui.galacticraft.status.collecting", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus PARTIALLY_BLOCKED = MachineStatus.create("ui.galacticraft.status.partially_blocked", ChatFormatting.GRAY, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus BLOCKED = MachineStatus.create("ui.galacticraft.status.blocked", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus NIGHT = MachineStatus.create("ui.galacticraft.status.night", ChatFormatting.DARK_BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus RAIN = MachineStatus.create("ui.galacticraft.status.rain", ChatFormatting.BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus FABRICATING = MachineStatus.create("ui.galacticraft.status.fabricating", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NO_FUEL = MachineStatus.create("ui.galacticraft.status.no_fuel", ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus GENERATING = MachineStatus.create("ui.galacticraft.status.generating", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus WARMING_UP = MachineStatus.create("ui.galacticraft.status.warming_up", ChatFormatting.GOLD, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.create("ui.galacticraft.status.cooling_down", ChatFormatting.AQUA, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.create("ui.galacticraft.status.compressing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.create("ui.galacticraft.status.not_enough_oxygen", ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus MISSING_OIL = MachineStatus.create("ui.galacticraft.status.missing_oil", ChatFormatting.RED, MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.create("ui.galacticraft.status.fuel_tank_full", ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.create("ui.galacticraft.status.missing_oxygen_tank", ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.create("ui.galacticraft.status.oxygen_tank_full", ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus DECOMPRESSING = MachineStatus.create("ui.galacticraft.status.decompressing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.create("ui.galacticraft.status.empty_oxygen_tank", ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ALREADY_SEALED = MachineStatus.create("ui.galacticraft.status.already_sealed", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.create("ui.galacticraft.status.area_too_large", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus SEALED = MachineStatus.create("ui.galacticraft.status.sealed", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus DISTRIBUTING = MachineStatus.create("ui.galacticraft.status.distributing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus LOADING = MachineStatus.create("ui.galacticraft.status.distributing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);

    /**
     * The fuel loader doesn't have any fuel.
     */
    public static final MachineStatus NOT_ENOUGH_FUEL = MachineStatus.create("ui.galacticraft.machinestatus.not_enough_fuel", ChatFormatting.GOLD, MachineStatus.Type.MISSING_FLUIDS);

    /**
     * The fuel loader doesn't have a rocket
     */
    public static final MachineStatus NO_ROCKET = MachineStatus.create("ui.galacticraft.machinestatus.no_rocket", ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);

    /**
     * The sun is not visible.
     */
    public static final MachineStatus ROCKET_IS_FULL = MachineStatus.create("ui.galacticraft.machinestatus.rocket_is_full", ChatFormatting.GOLD, MachineStatus.Type.OUTPUT_FULL);

    public static void register() {}
}
