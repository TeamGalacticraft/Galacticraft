package com.hrznstudio.galacticraft.container.button;

import com.hrznstudio.galacticraft.container.screen.SpaceRaceScreen;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceButtonWidget extends ButtonWidget {
    private final TextRenderer font;
    private int screenWidth;
    private int screenHeight;

    public SpaceRaceButtonWidget(MinecraftClient minecraft, int x, int y, int buttonWidth, int buttonHeight, int screenWidth, int screenHeight) {
        super(x, y, buttonWidth, buttonHeight, "", (button) -> minecraft.openScreen(new SpaceRaceScreen()));
        this.font = minecraft.textRenderer;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void renderButton(int int_1, int int_2, float float_1) {
        int screenWidth = this.screenWidth;
        int screenHeight = this.screenHeight;
        int buttonWidth = 100;
        int buttonHeight = 35;
        int x = screenWidth - buttonWidth;
        int y = screenHeight - buttonHeight;

        int spaceBetweenLines = 1;
        int lineHeight = font.fontHeight;
        int textYOffset = 9;

        fillGradient(x, y, x + buttonWidth, y + buttonHeight, 0xF0151515, 0xF00C0C0C);
        hLine(x, screenWidth, y, 0xFF000000);
        vLine(x, screenHeight, y, 0xFF000000);

        DrawableUtils.drawCenteredString(font, "Space Race", x + buttonWidth / 2, y + textYOffset, 0xFFFFFFFF);
        DrawableUtils.drawCenteredString(font, "Manager", x + buttonWidth / 2, y + textYOffset + lineHeight + spaceBetweenLines, 0xFFFFFFFF);
    }
}