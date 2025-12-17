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

package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/// This class acts as a superclass for the WebTorch and WebString classes, providing common code for handling of
/// the intersection between the player and the web string and web torches. This is so the player slows down, similar
/// to the way that a player slows down in a normal web block. It differs to the MC WebBlock in that the collision
/// box is based on the voxel shape as opposed to a full bounding box for a block. It still inherits the behaviour
/// from the MC WebBlock though.

public class GCWebBlock extends WebBlock {
    public static final MapCodec<WebBlock> CODEC = simpleCodec(GCWebBlock::new);

    @Override
    public MapCodec<WebBlock> codec() {
        return CODEC;
    }

    public GCWebBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    // Must override this method or an exception will be generated!
    protected VoxelShape getShape(BlockState state) {
        return null;
    }

    protected AABB getShapeInWorldCoordinates(BlockState state, BlockPos pos) {
        return getShape(state).bounds().move(pos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entityGoalInfo) {
        AABB entityBB = entityGoalInfo.getBoundingBox();
        AABB shapeBB = getShapeInWorldCoordinates(state, pos);
        if (shapeBB.intersects(entityBB)) {
            super.entityInside(state, level, pos, entityGoalInfo);
        }
    }
}
