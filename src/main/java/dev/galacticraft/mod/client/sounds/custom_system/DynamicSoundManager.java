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

package dev.galacticraft.mod.client.sounds.custom_system;

import java.util.ArrayList;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.sounds.SoundEvent;



public class DynamicSoundManager implements SoundInstanceCallback {
    
// An instance of the client to use Minecraft's default SoundManager
	private static final MinecraftClient client = MinecraftClient.getInstance();
	// static field to store the current instance for the Singleton Design Pattern
	private static DynamicSoundManager instance;
	// The list which keeps track of all currently playing dynamic SoundInstances
	private final List<AbstractDynamicSoundInstance> activeSounds = new ArrayList<>();

	private DynamicSoundManager() {
		// private constructor to make sure that the normal
		// instantiation of that object is not used externally
	}

	// when accessing this class for the first time a new instance
	// is created and stored. If this is called again only the already
	// existing instance will be returned, instead of creating a new instance
	public static DynamicSoundManager getInstance() {
		if (instance == null) {
			instance = new DynamicSoundManager();
		}

		return instance;
	}


// This is where the callback signal of a finished custom SoundInstance will arrive.
// For now, we can just stop and remove the sound from the list, but you can add
// your own functionality too
@Override
public <T extends AbstractDynamicSoundInstance> void onFinished(T soundInstance) {
    this.stop(soundInstance);
}

    // Plays a sound instance, if it doesn't already exist in the list
public <T extends AbstractDynamicSoundInstance> void play(T soundInstance) {
	if (this.activeSounds.contains(soundInstance)) return;

	client.getSoundManager().play(soundInstance);
	this.activeSounds.add(soundInstance);
}

// Stops a sound immediately. in most cases it is preferred to use
// the sound's ending phase, which will clean it up after completion
public <T extends AbstractDynamicSoundInstance> void stop(T soundInstance) {
	client.getSoundManager().stop(soundInstance);
	this.activeSounds.remove(soundInstance);
}

// Finds a SoundInstance from a SoundEvent, if it exists and is currently playing
public Optional<AbstractDynamicSoundInstance> getPlayingSoundInstance(SoundEvent soundEvent) {
	for (var activeSound : this.activeSounds) {
		// SoundInstances use their SoundEvent's id by default
		if (activeSound.getId().equals(soundEvent.getId())) {
			return Optional.of(activeSound);
		}
	}

	return Optional.empty();
}
}
