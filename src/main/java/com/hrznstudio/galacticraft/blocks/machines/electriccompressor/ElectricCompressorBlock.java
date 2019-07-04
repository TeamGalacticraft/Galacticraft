package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlock;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlockEntity;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorBlock extends ConfigurableElectricMachineBlock implements WireConnectable, MachineBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);
    public static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);
    public static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);
    public static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);
    public static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);
    public static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.BLANK, SideOption.POWER_INPUT);


    public ElectricCompressorBlock(Settings settings) {
        super(settings);
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

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext_1) {
        return super.getPlacementState(itemPlacementContext_1).with(FACING, itemPlacementContext_1.getPlayerFacing().getOpposite()).with(FRONT_SIDE_OPTION, SideOption.BLANK)
                .with(BACK_SIDE_OPTION, SideOption.BLANK)
                .with(RIGHT_SIDE_OPTION, SideOption.BLANK)
                .with(LEFT_SIDE_OPTION, SideOption.BLANK)
                .with(TOP_SIDE_OPTION, SideOption.BLANK)
                .with(BOTTOM_SIDE_OPTION, SideOption.BLANK);
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);

        stateBuilder.add(FRONT_SIDE_OPTION);
        stateBuilder.add(BACK_SIDE_OPTION);
        stateBuilder.add(RIGHT_SIDE_OPTION);
        stateBuilder.add(LEFT_SIDE_OPTION);
        stateBuilder.add(TOP_SIDE_OPTION);
        stateBuilder.add(BOTTOM_SIDE_OPTION);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new ElectricCompressorBlockEntity();
    }

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return WireConnectionType.NONE;
    }

    @Override
    public final void buildTooltip(ItemStack itemStack_1, BlockView blockView_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        if (Screen.hasShiftDown()) {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.electric_compressor").setStyle(new Style().setColor(Formatting.GRAY)));
        } else {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
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
    public final boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }

        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.ELECTRIC_COMPRESSOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof ElectricCompressorBlockEntity) {
                ElectricCompressorBlockEntity be = (ElectricCompressorBlockEntity) blockEntity;

                for (int i = 0; i < be.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = be.getInventory().getInvStack(i);

                    if (!itemStack.isEmpty()) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack.copy()));
                    }
                }
            }
        }
    }
}