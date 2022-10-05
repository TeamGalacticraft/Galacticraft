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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class GCMachineStatus {
    public static final MachineStatus COLLECTING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "collecting"), Component.translatable("machine_status.galacticraft.collecting").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus PARTIALLY_BLOCKED = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "partially_blocked"), Component.translatable("machine_status.galacticraft.partially_blocked").setStyle(Constant.Text.Color.GRAY_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus BLOCKED = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "blocked"), Component.translatable("machine_status.galacticraft.blocked").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus NIGHT = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "night"), Component.translatable("machine_status.galacticraft.night").setStyle(Constant.Text.Color.DARK_BLUE_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus RAIN = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "rain"), Component.translatable("machine_status.galacticraft.rain").setStyle(Constant.Text.Color.BLUE_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus FABRICATING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "fabricating"), Component.translatable("machine_status.galacticraft.fabricating").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus NO_FUEL = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "no_fuel"), Component.translatable("machine_status.galacticraft.no_fuel").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus GENERATING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "generating"), Component.translatable("machine_status.galacticraft.generating").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus WARMING_UP = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "warming_up"), Component.translatable("machine_status.galacticraft.warming_up").setStyle(Constant.Text.Color.GOLD_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COOLING_DOWN = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "cooling_down"), Component.translatable("machine_status.galacticraft.cooling_down").setStyle(Constant.Text.Color.AQUA_STYLE), MachineStatus.Type.PARTIALLY_WORKING);
    public static final MachineStatus COMPRESSING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "compressing"), Component.translatable("machine_status.galacticraft.compressing").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus ACTIVE = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "active"), Component.translatable("machine_status.galacticraft.active").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus NOT_ENOUGH_OXYGEN = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "not_enough_oxygen"), Component.translatable("machine_status.galacticraft.not_enough_oxygen").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus MISSING_OIL = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "missing_oil"), Component.translatable("machine_status.galacticraft.missing_oil").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.MISSING_FLUIDS);
    public static final MachineStatus FUEL_TANK_FULL = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "fuel_tank_full"), Component.translatable("machine_status.galacticraft.fuel_tank_full").setStyle(Constant.Text.Color.GOLD_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus MISSING_OXYGEN_TANK = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "missing_oxygen_tank"), Component.translatable("machine_status.galacticraft.missing_oxygen_tank").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.MISSING_ITEMS);
    public static final MachineStatus OXYGEN_TANK_FULL = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "oxygen_tank_full"), Component.translatable("machine_status.galacticraft.oxygen_tank_full").setStyle(Constant.Text.Color.GOLD_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus DECOMPRESSING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "decompressing"), Component.translatable("machine_status.galacticraft.decompressing").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus EMPTY_OXYGEN_TANK = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "empty_oxygen_tank"), Component.translatable("machine_status.galacticraft.empty_oxygen_tank").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.MISSING_RESOURCE);
    public static final MachineStatus ALREADY_SEALED = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "already_sealed"), Component.translatable("machine_status.galacticraft.already_sealed").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus AREA_TOO_LARGE = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "area_too_large"), Component.translatable("machine_status.galacticraft.area_too_large").setStyle(Constant.Text.Color.RED_STYLE), MachineStatus.Type.OTHER);
    public static final MachineStatus SEALED = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "sealed"), Component.translatable("machine_status.galacticraft.sealed").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    public static final MachineStatus DISTRIBUTING = MachineStatus.createAndRegister(new ResourceLocation(Constant.MOD_ID, "distributing"), Component.translatable("machine_status.galacticraft.distributing").setStyle(Constant.Text.Color.GREEN_STYLE), MachineStatus.Type.WORKING);
    /**
     * The fuel loader is loading fuel into the rocket.
     */
    public static final MachineStatus LOADING = MachineStatus.createAndRegister(Constant.id("loading"), Component.translatable("ui.galacticraft.machinestatus.loading").withStyle(ChatFormatting.GREEN), MachineStatus.Type.WORKING);

    /**
     * The fuel loader has enough fuel to load but not enough energy.
     */
    public static final MachineStatus NOT_ENOUGH_ENERGY = MachineStatus.createAndRegister(Constant.id("not_enough_energy"), Component.translatable("ui.galacticraft.machinestatus.not_enough_energy").withStyle(ChatFormatting.RED), MachineStatus.Type.MISSING_ENERGY);

    /**
     * The fuel loader doesn't have any fuel.
     */
    public static final MachineStatus NOT_ENOUGH_FUEL = MachineStatus.createAndRegister(Constant.id("not_enough_fuel"), Component.translatable("ui.galacticraft.machinestatus.not_enough_fuel").withStyle(ChatFormatting.GOLD), MachineStatus.Type.MISSING_FLUIDS);

    /**
     * The fuel loader doesn't have a rocket
     */
    public static final MachineStatus NO_ROCKET = MachineStatus.createAndRegister(Constant.id("no_rocket"), Component.translatable("ui.galacticraft.machinestatus.no_rocket").withStyle(ChatFormatting.RED), MachineStatus.Type.MISSING_RESOURCE);

    /**
     * The sun is not visible.
     */
    public static final MachineStatus ROCKET_IS_FULL = MachineStatus.createAndRegister(Constant.id("rocket_is_full"), Component.translatable("ui.galacticraft.machinestatus.rocket_is_full").withStyle(ChatFormatting.GOLD), MachineStatus.Type.OUTPUT_FULL);

    public static void register() {}
}
