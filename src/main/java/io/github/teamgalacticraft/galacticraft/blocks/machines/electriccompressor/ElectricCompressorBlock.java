package io.github.teamgalacticraft.galacticraft.blocks.machines.electriccompressor;

import io.github.teamgalacticraft.galacticraft.Galacticraft;
import io.github.teamgalacticraft.galacticraft.util.WireConnectable;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlockEntity;
import io.github.teamgalacticraft.galacticraft.blocks.machines.compressor.CompressorBlock;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class ElectricCompressorBlock extends CompressorBlock implements WireConnectable {
    public ElectricCompressorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected String getTooltipKey() {
        return "tooltip.galacticraft-rewoven.electric_compressor";
    }

    @Override
    protected void openContainer(PlayerEntity playerEntity, BlockPos blockPos) {
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftContainers.ELECTRIC_COMPRESSOR_CONTAINER, playerEntity, packetByteBuf -> packetByteBuf.writeBlockPos(blockPos));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new ElectricCompressorBlockEntity();
    }

    @Override
    public boolean canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!(world.getBlockEntity(connectionTargetPos) instanceof CircuitFabricatorBlockEntity)) {
            Galacticraft.logger.error("Not an electric compressor. rejecting connection.");
            return false;
        }
        return world.getBlockState(connectionTargetPos).get(FACING).getOpposite() == dir;
    }
}