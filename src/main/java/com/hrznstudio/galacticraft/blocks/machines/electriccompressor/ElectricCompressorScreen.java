package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorScreen;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;

public class ElectricCompressorScreen extends CompressorScreen {

    public static final ContainerFactory<AbstractContainerScreen> ELECTRIC_FACTORY = createFactory(ElectricCompressorBlockEntity.class, ElectricCompressorScreen::new);

    public ElectricCompressorScreen(int syncId, PlayerEntity playerEntity, ElectricCompressorBlockEntity blockEntity) {
        super(new ElectricCompressorContainer(syncId, playerEntity, blockEntity), playerEntity, new TranslatableComponent("ui.galacticraft-rewoven.electric_compressor.name"));
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