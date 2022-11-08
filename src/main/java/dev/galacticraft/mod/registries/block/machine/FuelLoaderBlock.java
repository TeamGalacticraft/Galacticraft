/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.registries.block.machine;

import dev.galacticraft.mod.registries.block.entity.FuelLoaderBlockEntity;
import dev.galacticraft.mod.registries.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderBlock extends SimpleMachineBlock<FuelLoaderBlockEntity> {
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public FuelLoaderBlock(Properties settings) {
        super(settings, FuelLoaderBlockEntity::new);
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(CONNECTED);
    }

    @Override
    public FuelLoaderBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FuelLoaderBlockEntity(pos, state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if (direction != Direction.UP && direction != Direction.DOWN && newState.getBlock() instanceof RocketLaunchPadBlock) {
            ((FuelLoaderBlockEntity) world.getBlockEntity(pos)).updateConnections(direction);
        }
        return super.updateShape(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public Component machineDescription(ItemStack stack, BlockGetter view, boolean context) {
        return Component.translatable("tooltip.galacticraft.fuel_loader");
    }
}