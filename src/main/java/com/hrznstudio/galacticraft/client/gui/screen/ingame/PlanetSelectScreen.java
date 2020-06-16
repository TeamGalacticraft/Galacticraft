package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class PlanetSelectScreen extends Screen {

    public PlanetSelectScreen() {
        super(new TranslatableText(""));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        matrices.push();





        matrices.pop();
    }
}
