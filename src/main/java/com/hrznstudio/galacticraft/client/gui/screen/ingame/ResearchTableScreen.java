package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.screen.ResearchTableScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableText;

public class ResearchTableScreen extends HandledScreen<ResearchTableScreenHandler> {
    public ResearchTableScreen(ResearchTableScreenHandler handler, PlayerInventory inventory) {
        super(handler, inventory, new TranslatableText("ui.galacti"));
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

}
