/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.machine;

import dev.galacticraft.machinelib.api.block.MachineBlock;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenCollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class OxygenCollectorBlock extends MachineBlock<OxygenCollectorBlockEntity> {
    public OxygenCollectorBlock(Properties settings) {
        super(settings, Constant.id(Constant.Block.OXYGEN_COLLECTOR));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (isActive(state) && world.getBlockEntity(pos) instanceof OxygenCollectorBlockEntity machine && machine.collectionAmount > 0) {
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

                    if (random.nextBoolean()) {
                        x2 = pos.getX() + 0.5D + 0.25D * dir;
                        mX = random.nextFloat() * 2.0F * dir;
                    } else {
                        z2 = pos.getZ() + 0.5D + 0.25D * dir;
                        mZ = random.nextFloat() * 2.0F * dir;
                    }

                    world.addParticle(
                            new DustParticleOptions(new Vector3f(0.8f, 0.8f, 1.0f), 1.0F),
                            x2, y2, z2,
                            mX, mY, mZ);
                }
            }
        }
    }
}