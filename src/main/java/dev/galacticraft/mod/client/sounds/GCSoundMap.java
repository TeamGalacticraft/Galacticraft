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

package dev.galacticraft.mod.client.sounds;

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.machine.GCMachineStatuses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;

public class GCSoundMap {
    private static final Map<MachineStatus,SoundEvent> INTERNAL = new HashMap<>();
    public static final Map<MachineStatus,SoundEvent> GC_SOUND_MAP;
    static {
        // MachineStatuses
        INTERNAL.put(MachineStatuses.NOT_ENOUGH_ENERGY,null);
        INTERNAL.put(MachineStatuses.INVALID_RECIPE,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(MachineStatuses.OUTPUT_FULL,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(MachineStatuses.CAPACITOR_FULL,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(MachineStatuses.IDLE,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(MachineStatuses.ACTIVE,GCSounds.MACHINE_BUZZ);

        // GCMachineStatuses
        // Energy Generation
        INTERNAL.put(GCMachineStatuses.GENERATING,null);
        // Energy Generation - Coal Generator
        INTERNAL.put(GCMachineStatuses.NO_FUEL,null);
        INTERNAL.put(GCMachineStatuses.WARMING_UP,null);
        INTERNAL.put(GCMachineStatuses.COOLING_DOWN,null);
        // Energy Generation - Solar Panels
        INTERNAL.put(GCMachineStatuses.PARTIALLY_GENERATING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.NOT_GENERATING,null);
        INTERNAL.put(GCMachineStatuses.BLOCKED,null);

        INTERNAL.put(GCMachineStatuses.FABRICATING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.COMPRESSING,null);
        INTERNAL.put(GCMachineStatuses.SMELTING,null);
        // Oxygen
        INTERNAL.put(GCMachineStatuses.NOT_ENOUGH_OXYGEN,GCSounds.MACHINE_BUZZ);
        // Oxygen - Collector
        INTERNAL.put(GCMachineStatuses.COLLECTING,GCSounds.MACHINE_HUM);
        // Oxygen - (De)compressor
        INTERNAL.put(GCMachineStatuses.COMPRESSING_OXYGEN,GCSounds.MACHINE_HUM);
        INTERNAL.put(GCMachineStatuses.DECOMPRESSING,GCSounds.MACHINE_HUM);
        INTERNAL.put(GCMachineStatuses.MISSING_OXYGEN_TANK,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.OXYGEN_TANK_FULL,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.EMPTY_OXYGEN_TANK,GCSounds.MACHINE_BUZZ);
        // Oxygen - Sealer
        INTERNAL.put(GCMachineStatuses.ALREADY_SEALED,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.AREA_TOO_LARGE,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.SEALED,GCSounds.MACHINE_BUZZ);
        // Oxygen - Bubble Distributor
        INTERNAL.put(GCMachineStatuses.DISTRIBUTING,GCSounds.MACHINE_HUM);
        // Refinery
        INTERNAL.put(GCMachineStatuses.REFINING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.MISSING_OIL,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.FUEL_TANK_FULL,GCSounds.MACHINE_BUZZ);
        // Fuel Loader
        INTERNAL.put(GCMachineStatuses.PREPARING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.LOADING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.NOT_ENOUGH_FUEL,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.NO_ROCKET,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.ROCKET_IS_FULL,GCSounds.MACHINE_BUZZ);
        // Food Canner
        INTERNAL.put(GCMachineStatuses.CANNING,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.TRANSFERRING_CAN,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.NO_FOOD,GCSounds.MACHINE_BUZZ);
        INTERNAL.put(GCMachineStatuses.MISSING_EMPTY_CAN,GCSounds.MACHINE_BUZZ);

        INTERNAL.put(null,null);


        GC_SOUND_MAP = Collections.unmodifiableMap(INTERNAL);
    }

}
