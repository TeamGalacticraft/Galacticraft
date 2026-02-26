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

import net.minecraft.sounds.SoundEvent;

public class MachineSoundInstance extends AbstractDynamicSoundInstance {

    
// Here we just use the default constructor parameters.
	// If you want to specifically set values here already,
	// you can clean up the constructor parameters a bit
	public MachineSoundInstance(DynamicSoundSource soundSource, SoundEvent soundEvent, SoundCategory soundCategory,
							int startTransitionTicks, int endTransitionTicks, float maxVolume, float minPitch, float maxPitch,
							SoundInstanceCallback callback) {
		super(soundSource, soundEvent, soundCategory, startTransitionTicks, endTransitionTicks, maxVolume, minPitch, maxPitch, callback);
	}

	@Override
	public void tick() {
		// check conditions which set this sound automatically into the ending phase
		if (soundSource instanceof EngineBlockEntity blockEntity && blockEntity.isRemoved()) {
			this.end();
		}

		// apply the default tick behaviour from the parent class
		super.tick();

		// modulate volume and pitch of the SoundInstance
		this.modulateSoundForTransition();
		this.modulateSoundForStress();
	}

	// you can also add sound modulation methods here,
	// which should be only accessible to this
	// specific SoundInstance
}

