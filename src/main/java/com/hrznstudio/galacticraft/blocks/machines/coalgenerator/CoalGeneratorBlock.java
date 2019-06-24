package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.blocks.MachineBlock;
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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
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
public class CoalGeneratorBlock extends BlockWithEntity implements AttributeProvider, Rotatable, WireConnectable, MachineBlock {

    private static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    private static final EnumProperty SIDES = EnumProperty.of("sides", CoalGeneratorSides.class);

    public CoalGeneratorBlock(Settings settings) {
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
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(SIDES, CoalGeneratorSides.DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CoalGeneratorBlockEntity();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) return true;
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.COAL_GENERATOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        Direction dir = to.getSearchDirection();
        if (dir != null) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CoalGeneratorBlockEntity)) return;
        CoalGeneratorBlockEntity generator = (CoalGeneratorBlockEntity) be;
        to.offer(generator.getEnergy());
        generator.getExposedInventory().offerSelfAsAttribute(to, null, null);
    }

    @Override
    public void buildTooltip(ItemStack itemStack_1, BlockView blockView_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        if (Screen.hasShiftDown()) {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.coal_generator").setStyle(new Style().setColor(Formatting.GRAY)));
        } else {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof CoalGeneratorBlockEntity) {
                CoalGeneratorBlockEntity coalGeneratorBlockEntity = (CoalGeneratorBlockEntity) blockEntity;

                for (int i = 0; i < coalGeneratorBlockEntity.getInventory().getSlotCount(); i++) {
                    ItemStack itemStack = coalGeneratorBlockEntity.getInventory().getInvStack(i);

                    if (itemStack != null) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack));
                    }
                }
            }
        }
    }

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!(world.getBlockEntity(connectionTargetPos) instanceof CoalGeneratorBlockEntity)) {
            Galacticraft.logger.error("Not a Coal Generator. Rejecting connection.");
            return WireConnectionType.NONE;
        }
        if (world.getBlockState(connectionTargetPos).get(FACING).getOpposite() == dir) {
            return WireConnectionType.ENERGY_OUTPUT;
        }
        return WireConnectionType.NONE;
    }

    public enum CoalGeneratorSides implements StringIdentifiable {
        DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT_DEFAULT("default"),
        POWERIN("powerin"),
        POWEROUT("powerout"),
        ;

        String name;
        CoalGeneratorSides(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
