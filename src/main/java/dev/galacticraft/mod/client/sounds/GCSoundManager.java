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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GCSoundManager implements SoundCallback {

    private static final Minecraft client = Minecraft.getInstance();
    private static GCSoundManager instance;
    private final List<MachineSound> activeSounds = new ArrayList<>();

    private GCSoundManager() {}

    public static GCSoundManager getInstance() {
        if (instance == null) {
            instance = new GCSoundManager();
        }
        return instance;
    }

    // for removing the sound but not the entity
    @Override
    public <T extends MachineSound> void onFinished(T soundInstance) {
        this.stop(soundInstance);
    }

    // Plays a sound instance, if it doesn't already exist in the list
    public <T extends MachineSound> void play(T soundInstance) {
        if (this.activeSounds.contains(soundInstance)) return;
        BlockEntity entity = soundInstance.machine;
        if (entity.getLevel().isClientSide) {
            client.getSoundManager().play(soundInstance);
            this.activeSounds.add(soundInstance);
        }
    }

    // Stops a sound immediately. in most cases it is preferred to use
    // the sound's ending phase, which will clean it up after completion
    public <T extends MachineSound> void stop(T soundInstance) {
        client.getSoundManager().stop(soundInstance);
        this.activeSounds.remove(soundInstance);
    }

    public Optional<MachineSound> getSoundFromEntity(BlockEntity entity, MachineStatus status, boolean isActive) {
        for (var activeSound : this.activeSounds) {
            if (activeSound.machine == entity && Objects.equals(GCSoundMap.get(status), activeSound.event)) {
                return Optional.of(activeSound);
            }
        }
        return Optional.empty();
    }

    public static void onStatusChanged(Minecraft minecraft, LocalPlayer player, BlockPos pos, MachineStatus status, MachineStatus oldStatus) {
        MachineBlockEntity machine = (MachineBlockEntity) minecraft.level.getBlockEntity(pos);
        GCSoundManager manager = GCSoundManager.getInstance();
        boolean isActive = status.getType().isActive();
        float maxVolume = isActive ? 1.0F : 0.2F;
        // Stop old sound (if there is one)
        manager.getSoundFromEntity(machine, oldStatus, isActive).ifPresent(oldSound -> oldSound.end());
        // Play new sound (if there is one)
        SoundEvent newSound = GCSoundMap.get(status);
        manager.play(new MachineSound(machine, newSound, manager, maxVolume));
    }
}
