package com.hrznstudio.galacticraft.container.screen;

import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.TranslatableTextComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceScreen extends Screen {
    private int widthSize = 0;
    private int heightSize = 0;

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public SpaceRaceScreen() {
        super(new TranslatableTextComponent("menu.space_race_manager"));
    }

    private boolean isAnimationComplete() {
        int sideMargins = (int) (this.width * 0.16);
        int maxWidth = this.width - sideMargins * 2;

        int topMargins = (int) (this.width * 0.09);
        int maxHeight = this.height - topMargins * 2;

        return widthSize >= maxWidth
                && heightSize >= maxHeight;
    }

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        this.widthSize = 0;
        this.heightSize = 0;
        super.resize(minecraftClient_1, int_1, int_2);
    }

    @Override
    public void renderBackground() {
        // 5% of width
        int maxWidth = this.width - getXMargins() * 2;
        if (widthSize < maxWidth) {
            widthSize += Math.min(3, maxWidth - widthSize);
        }

        int maxHeight = this.height - getYMargins() * 2;
        if (heightSize < maxHeight) {
            heightSize += Math.min(2, maxHeight - heightSize);
        }

        int midX = this.width / 2;
        int midY = this.height / 2;

        int x = midX - widthSize / 2;
        int y = midY - heightSize / 2;

        fill(x, y, x + widthSize, y + heightSize, 0x80000000);
    }

    private void renderForeground() {
        TextRenderer font = minecraft.textRenderer;
        DrawableUtils.drawCenteredString(font, "Space Race Manager", this.width / 2, getTop() + 2, 0xFFFFFF);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    private int getTop() {
        return getYMargins();
    }

    private int getBottom() {
        return this.height - getYMargins();
    }

    private int getLeft() {
        return getXMargins();
    }

    private int getRight() {
        return this.widthSize - getXMargins();
    }

    @Override
    public void render(int x, int y, float lastFrameDuration) {
        this.renderBackground();

        if (this.isAnimationComplete()) {
            this.renderForeground();
        }

        super.render(x, y, lastFrameDuration);
//        this.drawMouseoverTooltip(x, y);

//        this.mouseX = (float) x;
//        this.mouseY = (float)/*y*/ minecraft.window.getScaledHeight() / 2;
//
//        GuiLighting.enableForItems();
//        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getDefaultStack(), this.left + 6, this.top - 20);
//        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_FAN.getDefaultStack(), this.left + 35, this.top - 20);
    }
}