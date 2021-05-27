package dev.galacticraft.mod.client.gui.widget.machine;

import net.minecraft.client.util.math.MatrixStack;

public class RedstoneConfigurationWidget extends AbstractWidget {
    private static final Rectangle BOUNDS = new Rectangle(0, 0);

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return super.changeFocus(lookForwards);
    }

    @Override
    protected Rectangle getBounds() {
        return null;
    }
}
