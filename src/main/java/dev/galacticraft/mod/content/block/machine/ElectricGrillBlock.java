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

package dev.galacticraft.mod.content.block.machine;

import dev.galacticraft.machinelib.api.block.SimpleMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ElectricGrillBlock extends SimpleMachineBlock {
    public ElectricGrillBlock(Properties settings, ResourceLocation id) {
        super(settings.lightLevel(state -> state.getValue(SimpleMachineBlock.ACTIVE) ? 10 : 0), id);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (isActive(state) && rand.nextDouble() < 0.1D) {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction.Axis axis = direction.getAxis();
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            double d = rand.nextDouble() * 0.6D - 0.3D;
            double xo = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d;
            double yo = rand.nextDouble() * 6.0D / 16.0D;
            double zo = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d;
            world.addParticle(ParticleTypes.ELECTRIC_SPARK, x + xo, y + yo, z + zo, 0.0D, 0.0D, 0.0D);
        }
    }
}
