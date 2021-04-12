/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.client.gui.widget.machine.OxygenTankWidget;
import com.hrznstudio.galacticraft.screen.BubbleDistributorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorScreen extends MachineHandledScreen<BubbleDistributorScreenHandler> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.BUBBLE_DISTRIBUTOR_SCREEN));

    private final EditBox textField;

    public BubbleDistributorScreen(BubbleDistributorScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, inv.player.level, handler.machine.getBlockPos(), title);
        this.textField = new EditBox(Minecraft.getInstance().font, this.leftPos + 132, this.topPos + 59, 26, 20, new TextComponent(String.valueOf(handler.machine.getSize())));
        textField.setResponder((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setValue(String.valueOf(handler.machine.getTargetSize()));
                }
            } catch (NumberFormatException ignore) {
                textField.setValue(String.valueOf(handler.machine.getTargetSize()));
            }
        }));

        textField.setFilter((s -> {
            try {
                return Byte.parseByte(s) >= 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }));

        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
        this.addWidget(new OxygenTankWidget(handler.machine.getFluidTank().getTank(0), 31, 8, 48));
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.minecraft.getTextureManager().bind(BACKGROUND);

        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.minecraft.getTextureManager().bind(OVERLAY);

        if (!menu.machine.bubbleVisible) {
            if (!check(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constants.TextureCoordinates.BUTTON_RED_X, Constants.TextureCoordinates.BUTTON_RED_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            } else {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constants.TextureCoordinates.BUTTON_RED_HOVER_X, Constants.TextureCoordinates.BUTTON_RED_HOVER_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            }
            this.font.draw(matrices, new TranslatableComponent("ui.galacticraft-rewoven.bubble_distributor.not_visible"), this.leftPos + 60 , this.topPos + 18, ChatFormatting.RED.getColor());
        } else {
            if (!check(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constants.TextureCoordinates.BUTTON_GREEN_X, Constants.TextureCoordinates.BUTTON_GREEN_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            } else {
                this.blit(matrices, this.leftPos + 156, this.topPos + 16, Constants.TextureCoordinates.BUTTON_GREEN_HOVER_X, Constants.TextureCoordinates.BUTTON_GREEN_HOVER_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            }
            this.font.draw(matrices, new TranslatableComponent("ui.galacticraft-rewoven.bubble_distributor.visible"), this.leftPos + 60, this.topPos + 18, ChatFormatting.GREEN.getColor());
        }
        if (check(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
            this.blit(matrices, this.leftPos + 158, this.topPos + 59, Constants.TextureCoordinates.ARROW_UP_HOVER_X, Constants.TextureCoordinates.ARROW_UP_HOVER_Y, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT);
        }
        if (check(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
            this.blit(matrices, this.leftPos + 158, this.topPos + 69, Constants.TextureCoordinates.ARROW_DOWN_HOVER_X, Constants.TextureCoordinates.ARROW_DOWN_HOVER_Y, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT);
        }

        this.font.draw(matrices, new TranslatableComponent("ui.galacticraft-rewoven.bubble_distributor.size"), this.leftPos + 70, this.topPos + 64, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        textField.setValue(String.valueOf(menu.machine.getTargetSize()));
        DrawableUtils.drawCenteredString(matrices, this.font, new TranslatableComponent("block.galacticraft-rewoven.oxygen_bubble_distributor").getContents(), (this.width / 2) + 28, this.topPos + 5, ChatFormatting.DARK_GRAY.getColor());

        this.font.draw(matrices, new TranslatableComponent("ui.galacticraft-rewoven.machine.status").append(menu.machine.getStatus().getName()), this.leftPos + 60, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor());

        this.textField.render(matrices, mouseX, mouseY, delta);


        this.textField.x = this.leftPos + 132;
        this.textField.y = this.topPos + 59;

        if (menu.machine.getStatus().getType().isActive()) {
            this.font.draw(matrices, new TranslatableComponent("ui.galacticraft-rewoven.bubble_distributor.current_size", String.valueOf((int) Math.floor(menu.machine.getSize()))).setStyle(Constants.Styles.TOOLTIP_STYLE), this.leftPos + 60, this.topPos + 42, ChatFormatting.DARK_GRAY.getColor());
        }
        this.renderTooltip(matrices, mouseX, mouseY);
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
            if (check(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT)) {
                menu.machine.bubbleVisible = !menu.machine.bubbleVisible;
                ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "bubble_visible"), new FriendlyByteBuf(Unpooled.buffer().writeBoolean(menu.machine.bubbleVisible)).writeBlockPos(this.menu.machine.getBlockPos()));
                return true;
            }

            if (check(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
                if (menu.machine.getTargetSize() != Byte.MAX_VALUE) {
                    menu.machine.setTargetSize((byte) (menu.machine.getTargetSize() + 1));
                    textField.setValue(menu.machine.getTargetSize() + "");
                    ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "bubble_max"), new FriendlyByteBuf(Unpooled.buffer().writeByte(menu.machine.getTargetSize())).writeBlockPos(this.menu.machine.getBlockPos()));
                    return true;
                }
            }

            if (check(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
                if (menu.machine.getTargetSize() > 1) {
                    menu.machine.setTargetSize((byte) (menu.machine.getTargetSize() - 1));
                    textField.setValue(menu.machine.getTargetSize() + "");
                    ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "bubble_max"), new FriendlyByteBuf(Unpooled.buffer().writeByte(menu.machine.getTargetSize())).writeBlockPos(this.menu.machine.getBlockPos()));
                    return true;
                }
            }
        }
        return false;
    }
}
