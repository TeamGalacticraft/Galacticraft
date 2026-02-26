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
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;

public class CustomSoundInstance extends AbstractSoundInstance {
	private final MachineBlockEntity entity;

	public CustomSoundInstance(MachineBlockEntity entity, SoundEvents soundEvent, SoundCategory soundCategory) {
		super(soundEvent, soundCategory, SoundInstance.createUnseededRandom());
		// In this constructor we also add the sound source (LivingEntity) of
		// the SoundInstance and store it in the current object
		this.entity = entity;
		// set up default values when the sound is about to start
		this.volume = 1.0f;
		this.pitch = 1.0f;
		this.looping = true;
		this.setPositionToEntity();
	}

	@Override
	public void tick() {
		// stop sound instantly if sound source does not exist anymore
		if (this.entity == null || this.entity.isRemoved() || this.entity.isDead()) {
			this.setDone();
			return;
		}

	}

	@Override
	public boolean isLooping() {
		// override to true, so that the SoundInstance can start
		// or add your own condition to the SoundInstance, if necessary
		return true;
	}

	// small utility method to move the sound instance position
	// to the sound source's position
	private void setPositionToEntity() {
		this.x = this.entity.getX();
		this.y = this.entity.getY();
		this.z = this.entity.getZ();
	}
}
