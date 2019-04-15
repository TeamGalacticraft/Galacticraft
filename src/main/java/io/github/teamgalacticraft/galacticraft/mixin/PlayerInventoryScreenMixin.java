package io.github.teamgalacticraft.galacticraft.mixin;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.container.screen.PlayerInventoryGCScreen;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
@Mixin(PlayerInventoryScreen.class)
public abstract class PlayerInventoryScreenMixin extends AbstractPlayerInventoryScreen<PlayerContainer> {
    public PlayerInventoryScreenMixin(PlayerContainer container, PlayerInventory playerInventory, TextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
//        System.out.println("X: " + mouseX);
//        System.out.println("Y: " + mouseY);
//        System.out.println("b: " + button);

        if (PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseX), left + 30, left + 59)
                && PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseY), top - 26, top)) {
            System.out.println("Clicked on GC tab!");
            minecraft.openScreen(new PlayerInventoryGCScreen(playerInventory.player));
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void drawBackground(float v, int i, int i1, CallbackInfo callbackInfo) {
        this.minecraft.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));
        this.blit(this.left, this.top - 28, 0, 0, 57, 32);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        GuiLighting.enableForItems();
        this.itemRenderer.renderGuiItem(Items.CRAFTING_TABLE.getDefaultStack(), this.left + 6, this.top - 20);
        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_MASK.getDefaultStack(), this.left + 35, this.top - 20);
    }
}
