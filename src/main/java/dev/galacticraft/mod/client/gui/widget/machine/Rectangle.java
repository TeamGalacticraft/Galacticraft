package dev.galacticraft.mod.client.gui.widget.machine;

public class Rectangle {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isWithinBounds(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + width;
    }

    public boolean isWithinBounds(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, 0, 0);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
