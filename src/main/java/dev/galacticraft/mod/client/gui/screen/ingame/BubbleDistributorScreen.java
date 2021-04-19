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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.screen.MachineHandledScreen;
import dev.galacticraft.mod.client.gui.widget.machine.CapacitorWidget;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BubbleDistributorScreen extends MachineHandledScreen<BubbleDistributorScreenHandler> {
    private final TextFieldWidget textField;

    public BubbleDistributorScreen(BubbleDistributorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title);
        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + 132, this.y + 59, 26, 20, new LiteralText(String.valueOf(handler.machine.getSize())));
        textField.setChangedListener((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setText(String.valueOf(handler.machine.getTargetSize()));
                }
            } catch (NumberFormatException ignore) {
                textField.setText(String.valueOf(handler.machine.getTargetSize()));
            }
        }));

        textField.setTextPredicate((s -> {
            try {
                return Byte.parseByte(s) >= 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }));

        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(Constant.ScreenTexture.BUBBLE_DISTRIBUTOR_SCREEN);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.client.getTextureManager().bindTexture(Constant.ScreenTexture.OVERLAY);

        if (!handler.machine.bubbleVisible) {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constant.TextureCoordinate.BUTTON_RED_X, Constant.TextureCoordinate.BUTTON_RED_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constant.TextureCoordinate.BUTTON_RED_HOVER_X, Constant.TextureCoordinate.BUTTON_RED_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.bubble_distributor.not_visible"), this.x + 60 , this.y + 18, Formatting.RED.getColorValue());
        } else {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constant.TextureCoordinate.BUTTON_GREEN_X, Constant.TextureCoordinate.BUTTON_GREEN_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_X, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.bubble_distributor.visible"), this.x + 60, this.y + 18, Formatting.GREEN.getColorValue());
        }
        if (check(mouseX, mouseY, this.x + 158, this.y + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            this.drawTexture(matrices, this.x + 158, this.y + 59, Constant.TextureCoordinate.ARROW_UP_HOVER_X, Constant.TextureCoordinate.ARROW_UP_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }
        if (check(mouseX, mouseY, this.x + 158, this.y + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            this.drawTexture(matrices, this.x + 158, this.y + 69, Constant.TextureCoordinate.ARROW_DOWN_HOVER_X, Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }

        this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.bubble_distributor.size"), this.x + 70, this.y + 64, Formatting.DARK_GRAY.getColorValue());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        textField.setText(String.valueOf(handler.machine.getTargetSize()));
        drawCenteredString(matrices, this.textRenderer, new TranslatableText("block.galacticraft.oxygen_bubble_distributor").asString(), (this.width / 2) + 28, this.y + 5, Formatting.DARK_GRAY.getColorValue());

        this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.machine.status").append(handler.machine.getStatus().getName()), this.x + 60, this.y + 30, Formatting.DARK_GRAY.getColorValue());

        this.textField.render(matrices, mouseX, mouseY, delta);

        this.textField.x = this.x + 132;
        this.textField.y = this.y + 59;

        if (handler.machine.getStatus().getType().isActive()) {
            this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.bubble_distributor.current_size", String.valueOf((int) Math.floor(handler.machine.getSize()))).setStyle(Constant.Text.DARK_GRAY_STYLE), this.x + 60, this.y + 42, Formatting.DARK_GRAY.getColorValue());
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
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
            if (check(mouseX, mouseY, this.x + 156, this.y + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                handler.machine.bubbleVisible = !handler.machine.bubbleVisible;
                ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "bubble_visible"), new PacketByteBuf(Unpooled.buffer().writeBoolean(handler.machine.bubbleVisible)));
                return true;
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (handler.machine.getTargetSize() != Byte.MAX_VALUE) {
                    handler.machine.setTargetSize((byte) (handler.machine.getTargetSize() + 1));
                    textField.setText(String.valueOf(handler.machine.getTargetSize()));
                    ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.machine.getTargetSize())));
                    return true;
                }
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (handler.machine.getTargetSize() > 1) {
                    handler.machine.setTargetSize((byte) (handler.machine.getTargetSize() - 1));
                    textField.setText(String.valueOf(handler.machine.getTargetSize()));
                    ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.machine.getTargetSize())));
                    return true;
                }
            }
        }
        return false;
    }
}
