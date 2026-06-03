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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.block.entity.machine.CoalPoweredMachine;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class GCSoundMap {
    private static final Map<MachineStatus.Type, Optional<SoundEvent>> DEFAULTS = Map.ofEntries(
            Map.entry(MachineStatus.Type.WORKING, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.PARTIALLY_WORKING, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.MISSING_RESOURCE, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.MISSING_FLUIDS, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.MISSING_ENERGY, Optional.empty()),
            Map.entry(MachineStatus.Type.MISSING_ITEMS, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.OUTPUT_FULL, Optional.of(GCSounds.MACHINE_BUZZ)),
            Map.entry(MachineStatus.Type.OTHER, Optional.of(GCSounds.MACHINE_BUZZ)));

    private static final Map<MachineStatus, Optional<SoundEvent>> EXCEPTIONS = Map.ofEntries(
            Map.entry(MachineStatuses.NOT_ENOUGH_ENERGY, Optional.empty()),
            Map.entry(GCMachineStatuses.BLOCKED, Optional.empty()),
            Map.entry(GCMachineStatuses.NOT_GENERATING, Optional.empty()),
            Map.entry(GCMachineStatuses.COLLECTING, Optional.of(GCSounds.MACHINE_HUM)),
            Map.entry(GCMachineStatuses.DISTRIBUTING, Optional.of(GCSounds.MACHINE_HUM)));

    public static @Nullable SoundEvent get(MachineStatus status, MachineBlockEntity machine) {
        if (machine instanceof CoalPoweredMachine) {
            return null;
        }
        return EXCEPTIONS.getOrDefault(status, DEFAULTS.get(status.getType())).orElse(null);
    }
}
