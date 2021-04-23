package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class AlphaWarningTitleScreenMixin extends Screen {
    private boolean warningHidden = false;
    private static final Identifier ALPHA_WARNING_GC_TEXTURE = new Identifier(Constant.MOD_ID, "textures/gui/alpha_warning.png");
    private static final TranslatableText ALPHA_WARNING_GC_HEADER = new TranslatableText("ui.galacticraft.alpha_warning.header");
    private static final TranslatableText ALPHA_WARNING_GC_CONTENT1 = new TranslatableText("ui.galacticraft.alpha_warning.content1");
    private static final TranslatableText ALPHA_WARNING_GC_CONTENT2 = new TranslatableText("ui.galacticraft.alpha_warning.content2");
    private static final TranslatableText ALPHA_WARNING_GC_CONTENT3 = new TranslatableText("ui.galacticraft.alpha_warning.content3");

    protected AlphaWarningTitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        if (Galacticraft.CONFIG_MANAGER.get().isAlphaWarningHidden()) {
            warningHidden = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!warningHidden) {
            this.client.getTextureManager().bindTexture(ALPHA_WARNING_GC_TEXTURE);
            drawTexture(matrices, MathHelper.clamp(this.width / 2 - 128, 0, this.width), MathHelper.clamp(this.height / 2 - 64, 0, this.height), 0.0F, 0.0F, 256, 128, 256, 128);
            drawCenteredText(matrices, this.textRenderer, ALPHA_WARNING_GC_HEADER, this.width / 2, this.height / 2 - 48, 0xFF0000);
            drawCenteredText(matrices, this.textRenderer, ALPHA_WARNING_GC_CONTENT1, this.width / 2, this.height / 2 - 16, 0xFFFFFF);
            drawCenteredText(matrices, this.textRenderer, ALPHA_WARNING_GC_CONTENT2, this.width / 2, this.height / 2 + 16, 0xFFFFFF);
            drawCenteredText(matrices, this.textRenderer, ALPHA_WARNING_GC_CONTENT3, this.width / 2, this.height / 2 + 48, 0xFFFFFF);
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
