package io.github.teamgalacticraft.galacticraft.blocks.machines.refinery;

import io.github.teamgalacticraft.galacticraft.Galacticraft;
import io.github.teamgalacticraft.galacticraft.blocks.machines.MachineBlockEntity;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlockEntity;
import io.github.teamgalacticraft.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlockEntity;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import io.github.teamgalacticraft.galacticraft.util.Rotatable;
import io.github.teamgalacticraft.galacticraft.util.WireConnectable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;

public class RefineryBlock extends Block implements Rotatable, BlockEntityProvider, WireConnectable {

    private static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;

    public RefineryBlock(Settings settings) {
        super(settings);
    }


    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.REFINERY_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void buildTooltip(ItemStack itemStack, BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.refinery").setStyle(new Style().setColor(TextFormat.GRAY)));
        } else {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(TextFormat.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof RefineryBlockEntity) {
                RefineryBlockEntity be = (RefineryBlockEntity) blockEntity;

                for (int i = 0; i < be.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = be.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.with(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.stateFactory.getDefaultState().with(FACING, context.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new RefineryBlockEntity();
    }

    @Override
    public boolean canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!(world.getBlockEntity(connectionTargetPos) instanceof MachineBlockEntity)) {
            Galacticraft.logger.error("Not a fab. Rejecting connection.");
            return false;
        }
        return world.getBlockState(connectionTargetPos).get(FACING).getOpposite() == dir;
    }
}
