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

package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface WalkwayBlock {
    // Maps connection state (from PipeShapedBlock.generateAABBIndex()) and walkway facing direction to the right VoxelShape
    Map<Pair<Integer, Direction>, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        float pipeRadius = 0.125f;
        VoxelShape[] pipeShapes = PipeShapedBlock.makeShapes(pipeRadius);

        for (int pipeAabb = 0; pipeAabb < Math.pow(2, 6); pipeAabb++) {
            for (Direction platformDirection : Direction.values()) {
                map.put(Pair.of(pipeAabb, platformDirection), Shapes.or(
                        pipeShapes[pipeAabb],
                        ConnectingBlockUtil.WALKWAY_SHAPES.get(platformDirection)
                ));
            }
        }
    });

    static @NotNull VoxelShape getShape(Connected connected, BlockState blockState) {
        return SHAPES.get(Pair.of(PipeShapedBlock.generateAABBIndex(connected), blockState.getValue(BlockStateProperties.FACING)));
    }

    // Returns a shape with all connections active
    // Should be used when no block entity is available, like when the game is checking if the player is obstructing placement
    static @NotNull VoxelShape getShape(BlockState blockState) {
        return SHAPES.get(Pair.of(63, blockState.getValue(BlockStateProperties.FACING)));
    }

    static @NotNull BlockState applyStateForPlacement(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() != null) {
            state = state.setValue(BlockStateProperties.FACING, Direction.orderedByNearest(context.getPlayer())[0].getOpposite());
        }
        return state;
    }

    static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state.setValue(BlockStateProperties.FACING, Direction.UP);
    }

    static void addStateDefinitions(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
}