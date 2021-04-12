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

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlock extends ConfigurableMachineBlock {
    public OxygenCollectorBlock(Properties settings) {
        super(settings, OxygenCollectorBlockEntity::new,
                new TranslatableComponent("tooltip.galacticraft-rewoven.oxygen_collector")
                        .setStyle(Constants.Styles.TOOLTIP_STYLE));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof OxygenCollectorBlockEntity && ((OxygenCollectorBlockEntity) blockEntity).collectionAmount > 0) {
            for (int count = 0; count < 10; count++) {
                for (int i = 0; i < 32; ++i) {
                    double x2 = pos.getX() + random.nextFloat();
                    double y2 = pos.getY() + random.nextFloat();
                    double z2 = pos.getZ() + random.nextFloat();
                    double mX, mY, mZ;
                    int dir = random.nextInt(2) * 2 - 1;
                    mX = (random.nextFloat() - 0.5D) * 0.5D;
                    mY = (random.nextFloat() - 0.5D) * 0.5D;
                    mZ = (random.nextFloat() - 0.5D) * 0.5D;

                    if (random.nextBoolean())
                    {
                        x2 = pos.getX() + 0.5D + 0.25D * dir;
                        mX = random.nextFloat() * 2.0F * dir;
                    }
                    else
                    {
                        z2 = pos.getZ() + 0.5D + 0.25D * dir;
                        mZ = random.nextFloat() * 2.0F * dir;
                    }

                    world.addParticle(
                            new DustParticleOptions(0.8f, 0.8f, 1.0f, 1.0F),
                            x2, y2, z2,
                            mX, mY, mZ);
                }
            }
        }
    }
}