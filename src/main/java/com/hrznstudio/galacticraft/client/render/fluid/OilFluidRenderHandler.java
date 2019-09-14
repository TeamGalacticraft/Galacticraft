/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.client.render.fluid;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.texture.FabricSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OilFluidRenderHandler implements FluidRenderHandler {

    public static final Sprite STILL_TEXTURE = new FabricSprite(new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_STILL), 16, 16);
    public static final Sprite FLOWING_TEXTURE = new FabricSprite(new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_FLOWING), 16, 16);

    @Override
    public Sprite[] getFluidSprites(ExtendedBlockView view, BlockPos pos, FluidState state) {
        return new Sprite[]{STILL_TEXTURE, FLOWING_TEXTURE};
    }

    @Override
    public int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
        return 0;
    }
}
