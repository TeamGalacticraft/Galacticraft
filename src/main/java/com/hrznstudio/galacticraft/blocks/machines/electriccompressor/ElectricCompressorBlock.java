package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlock;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class ElectricCompressorBlock extends CompressorBlock implements WireConnectable, MachineBlock {
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
    public WireConnectionType canWireConnect(IWorld world, Direction dir, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        if (!(world.getBlockEntity(connectionTargetPos) instanceof CircuitFabricatorBlockEntity)) {
            Galacticraft.logger.error("Not an electric compressor. rejecting connection.");
            return WireConnectionType.NONE;
        }
        if (world.getBlockState(connectionTargetPos).get(FACING).getOpposite() == dir) {
            return WireConnectionType.ENERGY_INPUT;
        }
        return WireConnectionType.NONE;
    }
}