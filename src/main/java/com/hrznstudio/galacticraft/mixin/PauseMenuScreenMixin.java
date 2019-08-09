package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.container.button.SpaceRaceButtonWidget;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(GameMenuScreen.class)
public abstract class PauseMenuScreenMixin extends Screen {
    protected PauseMenuScreenMixin(Text textComponent_1) {
        super(textComponent_1);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(int int_1, int int_2, float float_1, CallbackInfo info) {
//        this.renderSpaceRaceButton(int_1, int_2, float_1);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        int screenWidth = this.width;
        int screenHeight = this.height;
        int buttonWidth = 100;
        int buttonHeight = 35;
        int x = screenWidth - buttonWidth;
        int y = screenHeight - buttonHeight;

        addButton(new SpaceRaceButtonWidget(minecraft, x, y, buttonWidth, buttonHeight, screenWidth, screenHeight));
    }

//    private void renderSpaceRaceButton(int int_1, int int_2, float float_1) {
//        int screenWidth = this.width;
//        int screenHeight = this.height;
//        int buttonWidth = 100;
//        int buttonHeight = 35;
//        int x = screenWidth - buttonWidth;
//        int y = screenHeight - buttonHeight;
//
//        int spaceBetweenLines = 1;
//        int lineHeight = font.fontHeight;
//        int textYOffset = 9;
//    }
}