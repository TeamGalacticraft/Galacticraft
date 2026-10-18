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

package dev.galacticraft.mod.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

/**
 * Switches to third person while the player's parachute is open, so they can see it,
 * and back to first person when it closes if that is where the player started.
 */
public final class ParachuteCamera {
    private static boolean wasParachuting = false;
    private static boolean switchedFromFirstPerson = false;

    private ParachuteCamera() {
    }

    public static void clientTick(Minecraft client) {
        boolean parachuting = client.player != null && client.player.galacticraft$isParachuting();
        if (parachuting && !wasParachuting) {
            if (client.options.getCameraType().isFirstPerson()) {
                client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                switchedFromFirstPerson = true;
            }
        } else if (!parachuting && wasParachuting) {
            if (switchedFromFirstPerson && client.player != null) {
                client.options.setCameraType(CameraType.FIRST_PERSON);
            }
            switchedFromFirstPerson = false;
        }
        wasParachuting = parachuting;
    }
}
