package dev.galacticraft.mod.client.gui.widget;

import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RadioButton extends AbstractWidget {

    ResourceLocation buttonTex = Constant.id("textures/gui/radiobutton_gear_inventory_buttons.png");

    private static final int BTN_WIDTH = 11;
    private static final int BTN_HEIGHT = 10;

    private boolean isBottomButtonActive = false;

    public boolean getIsBottomButtonActive() { return isBottomButtonActive; }

    public Runnable radioButtonOnClick;

    public RadioButton(int x, int y){
        super(x,y,BTN_WIDTH, BTN_HEIGHT*2, Component.empty());
    }

    public void setIsBottomButtonActive(boolean newActive)
    {
        isBottomButtonActive = newActive;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

        int hovered = getButtonHovered(mouseX, mouseY);
        boolean isTopHovered = hovered == 0;
        boolean isBottomHovered = hovered == 1;
        context.blit(buttonTex, getX(), getY(),getArrowBlitCoordsU(isBottomButtonActive ? (isTopHovered ? 1 : 0) : 2),getArrowBlitCoordsV(isBottomButtonActive ? (isTopHovered ? 1 : 0) : 2, false),BTN_WIDTH,BTN_HEIGHT,256,256);
        context.blit(buttonTex, getX(), getY()+BTN_HEIGHT,getArrowBlitCoordsU(isBottomButtonActive ? 2 : (isBottomHovered ? 1 : 0)),getArrowBlitCoordsV(isBottomButtonActive ? 2 : (isBottomHovered ? 1 : 0), true),BTN_WIDTH,BTN_HEIGHT,256,256);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsV(int style,boolean isBottomTex)
    {
        return switch (style) {
            case 0 -> isBottomTex ? 226 : 216;
            case 1 -> isBottomTex ? 246 : 236;
            case 2 -> isBottomTex ? 226 : 216;
            default -> isBottomTex ? 226 : 216;
        };
    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsU(int style)
    {
        return switch (style) {
            case 0 -> 0;
            case 1 -> 0;
            case 2 -> 11;
            default -> 0;
        };
    }

    public int getButtonHovered(int MouseX, int MouseY)
    {
        boolean top = MouseX >= getX() && MouseX < getX() + BTN_WIDTH &&
                MouseY >= getY() && MouseY < getY() + BTN_HEIGHT;

        boolean bottom = MouseX >= getX() && MouseX < getX() + BTN_WIDTH &&
                MouseY >= getY()+BTN_HEIGHT && MouseY < getY() + BTN_HEIGHT*2;

        if(top)
        {
            return 0;
        }
        if(bottom)
        {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                int hovered = getButtonHovered((int)mouseX,(int)mouseY);
                boolean isTopHovered = hovered == 0;
                boolean isBottomHovered = hovered == 1;

                if(!isBottomButtonActive)
                {
                    if(isBottomHovered)
                    {
                        isBottomButtonActive = !isBottomButtonActive;
                        this.playDownSound(Minecraft.getInstance().getSoundManager());
                        this.onClick(mouseX, mouseY);
                        radioButtonOnClick.run();
                        return true;
                    }
                }
                else
                {
                    if(isTopHovered)
                    {
                        isBottomButtonActive = !isBottomButtonActive;
                        this.playDownSound(Minecraft.getInstance().getSoundManager());
                        this.onClick(mouseX, mouseY);
                        radioButtonOnClick.run();
                        return true;
                    }
                }


            }

            return false;
        } else {
            return false;
        }
    }
}
