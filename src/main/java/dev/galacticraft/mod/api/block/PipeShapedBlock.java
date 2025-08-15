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

package dev.galacticraft.mod.api.block;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class PipeShapedBlock extends ConnectedBlock {
    public final VoxelShape[] shapes;

    protected PipeShapedBlock(float radius, BlockBehaviour.Properties properties) {
        super(properties);
        this.shapes = makeShapes(radius);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.shapes[ConnectingBlockUtil.generateAABBIndex(state)];
    }

    public static VoxelShape[] makeShapes(float radius) {
        float f = 0.5F - radius;
        float g = 0.5F + radius;
        VoxelShape baseShape = Block.box(
                f * 16.0F, f * 16.0F, f * 16.0F, g * 16.0F, g * 16.0F, g * 16.0F
        );
        VoxelShape[] outerShapes = new VoxelShape[Constant.Misc.DIRECTIONS.length];

        for (int i = 0; i < Constant.Misc.DIRECTIONS.length; i++) {
            Direction direction = Constant.Misc.DIRECTIONS[i];
            outerShapes[i] = Shapes.box(
                    0.5 + Math.min(-radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepZ() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepZ() * 0.5)
            );
        }

        return ConnectingBlockUtil.generateShapeCache(baseShape, outerShapes);
    }
}
