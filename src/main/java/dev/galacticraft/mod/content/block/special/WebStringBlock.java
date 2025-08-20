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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WebStringBlock extends WebBlock {

    public static final MapCodec<WebBlock> CODEC = simpleCodec(WebStringBlock::new);

    public static final EnumProperty<WebStringPart> WEB_STRING_PART = WebStringPart.WEB_STRING_PART;

    public WebStringBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WEB_STRING_PART, WebStringPart.MIDDLE));
    }

    protected static final VoxelShape FULL_VOXEL = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape BOTTOM_VOXEL = Block.box(5.0, 4.0, 5.0, 11.0, 16.0, 11.0);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(WEB_STRING_PART) == WebStringPart.BOTTOM) {
            return BOTTOM_VOXEL;
        } else {
            return FULL_VOXEL;
        }
    }

    @Override
    public MapCodec<WebBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos().above();
        BlockState state = ctx.getLevel().getBlockState(pos);
        if (state.is(GCBlocks.WEB_STRING)) {
            return defaultBlockState().setValue(WEB_STRING_PART, WebStringPart.BOTTOM);
        }
        return defaultBlockState().setValue(WEB_STRING_PART, WebStringPart.TOP_BOTTOM);
    }

    @Nullable
    private ItemInteractionResult checkCanBlockBePlaced(Level level, BlockPos checkPos, Player player, ItemStack itemStack, BlockState newState) {
        BlockState checkState = level.getBlockState(checkPos);
        if (checkState.canBeReplaced() && newState.canSurvive(level, checkPos)) {
            level.setBlockAndUpdate(checkPos, newState);
            level.playSound(null, checkPos, newState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (newState.getSoundType().getVolume() + 1.0F) / 2.0F, newState.getSoundType().getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, checkPos, GameEvent.Context.of(player, newState));

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        } else if (!checkState.is(this)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return null;
    }

    private ItemInteractionResult convertToWebTorch(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        BlockState newState = GCBlocks.WEB_TORCH.defaultBlockState().setValue(WebTorchBlock.TOP,
                state.getValue(WEB_STRING_PART) == WebStringPart.TOP
                        || state.getValue(WEB_STRING_PART) == WebStringPart.TOP_BOTTOM);
        level.setBlockAndUpdate(pos, newState);
        level.playSound(null, pos, newState.getSoundType().getPlaceSound(), SoundSource.BLOCKS,
                (newState.getSoundType().getVolume() + 1.0F) / 2.0F, newState.getSoundType().getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, newState));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult extendBlockDown(ItemStack stack, Level level, BlockPos pos, Player player, BlockState newState) {
        // Find a pos directly below the existing block (this) but above world bottom where the new block can be placed.
        for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() > level.getMinBuildHeight(); checkPos = checkPos.offset(0, -1, 0)) {
            ItemInteractionResult result = checkCanBlockBePlaced(level, checkPos, player, stack, newState);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /// This method is called whe the player tries to use something on a Web String block.
    /// The default behaviour is extended by checking if that item is a glow stone torch, which
    /// automatically hands the torch in the web. Web strings below the torch are broken.
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemInteractionResult result = null;
        // If using a glowstone torch on a web string, then it gets hung in the web string becoming a torch web block.
        if (stack.is(GCItems.GLOWSTONE_TORCH)) {
            result = convertToWebTorch(stack, state, level, pos, player);
        // If using a Web string on the web string, then extend the whole column of web strings down if we can.
        } else if (stack.is(GCBlocks.WEB_STRING.asItem())) {
            result = extendBlockDown(stack, level, pos, player, defaultBlockState().setValue(WEB_STRING_PART, WebStringPart.BOTTOM));
        // If using a torch web on the web string, then extend the whole column of web strings down if we can.
        } else if (stack.is(GCBlocks.WEB_TORCH.asItem())) {
            result = extendBlockDown(stack, level, pos, player, GCBlocks.WEB_TORCH.defaultBlockState());
        }

        if (result == null) {
            result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        return result;
    }

    ///  Manages the appearance of the web strings via the web string part property. Effectivel top means its attached
    /// to something, bottom means it is the end dangling down, and middle means it is neither the top nor the bottom.
    /// Top_Bottom means it is both the top and the only bit. The method also handles breaking of the web string
    /// if the block above is broken.
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If above changed and it isn't a web wtring block (and in theory not a solid block) then it breaks.
        if (direction == Direction.UP && !neighborState.is(GCBlocks.WEB_STRING)) {
            return Blocks.AIR.defaultBlockState();
            // If below changed
        } else if (direction == Direction.DOWN) {
            WebStringPart webStringState = state.getValue(WEB_STRING_PART);
            // if below is a web string or a torch web
            if (neighborState.is(GCBlocks.WEB_STRING) || neighborState.is(GCBlocks.WEB_TORCH)) {
                // If it was the top item
                if (webStringState == WebStringPart.TOP_BOTTOM || webStringState == WebStringPart.TOP) {
                    return state.setValue(WEB_STRING_PART, WebStringPart.TOP); // Then still top
                } else {
                    return state.setValue(WEB_STRING_PART, WebStringPart.MIDDLE); // Otherwise middle
                }
            } else {
                // If it was top item
                if (webStringState == WebStringPart.TOP) {
                    return state.setValue(WEB_STRING_PART, WebStringPart.TOP_BOTTOM); // Then top and bottom
                } else {
                    return state.setValue(WEB_STRING_PART, WebStringPart.BOTTOM); // Otherwise bottom
                }
            }
        }
        // No change
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState checkState = level.getBlockState(pos);
        return this.canAttachTo(level, pos.above(), Direction.DOWN) && (checkState.getFluidState().is(Fluids.EMPTY) || !checkState.is(Blocks.WATER));
    }

    private boolean canAttachTo(BlockGetter world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isFaceSturdy(world, pos, side) || blockState.is(GCBlocks.WEB_STRING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> compositeStateBuilder) {
        compositeStateBuilder.add(WEB_STRING_PART);
    }

    public enum WebStringPart implements StringRepresentable {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom"),
        TOP_BOTTOM( "top_bottom");

        private final String name;

        private WebStringPart(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static final EnumProperty<WebStringPart> WEB_STRING_PART = EnumProperty.create("web_string_part", WebStringPart.class);
    }

}