/*
 * Copyright (c) 2019 HRZN LTD
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
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.screen.BubbleDistributorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorScreen extends MachineHandledScreen<BubbleDistributorScreenHandler> {
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.BUBBLE_DISTRIBUTOR_SCREEN));
    private static final int OVERLAY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int OVERLAY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int OXYGEN_X = Constants.TextureCoordinates.OXYGEN_LIGHT_X;
    private static final int OXYGEN_Y = Constants.TextureCoordinates.OXYGEN_LIGHT_Y;
    private static final int OXYGEN_DIMMED_X = Constants.TextureCoordinates.OXYGEN_DARK_X;
    private static final int OXYGEN_DIMMED_Y = Constants.TextureCoordinates.OXYGEN_DARK_Y;
    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 13;
    private static final int ARROW_WIDTH = 11;
    private static final int ARROW_HEIGHT = 10;
    private final TextFieldWidget textField;

    public BubbleDistributorScreen(BubbleDistributorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + 132, this.y + 53, 26, 20, new LiteralText(String.valueOf(handler.blockEntity.getSize())));
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
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float v, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        DiffuseLighting.disable();
        this.client.getTextureManager().bindTexture(BACKGROUND);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.client.getTextureManager().bindTexture(OVERLAY);

        if (!handler.blockEntity.bubbleVisible) {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, 0, 182, 13, 13);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, 0, 169, 13, 13);
            }
            client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.not_visible"), this.x + 60 , this.y + 18, Formatting.RED.getColorValue());
        } else {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.drawTexture(matrices, this.x + 156, this.y + 16, 13, 182, 13, 13);
            } else {
                this.drawTexture(matrices, this.x + 156, this.y + 16, 13, 169, 13, 13);
            }
            client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.visible"), this.x + 60, this.y + 18, Formatting.GREEN.getColorValue());
        }

        client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.size"), this.x + 70, this.y + 58, Formatting.DARK_GRAY.getColorValue());

        this.drawEnergyBufferBar(matrices, this.x + 10, this.y + 9);
        this.drawOxygenBufferBar(matrices);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        textField.setText("" + handler.blockEntity.getTargetSize());
        DrawableUtils.drawCenteredString(matrices, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_bubble_distributor").asString(), (this.width / 2) + 28, this.y + 5, Formatting.DARK_GRAY.getColorValue());

        String status = handler.blockEntity.status == BubbleDistributorBlockEntity.BubbleDistributorStatus.DISTRIBUTING ? "ui.galacticraft-rewoven.machinestatus.distributing"
                : handler.blockEntity.status == BubbleDistributorBlockEntity.BubbleDistributorStatus.NOT_ENOUGH_POWER ? "ui.galacticraft-rewoven.machinestatus.not_enough_power"
                : handler.blockEntity.status == BubbleDistributorBlockEntity.BubbleDistributorStatus.NOT_ENOUGH_OXYGEN ? "ui.galacticraft-rewoven.machinestatus.not_enough_oxygen" : "ui.galacticraft-rewoven.machinestatus.off";

        client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.status").append(new TranslatableText(status).setStyle(Style.EMPTY.withColor(handler.blockEntity.status.getColor()))), this.x + 60, this.y + 30, Formatting.DARK_GRAY.getColorValue());

        this.textField.render(matrices, mouseX, mouseY, delta);


        this.textField.x = this.x + 132;
        this.textField.y = this.y + 53;

        if (handler.blockEntity.status == BubbleDistributorBlockEntity.BubbleDistributorStatus.DISTRIBUTING) {
            this.client.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.current_size", String.valueOf((int) Math.floor(handler.blockEntity.getSize()))).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), this.x + 60, this.y + 42, Formatting.DARK_GRAY.getColorValue());
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void drawOxygenBufferBar(MatrixStack matrices) {
        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(matrices, this.x + 33, this.y + 9, OXYGEN_DIMMED_X, OXYGEN_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.drawTexture(matrices, this.x + 44, this.y + 48, OXYGEN_X, OXYGEN_Y, OVERLAY_WIDTH, (int) -((double) OVERLAY_HEIGHT * (handler.oxygen.get() / (handler.blockEntity.getOxygenTank().getMaxCapacity(0).doubleValue() * 100.0D))));
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.drawEnergyTooltip(matrices, mouseX, mouseY, this.x + 10, this.y + 9);

        if (check(mouseX, mouseY, this.x + 33, this.y + 9, OVERLAY_WIDTH, OVERLAY_HEIGHT)) {
            List<OrderedText> toolTipLines = new ArrayList<>();
            toolTipLines.addAll(client.textRenderer.wrapLines(new TranslatableText("ui.galacticraft-rewoven.machine.current_oxygen", new LiteralText(String.valueOf(handler.oxygen.get())).setStyle(Style.EMPTY.withColor(Formatting.BLUE))).setStyle(Style.EMPTY.withColor(Formatting.GOLD)), 10000));
            toolTipLines.addAll(client.textRenderer.wrapLines(new TranslatableText("ui.galacticraft-rewoven.machine.max_oxygen", new LiteralText(String.valueOf((int)(handler.blockEntity.getOxygenTank().getMaxCapacity(0).doubleValue() * 100.0D))).setStyle(Style.EMPTY.withColor(Formatting.BLUE))).setStyle(Style.EMPTY.withColor(Formatting.RED)), 10000));
            this.renderOrderedTooltip(matrices, toolTipLines, mouseX, mouseY);
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
            if (check(mouseX, mouseY, this.x + 156, this.y + 16, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                handler.blockEntity.bubbleVisible = !handler.blockEntity.bubbleVisible;
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_visible"), new PacketByteBuf(Unpooled.buffer().writeBoolean(handler.blockEntity.bubbleVisible)).writeBlockPos(this.handler.blockEntity.getPos())));
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 53, ARROW_WIDTH, ARROW_HEIGHT)) {
                if (handler.blockEntity.getTargetSize() != Byte.MAX_VALUE) {
                    handler.blockEntity.setTargetSize((byte) (handler.blockEntity.getTargetSize() + 1));
                    textField.setText(handler.blockEntity.getTargetSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.blockEntity.getTargetSize())).writeBlockPos(this.handler.blockEntity.getPos())));
                }
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 63, ARROW_WIDTH, ARROW_HEIGHT)) {
                if (handler.blockEntity.getTargetSize() > 1) {
                    handler.blockEntity.setTargetSize((byte) (handler.blockEntity.getTargetSize() - 1));
                    textField.setText(handler.blockEntity.getTargetSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(handler.blockEntity.getTargetSize())).writeBlockPos(this.handler.blockEntity.getPos())));
                }
            }
        }
        return false;
    }
}
