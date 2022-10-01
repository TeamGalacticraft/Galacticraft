package dev.galacticraft.mod.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CheckboxWidget extends AbstractButton {

    private boolean checked = false;

    public CheckboxWidget(int x, int y) {
        super(x, y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT, Component.empty());
    }

    @Override
    public void renderButton(PoseStack poseStack, int i, int j, float f) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        blit(poseStack, this.x, this.y, checked ? Constant.TextureCoordinate.BUTTON_GREEN_X : Constant.TextureCoordinate.BUTTON_RED_X, isHoveredOrFocused() ? 115 : 102, getWidth(), getHeight());
        poseStack.popPose();
    }

    @Override
    public void onPress() {
        checked = !checked;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
