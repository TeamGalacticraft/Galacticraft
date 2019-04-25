package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import com.hrznstudio.galacticraft.api.blocks.MachineBlock;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.Rotatable;
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
import net.minecraft.particle.DustParticleParameters;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class OxygenCollectorBlock extends Block implements Rotatable, BlockEntityProvider, MachineBlock {
    private static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;

    public OxygenCollectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }

        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.OXYGEN_COLLECTOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
        return true;
    }

    @Override
    public void buildTooltip(ItemStack itemStack, BlockView blockView, List<TextComponent> list, TooltipContext tooltipContext) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.oxygen_collector").setStyle(new Style().setColor(TextFormat.GRAY)));
        } else {
            list.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(TextFormat.GRAY)));
        }
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof OxygenCollectorBlockEntity) {
                OxygenCollectorBlockEntity be = (OxygenCollectorBlockEntity) blockEntity;

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
        return this.getDefaultState().with(FACING, context.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new OxygenCollectorBlockEntity();
    }

    @Override
    public void randomDisplayTick(BlockState blockState_1, World world, BlockPos pos, Random random_1) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof OxygenCollectorBlockEntity)) {
            return;
        }

        OxygenCollectorBlockEntity collector = (OxygenCollectorBlockEntity) blockEntity;
        if (collector.lastCollectAmount > 0) {
            for (int particleCount = 0; particleCount < 10; particleCount++) {
                Random random = world.random;

                for (int int_1 = 0; int_1 < 32; ++int_1) {
                    world.addParticle(
                            new DustParticleParameters(0.9f, 0.9f, 1.0f, 1.0F),
                            pos.getX() + 0.5D,
                            (random.nextFloat() - 0.5D) * 0.5D + /*random.nextDouble() * 2.0D*/ 0.5D,
                            pos.getZ() + 0.5D,
                            random.nextGaussian(),
                            0.0D,
                            random.nextGaussian());
                }
            }
        }
    }
}