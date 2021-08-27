/*
 * Copyright (c) 2019-2021 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.GalacticraftPlayerInventoryScreen;
import dev.galacticraft.mod.item.GalacticraftItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(InventoryScreen.class)
@Environment(EnvType.CLIENT)
public abstract class PlayerInventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public PlayerInventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text textComponent) {
        super(screenHandler, playerInventory, textComponent);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseX), x + 30, x + 59)
                && GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseY), y - 26, y)) {
            ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "open_gc_inv"), new PacketByteBuf(Unpooled.buffer(0)));
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void drawBackground(MatrixStack matrices, float v, int i, int i1, CallbackInfo callbackInfo) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.PLAYER_INVENTORY_TABS);
        this.drawTexture(matrices, this.x, this.y - 28, 0, 0, 57, 32);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        DiffuseLighting.enableGuiDepthLighting();
        this.itemRenderer.renderInGuiWithOverrides(Items.CRAFTING_TABLE.getDefaultStack(), this.x + 6, this.y - 20);
        this.itemRenderer.renderInGuiWithOverrides(GalacticraftItem.OXYGEN_MASK.getDefaultStack(), this.x + 35, this.y - 20);
        DiffuseLighting.disableGuiDepthLighting();
    }
}
