package io.github.teamgalacticraft.galacticraft.api.screen;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public abstract class MachineContainerScreen extends ContainerScreen {

    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID,Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID,Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    private static final int CONFIG_PANEL_X = 0;
    private static final int CONFIG_PANEL_Y = 0;
    private static final int CONFIG_PANEL_WIDTH = 100;
    private static final int CONFIG_PANEL_HEIGHT = 92;

    public static boolean IS_CONFIG_OPEN = false;

    public MachineContainerScreen(Container container, PlayerInventory playerInventory, TextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    public void drawConfigTabs() {
        if(IS_CONFIG_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - CONFIG_PANEL_WIDTH, this.top + 3, CONFIG_PANEL_X, CONFIG_PANEL_Y, CONFIG_PANEL_WIDTH, CONFIG_PANEL_HEIGHT);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.left - CONFIG_PANEL_WIDTH + 22, this.top + 10, TextFormat.GRAY.getColor());
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 3, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
        }
    }

    public boolean checkTabsClick(double mouseX, double mouseY, int button) {
        if(!IS_CONFIG_OPEN) {
            if (mouseX >= this.left - CONFIG_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (CONFIG_TAB_HEIGHT + 3) && button == 0) {
                IS_CONFIG_OPEN = true;
                return true;
            }
        } else {
            if (mouseX >= this.left - CONFIG_PANEL_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (CONFIG_TAB_HEIGHT + 3) && button == 0) {
                IS_CONFIG_OPEN = false;
                return true;
            }

            
        }
        return false;
    }

    public void drawTabTooltips(int mouseX, int mouseY) {
        if(!IS_CONFIG_OPEN) {
            if (mouseX >= this.left - CONFIG_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (22 + 3)) {
                this.renderTooltip("\u00A77" + new TranslatableTextComponent("ui.galacticraft-rewoven.tabs.side_config").getText(), mouseX, mouseY);
            }
        }
    }
}
