package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.Rotatable;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
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

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorBlock extends ConfigurableElectricMachineBlock implements AttributeProvider, Rotatable, WireConnectable, MachineBlock {
    private static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public CircuitFabricatorBlock(Settings settings) {
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
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CircuitFabricatorBlockEntity();
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
        stateBuilder.add(FACING);
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
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }

        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.CIRCUIT_FABRICATOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        Direction dir = to.getSearchDirection();
        if (dir != null) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CircuitFabricatorBlockEntity)) return;
        CircuitFabricatorBlockEntity fabricator = (CircuitFabricatorBlockEntity) be;
        to.offer(fabricator.getEnergy());
        fabricator.getExposedInventory().offerSelfAsAttribute(to, null, null);
    }

    @Override
    public void buildTooltip(ItemStack itemStack, BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableText("tooltip.galacticraft-rewoven.circuit_fabricator").setStyle(new Style().setColor(Formatting.GRAY)));
        } else {
            list.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof CircuitFabricatorBlockEntity) {
                CircuitFabricatorBlockEntity circuitFabricatorBlockEntity = (CircuitFabricatorBlockEntity) blockEntity;

                for (int i = 0; i < circuitFabricatorBlockEntity.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = circuitFabricatorBlockEntity.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return super.canWireConnect(world, opposite, connectionSourcePos, connectionTargetPos);
    }
}