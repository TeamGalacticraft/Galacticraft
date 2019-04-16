package io.github.teamgalacticraft.galacticraft.blocks.machines.compressor;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorStatus;
import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CompressorScreen extends ContainerScreen {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.COMPRESSOR_SCREEN));
    private static final Identifier CONFIG_TABS = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    private int progressDisplayX;
    private int progressDisplayY;

    BlockPos blockPos;
    private World world;

    public CompressorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new CompressorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, new TranslatableTextComponent("ui.galacticraft-rewoven.compressor.name"));
        this.blockPos = blockPos;
        this.world = playerEntity.world;
        this.containerHeight = 192;
    }

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        progressDisplayX = left + 77;
        progressDisplayY = top + 28;

        //this.drawTexturedRect(...)
        this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);

        this.drawFuelProgressBar();
        this.drawCraftProgressBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableTextComponent("block.galacticraft-rewoven.compressor_block").getText(), (this.width / 2), this.top + 6, TextFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawConfigTabs() {
        this.minecraft.getTextureManager().bindTexture(CONFIG_TABS);
        this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 3, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
    }

    private void drawFuelProgressBar() {
        //this.drawTexturedReact(...)
        this.blit(left, top, 0, 0, this.containerWidth, this.containerHeight);
        int fuelUsageScale;
        CompressorStatus status = ((CompressorBlockEntity) world.getBlockEntity(blockPos)).status;

        if (status != CompressorStatus.INACTIVE) {
            fuelUsageScale = getFuelProgress();
            this.blit(left + 80, top + 29 + 12 - fuelUsageScale, 203, 39 - fuelUsageScale, 14, fuelUsageScale + 1);
        }
    }

    private void drawCraftProgressBar() {
        float progress = (float) ((CompressorBlockEntity) world.getBlockEntity(blockPos)).getProgress();
        float maxProgress = (float) ((CompressorBlockEntity) world.getBlockEntity(blockPos)).getMaxProgress();
        float progressScale = (progress / maxProgress);
        // Progress confirmed to be working properly, below code is the problem.

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.blit(progressDisplayX, progressDisplayY, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    private int getFuelProgress() {
        CompressorBlockEntity compressor = ((CompressorBlockEntity) world.getBlockEntity(blockPos));

        int maxFuelTime = compressor.maxFuelTime;
        if (maxFuelTime == 0) {
            maxFuelTime = 200;
        }

        // 0 = CompressorBlockEntity#fuelTime
        return compressor.fuelTime * 13 / maxFuelTime;
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        if (mouseX >= this.left - 22 && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (22 + 3)) {
            this.renderTooltip("\u00A77" + new TranslatableTextComponent("ui.galacticraft-rewoven.tabs.side_config").getText(), mouseX, mouseY);
        }
    }
}