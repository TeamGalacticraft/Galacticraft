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
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GCSoundManager implements SoundInstanceCallback {

    private static final Minecraft client = Minecraft.getInstance();
    private static GCSoundManager instance;
    private final List<GCSoundInstance> activeSounds = new ArrayList<>();

    private GCSoundManager() {

    }

    public static GCSoundManager getInstance() {
        if (instance == null) {
            instance = new GCSoundManager();
        }
        return instance;
    }

    @Override
    public <T extends GCSoundInstance> void onFinished(T soundInstance) {
        this.stop(soundInstance);
    }

    // TODO: manage this sound manager in the MachineBlockEntity constructors
    // TODO: make it so it only runs client or server, not both

    // Plays a sound instance, if it doesn't already exist in the list
    public <T extends GCSoundInstance> void play(T soundInstance) {
        if (level instanceof ServerLevel ServerLevel)
	    if (this.activeSounds.contains(soundInstance)) return;

	    client.getSoundManager().play(soundInstance);
	    this.activeSounds.add(soundInstance);
        System.out.println(this.activeSounds.size());
        System.out.println(this.activeSounds);
    }

    // Stops a sound immediately. in most cases it is preferred to use
    // the sound's ending phase, which will clean it up after completion
    public <T extends GCSoundInstance> void stop(T soundInstance) {
        client.getSoundManager().stop(soundInstance);
        this.activeSounds.remove(soundInstance);
        System.out.println(this.activeSounds.size());
    }

    // Finds a SoundInstance from a SoundEvent, if it exists and is currently playing
    public Optional<GCSoundInstance> getPlayingSoundInstance(SoundEvent soundEvent) {
        for (var activeSound : this.activeSounds) {
            // SoundInstances use their SoundEvent's id by default
            if (activeSound.getLocation().equals(soundEvent.getLocation())) {
                return Optional.of(activeSound);
            }
        }

        return Optional.empty();
    }
}
