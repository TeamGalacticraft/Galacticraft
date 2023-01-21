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
import dev.galacticraft.mod.client.gui.widget.CheckboxButton;
import dev.galacticraft.mod.screen.NasaWorkbenchMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NasaWorkbenchScreen extends AbstractContainerScreen<NasaWorkbenchMenu> { // recipe update listener
    private static final ResourceLocation TEXTURE = Constant.id("textures/gui/air_lock_controller.png");

    public NasaWorkbenchScreen(NasaWorkbenchMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CheckboxButton(this.leftPos + 8, this.topPos + 20));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        // TODO Auto-generated method stub
        // RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        // int k = this.leftPos;
        // int l = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, 256, 256);
    }
        

    // @Override
    // protected void renderBg(PoseStack poseStack, int i, int j, float f) {
    // 	this.renderBackground(poseStack);
    // 	// TODO Auto-generated method stub
        
    // }
}
