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
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.WireNetworkAccessor;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.util.DirectionUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class WireBlock extends PipeShapedBlock implements EntityBlock {
    public static final long TIER_1_THROUGHPUT = 240;
    public static final long TIER_2_THROUGHPUT = 480;

    private final long throughput;

    public WireBlock(long throughput, float radius, Properties settings) {
        super(radius, settings.pushReaction(PushReaction.BLOCK));
        this.throughput = throughput;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && Galacticraft.CONFIG.isDebugLogEnabled() && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof Wire wire) {
                Constant.LOGGER.info("Network: {}", wire.getNetwork());
            }
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState blockState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WireBlockEntity wire && blockState != state) {
            Direction dir = DirectionUtil.fromNormal(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
            wire.setBlockState(blockState); //fixme
            ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().wireUpdated(pos, wire, dir);
        }

        return blockState;
    }

    @Override
    public boolean canConnectTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos, BlockState neighborState) {
        return (level instanceof Level l && EnergyStorage.SIDED.find(l, neighborPos, direction.getOpposite()) != null) || neighborState.getBlock() instanceof WireBlock wb && wb.getThroughput() == ((WireBlock) state.getBlock()).getThroughput();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!level.isClientSide && !newState.is(state.getBlock())) ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().wireRemoved(pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        if (!level.isClientSide) ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().wirePlaced(pos, (Wire) level.getBlockEntity(pos));
    }

    @Nullable
    @Override
    public WireBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    public long getThroughput() {
        return this.throughput;
    }
}
