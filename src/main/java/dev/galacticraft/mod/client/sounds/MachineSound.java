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
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MachineSound extends AbstractTickableSoundInstance {
    protected final BlockEntity machine;
    protected final SoundCallback callback;
    protected TransitionState transitionState = TransitionState.STARTING;
    protected final int startTransitionTicks, endTransitionTicks;
    protected int transitionTick = 0;
    protected final float maxVolume;
    protected final SoundEvent event;

    public MachineSound(MachineBlockEntity machine, SoundEvent event, SoundCallback callback, float maxVolume) {
        super(event, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        // references to other objects
        this.machine = machine;
        this.event = event;
        this.callback = callback;
        // important constants
        this.maxVolume = maxVolume;
        this.looping = true;
        this.delay = 0;
        this.startTransitionTicks = 30;
        this.endTransitionTicks = 20;
        // starting values
        this.volume = 0.0F; // will still work if canStartSilent() == true
        this.pitch = 1.0F;
        this.setPosition();
    }

    @Override
    public void tick() {
        if (this.machine instanceof MachineBlockEntity blockEntity && blockEntity.isRemoved()) {
            this.end();
        }
        if (this.machine == null) {
            this.callback.onFinished(this);
            return;
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
            case RUNNING:
                break;
        }
        this.modulateSoundforTransition();
    }

    protected void setPosition() {
        this.x = machine.getBlockPos().getX();
        this.y = machine.getBlockPos().getY();
        this.z = machine.getBlockPos().getZ();
    }

    public void end() {
        this.transitionState = TransitionState.ENDING;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    protected void modulateSoundforTransition() {
        float normTick = switch (transitionState) {
            case STARTING -> (float) this.transitionTick / this.startTransitionTicks;
            case ENDING -> 1.0F - ((float) this.transitionTick / this.endTransitionTicks);
            case RUNNING -> 1.0F;
        };
        this.volume = Mth.lerp(normTick, 0.0F, this.maxVolume);
    }

        public enum TransitionState {
        STARTING, RUNNING, ENDING
        }

}
