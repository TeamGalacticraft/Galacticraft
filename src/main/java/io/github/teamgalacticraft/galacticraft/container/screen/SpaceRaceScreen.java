package io.github.teamgalacticraft.galacticraft.container.screen;

import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.TranslatableTextComponent;

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

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        this.widthSize = 0;
        this.heightSize = 0;
        super.resize(minecraftClient_1, int_1, int_2);
    }

    @Override
    public void renderBackground() {
        // 5% of width
        int sideMargins = (int) (this.width * 0.16);
        int maxWidth = this.width - sideMargins * 2;
        if (widthSize < maxWidth) {
            widthSize += 2;
        }

        int topMargins = (int) (this.width * 0.09);
        int maxHeight = this.height - topMargins * 2;
        if (heightSize < maxHeight) {
            heightSize += 2;
        }

        int midX = this.width / 2;
        int midY = this.height / 2;

        int x = midX - widthSize / 2;
        int y = midY - heightSize / 2;

        fill(x, y, x + widthSize, y + heightSize, 0x80000000);
    }

    private void renderForeground() {
        TextRenderer font = minecraft.textRenderer;
        DrawableUtils.drawCenteredString(font, "Space Race Manager", this.width/2,50, 0xFFFFFF);
    }

    @Override
    public void render(int x, int y, float lastFrameDuration) {
        this.renderBackground();
        this.renderForeground();
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