/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.block.entity;

import com.hrznstudio.galacticraft.accessor.ServerWorldAccessor;
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.api.wire.NetworkManager;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlockEntity extends BlockEntity implements Tickable {
    public WireBlockEntity() {
        super(GalacticraftBlockEntities.WIRE_TYPE);
    }


    @Override
    public void tick() {
        assert world instanceof ServerWorld;
        if (((ServerWorldAccessor) world).getNetworkManager().getNetwork(pos) == null && world.getBlockState(pos).getBlock() instanceof WireBlock && !world.isClient) {
            this.world.getBlockState(pos).onBlockAdded(world, pos, Blocks.AIR.getDefaultState(), false);
        }
    }
}
