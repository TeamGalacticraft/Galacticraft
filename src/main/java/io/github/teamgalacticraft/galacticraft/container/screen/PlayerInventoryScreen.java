package io.github.teamgalacticraft.galacticraft.container.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.container.PlayerInventoryContainer;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class PlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerInventoryContainer> {

    public static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));

    public PlayerInventoryScreen(PlayerInventoryContainer container, PlayerInventory playerInventory, TextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    @Override
    public void drawBackground(float v, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        //this.drawTexturedReact(...)
        this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);
    }
}
