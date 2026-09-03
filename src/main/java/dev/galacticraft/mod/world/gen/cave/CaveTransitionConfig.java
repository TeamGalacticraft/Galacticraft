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

package dev.galacticraft.mod.world.gen.cave;

public record CaveTransitionConfig(
        boolean enabled,
        CaveTransitionStrength strength
) {
    public static CaveTransitionConfig none() {
        return new CaveTransitionConfig(false, CaveTransitionStrength.VERY_STRONG);
    }

    public static CaveTransitionConfig veryWeak() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.VERY_WEAK);
    }

    public static CaveTransitionConfig weak() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.WEAK);
    }

    public static CaveTransitionConfig medium() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.MEDIUM);
    }

    public static CaveTransitionConfig strong() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.STRONG);
    }

    public static CaveTransitionConfig veryStrong() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.VERY_STRONG);
    }
}