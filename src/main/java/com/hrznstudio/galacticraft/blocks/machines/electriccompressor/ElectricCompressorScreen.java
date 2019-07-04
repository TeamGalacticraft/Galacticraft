package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorContainer;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorScreen;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorStatus;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorScreen extends MachineContainerScreen<ElectricCompressorContainer> {

    public static final ContainerFactory<AbstractContainerScreen> ELECTRIC_FACTORY = createFactory(ElectricCompressorBlockEntity.class, ElectricCompressorScreen::new);

    public ElectricCompressorScreen(int syncId, PlayerEntity playerEntity, ElectricCompressorBlockEntity blockEntity) {
        this(new ElectricCompressorContainer(syncId, playerEntity, blockEntity), playerEntity, blockEntity, new TranslatableText("ui.galacticraft-rewoven.electric_compressor.name"));
        this.containerHeight = 199;
    }

    private ElectricCompressorScreen(ElectricCompressorContainer electricCompressorContainer, PlayerEntity playerEntity, ElectricCompressorBlockEntity blockEntity, Text textComponents) {
        super(electricCompressorContainer, playerEntity.inventory, playerEntity.world, blockEntity.getPos(), textComponents);
        this.world = playerEntity.world;
    }

    protected void updateProgressDisplay() {
        progressDisplayX = left + 77;
        progressDisplayY = top + 28;
//        progressDisplayX = left + 105;
        progressDisplayY = top + 29;
    }

    protected String getBackgroundLocation() {
        return Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ELECTRIC_COMPRESSOR_SCREEN);
    }

    private String getContainerDisplayName() {
        return new TranslatableText("block.galacticraft-rewoven.electric_compressor").asFormattedString();
    }

    //////////////////////////////

    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;
    private final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, getBackgroundLocation());
    private int progressDisplayX;

    private int progressDisplayY;
    protected BlockPos blockPos;

    protected World world;

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        updateProgressDisplay();

        //this.drawTexturedRect(...)
        this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);

        this.drawCraftProgressBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, getContainerDisplayName(), (this.width / 2), this.top + 6, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    protected void drawCraftProgressBar() {
        float progress = container.blockEntity.getProgress();
        float maxProgress = container.blockEntity.getMaxProgress();
        float progressScale = (progress / maxProgress);
        // Progress confirmed to be working properly, below code is the problem.

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.blit(progressDisplayX, progressDisplayY, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
    }
}