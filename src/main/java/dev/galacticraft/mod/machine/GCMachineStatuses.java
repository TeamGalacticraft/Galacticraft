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
    public static final MachineStatus COLLECTING = MachineStatus.create("machine_status.galacticraft.collecting", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus PARTIALLY_BLOCKED = MachineStatus.create("machine_status.galacticraft.partially_blocked", ChatFormatting.GRAY, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus BLOCKED = MachineStatus.create("machine_status.galacticraft.blocked", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus NIGHT = MachineStatus.create("machine_status.galacticraft.night", ChatFormatting.DARK_BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus RAIN = MachineStatus.create("machine_status.galacticraft.rain", ChatFormatting.BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus FABRICATING = MachineStatus.create("machine_status.galacticraft.fabricating", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NO_FUEL = MachineStatus.create("machine_status.galacticraft.no_fuel", ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus GENERATING = MachineStatus.create("machine_status.galacticraft.generating", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus WARMING_UP = MachineStatus.create("machine_status.galacticraft.warming_up", ChatFormatting.GOLD, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.create("machine_status.galacticraft.cooling_down", ChatFormatting.AQUA, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.create("machine_status.galacticraft.compressing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.create("machine_status.galacticraft.not_enough_oxygen", ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus MISSING_OIL = MachineStatus.create("machine_status.galacticraft.missing_oil", ChatFormatting.RED, MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.create("machine_status.galacticraft.fuel_tank_full", ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.create("machine_status.galacticraft.missing_oxygen_tank", ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.create("machine_status.galacticraft.oxygen_tank_full", ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus DECOMPRESSING = MachineStatus.create("machine_status.galacticraft.decompressing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.create("machine_status.galacticraft.empty_oxygen_tank", ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ALREADY_SEALED = MachineStatus.create("machine_status.galacticraft.already_sealed", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.create("machine_status.galacticraft.area_too_large", ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus SEALED = MachineStatus.create("machine_status.galacticraft.sealed", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus DISTRIBUTING = MachineStatus.create("machine_status.galacticraft.distributing", ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    /**
     * The fuel loader is loading fuel into the rocket.
     */
    public static final MachineStatus LOADING = MachineStatus.create("ui.galacticraft.machinestatus.loading", ChatFormatting.GREEN, MachineStatus.Type.WORKING);

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
