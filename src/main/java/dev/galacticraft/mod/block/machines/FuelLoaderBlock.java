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

package dev.galacticraft.mod.block.machines;

import dev.galacticraft.mod.api.block.ConfigurableMachineBlock;
import dev.galacticraft.mod.api.block.entity.ConfigurableMachineBlockEntity;
import dev.galacticraft.mod.block.entity.FuelLoaderBlockEntity;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderBlock extends ConfigurableMachineBlock {
    public static final BooleanProperty CONNECTED = BooleanProperty.of("connected");

    public FuelLoaderBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(CONNECTED, false));
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new FuelLoaderBlockEntity();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
        stateBuilder.add(CONNECTED);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction != Direction.UP && direction != Direction.DOWN && newState.getBlock() instanceof RocketLaunchPadBlock) {
            ((FuelLoaderBlockEntity) world.getBlockEntity(pos)).updateConnections(direction);
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean context) {
        return new TranslatableText("tooltip.galacticraft.fuel_loader");
    }
}