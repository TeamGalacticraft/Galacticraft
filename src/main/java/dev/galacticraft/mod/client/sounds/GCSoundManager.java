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

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;

public class GCSoundManager implements SoundCallback {

    private static final Minecraft client = Minecraft.getInstance();
    private static GCSoundManager instance;
    private final List<GCSound> activeSounds = new ArrayList<>();

    private GCSoundManager() {

    }

    public static GCSoundManager getInstance() {
        if (instance == null) {
            instance = new GCSoundManager();
        }
        return instance;
    }

    // for removing the sound but not the entity
    @Override
    public <T extends GCSound> void onFinished(T soundInstance) {
        this.stop(soundInstance);
    }

    // Plays a sound instance, if it doesn't already exist in the list
    public <T extends GCSound> void play(T soundInstance) {
        if (this.activeSounds.contains(soundInstance)) return;
        BlockEntity entity = soundInstance.entity;

        if (entity.getLevel().isClientSide) {
            client.getSoundManager().play(soundInstance);
            this.activeSounds.add(soundInstance);
        }

    }

    // Stops a sound immediately. in most cases it is preferred to use
    // the sound's ending phase, which will clean it up after completion
    public <T extends GCSound> void stop(T soundInstance) {
        client.getSoundManager().stop(soundInstance);
        this.activeSounds.remove(soundInstance);
    }

    // Finds a SoundInstance from a SoundEvent, if it exists and is currently playing
    public Optional<GCSound> getPlayingSoundInstance(SoundEvent soundEvent) {
        for (var activeSound : this.activeSounds) {
            // SoundInstances use their SoundEvent's id by default
            if (activeSound.getLocation().equals(soundEvent.getLocation())) {
                return Optional.of(activeSound);
            }
        }

        return Optional.empty();
    }

    public Optional<GCSound> getSoundFromEntity(BlockEntity entity, MachineStatus status) {
        for (var activeSound : this.activeSounds) {
            if (activeSound.entity == entity && Objects.equals(GCSoundMap.GC_SOUND_MAP.get(status),activeSound.event)) {
                return Optional.of(activeSound);
            }
        }
        return Optional.empty();
    }

    public static void onStatusChanged(Minecraft minecraft, LocalPlayer player, BlockPos pos, MachineStatus status, MachineStatus oldStatus) {
        MachineBlockEntity machine = (MachineBlockEntity) minecraft.level.getBlockEntity(pos);
        GCSoundManager manager = GCSoundManager.getInstance();
        SoundEvent newSound = GCSoundMap.GC_SOUND_MAP.get(status);

        // Stop old sound (if there is one)
        manager.getSoundFromEntity(machine, oldStatus).ifPresent(oldSound->{oldSound.end();});
        // Play new sound (if there is one)
        switch (status.getType()) {
            case MISSING_ENERGY:
                break;
            case MISSING_FLUIDS:
                manager.play(new IdleMachineSound(machine,newSound, manager));
                break;
            case MISSING_ITEMS:
                manager.play(new IdleMachineSound(machine,newSound, manager));
                break;
            case MISSING_RESOURCE:
                manager.play(new IdleMachineSound(machine,newSound, manager));
                break;
            case OTHER:
                manager.play(new IdleMachineSound(machine,newSound, manager));
                break;
            case OUTPUT_FULL:
                manager.play(new IdleMachineSound(machine,newSound, manager));
                break;
            case PARTIALLY_WORKING:
                manager.play(new ActiveMachineSound(machine,newSound, manager));
                break;
            case WORKING:
                manager.play(new ActiveMachineSound(machine,newSound, manager));
                break;
            default:
                break;

        }

    }
}
