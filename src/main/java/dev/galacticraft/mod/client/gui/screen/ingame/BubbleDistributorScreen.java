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
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.screen.BubbleDistributorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BubbleDistributorScreen extends MachineScreen<OxygenBubbleDistributorBlockEntity, BubbleDistributorMenu> {
    private final EditBox textField;

    public BubbleDistributorScreen(BubbleDistributorMenu handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.BUBBLE_DISTRIBUTOR_SCREEN);
        this.textField = new EditBox(Minecraft.getInstance().font, this.leftPos + 132, this.topPos + 59, 26, 20, Component.literal(String.valueOf(this.machine.getSize())));
        this.textField.setResponder((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setValue(String.valueOf(this.machine.getTargetSize()));
                }
            } catch (NumberFormatException ignore) {
                textField.setValue(String.valueOf(this.machine.getTargetSize()));
            }
        }));

        this.textField.setFilter((s -> {
            try {
                return Byte.parseByte(s) >= 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }));
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;
    }

    @Override
    protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);

        if (! this.machine.bubbleVisible) {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_X, Constant.TextureCoordinate.BUTTON_RED_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_HOVER_X, Constant.TextureCoordinate.BUTTON_RED_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            this.font.draw(matrices, Component.translatable("ui.galacticraft.bubble_distributor.not_visible"), this.leftPos + 60 , this.topPos + 18, ChatFormatting.RED.getColor());
        } else {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_X, Constant.TextureCoordinate.BUTTON_GREEN_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_X, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            this.font.draw(matrices, Component.translatable("ui.galacticraft.bubble_distributor.visible"), this.leftPos + 60, this.topPos + 18, ChatFormatting.GREEN.getColor());
        }
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            this.blit(matrices, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_UP_HOVER_X, Constant.TextureCoordinate.ARROW_UP_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            this.blit(matrices, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_DOWN_HOVER_X, Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }

        this.font.draw(matrices, Component.translatable("ui.galacticraft.bubble_distributor.size"), this.leftPos + 70, this.topPos + 64, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    protected void renderForeground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderForeground(matrices, mouseX, mouseY, delta);
        textField.setValue(String.valueOf(this.machine.getTargetSize()));

        this.font.draw(matrices, Component.translatable("ui.galacticraft.machine.status").append(this.machine.getStatus().name()), this.leftPos + 60, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor());

        this.textField.render(matrices, mouseX, mouseY, delta);

        this.textField.x = this.leftPos + 132;
        this.textField.y = this.topPos + 59;

        if (this.machine.getStatus().type().isActive()) {
            this.font.draw(matrices, Component.translatable("ui.galacticraft.bubble_distributor.current_size", String.valueOf((int) Math.floor(this.machine.getSize()))).setStyle(Constant.Text.Color.DARK_GRAY_STYLE), this.leftPos + 60, this.topPos + 42, ChatFormatting.DARK_GRAY.getColor());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | checkClick(mouseX, mouseY, button) | textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) | textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers) | textField.keyReleased(keyCode, scanCode, modifiers);
    }

    private boolean checkClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                this.machine.bubbleVisible = ! this.machine.bubbleVisible;
                ClientPlayNetworking.send(Constant.Packet.BUBBLE_VISIBLE, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(this.machine.bubbleVisible)));
                return true;
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.machine.getTargetSize() != Byte.MAX_VALUE) {
                    this.machine.setTargetSize((byte) (this.machine.getTargetSize() + 1));
                    textField.setValue(String.valueOf(this.machine.getTargetSize()));
                    ClientPlayNetworking.send(Constant.Packet.BUBBLE_MAX, new FriendlyByteBuf(Unpooled.buffer().writeByte(this.machine.getTargetSize())));
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.machine.getTargetSize() > 1) {
                    this.machine.setTargetSize((byte) (this.machine.getTargetSize() - 1));
                    textField.setValue(String.valueOf(this.machine.getTargetSize()));
                    ClientPlayNetworking.send(Constant.Packet.BUBBLE_MAX, new FriendlyByteBuf(Unpooled.buffer().writeByte(this.machine.getTargetSize())));
                    return true;
                }
            }
        }
        return false;
    }
}
