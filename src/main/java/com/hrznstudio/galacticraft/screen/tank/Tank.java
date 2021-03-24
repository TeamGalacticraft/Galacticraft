/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.screen.tank;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.Constants;

public class Tank {
    private static final int[] FLUID_TANK_8_16_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_8_16_X,
            Constants.TextureCoordinate.FLUID_TANK_8_16_Y,
            Constants.TextureCoordinate.FLUID_TANK_8_16_HEIGHT
    };
    private static final int[] FLUID_TANK_7_14_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_7_14_X,
            Constants.TextureCoordinate.FLUID_TANK_7_14_Y,
            Constants.TextureCoordinate.FLUID_TANK_7_14_HEIGHT
    };
    private static final int[] FLUID_TANK_6_12_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_6_12_X,
            Constants.TextureCoordinate.FLUID_TANK_6_12_Y,
            Constants.TextureCoordinate.FLUID_TANK_6_12_HEIGHT
    };
    private static final int[] FLUID_TANK_5_10_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_5_10_X,
            Constants.TextureCoordinate.FLUID_TANK_5_10_Y,
            Constants.TextureCoordinate.FLUID_TANK_5_10_HEIGHT
    };
    private static final int[] FLUID_TANK_4_8_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_4_8_X,
            Constants.TextureCoordinate.FLUID_TANK_4_8_Y,
            Constants.TextureCoordinate.FLUID_TANK_4_8_HEIGHT
    };
    private static final int[] FLUID_TANK_3_6_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_3_6_X,
            Constants.TextureCoordinate.FLUID_TANK_3_6_Y,
            Constants.TextureCoordinate.FLUID_TANK_3_6_HEIGHT
    };
    private static final int[] FLUID_TANK_2_4_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_2_4_X,
            Constants.TextureCoordinate.FLUID_TANK_2_4_Y,
            Constants.TextureCoordinate.FLUID_TANK_2_4_HEIGHT
    };
    private static final int[] FLUID_TANK_1_2_DATA = new int[]{
            Constants.TextureCoordinate.FLUID_TANK_1_2_X,
            Constants.TextureCoordinate.FLUID_TANK_1_2_Y,
            Constants.TextureCoordinate.FLUID_TANK_1_2_HEIGHT
    };

    public int id;
    public final int index;
    public final FixedFluidInv inv;
    public final int x;
    public final int y;
    public final int scale;

    public Tank(int index, FixedFluidInv inv, int x, int y, int scale) {
        this.index = index;
        this.inv = inv;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public FluidVolume getFluid() {
        return this.inv.getInvFluid(this.index);
    }

    public FluidAmount getCapacity() {
        return this.inv.getMaxAmount_F(this.index);
    }

    public int[] getPositionData() {
        if (getCapacity().asInexactDouble() != Math.floor(getCapacity().asInexactDouble())) {
            throw new UnsupportedOperationException("NYI");
        }
        int size = getCapacity().asInt(1) * scale;

        if (size < 1 || size > 16) {
            throw new UnsupportedOperationException("NYI");
        }
        if (size > 8 && size % 2 == 1) {
            throw new UnsupportedOperationException("NYI");
        }

        if (size == 8 || size == 16) {
            return FLUID_TANK_8_16_DATA;
        } else if (size == 7 || size == 14) {
            return FLUID_TANK_7_14_DATA;
        } else if (size == 6 || size == 12) {
            return FLUID_TANK_6_12_DATA;
        } else if (size == 5 || size == 10) {
            return FLUID_TANK_5_10_DATA;
        } else if (size == 4) {
            return FLUID_TANK_4_8_DATA;
        } else if (size == 3) {
            return FLUID_TANK_3_6_DATA;
        } else if (size == 2) {
            return FLUID_TANK_2_4_DATA;
        } else if (size == 1) {
            return FLUID_TANK_1_2_DATA;
        }
        throw new AssertionError();
    }

}
