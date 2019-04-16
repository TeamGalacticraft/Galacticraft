package io.github.teamgalacticraft.galacticraft.blocks.machines.electriccompressor;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.compressor.CompressorScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class ElectricCompressorScreen extends CompressorScreen {
    public ElectricCompressorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new ElectricCompressorContainer(syncId, blockPos, playerEntity), blockPos, playerEntity, new TranslatableTextComponent("ui.galacticraft-rewoven.electric_compressor.name"));
//        BlockPos blockPos, PlayerEntity playerEntity, TranslatableTextComponent textComponents
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
        return new TranslatableTextComponent("block.galacticraft-rewoven.electric_compressor_block").getText();
    }
}