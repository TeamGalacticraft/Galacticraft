/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.MultiBlockBase;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.block.entity.MultiBlockPartBlockEntity;
import com.hrznstudio.galacticraft.screen.BasicSolarPanelScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlock extends ConfigurableMachineBlock implements MultiBlockBase {

    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);

    public BasicSolarPanelBlock(Settings settings) {
        super(settings, FRONT_SIDE_OPTION, BACK_SIDE_OPTION, RIGHT_SIDE_OPTION, LEFT_SIDE_OPTION, TOP_SIDE_OPTION, BOTTOM_SIDE_OPTION);
    }

    @Override
    public boolean consumesOxygen() {
        return false;
    }

    @Override
    public boolean generatesOxygen() {
        return false;
    }

    @Override
    public boolean consumesPower() {
        return false;
    }

    @Override
    public boolean generatesPower() {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite())
                .with(FRONT_SIDE_OPTION, SideOption.DEFAULT)
                .with(BACK_SIDE_OPTION, SideOption.DEFAULT)
                .with(RIGHT_SIDE_OPTION, SideOption.DEFAULT)
                .with(LEFT_SIDE_OPTION, SideOption.DEFAULT)
                .with(TOP_SIDE_OPTION, SideOption.DEFAULT)
                .with(BOTTOM_SIDE_OPTION, SideOption.DEFAULT);
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new BasicSolarPanelBlockEntity();
    }

    @Override
    public boolean consumesFluids() {
        return false;
    }

    @Override
    public boolean generatesFluids() {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return ActionResult.SUCCESS;
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Text getDisplayName() {
                return new TranslatableText("block.galacticraft-rewoven.basic_solar_panel");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(pos); // idk why we have to do this again, might want to look into it
                //TODO: Look into why we have to create a new PacketByteBuf.
                return new BasicSolarPanelScreenHandler(syncId, inv, buf);
            }
        });
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        dropInventory(world, pos);

        for (BlockPos otherPart : getOtherParts(state, pos)) {
            world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
        }

        super.onBroken(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(FRONT_SIDE_OPTION);
        builder.add(BACK_SIDE_OPTION);
        builder.add(RIGHT_SIDE_OPTION);
        builder.add(LEFT_SIDE_OPTION);
        builder.add(TOP_SIDE_OPTION);
        builder.add(BOTTOM_SIDE_OPTION);
    }

    private void dropInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity != null) {
            if (blockEntity instanceof BasicSolarPanelBlockEntity) {
                BasicSolarPanelBlockEntity basicSolarPanelBlockEntity = (BasicSolarPanelBlockEntity) blockEntity;

                ConfigurableMachineBlock.dropItems(world, pos, basicSolarPanelBlockEntity.getInventory(), basicSolarPanelBlockEntity);
            }
        }
    }

    @Override
    public List<BlockPos> getOtherParts(BlockState state, BlockPos pos) {
        List<BlockPos> parts = new LinkedList<>();
        BlockPos rod = pos.up();
        BlockPos mid = rod.up();
        BlockPos front = mid.north();
        BlockPos back = mid.south();

        BlockPos right = mid.east();
        BlockPos left = mid.west();

        BlockPos frontLeft = front.east();
        BlockPos frontRight = front.west();
        BlockPos backLeft = back.east();
        BlockPos backRight = back.west();

        parts.add(rod);
        parts.add(mid);
        parts.add(front);
        parts.add(back);

        parts.add(right);
        parts.add(left);

        parts.add(frontLeft);
        parts.add(frontRight);
        parts.add(backLeft);
        parts.add(backRight);

        return parts;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView WorldView, BlockPos pos) {
        for (BlockPos otherPart : getOtherParts(state, pos)) {
            if (!WorldView.getBlockState(otherPart).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return super.canPlaceAt(state, WorldView, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos basePos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockState defaultState = GalacticraftBlocks.SOLAR_PANEL_PART.getDefaultState();
        for (BlockPos otherPart : getOtherParts(state, basePos)) {
            world.setBlockState(otherPart, defaultState);

            BlockEntity partEntity = world.getBlockEntity(otherPart);
            assert partEntity != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPartBlockEntity) partEntity).setBasePos(basePos);
            partEntity.markDirty();
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public void onPartDestroyed(World world, PlayerEntity player, BlockState state, BlockPos pos, BlockState partState, BlockPos partPos) {
        dropInventory(world, pos);
        world.breakBlock(pos, !player.isCreative());

        for (BlockPos otherPart : getOtherParts(state, pos)) {
            if (!world.getBlockState(otherPart).isAir()) {
                world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView blockView, TooltipContext tooltipContext) {
        return new TranslatableText("tooltip.galacticraft-rewoven.basic_solar_panel").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    }

    @Override
    public List<Direction> disabledSides() {
        return Collections.singletonList(Direction.UP);
    }
}