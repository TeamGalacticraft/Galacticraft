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

public final class CaveCarvingMask {
    public static final byte NONE = 0;
    public static final byte OUTER = 1;
    public static final byte INNER = 2;
    public static final byte AIR = 3;

    private final int minY;
    private final int maxY;
    private final int height;
    private final byte[] zones;
    private final MoonCavePlan[] owners;

    public CaveCarvingMask(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        this.height = maxY - minY + 1;
        this.zones = new byte[16 * 16 * this.height];
        this.owners = new MoonCavePlan[this.zones.length];
    }

    public void set(int localX, int y, int localZ, CaveZone zone, MoonCavePlan owner) {
        this.setRaw(localX, y, localZ, encode(zone), owner);
    }

    public void setRaw(int localX, int y, int localZ, byte zone, MoonCavePlan owner) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return;
        }

        int index = this.index(localX, y, localZ);

        if (zone >= this.zones[index]) {
            this.zones[index] = zone;
            this.owners[index] = owner;
        }
    }

    public byte getRaw(int localX, int y, int localZ) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return NONE;
        }

        return this.zones[this.index(localX, y, localZ)];
    }

    public CaveZone get(int localX, int y, int localZ) {
        return decode(this.getRaw(localX, y, localZ));
    }

    public MoonCavePlan owner(int localX, int y, int localZ) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return null;
        }

        return this.owners[this.index(localX, y, localZ)];
    }

    private int index(int localX, int y, int localZ) {
        return ((y - this.minY) * 16 + localZ) * 16 + localX;
    }

    private static byte encode(CaveZone zone) {
        return switch (zone) {
            case OUTER_SHELL -> OUTER;
            case INNER_SHELL -> INNER;
            case AIR -> AIR;
            default -> NONE;
        };
    }

    public static CaveZone decode(byte value) {
        return switch (value) {
            case OUTER -> CaveZone.OUTER_SHELL;
            case INNER -> CaveZone.INNER_SHELL;
            case AIR -> CaveZone.AIR;
            default -> CaveZone.NONE;
        };
    }
}