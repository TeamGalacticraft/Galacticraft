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
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CoalGeneratorBlock extends MachineBlock<CoalGeneratorBlockEntity> {
    public CoalGeneratorBlock(Properties settings) {
        super(settings, Constant.id(Constant.Block.COAL_GENERATOR));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (isActive(state)) {
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            if (rand.nextDouble() < 0.1D) {
                world.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction.Axis axis = direction.getAxis();
            double d = rand.nextDouble() * 0.6D - 0.3D;
            double xo = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d;
            double yo = rand.nextDouble() * 6.0D / 16.0D;
            double zo = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d;
            world.addParticle(ParticleTypes.SMOKE, x + xo, y + yo, z + zo, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, x + xo, y + yo, z + zo, 0.0D, 0.0D, 0.0D);
        }
    }
}
