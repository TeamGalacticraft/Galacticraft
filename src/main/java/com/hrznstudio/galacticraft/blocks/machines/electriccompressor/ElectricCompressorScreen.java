package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.math.BlockPos;

public class ElectricCompressorScreen extends CompressorScreen {
    public ElectricCompressorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new ElectricCompressorContainer(syncId, blockPos, playerEntity), blockPos, playerEntity, new TranslatableComponent("ui.galacticraft-rewoven.electric_compressor.name"));
//        BlockPos blockPos, PlayerEntity playerEntity, TranslatableComponent textComponents
        this.containerHeight = 199;
    }

    @Override
    protected void drawFuelProgressBar() {
        // Do nothing. Electric compressor has no fuel. Draw energy here instead
    }

    @Override
    protected void updateProgressDisplay() {
        super.updateProgressDisplay();
//        progressDisplayX = left + 105;
        progressDisplayY = top + 29;
    }

    @Override
    protected String getBackgroundLocation() {
        return Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ELECTRIC_COMPRESSOR_SCREEN);
    }

    @Override
    protected String getContainerDisplayName() {
        return new TranslatableComponent("block.galacticraft-rewoven.electric_compressor").getText();
    }
}