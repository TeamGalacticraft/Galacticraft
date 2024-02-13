/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.impl.internal.client.tabs.InventoryTabRegistryImpl;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    @Shadow protected int leftPos;

    @Shadow protected int topPos;

    @Shadow @Final protected T menu;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onTabClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        boolean tabsVisible = false;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (this.menu.getClass().equals(data.clazz())) {
                tabsVisible = true;
                break;
            }
        }
        if (!tabsVisible)
            return;
        int i = 0;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (this.menu.getClass().equals(data.clazz())) {
                i++;
                continue;
            }
            if (data.visiablePredicate().test(Minecraft.getInstance().player)) {
                if (isCoordinateBetween((int) Math.floor(mouseX), this.leftPos + (30 * i), this.leftPos + (29 * (i + 1)))
                        && isCoordinateBetween((int) Math.floor(mouseY), this.topPos - 26, this.topPos)) {
                    data.onClick().run();
                    ci.setReturnValue(true);
                }
                i++;
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void drawBackground(GuiGraphics graphics, int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        boolean tabsVisible = false;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (this.menu.getClass().equals(data.clazz())) {
                tabsVisible = true;
                break;
            }
        }
        if (!tabsVisible)
            return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation texture = Constant.id("textures/gui/player_inventory_switch_tabs.png");
        int i = 0;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (this.menu.getClass().equals(data.clazz())) {
                if (i == 0)
                    graphics.blit(texture, this.leftPos, this.topPos - 28, 0, 0, 29, 32);
                else
                    graphics.blit(texture, this.leftPos + (28 * i) + 1, this.topPos - 29, 29, 32, 56, 63);
                i++;
                continue;
            }
            if (data.visiablePredicate().test(Minecraft.getInstance().player)) {
                if (i == 0)
                    graphics.blit(texture, this.leftPos, this.topPos - 29, 0, 32, 27, 63);
                else
                    graphics.blit(texture, this.leftPos + (28 * i) + 1, this.topPos - 28, 29, 0, 56, 30);
                i++;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float v, CallbackInfo callbackInfo) {
        boolean tabsVisible = false;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (this.menu.getClass().equals(data.clazz())) {
                tabsVisible = true;
                break;
            }
        }
        if (!tabsVisible)
            return;
        Lighting.setupFor3DItems();
        int i = 0;
        for (InventoryTabRegistryImpl.TabData data : InventoryTabRegistryImpl.INSTANCE.TABS) {
            if (i == 0) {
                graphics.renderItem(data.icon(), this.leftPos + 6, this.topPos - 20);
                i++;
                continue;
            }
            if (data.visiablePredicate().test(Minecraft.getInstance().player)) {
                graphics.renderItem(data.icon(), (this.leftPos + 6) + (28 * i) + 1, this.topPos - 20);
                i++;
            }
        }
        Lighting.setupForFlatItems();
    }

    @Unique
    private static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }
}
