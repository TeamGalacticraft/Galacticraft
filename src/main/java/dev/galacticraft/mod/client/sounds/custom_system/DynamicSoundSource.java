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



// This is a utility, which may be used on both the client and the logical server side.
// So this Interface should be stored in the common packages instead of the client only packages, 
// if you make use of the "split sources" option

package dev.galacticraft.mod.client.sounds.custom_system;

import net.minecraft.world.phys.Vec3;

public interface DynamicSoundSource {

	// gets access to how many ticks have passed for e.g. a BlockEntity instance
	int getTick();

	// gets access to where currently this instance is placed in the world
	Vec3 getPosition();

	// holds a normalized (range of 0-1) value, showing how much stress this instance is currently experiencing
	// It is more or less just an arbitrary value, which will cause the sound to change its pitch while playing.
	float getState();
}
