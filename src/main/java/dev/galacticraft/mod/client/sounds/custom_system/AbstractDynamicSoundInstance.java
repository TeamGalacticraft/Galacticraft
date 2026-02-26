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

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.Direction;


public class AbstractDynamicSoundInstance extends AbstractTickableSoundInstance {
	protected final DynamicSoundSource DynSource;                 // Entities, BlockEntities, ...
	protected TransitionState transitionState;                      // current TransitionState of the SoundInstance

	protected final int startTransitionTicks, endTransitionTicks;   // duration of starting and ending phases

	// possible volume range when adjusting sound values
	protected final float maxVolume;                                // only max value since the minimum is always 0
	// possible pitch range when adjusting sound values
	protected final float minPitch, maxPitch;

	protected int currentTick = 0, transitionTick = 0;              // current tick values for the instance

	protected final SoundInstanceCallback callback;   

    protected AbstractDynamicSoundInstance(SoundEvent sound, SoundSource Source, RandomSource random,DynamicSoundSource DynSource,
                                        int startTransitionTicks, int endTransitionTicks, float maxVolume, float minPitch, float maxPitch,
                                        SoundInstanceCallback callback) {
        super(sound, Source, SoundInstance.createUnseededRandom());

        // store important references to other objects
        this.DynSource = DynSource;
        this.callback = callback;

        // store the limits for the SoundInstance
        this.maxVolume = maxVolume;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
        this.startTransitionTicks = startTransitionTicks;    // starting phase duration
        this.endTransitionTicks = endTransitionTicks;        // ending phase duration

        // set start values
        this.volume = 0.0f;
        this.pitch = minPitch;
        this.looping = true;
        this.transitionState = TransitionState.STARTING;
        this.setPositionToEntity();
    }

        @Override
        public boolean isLooping() {

            return true;
        }

    @Override
    public void tick() {
        // handle states where sound might be actually stopped instantly
        if (this.source == null) {
            this.callback.onFinished(this);
        }

        // basic tick behaviour
        this.currentTick++;
        this.setPositionToEntity();

        // SoundInstance phase switching
        switch (this.transitionState) {
            case STARTING -> {
                this.transitionTick++;

                // go into next phase if starting phase finished its duration
                if (this.transitionTick > this.startTransitionTicks) {
                    this.transitionTick = 0;	// reset tick for future ending phase
                    this.transitionState = TransitionState.RUNNING;
                }
            }
            case RUNNING -> {
                this.transitionTick++;

                // go into next phase if starting phase finished its duration
                if (this.transitionTick > this.startTransitionTicks) {
                    this.transitionTick = 0;	// reset tick for future ending phase
                    this.transitionState = TransitionState.RUNNING;
                }
            }
            case ENDING -> {
                this.transitionTick++;

                // set SoundInstance as finished if ending phase finished its duration
                if (this.transitionTick > this.endTransitionTicks) {
                    this.callback.onFinished(this);
                }
            }
        }

        // apply volume and pitch modulation here,
        // if you use a normal SoundInstance class
    }

    // moves the sound instance position to the sound source's position
    protected void setPositionToEntity() {
        this.x = DynSource.getPosition().get(Direction.Axis.X);
        this.y = DynSource.getPosition().get(Direction.Axis.Y);
        this.z = DynSource.getPosition().get(Direction.Axis.Z);
    }

    // Sets the SoundInstance into its ending phase.
    // This is especially useful for external access to this SoundInstance
    public void end() {
        this.transitionState = TransitionState.ENDING;
}
}
