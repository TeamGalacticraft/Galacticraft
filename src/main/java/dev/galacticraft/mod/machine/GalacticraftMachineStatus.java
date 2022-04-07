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

package dev.galacticraft.mod.machine;

import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.mod.Constant;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public final class GalacticraftMachineStatus {
    public static final MachineStatus COLLECTING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "collecting"), new TranslatableText("machine_status.galacticraft.collecting").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus PARTIALLY_BLOCKED = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "partially_blocked"), new TranslatableText("machine_status.galacticraft.partially_blocked").setStyle(Constant.Text.GRAY_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus BLOCKED = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "blocked"), new TranslatableText("machine_status.galacticraft.blocked").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus NIGHT = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "night"), new TranslatableText("machine_status.galacticraft.night").setStyle(Constant.Text.DARK_BLUE_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus RAIN = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "rain"), new TranslatableText("machine_status.galacticraft.rain").setStyle(Constant.Text.BLUE_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus FABRICATING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "fabricating"), new TranslatableText("machine_status.galacticraft.fabricating").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus NO_FUEL = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "no_fuel"), new TranslatableText("machine_status.galacticraft.no_fuel").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus GENERATING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "generating"), new TranslatableText("machine_status.galacticraft.generating").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus WARMING_UP = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "warming_up"), new TranslatableText("machine_status.galacticraft.warming_up").setStyle(Constant.Text.GOLD_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "cooling_down"), new TranslatableText("machine_status.galacticraft.cooling_down").setStyle(Constant.Text.AQUA_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "compressing"), new TranslatableText("machine_status.galacticraft.compressing").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus ACTIVE = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "active"), new TranslatableText("machine_status.galacticraft.active").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "not_enough_oxygen"), new TranslatableText("machine_status.galacticraft.not_enough_oxygen").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus MISSING_OIL = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "missing_oil"), new TranslatableText("machine_status.galacticraft.missing_oil").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "fuel_tank_full"), new TranslatableText("machine_status.galacticraft.fuel_tank_full").setStyle(Constant.Text.GOLD_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "missing_oxygen_tank"), new TranslatableText("machine_status.galacticraft.missing_oxygen_tank").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "oxygen_tank_full"), new TranslatableText("machine_status.galacticraft.oxygen_tank_full").setStyle(Constant.Text.GOLD_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus DECOMPRESSING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "decompressing"), new TranslatableText("machine_status.galacticraft.decompressing").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "empty_oxygen_tank"), new TranslatableText("machine_status.galacticraft.empty_oxygen_tank").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ALREADY_SEALED = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "already_sealed"), new TranslatableText("machine_status.galacticraft.already_sealed").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "area_too_large"), new TranslatableText("machine_status.galacticraft.area_too_large").setStyle(Constant.Text.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus SEALED = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "sealed"), new TranslatableText("machine_status.galacticraft.sealed").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus DISTRIBUTING = MachineStatus.createAndRegister(new Identifier(Constant.MOD_ID, "distributing"), new TranslatableText("machine_status.galacticraft.distributing").setStyle(Constant.Text.GREEN_STYLE), MachineStatus.Type.WORKING);
}
