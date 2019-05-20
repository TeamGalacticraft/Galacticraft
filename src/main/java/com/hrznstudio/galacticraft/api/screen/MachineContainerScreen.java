package com.hrznstudio.galacticraft.api.screen;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.*;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineContainerScreen extends AbstractContainerScreen {

    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
    public static final Identifier ICONS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_ICONS));

    private static final int BUTTON_OFF_X = 0;
    private static final int BUTTON_OFF_Y = 240;
    private static final int BUTTON_ON_X = 0;
    private static final int BUTTON_ON_Y = 224;
    private static final int BUTTONS_WIDTH = 16;
    private static final int BUTTONS_HEIGHT = 16;

    private static final int REDSTONE_TORCH_OFF_X = 16;
    private static final int REDSTONE_TORCH_OFF_Y = 16;

    private static final int LOCK_OWNER_X = 0;
    private static final int LOCK_OWNER_Y = 0;
    private static final int LOCK_PARTY_X = 16;
    private static final int LOCK_PARTY_Y = 0;
    private static final int LOCK_PUBLIC_X = 0;
    private static final int LOCK_PULBIC_Y = 16;

    private static final int ICONS_WIDTH = 16;
    private static final int ICONS_HEIGHT = 16;

    private static final int REDSTONE_TAB_X = 0;
    private static final int REDSTONE_TAB_Y = 46;
    private static final int REDSTONE_TAB_WIDTH = 22;
    private static final int REDSTONE_TAB_HEIGHT = 22;

    private static final int REDSTONE_PANEL_X = 0; //Top-left corner of panel in texture file - X value
    private static final int REDSTONE_PANEL_Y = 0; //Top-left corner of panel in texture file - Y value
    private static final int REDSTONE_PANEL_WIDTH = 99;
    private static final int REDSTONE_PANEL_HEIGHT = 91;

    public boolean IS_REDSTONE_OPEN = false;

    private int selectedRedstoneOption = 0; //0 = disabled (redstone doesn't matter), 1 = off (if redstone is off, the machine is on), 2 = on (if redstone is on, the machine turns off)

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    private static final int CONFIG_PANEL_X = 0; //Top-left corner of panel in texture file - X value
    private static final int CONFIG_PANEL_Y = 93; //Top-left corner of panel in texture file - Y value
    private static final int CONFIG_PANEL_WIDTH = 99;
    private static final int CONFIG_PANEL_HEIGHT = 91;

    public boolean IS_CONFIG_OPEN = false;

    private static final int SECURITY_TAB_X = 23;
    private static final int SECURITY_TAB_Y = 23;
    private static final int SECURITY_TAB_WIDTH = 22;
    private static final int SECURITY_TAB_HEIGHT = 22;

    private static final int SECURITY_PANEL_X = 101;
    private static final int SECURITY_PANEL_Y = 0;
    private static final int SECURITY_PANEL_WIDTH = 99;
    private static final int SECURITY_PANEL_HEIGHT = 91;

    public boolean IS_SECURITY_OPEN = false;

    public MachineContainerScreen(Container container, PlayerInventory playerInventory, TextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    //x(location on screen), y(location on screen), x(location in texture), y(location in texture), width of the snippet in the texture file, height of the snippet in the texture file
    public void drawConfigTabs() {
        if (IS_REDSTONE_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH, this.top + 3, REDSTONE_PANEL_X, REDSTONE_PANEL_Y, REDSTONE_PANEL_WIDTH, REDSTONE_PANEL_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE), this.left - REDSTONE_PANEL_WIDTH + 6, this.top + 7);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.left - REDSTONE_PANEL_WIDTH + 23, this.top + 12, ChatFormat.GRAY.getColor());

            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

            if (selectedRedstoneOption == 0) {
                this.blit(this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else if (selectedRedstoneOption == 1) {
                this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else if (selectedRedstoneOption == 2) {
                this.blit(this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else {
                throw new IllegalStateException("The selected redstone config option is not valid!");
            }

            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.GUNPOWDER), this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26);
            this.minecraft.getTextureManager().bindTexture(ICONS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 26, REDSTONE_TORCH_OFF_X, REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE_TORCH), this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 25);
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            this.blit(this.left - REDSTONE_TAB_WIDTH, this.top + 3, REDSTONE_TAB_X, REDSTONE_TAB_Y, REDSTONE_TAB_WIDTH, REDSTONE_TAB_HEIGHT);
        }
        if (IS_CONFIG_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - CONFIG_PANEL_WIDTH, this.top + 26, CONFIG_PANEL_X, CONFIG_PANEL_Y, CONFIG_PANEL_WIDTH, CONFIG_PANEL_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.left - REDSTONE_PANEL_WIDTH + 6, this.top + 28);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.left - REDSTONE_PANEL_WIDTH + 23, this.top + 33, ChatFormat.GRAY.getColor());
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            if (!IS_REDSTONE_OPEN) {
                this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 26, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
            } else {
                this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 96, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
            }
        }
        if (IS_SECURITY_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left + 176, this.top + 3, SECURITY_PANEL_X, SECURITY_PANEL_Y, SECURITY_PANEL_WIDTH, SECURITY_PANEL_HEIGHT);
            this.minecraft.getTextureManager().bindTexture(ICONS_TEXTURE);
            this.blit(this.left + 176 + 6, this.top + 3, LOCK_PARTY_X, LOCK_PARTY_Y, ICONS_WIDTH, ICONS_HEIGHT);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.security"), this.left + 176 + 23, this.top + 12, ChatFormat.GRAY.getColor());
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            this.blit(this.left + 176, this.top + 3, SECURITY_TAB_X, SECURITY_TAB_Y, SECURITY_TAB_WIDTH, SECURITY_TAB_HEIGHT);
        }
    }

    public boolean checkTabsClick(double mouseX, double mouseY, int button) {
        if (!IS_REDSTONE_OPEN) {
            if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 3) && button == 0) {
                IS_REDSTONE_OPEN = true;
                IS_CONFIG_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else {
            if (mouseX >= this.left - REDSTONE_PANEL_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 3) && button == 0) {
                IS_REDSTONE_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (!IS_CONFIG_OPEN) {
            if (IS_REDSTONE_OPEN) {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 96 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 96) && button == 0) {
                    IS_REDSTONE_OPEN = false;
                    IS_CONFIG_OPEN = true;
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            } else {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 26 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 26) && button == 0) {
                    IS_REDSTONE_OPEN = false;
                    IS_CONFIG_OPEN = true;
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        } else {
            if (mouseX >= this.left - REDSTONE_PANEL_WIDTH && mouseX <= this.left && mouseY >= this.top + 26 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 26) && button == 0) {
                IS_CONFIG_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        if (!IS_SECURITY_OPEN) {
            if (mouseX >= this.left - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.left + 176 + 21 && mouseY >= this.top + 3 && mouseY <= this.top + (SECURITY_TAB_HEIGHT + 3) && button == 0) {
                IS_SECURITY_OPEN = true;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else {
            if (mouseX >= this.left - SECURITY_PANEL_WIDTH + 176 + 21 && mouseX <= this.left + 176 + 21 && mouseY >= this.top + 3 && mouseY <= this.top + (SECURITY_TAB_HEIGHT + 3) && button == 0) {
                IS_SECURITY_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return false;
    }

    public void drawTabTooltips(int mouseX, int mouseY) {
        if (!IS_REDSTONE_OPEN) {
            if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (22 + 3)) {
                this.renderTooltip("\u00A77" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config").getText(), mouseX, mouseY);
            }
        }
    }
}
