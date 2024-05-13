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
import dev.galacticraft.mod.util.Translations;
import net.minecraft.ChatFormatting;

public final class GCMachineStatuses {
    public static final MachineStatus COLLECTING = MachineStatus.create(Translations.MachineStatus.COLLECTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus PARTIALLY_BLOCKED = MachineStatus.create(Translations.MachineStatus.PARTIALLY_BLOCKED, ChatFormatting.GRAY, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus BLOCKED = MachineStatus.create(Translations.MachineStatus.BLOCKED, ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus NIGHT = MachineStatus.create(Translations.MachineStatus.NIGHT, ChatFormatting.DARK_BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus RAIN = MachineStatus.create(Translations.MachineStatus.RAIN, ChatFormatting.BLUE, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus FABRICATING = MachineStatus.create(Translations.MachineStatus.FABRICATING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NO_FUEL = MachineStatus.create(Translations.MachineStatus.NO_FUEL, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus GENERATING = MachineStatus.create(Translations.MachineStatus.GENERATING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus WARMING_UP = MachineStatus.create(Translations.MachineStatus.WARMING_UP, ChatFormatting.GOLD, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.create(Translations.MachineStatus.COOLING_DOWN, ChatFormatting.AQUA, MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.create(Translations.MachineStatus.COMPRESSING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.create(Translations.MachineStatus.NOT_ENOUGH_OXYGEN, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus MISSING_OIL = MachineStatus.create(Translations.MachineStatus.MISSING_OIL, ChatFormatting.RED, MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.create(Translations.MachineStatus.FUEL_TANK_FULL, ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.create(Translations.MachineStatus.MISSING_OXYGEN_TANK, ChatFormatting.RED, MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.create(Translations.MachineStatus.OXYGEN_TANK_FULL, ChatFormatting.GOLD, MachineStatus.Type.OTHER);
    public static final MachineStatus DECOMPRESSING = MachineStatus.create(Translations.MachineStatus.DECOMPRESSING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.create(Translations.MachineStatus.EMPTY_OXYGEN_TANK, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ALREADY_SEALED = MachineStatus.create(Translations.MachineStatus.ALREADY_SEALED, ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.create(Translations.MachineStatus.AREA_TOO_LARGE, ChatFormatting.RED, MachineStatus.Type.OTHER);
    public static final MachineStatus SEALED = MachineStatus.create(Translations.MachineStatus.SEALED, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus DISTRIBUTING = MachineStatus.create(Translations.MachineStatus.DISTRIBUTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);
    public static final MachineStatus LOADING = MachineStatus.create(Translations.MachineStatus.DISTRIBUTING, ChatFormatting.GREEN, MachineStatus.Type.WORKING);

    /**
     * The fuel loader doesn't have any fuel.
     */
    public static final MachineStatus NOT_ENOUGH_FUEL = MachineStatus.create(Translations.MachineStatus.NOT_ENOUGH_FUEL, ChatFormatting.GOLD, MachineStatus.Type.MISSING_FLUIDS);

    /**
     * The fuel loader doesn't have a rocket
     */
    public static final MachineStatus NO_ROCKET = MachineStatus.create(Translations.MachineStatus.NO_ROCKET, ChatFormatting.RED, MachineStatus.Type.MISSING_RESOURCE);

    /**
     * The sun is not visible.
     */
    public static final MachineStatus ROCKET_IS_FULL = MachineStatus.create(Translations.MachineStatus.ROCKET_IS_FULL, ChatFormatting.GOLD, MachineStatus.Type.OUTPUT_FULL);

    public static void register() {}
}
