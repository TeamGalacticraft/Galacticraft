/*
 *
 *  * Copyright (c) 2019-2023 Team Galacticraft
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIfDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.screen.RocketMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RocketInventoryScreen extends AbstractContainerScreen<RocketMenu> {
    public RocketInventoryScreen(RocketMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 0;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, tickDelta);
        renderTooltip(poseStack, mouseX, mouseY);

        this.font.draw(poseStack, Component.translatable("ui.galacticraft.rocket.fuel"), this.leftPos + 125, this.topPos + 15 + 3, 4210752);

        final double percentage = this.menu.rocket.getFuelTankAmount() * 100D / this.menu.rocket.getFuelTankCapacity();
        final ChatFormatting color = percentage > 80.0D ? ChatFormatting.GREEN : percentage > 40.0D ? ChatFormatting.GOLD : ChatFormatting.RED;
        final String str = percentage + Language.getInstance().getOrDefault("ui.galacticraft.rocket.full");
        this.font.draw(poseStack, Component.literal(str).withStyle(color), this.leftPos + 117 - str.length() / 2, this.topPos + 20 + 8, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrices, float v, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_INVENTORY);
        blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
