/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.screen.OxygenCollectorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlock extends ConfigurableMachineBlock {
    public OxygenCollectorBlock(Settings settings) {
        super(settings, OxygenCollectorScreenHandler::new, OxygenCollectorBlockEntity::new,
                new TranslatableText("tooltip.galacticraft-rewoven.oxygen_collector")
                        .setStyle(Constants.Misc.TOOLTIP_STYLE));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof OxygenCollectorBlockEntity && ((OxygenCollectorBlockEntity) blockEntity).collectionAmount > 0) {
            for (int particleCount = 0; particleCount < 10; particleCount++) {
                for (int int_1 = 0; int_1 < 32; ++int_1) {
                    world.addParticle(
                            new DustParticleEffect(0.9f, 0.9f, 1.0f, 1.0F),
                            pos.getX() + 0.5D,
                            (random.nextFloat() - 0.5D) * 0.5D + /*random.nextDouble() * 2.0D*/ 0.5D,
                            pos.getZ() + 0.5D,
                            random.nextGaussian(),
                            0.0D,
                            random.nextGaussian());
                }
            }
        }
    }
}