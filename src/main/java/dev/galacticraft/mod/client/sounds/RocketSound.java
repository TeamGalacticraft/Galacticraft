/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class RocketSound extends AbstractTickableSoundInstance {
    private final RocketEntity rocket;
    private boolean ignition = false;

    public RocketSound(RocketEntity rocket) {
        super(GCSounds.SHUTTLE_SHUTTLE, SoundSource.NEUTRAL, rocket.level().getRandom());
        this.rocket = rocket;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.volume = 0.00001F;  //If it's zero it won't start playing
        this.pitch = 0.0F;  //pitch
        this.looping = true;
        this.delay = 0;  //repeat delay
        setSoundLocation(rocket.getX(), rocket.getY(), rocket.getZ());
    }

    @Override
    public void tick() {
        if (this.rocket.isAlive()) {
            if (this.rocket.getLaunchStage() == LaunchStage.IGNITED) {
                if (!ignition) {
                    this.pitch = 0.0F;
                    ignition = true;
                }
                if (this.rocket.getTimeBeforeLaunch() < this.rocket.getPreLaunchWait()) {
                    if (this.pitch < 1.0F) {
                        this.pitch += 0.0025F;
                    }

                    if (this.pitch > 1.0F) {
                        this.pitch = 1.0F;
                    }
                }
            } else {
                this.pitch = 1.0F;
            }

            if (this.rocket.getLaunchStage().ordinal() >= LaunchStage.IGNITED.ordinal()) {
                if (this.rocket.getY() > 1000) {
                    this.volume = 0F;
                    if (this.rocket.getLaunchStage() != LaunchStage.FAILED) {
                        stop();
                    }
                } else if (this.rocket.getY() > Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                    this.volume = 1.0F - (float) ((this.rocket.getY() - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / (1000.0 - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT));
                } else {
                    this.volume = 1.0F;
                }
            }

            this.setSoundLocation(rocket.getX(), rocket.getY(), rocket.getZ());
        } else {
            stop();
        }
    }

    public void setSoundLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
