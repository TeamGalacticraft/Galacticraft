/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.screen.BubbleDistributorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorScreen extends MachineHandledScreen<BubbleDistributorScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.BUBBLE_DISTRIBUTOR_SCREEN));

    private final TextFieldWidget textField;

    public BubbleDistributorScreen(BubbleDistributorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + 132, this.y + 59, 26, 20, new LiteralText(String.valueOf(handler.blockEntity.getSize())));
        textField.setChangedListener((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setText(String.valueOf(handler.blockEntity.getTargetSize()));
                }
            } catch (NumberFormatException ignore) {
                textField.setText(String.valueOf(handler.blockEntity.getTargetSize()));
            }
        }));

        textField.setTextPredicate((s -> {
            try {
                return Byte.parseByte(s) >= 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }));

        this.addWidget(new CapacitorWidget(handler.blockEntity.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.blockEntity::getStatus));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float v, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.client.getTextureManager().bindTexture(OVERLAY);

        if (!handler.blockEntity.bubbleVisible) {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constants.TextureCoordinates.BUTTON_RED_X, Constants.TextureCoordinates.BUTTON_RED_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constants.TextureCoordinates.BUTTON_RED_HOVER_X, Constants.TextureCoordinates.BUTTON_RED_HOVER_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            }
            client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.not_visible"), this.x + 60 , this.y + 18, Formatting.RED.getColorValue());
        } else {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constants.TextureCoordinates.BUTTON_GREEN_X, Constants.TextureCoordinates.BUTTON_GREEN_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constants.TextureCoordinates.BUTTON_GREEN_HOVER_X, Constants.TextureCoordinates.BUTTON_GREEN_HOVER_Y, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT);
            }
            client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.visible"), this.x + 60, this.y + 18, Formatting.GREEN.getColorValue());
        }
        if (check(mouseX, mouseY, this.x + 158, this.y + 59, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
            this.drawTexture(matrices, this.x + 158, this.y + 59, Constants.TextureCoordinates.ARROW_UP_HOVER_X, Constants.TextureCoordinates.ARROW_UP_HOVER_Y, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT);
        }
        if (check(mouseX, mouseY, this.x + 158, this.y + 69, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
            this.drawTexture(matrices, this.x + 158, this.y + 69, Constants.TextureCoordinates.ARROW_DOWN_HOVER_X, Constants.TextureCoordinates.ARROW_DOWN_HOVER_Y, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT);
        }

        client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.size"), this.x + 70, this.y + 64, Formatting.DARK_GRAY.getColorValue());

        this.drawOxygenBufferBar(matrices, this.x + 33, this.y + 9, 0);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        textField.setText(String.valueOf(handler.blockEntity.getTargetSize()));
        DrawableUtils.drawCenteredString(matrices, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_bubble_distributor").asString(), (this.width / 2) + 28, this.y + 5, Formatting.DARK_GRAY.getColorValue());

        client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.status").append(handler.blockEntity.getStatus().getName()), this.x + 60, this.y + 30, Formatting.DARK_GRAY.getColorValue());

        this.textField.render(matrices, mouseX, mouseY, delta);


        this.textField.x = this.x + 132;
        this.textField.y = this.y + 59;

        if (handler.blockEntity.getStatus().getType().isActive()) {
            this.client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.current_size", String.valueOf((int) Math.floor(handler.blockEntity.getSize()))).setStyle(Constants.Misc.TOOLTIP_STYLE), this.x + 60, this.y + 42, Formatting.DARK_GRAY.getColorValue());
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.drawOxygenTooltip(matrices, mouseX, mouseY, this.x + 33, this.y + 9, 0);
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
            if (check(mouseX, mouseY, this.x + 156, this.y + 16, Constants.TextureCoordinates.BUTTON_WIDTH, Constants.TextureCoordinates.BUTTON_HEIGHT)) {
                handler.blockEntity.bubbleVisible = !handler.blockEntity.bubbleVisible;
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_visible"), new PacketByteBuf(Unpooled.buffer().writeBoolean(handler.blockEntity.bubbleVisible)).writeBlockPos(this.handler.blockEntity.getPos())));
                return true;
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 59, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
                if (handler.blockEntity.getTargetSize() != Byte.MAX_VALUE) {
                    handler.blockEntity.setTargetSize((byte) (handler.blockEntity.getTargetSize() + 1));
                    textField.setText(handler.blockEntity.getTargetSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.blockEntity.getTargetSize())).writeBlockPos(this.handler.blockEntity.getPos())));
                    return true;
                }
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 69, Constants.TextureCoordinates.ARROW_VERTICAL_WIDTH, Constants.TextureCoordinates.ARROW_VERTICAL_HEIGHT)) {
                if (handler.blockEntity.getTargetSize() > 1) {
                    handler.blockEntity.setTargetSize((byte) (handler.blockEntity.getTargetSize() - 1));
                    textField.setText(handler.blockEntity.getTargetSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.blockEntity.getTargetSize())).writeBlockPos(this.handler.blockEntity.getPos())));
                    return true;
                }
            }
        }
        return false;
    }
}
