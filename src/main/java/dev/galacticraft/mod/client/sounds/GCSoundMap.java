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
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import java.util.Map;

public class GCSoundMap {
    private static final Map<MachineStatus.Type, SoundEvent> DEFAULTS = Map.ofEntries(
            Map.entry(MachineStatus.Type.WORKING, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.PARTIALLY_WORKING, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.MISSING_RESOURCE, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.MISSING_FLUIDS, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.MISSING_ENERGY, null),
            Map.entry(MachineStatus.Type.MISSING_ITEMS, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.OUTPUT_FULL, GCSounds.MACHINE_BUZZ),
            Map.entry(MachineStatus.Type.OTHER, GCSounds.MACHINE_BUZZ)
    );

    private static final Map<MachineStatus, SoundEvent> EXCEPTIONS = Map.ofEntries(
            Map.entry(MachineStatuses.NOT_ENOUGH_ENERGY, null),
            Map.entry(GCMachineStatuses.BLOCKED, null),
            Map.entry(GCMachineStatuses.NO_FUEL, null),
            Map.entry(GCMachineStatuses.NOT_GENERATING, null),
            Map.entry(GCMachineStatuses.COLLECTING, GCSounds.MACHINE_HUM),
            Map.entry(GCMachineStatuses.DISTRIBUTING, GCSounds.MACHINE_HUM)
    );

    public static @Nullable SoundEvent get(MachineStatus status) {
        return EXCEPTIONS.getOrDefault(status, DEFAULTS.get(status.getType()));
    }
}
