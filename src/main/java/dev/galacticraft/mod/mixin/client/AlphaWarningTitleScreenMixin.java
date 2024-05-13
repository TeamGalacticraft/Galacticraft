/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
@Environment(EnvType.CLIENT)
public abstract class AlphaWarningTitleScreenMixin extends Screen {

    @Unique
    private static boolean warningHidden = false;
    @Unique
    private static final ResourceLocation ALPHA_WARNING_GC_TEXTURE = Constant.id("textures/gui/alpha_warning.png");
    @Unique
    private static final Component ALPHA_WARNING_GC_HEADER = Component.translatable(Translations.Ui.ALPHA_WARNING_HEADER);
    @Unique
    private static final Component ALPHA_WARNING_GC_CONTENT1 = Component.translatable(Translations.Ui.ALPHA_WARNING_1);
    @Unique
    private static final Component ALPHA_WARNING_GC_CONTENT2 = Component.translatable(Translations.Ui.ALPHA_WARNING_2);
    @Unique
    private static final Component ALPHA_WARNING_GC_CONTENT3 = Component.translatable(Translations.Ui.ALPHA_WARNING_3);

    protected AlphaWarningTitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        if (Galacticraft.CONFIG.isAlphaWarningHidden()) {
            warningHidden = true;
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!warningHidden) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.blit(ALPHA_WARNING_GC_TEXTURE, Mth.clamp(this.width / 2 - 128, 0, this.width), Mth.clamp(this.height / 2 - 64, 0, this.height), 0.0F, 0.0F, 256, 128, 256, 128);
            graphics.drawCenteredString(this.font, ALPHA_WARNING_GC_HEADER, this.width / 2, this.height / 2 - 48, 0xFF0000);
            graphics.drawCenteredString(this.font, ALPHA_WARNING_GC_CONTENT1, this.width / 2, this.height / 2 - 16, 0xFFFFFF);
            graphics.drawCenteredString(this.font, ALPHA_WARNING_GC_CONTENT2, this.width / 2, this.height / 2 + 16, 0xFFFFFF);
            graphics.drawCenteredString(this.font, ALPHA_WARNING_GC_CONTENT3, this.width / 2, this.height / 2 + 48, 0xFFFFFF);
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!warningHidden) {
            warningHidden = true;
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !warningHidden) {
            warningHidden = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
