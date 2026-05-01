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

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class GCSound extends AbstractTickableSoundInstance {
    protected final BlockEntity entity;
    protected final SoundCallback callback;
    protected TransitionState transitionState;
    protected final int startTransitionTicks, endTransitionTicks;
    protected int transitionTick = 0;
    protected final float maxVolume;
    protected final SoundEvent event;

    public GCSound(BlockEntity entity, SoundEvent event, SoundSource source, SoundCallback callback, float maxVolume) {
        super(event, source, SoundInstance.createUnseededRandom());
        // references to other objects
        this.entity = entity;
        this.callback = callback;
        this.event = event;
        // important behavior
        this.looping = true;
        this.delay = 0;
        // starting values
        this.volume = 0.0F; // will still work if canStartSilent() == true
        this.pitch = 1.0F;
        this.setPosition();
        this.transitionState = TransitionState.STARTING;
        // constants
        this.startTransitionTicks = 30;
        this.endTransitionTicks = 20;
        this.maxVolume = maxVolume;
    }


    @Override
    public void tick() {
        if (this.entity == null) {
            this.callback.onFinished(this);
        }

        switch (this.transitionState) {
            case STARTING:
                this.transitionTick++;
                if (this.transitionTick > this.startTransitionTicks) {
                    this.transitionTick = 0;
                    this.transitionState = TransitionState.RUNNING;
                }
                break;
            case ENDING:
                this.transitionTick++;
                if (this.transitionTick > this.endTransitionTicks) {
                    this.callback.onFinished(this);
                }
                break;
            default:
                break;
            }

        }

    protected void setPosition() {
        this.x = entity.getBlockPos().getX();
        this.y = entity.getBlockPos().getY();
        this.z = entity.getBlockPos().getZ();
    }

    public void end() {
        this.transitionState = TransitionState.ENDING;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    protected void transitionSound() {
        float normTick = switch (transitionState) {
            case STARTING -> (float) this.transitionTick/this.startTransitionTicks;
            case ENDING -> 1.0F-((float) this.transitionTick/this.endTransitionTicks);
            default -> 1.0F;
        };
        this.volume = Mth.lerp(normTick,0.0F,this.maxVolume);
    }

}
