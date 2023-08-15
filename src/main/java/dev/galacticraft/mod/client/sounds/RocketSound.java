/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.content.entity.RocketEntity;
import dev.galacticraft.mod.content.GCSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class RocketSound extends AbstractTickableSoundInstance {
    private final RocketEntity rocket;
    private boolean ignition = false;

    public RocketSound(RocketEntity rocket) {
        super(GCSounds.SHUTTLE_SHUTTLE, SoundSource.NEUTRAL, rocket.level().getRandom());
        this.rocket = rocket;
        this.x = rocket.getX();
        this.y = rocket.getY();
        this.z = rocket.getZ();
    }

    @Override
    public void tick() {
        if (this.rocket.getLaunchStage() == LaunchStage.IGNITED) {
            if (!ignition) {
                this.pitch = 0.0F;
                ignition = true;
            }
            if (this.rocket.getTimeBeforeLaunch() < 400)
            {
                if (this.pitch < 1.0F)
                {
                    this.pitch += 0.0025F;
                }

                if (this.pitch > 1.0F)
                {
                    this.pitch = 1.0F;
                }
            }
        } else
        {
            this.pitch = 1.0F;
        }

        if (this.rocket.getLaunchStage().ordinal() >= LaunchStage.IGNITED.ordinal())
        {
            if (this.rocket.getY() > 1000)
            {
                this.volume = 0F;
            } else if (this.rocket.getY() > 200)
            {
                this.volume = 1.0F - (float) ((this.rocket.getY() - 200) / (1000.0 - 200));
            } else
            {
                this.volume = 1.0F;
            }
        }

        this.x = rocket.getX();
        this.y = rocket.getY();
        this.z = rocket.getZ();
    }
}
