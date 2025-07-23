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

import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.compat.omnishape.OmnishapeCompat;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.omnishape.api.OmnishapeData;
import dev.omnishape.registry.OmnishapeBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class PipeShapedBlock<BE extends BlockEntity & Connected> extends Block implements EntityBlock {
    public final VoxelShape[] shapes;

    protected PipeShapedBlock(float radius, BlockBehaviour.Properties properties) {
        super(properties);
        this.shapes = makeShapes(radius);
    }

    public VoxelShape makeShape(WireBlockEntity wire) {
        return this.shapes[generateAABBIndex(wire)];
    }

    @Override
    public abstract @Nullable BE newBlockEntity(BlockPos pos, BlockState state);

    public abstract boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState);

    protected abstract void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos);

    protected boolean updateConnection(BlockState currentState, BlockPos pos, Direction side, BlockPos neighborPos, Level level) {
        if (level.getBlockEntity(pos) instanceof Connected pipe) {
            boolean canConnect = this.canConnectTo(level, pos, side, neighborPos, currentState);

            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof PipeShapedBlock<?> neighbor) {
                canConnect &= neighbor.canConnectTo(level, neighborPos, side.getOpposite(), pos, neighborState);
            }

            boolean currentlyConnected = pipe.getConnections()[side.get3DDataValue()];
            pipe.getConnections()[side.get3DDataValue()] = canConnect;
            level.sendBlockUpdated(pos, currentState, currentState, Block.UPDATE_IMMEDIATE);
            return canConnect != currentlyConnected;
        } else {
            return false;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);

        for (Direction direction : Direction.values()) {
            this.updateConnection(state, pos, direction, pos.relative(direction), level);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);

        Direction direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        if (direction == null)
            return;

        if (this.updateConnection(state, pos, direction, neighborPos, level)) {
            this.onConnectionChanged(level, pos, direction, neighborPos);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (levelAccessor instanceof Level level) {
            if (this.updateConnection(state, pos, direction, neighborPos, level)) {
                this.onConnectionChanged(level, pos, direction, neighborPos);
            }
        }

        return state;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape baseShape = Shapes.empty();
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof Connected connected) {
            baseShape = this.shapes[generateAABBIndex(connected)];
        }

        // Add Omnishape overlay if present
        if (OmnishapeCompat.isLoaded() && be instanceof WireBlockEntity wire && wire.getOverlay() != null) {
            Matrix3f rot = wire.getRotationMatrix();
            VoxelShape overlayShape = wire.getOrCreateHitbox(rot);
            return Shapes.or(baseShape, overlayShape);
        }

        return baseShape;
    }

    public static VoxelShape[] makeShapes(float radius) {
        Direction[] directions = Direction.values();

        float f = 0.5F - radius;
        float g = 0.5F + radius;
        VoxelShape voxelShape = Block.box(
                f * 16.0F, f * 16.0F, f * 16.0F, g * 16.0F, g * 16.0F, g * 16.0F
        );
        VoxelShape[] voxelShapes = new VoxelShape[directions.length];

        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[i];
            voxelShapes[i] = Shapes.box(
                    0.5 + Math.min(-radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepZ() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepZ() * 0.5)
            );
        }

        VoxelShape[] voxelShapes2 = new VoxelShape[64];

        for (int j = 0; j < 64; j++) {
            VoxelShape voxelShape2 = voxelShape;

            for (int k = 0; k < directions.length; k++) {
                if ((j & 1 << k) != 0) {
                    voxelShape2 = Shapes.or(voxelShape2, voxelShapes[k]);
                }
            }

            voxelShapes2[j] = voxelShape2;
        }

        return voxelShapes2;
    }

    public static int generateAABBIndex(Connected connected) {
        int i = 0;

        Direction[] directions = Direction.values();
        for (int j = 0; j < directions.length; j++) {
            if (connected.getConnections()[directions[j].ordinal()]) {
                i |= 1 << j;
            }
        }

        return i;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack1, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        if (OmnishapeCompat.isLoaded()) {
            BlockEntity be = level.getBlockEntity(pos);
            ItemStack stack = player.getMainHandItem();
            // --- Inject Omnishape frame placement ---
            if (OmnishapeCompat.canExtractFromItem(stack)) {
                if (!level.isClientSide) {
                    if (be instanceof WireBlockEntity wireBe && wireBe.getOverlay() == null) {
                        OmnishapeData data = OmnishapeData.extractFromItem(stack);
                        wireBe.setOverlay(data);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1); // Consume frame item in survival
                        }
                        return ItemInteractionResult.SUCCESS;
                    }
                } else {
                    if (be instanceof WireBlockEntity wireBe && wireBe.getOverlay() == null) {
                        return ItemInteractionResult.SUCCESS; // client-side suppression
                    }
                }
            }

            // --- Inject wrench-removal of overlay ---
            if (be instanceof WireBlockEntity wire && wire.getOverlay() != null) {
                Vec3 localHit = hit.getLocation().subtract(Vec3.atLowerCornerOf(pos));
                VoxelShape wireShape = this.shapes[generateAABBIndex(wire)];

                boolean hitWire = preciseHit(wireShape, localHit);

                if (hitWire && isWrench(stack)) {
                    if (!level.isClientSide) {
                        // Drop frame
                        ItemStack frameStack = new ItemStack(OmnishapeBlocks.FRAME_BLOCK);
                        OmnishapeData.writeToItem(frameStack, wire.getOverlay());
                        Block.popResource(level, pos, frameStack);
                    }

                    wire.setOverlay(null);
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    wire.setChanged();

                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean isWrench(ItemStack stack) {
        return stack.is(GCItemTags.WRENCHES);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (OmnishapeCompat.isLoaded()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof WireBlockEntity wire && wire.hasOverlay()) {
                BlockHitResult hitResult = (BlockHitResult) player.pick(5.0, 0.0F, false);
                Vec3 localHit = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());

                VoxelShape wireShape = this.shapes[generateAABBIndex(wire)];
                VoxelShape frameShape = wire.getOrCreateHitbox(wire.getRotationMatrix());

                boolean hitWire = preciseHit(wireShape, localHit);
                boolean hitFrame = !hitWire && preciseHit(frameShape, localHit);

                wire.setTargetingFrame(hitFrame);

                if (hitFrame) {
                    BlockState camo = wire.getOverlay().camouflage();
                    return camo.getDestroyProgress(player, world, pos);
                }
            }
        }

        return super.getDestroyProgress(state, player, world, pos);
    }

    public static boolean preciseHit(VoxelShape shape, Vec3 point) {
        double px = point.x;
        double py = point.y;
        double pz = point.z;
        for (AABB box : shape.toAabbs()) {
            if (box.inflate(1e-6).contains(px, py, pz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (OmnishapeCompat.isLoaded()) {
            BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (!(be instanceof WireBlockEntity wire) || !wire.hasOverlay()) {
                return super.getDrops(state, builder);
            }

            // Read targeting info
            Boolean hitFrame = wire.getAndClearTargetingFrame();
            ItemStack frameStack = new ItemStack(OmnishapeBlocks.FRAME_BLOCK);
            OmnishapeData.writeToItem(frameStack, wire.getOverlay());

            if (hitFrame != null && hitFrame) {
                // Player was targeting the frame: drop only the frame
                return List.of(frameStack);
            }

            // Player hit the wire (or fallback): drop both
            List<ItemStack> baseDrops = super.getDrops(state, builder);
            baseDrops.add(frameStack);
            return baseDrops;
        }

        return super.getDrops(state, builder);
    }
}
