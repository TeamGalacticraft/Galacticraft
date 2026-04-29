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
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class GCSound extends AbstractTickableSoundInstance {
    protected final BlockEntity entity;
    protected final SoundCallback callback;

    public GCSound(BlockEntity entity, SoundEvent event, SoundSource source, SoundCallback callback) {
        super(event, source, SoundInstance.createUnseededRandom());
        // references to other objects
        this.entity = entity;
        this.callback = callback;
        // important behavior
        this.looping = true;
        this.delay = 0;
        // starting values
        this.volume = 0.0F; // will still work if canStartSilent() == true
        this.pitch = 1.0F;
        this.setPosition();
    }


    @Override
    public void tick() {
        if (this.entity == null) {
            this.end();
        }

    }

    protected void setPosition() {
        this.x = entity.getBlockPos().getX();
        this.y = entity.getBlockPos().getY();
        this.z = entity.getBlockPos().getZ();
    }

    public void end() {
        this.callback.onFinished(this);
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
