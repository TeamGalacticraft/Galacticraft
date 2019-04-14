package io.github.teamgalacticraft.galacticraft.blocks.machines.energystoragemodule;

import io.github.teamgalacticraft.galacticraft.Galacticraft;
import io.github.teamgalacticraft.galacticraft.blocks.machines.WireConnectable;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import io.github.teamgalacticraft.galacticraft.util.Rotatable;
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

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class EnergyStorageModuleBlock extends Block implements Rotatable, BlockEntityProvider, WireConnectable {
    private static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;

    public EnergyStorageModuleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }

        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.ENERGY_STORAGE_MODULE_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void buildTooltip(ItemStack itemStack, BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.energy_storage_module").setStyle(new Style().setColor(TextFormat.GRAY)));
        } else {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(TextFormat.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof EnergyStorageModuleBlockEntity) {
                EnergyStorageModuleBlockEntity be = (EnergyStorageModuleBlockEntity) blockEntity;

                for (int i = 0; i < be.inventory.getSlotCount(); i++) {
                    ItemStack itemStack = be.inventory.getInvStack(i);

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
        return this.getDefaultState().with(FACING, context.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new EnergyStorageModuleBlockEntity();
    }

    @Override
    public boolean canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!( world.getBlockEntity(connectionTargetPos) instanceof EnergyStorageModuleBlockEntity)) {
            Galacticraft.logger.error("Not a Energy Storage Module. Rejecting connection.");
            return false;
        }
        Direction d = world.getBlockState(connectionTargetPos).get(FACING);
        System.out.println(opposite);
        if (d == Direction.NORTH) {
            return opposite == Direction.WEST || opposite == Direction.EAST;
        } else if (d == Direction.SOUTH) {
            return opposite == Direction.WEST || opposite == Direction.EAST;
        } else if (d == Direction.EAST) {
            return opposite == Direction.NORTH || opposite == Direction.SOUTH;
        } else if (d == Direction.WEST) {
            return opposite == Direction.NORTH || opposite == Direction.SOUTH;
        } else {
            return false;
        }
    }
}