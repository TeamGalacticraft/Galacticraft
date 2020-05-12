/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.block.entity.CompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class CompressorScreen extends HandledScreen<CompressorScreenHandler> {

    public static final ContainerFactory<HandledScreen> FACTORY = createFactory(CompressorBlockEntity.class, CompressorScreen::new);
    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;
    protected final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, getBackgroundLocation());
    protected int progressDisplayX;
    protected int progressDisplayY;

    public CompressorScreen(int syncId, PlayerEntity playerEntity, CompressorBlockEntity blockEntity) {
        this(new CompressorScreenHandler(syncId, playerEntity, blockEntity), playerEntity, new TranslatableText("ui.galacticraft-rewoven.compressor.name"));
        this.backgroundHeight = 192;
    }

    public CompressorScreen(CompressorScreenHandler electricCompressorContainer, PlayerEntity playerEntity, Text textComponents) {
        super(electricCompressorContainer, playerEntity.inventory, textComponents);
    }

    public static <T extends ConfigurableElectricMachineBlockEntity> ContainerFactory<HandledScreen> createFactory(
            Class<T> machineClass, MachineScreenHandler.MachineContainerConstructor<? extends HandledScreen<?>, T> constructor) {
        return (syncId, id, player, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            BlockEntity be = player.world.getBlockEntity(pos);
            if (machineClass.isInstance(be)) {
                return constructor.create(syncId, player, machineClass.cast(be));
            } else {
                return null;
            }
        };
    }

    protected String getBackgroundLocation() {
        return Constants.ScreenTextures.getRaw(Constants.ScreenTextures.COMPRESSOR_SCREEN);
    }

    protected void updateProgressDisplay() {
        progressDisplayX = this.x + 77;
        progressDisplayY = this.y + 28;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float var1, int var2, int var3) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        updateProgressDisplay();

        //this.drawTexturedRect(...)
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawFuelProgressBar(stack);
        this.drawCraftProgressBar(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, getContainerDisplayName(), (this.width / 2), this.y + 6, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    protected String getContainerDisplayName() {
        return new TranslatableText("block.galacticraft-rewoven.compressor").getString();
    }

    protected void drawFuelProgressBar(MatrixStack stack) {
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int fuelUsageScale;
        CompressorBlockEntity.CompressorStatus status = this.handler.blockEntity.status;

        if (status != CompressorBlockEntity.CompressorStatus.INACTIVE) {
            fuelUsageScale = getFuelProgress();
            this.drawTexture(stack, this.x + 80, this.y + 29 + 12 - fuelUsageScale, 203, 39 - fuelUsageScale, 14, fuelUsageScale + 1);
        }
    }

    protected void drawCraftProgressBar(MatrixStack stack) {
        float progress = this.handler.blockEntity.getProgress();
        float maxProgress = this.handler.blockEntity.getMaxProgress();
        float progressScale = (progress / maxProgress);
        // Progress confirmed to be working properly, below code is the problem.

        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, progressDisplayX, progressDisplayY, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    private int getFuelProgress() {
        int maxFuelTime = this.handler.blockEntity.maxFuelTime;
        if (maxFuelTime == 0) {
            maxFuelTime = 200;
        }

        // 0 = CompressorBlockEntity#fuelTime
        return this.handler.fuelTime.get() * 13 / maxFuelTime;
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        if (mouseX >= this.x - 22 && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + (22 + 3)) {
            this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), mouseX, mouseY);
        }
    }
}