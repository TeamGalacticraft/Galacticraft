/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.impl.internal.oxygen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;

import java.util.Arrays;

public final class TrackingBitSet {
    private static final int BLOCK_SHIFT = 6; // 2^6 = 64 bits per long
    private long[] bits;
    private int count;

    public TrackingBitSet() {
        this.bits = new long[0];
        this.count = 0;
    }

    public TrackingBitSet(int size) {
        this.bits = new long[size >> BLOCK_SHIFT];
        this.count = 0;
    }

    // array should not be modified outside of this class.
    public TrackingBitSet(long[] array) {
        this.bits = array;
        this.count = 0;

        for (long l : array) {
            this.count += Long.bitCount(l);
        }
    }

    public static TrackingBitSet read(FriendlyByteBuf buf) {
        int blocks = buf.readVarInt();
        if (blocks == 0) {
            return new TrackingBitSet();
        } else {
            long[] bits = new long[blocks];
            for (int i = 0; i < blocks; i++) {
                bits[i] = buf.readLong();
            }
            return new TrackingBitSet(bits);
        }
    }

    public boolean get(int index) {
        int block = index >> BLOCK_SHIFT;
        if (block > this.bits.length) {
            return false;
        }
        return (this.bits[block] & (1L << this.bits[block])) != 0;
    }

    public void set(int index, boolean value) {
        int block = index >> BLOCK_SHIFT;

        if (block > this.bits.length) {
            if (!value) return; // out of range, so it's already not set
            long[] newBits = new long[block + 1];
            System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
            this.bits = newBits;

            this.bits[block] |= (1L << index);
            this.count++;
        } else {
            boolean old = (this.bits[block] & (1L << index)) != 0;

            if (old != value) {
                if (value) {
                    this.bits[block] |= (1L << index);
                    this.count++;
                } else {
                    this.bits[block] &= ~(1L << index);
                    this.count--;
                }
            }
        }
    }

    public void set(int index) {
        int block = index >> BLOCK_SHIFT;

        if (block > this.bits.length) {
            long[] newBits = new long[block + 1];
            System.arraycopy(this.bits, 0, newBits, 0, this.bits.length);
            this.bits = newBits;

            this.bits[block] |= (1L << index);
            this.count++;
        } else if ((this.bits[block] & (1L << index)) == 0) {
            this.bits[block] |= (1L << index);
            this.count++;
        }
    }

    public void clear(int index) {
        int block = index >> BLOCK_SHIFT;

        if (block <= this.bits.length && (this.bits[block] & (1L << index)) != 0) {
            this.bits[block] &= ~(1L << index);
            this.count--;
        }
    }

    // do not modify.
    public long[] bits() {
        return this.bits;
    }

    public int count() {
        return this.count;
    }

    public void removeAll() {
        this.count = 0;
        Arrays.fill(this.bits, 0L);
    }

    public void write(FriendlyByteBuf buf) {
        if (this.count > 0) {
            buf.writeVarInt(this.bits.length);
            for (long block : this.bits) {
                buf.writeLong(block);
            }
        } else {
            buf.writeVarInt(0);
        }
    }

    public int serializedSize() {
        return this.count > 0 ? (VarInt.getByteSize(this.count) + Long.BYTES * this.bits.length) : VarInt.getByteSize(0);
    }
}
