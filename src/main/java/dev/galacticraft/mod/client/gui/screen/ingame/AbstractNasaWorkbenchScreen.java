/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AbstractNasaWorkbenchScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
    private final Inventory inventory;
    private final ResourceLocation texture;
    private final int page;

    public AbstractNasaWorkbenchScreen(M menu, Inventory inventory, Component component, ResourceLocation texture, int page) {
        super(menu, inventory, component);
        this.inventory = inventory;
        this.texture = texture;
        this.page = page;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int var3, int var4) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, this.texture);
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public void init() { // TODO: we can pull this up into an abstract class because of the similar logic
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        // TODO: Translations
        this.addRenderableWidget(new Button(this.leftPos - 41, this.topPos + 40, 40, 20, Component.literal("Next"), (button) -> {
            // TODO: extract resource location to a constant
            // TODO: add translations
            ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "change_workbench_menu"), new FriendlyByteBuf(PacketByteBufs.create().writeInt(this.page).writeBoolean(true)));
        }));
        this.addRenderableWidget(new Button(this.leftPos - 41, this.topPos + 62, 40, 20, Component.literal("Back"), (button) -> {
            ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "change_workbench_menu"), new FriendlyByteBuf(PacketByteBufs.create().writeInt(this.page).writeBoolean(false)));
        }));
    }
}
