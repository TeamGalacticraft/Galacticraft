package io.github.teamgalacticraft.galacticraft.blocks.machines.electriccompressor;

import io.github.teamgalacticraft.galacticraft.blocks.machines.compressor.CompressorBlockEntity;
import io.github.teamgalacticraft.galacticraft.blocks.machines.compressor.CompressorContainer;
import io.github.teamgalacticraft.galacticraft.container.slot.ChargeSlot;
import net.minecraft.container.FurnaceOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ElectricCompressorContainer extends CompressorContainer {
    public ElectricCompressorContainer(int syncId, BlockPos blockPos, PlayerEntity player) {
        super(syncId, blockPos, player);

        addSlot(new FurnaceOutputSlot(player, this.inventory, ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT, getOutputSlotPos()[0], getOutputSlotPos()[1] + 18));
        addSlot(new ChargeSlot(this.inventory, CompressorBlockEntity.FUEL_INPUT_SLOT, 3 * 18 + 1, 75));
    }

    @Override
    protected int[] getOutputSlotPos() {
        int[] outputSlotPos = super.getOutputSlotPos();
        // Move output slot up by half a slot
        outputSlotPos[1] = outputSlotPos[1] - (18 / 2);
        return outputSlotPos;
    }

    @Override
    protected int getPlayerInvYOffset() {
        return super.getPlayerInvYOffset() + 7;
    }
}