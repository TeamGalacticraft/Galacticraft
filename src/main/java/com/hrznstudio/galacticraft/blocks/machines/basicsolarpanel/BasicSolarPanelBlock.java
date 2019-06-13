package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.blocks.MachineBlock;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.MultiBlock;
import com.hrznstudio.galacticraft.util.Rotatable;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.ChatFormat;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlock extends BlockWithEntity implements AttributeProvider, Rotatable, MultiBlock, WireConnectable, MachineBlock {

    private static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public BasicSolarPanelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
        stateBuilder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BasicSolarPanelBlockEntity();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return true;
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.BASIC_SOLAR_PANEL_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        Direction dir = to.getSearchDirection();
        if (dir != null) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BasicSolarPanelBlockEntity)) return;
        BasicSolarPanelBlockEntity generator = (BasicSolarPanelBlockEntity) be;
        to.offer(generator.getEnergy());
        to.offer(generator);
//        generator.getItems().offerSelfAsAttribute(to, null, null);
    }

    @Override
    public void buildTooltip(ItemStack itemStack_1, BlockView blockView_1, List<Component> list_1, TooltipContext tooltipContext_1) {
        if (Screen.hasShiftDown()) {
            list_1.add(new TranslatableComponent("tooltip.galacticraft-rewoven.basic_solar_panel").setStyle(new Style().setColor(ChatFormat.GRAY)));
        } else {
            list_1.add(new TranslatableComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(ChatFormat.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        dropInventory(world, blockPos);

        for (BlockPos otherPart : getOtherParts(blockState, blockPos)) {
            world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
        }

        super.onBroken(world, blockPos, blockState);
    }

    private void dropInventory(World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof BasicSolarPanelBlockEntity) {
                BasicSolarPanelBlockEntity basicSolarPanelBlockEntity = (BasicSolarPanelBlockEntity) blockEntity;

                for (int i = 0; i < basicSolarPanelBlockEntity.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = basicSolarPanelBlockEntity.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
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
    public boolean canPlaceAt(BlockState blockState_1, ViewableWorld viewableWorld_1, BlockPos blockPos_1) {
        for (BlockPos otherPart : getOtherParts(blockState_1, blockPos_1)) {
            if (!viewableWorld_1.getBlockState(otherPart).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return super.canPlaceAt(blockState_1, viewableWorld_1, blockPos_1);
    }

    @Override
    public void onPlaced(World world_1, BlockPos basePos, BlockState blockState_1, LivingEntity livingEntity_1, ItemStack itemStack_1) {
        for (BlockPos otherPart : getOtherParts(blockState_1, basePos)) {
            BlockState defaultState = GalacticraftBlocks.BASIC_SOLAR_PANEL_PART.getDefaultState();
            world_1.setBlockState(otherPart, defaultState);

            BlockEntity partEntity = world_1.getBlockEntity(otherPart);
            assert partEntity != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((BasicSolarPanelPartBlockEntity) partEntity).setBasePos(basePos);
            partEntity.markDirty();
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState_1) {
        return PistonBehavior.BLOCK;
    }

    void onPartDestroyed(World world, BlockState partState, BlockPos partPos, BlockState baseState, BlockPos basePos, boolean dropBase) {
        dropInventory(world, basePos);
        world.breakBlock(basePos, dropBase);

        for (BlockPos otherPart : getOtherParts(baseState, basePos)) {
            if (!world.getBlockState(otherPart).isAir()) {
                world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!(world.getBlockEntity(connectionTargetPos) instanceof BasicSolarPanelBlockEntity)) {
            Galacticraft.logger.error("Not a Solar Panel. Rejecting connection.");
            return WireConnectionType.NONE;
        }
        if (world.getBlockState(connectionTargetPos).get(FACING).getOpposite() == dir) {
            return WireConnectionType.ENERGY_OUTPUT;
        }
        return WireConnectionType.NONE;
    }

}