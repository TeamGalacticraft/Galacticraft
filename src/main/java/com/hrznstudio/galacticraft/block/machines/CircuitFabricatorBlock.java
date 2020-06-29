/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.block.entity.CircuitFabricatorBlockEntity;
import com.hrznstudio.galacticraft.screen.BasicSolarPanelScreenHandler;
import com.hrznstudio.galacticraft.screen.CircuitFabricatorScreenHandler;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.hrznstudio.galacticraft.util.Rotatable;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlock extends ConfigurableElectricMachineBlock implements Rotatable, WireConnectable, MachineBlock {
    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.DEFAULT, SideOption.POWER_INPUT);

    public CircuitFabricatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ConfigurableElectricMachineBlockEntity createBlockEntity(BlockView blockView) {
        return new CircuitFabricatorBlockEntity();
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
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
        stateBuilder.add(FACING);
        stateBuilder.add(FRONT_SIDE_OPTION);
        stateBuilder.add(BACK_SIDE_OPTION);
        stateBuilder.add(RIGHT_SIDE_OPTION);
        stateBuilder.add(LEFT_SIDE_OPTION);
        stateBuilder.add(TOP_SIDE_OPTION);
        stateBuilder.add(BOTTOM_SIDE_OPTION);
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
        return true;
    }

    @Override
    public boolean generatesPower() {
        return false;
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
    public Property<SideOption> getProperty(@Nonnull BlockFace direction) {
        switch (direction) {
            case FRONT:
                return FRONT_SIDE_OPTION;
            case RIGHT:
                return RIGHT_SIDE_OPTION;
            case LEFT:
                return LEFT_SIDE_OPTION;
            case BACK:
                return BACK_SIDE_OPTION;
            case TOP:
                return TOP_SIDE_OPTION;
            case BOTTOM:
                return BOTTOM_SIDE_OPTION;
        }
        throw new NullPointerException();
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        playerEntity.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(blockPos);
            }

            @Override
            public Text getDisplayName() {
                return new TranslatableText("block.galacticraft-rewoven.circuit_fabricator");
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(blockPos); // idk why we have to do this again, might want to look into it
                //TODO: Look into why we have to create a new PacketByteBuf.
                return new CircuitFabricatorScreenHandler(syncId, inv, buf);
            }
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public Text machineInfo(ItemStack itemStack_1, BlockView blockView_1, TooltipContext tooltipContext_1) {
        return new TranslatableText("tooltip.galacticraft-rewoven.circuit_fabricator");
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof CircuitFabricatorBlockEntity) {
                CircuitFabricatorBlockEntity circuitFabricatorBlockEntity = (CircuitFabricatorBlockEntity) blockEntity;

                for (int i = 0; i < circuitFabricatorBlockEntity.getInventory().getSize(); i++) {
                    ItemStack itemStack = circuitFabricatorBlockEntity.getInventory().getStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public List<Direction> disabledSides() {
        return new ArrayList<>();
    }
}