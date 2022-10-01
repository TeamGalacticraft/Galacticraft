/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.GCPlayerInventoryScreen;
import dev.galacticraft.mod.item.GCItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
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
public abstract class PlayerInventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    public PlayerInventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component textComponent) {
        super(screenHandler, playerInventory, textComponent);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (GCPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseX), leftPos + 30, leftPos + 59)
                && GCPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseY), topPos - 26, topPos)) {
            ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "open_gc_inv"), new FriendlyByteBuf(Unpooled.buffer(0)));
        }
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    public void drawBackground(PoseStack matrices, float v, int i, int i1, CallbackInfo callbackInfo) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.PLAYER_INVENTORY_TABS);
        this.blit(matrices, this.leftPos, this.topPos - 28, 0, 0, 57, 32);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(PoseStack matrices, int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        Lighting.setupFor3DItems();
        this.itemRenderer.renderAndDecorateItem(Items.CRAFTING_TABLE.getDefaultInstance(), this.leftPos + 6, this.topPos - 20);
        this.itemRenderer.renderAndDecorateItem(GCItem.OXYGEN_MASK.getDefaultInstance(), this.leftPos + 35, this.topPos - 20);
        Lighting.setupForFlatItems();
    }
}
