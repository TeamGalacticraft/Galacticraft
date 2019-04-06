package io.github.teamgalacticraft.galacticraft.mixin;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

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
        System.out.println("X: " + mouseX);
        System.out.println("Y: " + mouseY);
        System.out.println("b: " + button);

        if(mouseX <= this.left && mouseX >= this.left + 28 && mouseY <= this.top && mouseY >= this.top - 28 && button == 0) {
            // vanill button
        }
        if(mouseX <= this.left && mouseX >= this.left + 28 && mouseY <= this.top && mouseY >= this.top - 28 && button == 0) {
            // vanill button
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void drawBackground(float v, int i, int i1, CallbackInfo callbackInfo) {
        this.minecraft.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN)));
        this.blit(this.left, this.top - 28, 0, 0, 57, 32);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        GuiLighting.enableForItems();
        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getDefaultStack(), this.left + 6, this.top - 20);
        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_FAN.getDefaultStack(), this.left + 35, this.top - 20);
    }
}
