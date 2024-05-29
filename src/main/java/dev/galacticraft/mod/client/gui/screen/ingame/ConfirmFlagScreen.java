package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.NativeImage;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfirmFlagScreen extends ConfirmScreen {
    protected final NativeImage image;
    protected ResourceLocation imageLocation;
    public ConfirmFlagScreen(BooleanConsumer booleanConsumer, NativeImage image, Component component, Component component2) {
        super(booleanConsumer, component, component2);
        this.image = image;
    }

    @Override
    protected void init() {
        super.init();
        DynamicTexture texture = new DynamicTexture(this.image);
        texture.upload();
        this.imageLocation = Constant.id("test_flag");
        this.minecraft.getTextureManager().register(this.imageLocation, texture);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.blit(this.imageLocation, 0, 0, 0, 0, 48, 32);
    }
}
