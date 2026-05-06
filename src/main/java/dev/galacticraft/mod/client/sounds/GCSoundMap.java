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
import net.minecraft.sounds.SoundEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GCSoundMap {

    public static final Map<Boolean, Map<MachineStatus, SoundEvent>> GC_SOUND_MAP;
    static {
        final Map<Boolean, Map<MachineStatus, SoundEvent>> INTERNAL = new HashMap<>();
        final Map<MachineStatus, SoundEvent> IDLE = new HashMap<>();
        final Map<MachineStatus, SoundEvent> ACTIVE = new HashMap<>();
        // Idle/unpowered sounds
        IDLE.put(GCMachineStatuses.BLOCKED, null);
        IDLE.put(GCMachineStatuses.NO_FUEL, null);
        IDLE.put(MachineStatuses.NOT_ENOUGH_ENERGY, null);
        IDLE.put(GCMachineStatuses.NOT_GENERATING, null);
        // Active sounds
        ACTIVE.put(GCMachineStatuses.COLLECTING, GCSounds.MACHINE_HUM);
        ACTIVE.put(GCMachineStatuses.DISTRIBUTING, GCSounds.MACHINE_HUM);
        // All sounds
        INTERNAL.put(false, IDLE);
        INTERNAL.put(true, ACTIVE);
        GC_SOUND_MAP = Collections.unmodifiableMap(INTERNAL);
    }
}
