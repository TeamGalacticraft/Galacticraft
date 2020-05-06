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

package com.hrznstudio.galacticraft.blocks.machines.bubbledistributor;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorScreen extends MachineContainerScreen<BubbleDistributorContainer> {
    public static final ContainerFactory<ContainerScreen> FACTORY = createFactory(BubbleDistributorBlockEntity.class, BubbleDistributorScreen::new);

    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.BUBBLE_DISTRIBUTOR_SCREEN));
    private static final int OVERLAY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int OVERLAY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;
    private static final int OXYGEN_X = Constants.TextureCoordinates.OXYGEN_LIGHT_X;
    private static final int OXYGEN_Y = Constants.TextureCoordinates.OXYGEN_LIGHT_Y;
    private static final int OXYGEN_DIMMED_X = Constants.TextureCoordinates.OXYGEN_DARK_X;
    private static final int OXYGEN_DIMMED_Y = Constants.TextureCoordinates.OXYGEN_DARK_Y;
    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 13;
    private static final int ARROW_WIDTH = 11;
    private static final int ARROW_HEIGHT = 10;
    private final TextFieldWidget textField;

    public BubbleDistributorScreen(int syncId, PlayerEntity playerEntity, BubbleDistributorBlockEntity blockEntity) {
        super(new BubbleDistributorContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, playerEntity.world, blockEntity.getPos(), new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.name"));
        this.textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.x + 132, this.y + 53, 26, 20, String.valueOf(container.blockEntity.getSize()));
        textField.setChangedListener((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setText("" + container.blockEntity.getMaxSize());
                }
            } catch (NumberFormatException ignore) {
                textField.setText("" + container.blockEntity.getMaxSize());
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
    protected void drawBackground(float v, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);

        this.minecraft.getTextureManager().bindTexture(OVERLAY);

        if (!container.blockEntity.bubbleVisible) {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.blit(this.x + 156, this.y + 16, 0, 182, 13, 13);
            } else {
                this.blit(this.x + 156, this.y + 16, 0, 169, 13, 13);
            }
            drawRightAlignedString(minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.not_visible").asFormattedString(), this.x + 152, this.y + 18, Formatting.RED.getColorValue());
        } else {
            if (!check(mouseX, mouseY, this.x + 156, this.y + 16, 13, 13)) {
                this.blit(this.x + 156, this.y + 16, 13, 182, 13, 13);
            } else {
                this.blit(this.x + 156, this.y + 16, 13, 169, 13, 13);
            }
            drawRightAlignedString(minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.visible").asFormattedString(), this.x + 152, this.y + 18, Formatting.GREEN.getColorValue());
        }

        drawRightAlignedString(minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.size").asFormattedString(), this.x + 129, this.y + 58, Formatting.GRAY.getColorValue());

        this.drawEnergyBufferBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        textField.setText("" + container.blockEntity.getMaxSize());
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_bubble_distributor").asFormattedString(), (this.width / 2) + 28, this.y + 5, Formatting.DARK_GRAY.getColorValue());

        minecraft.textRenderer.draw(new TranslatableText("ui.galacticraft-rewoven.machine.status").asFormattedString(), this.x + 58, this.y + 32, Formatting.DARK_GRAY.getColorValue());

        this.textField.render(mouseX, mouseY, delta);

        String status = container.blockEntity.status == BubbleDistributorStatus.DISTRIBUTING ? "ui.galacticraft-rewoven.machinestatus.distributing"
                : container.blockEntity.status == BubbleDistributorStatus.NOT_ENOUGH_POWER ? "ui.galacticraft-rewoven.machinestatus.not_enough_power"
                : container.blockEntity.status == BubbleDistributorStatus.NOT_ENOUGH_OXYGEN ? "ui.galacticraft-rewoven.machinestatus.not_enough_oxygen" : "ui.galacticraft-rewoven.machinestatus.off";

        this.textField.x = this.x + 132;
        this.textField.y = this.y + 53;
        this.minecraft.textRenderer.draw(new TranslatableText(status).asFormattedString(), this.x + 58 + minecraft.textRenderer.getStringWidth(new TranslatableText("ui.galacticraft-rewoven.machine.status").asFormattedString() + " "), this.y + 32, container.blockEntity.status.getTextColor());

        if (container.blockEntity.status == BubbleDistributorStatus.DISTRIBUTING) {
            this.minecraft.textRenderer.draw(new TranslatableText("ui.galacticraft-rewoven.bubble_distributor.current_size").setStyle(new Style().setColor(Formatting.GRAY)).append(String.valueOf((int) Math.floor(container.blockEntity.getSize()))).asFormattedString(), this.x + 58, this.y + 44, container.blockEntity.status.getTextColor());
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawEnergyBufferBar() {
        this.minecraft.getTextureManager().bindTexture(OVERLAY);
        this.blit(this.x + 10, this.y + 9, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.blit(this.x + 21, this.y + 48, ENERGY_X, ENERGY_Y, OVERLAY_WIDTH, (int) -((float) OVERLAY_HEIGHT * ((float) container.energy.get() / (float) container.getMaxEnergy())));

        this.blit(this.x + 33, this.y + 9, OXYGEN_DIMMED_X, OXYGEN_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.blit(this.x + 44, this.y + 48, OXYGEN_X, OXYGEN_Y, OVERLAY_WIDTH, (int) -((float) OVERLAY_HEIGHT * ((float) container.oxygen.get() / (float) BubbleDistributorBlockEntity.MAX_OXYGEN)));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);

        if (check(mouseX, mouseY, this.x + 10, this.y + 9, OVERLAY_WIDTH, OVERLAY_HEIGHT)) {
            List<String> toolTipLines = new ArrayList<>();
            toolTipLines.add("\u00A76" + new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(container.energy.get()).setStyle(new Style().setColor(Formatting.BLUE))).asFormattedString() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableText("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(container.getMaxEnergy())).asFormattedString() + "\u00A7r");
            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }

        if (check(mouseX, mouseY, this.x + 33, this.y + 9, OVERLAY_WIDTH, OVERLAY_HEIGHT)) {
            List<String> toolTipLines = new ArrayList<>();
            toolTipLines.add("\u00A76" + new TranslatableText("ui.galacticraft-rewoven.machine.current_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(container.oxygen.get()).setStyle(new Style().setColor(Formatting.BLUE))).asFormattedString() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableText("ui.galacticraft-rewoven.machine.max_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(OxygenCollectorBlockEntity.MAX_OXYGEN)).asFormattedString() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
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
                container.blockEntity.bubbleVisible = !container.blockEntity.bubbleVisible;
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_visible"), new PacketByteBuf(Unpooled.buffer().writeBoolean(container.blockEntity.bubbleVisible)).writeBlockPos(this.container.blockEntity.getPos())));
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 53, ARROW_WIDTH, ARROW_HEIGHT)) {
                if (container.blockEntity.getMaxSize() != Byte.MAX_VALUE) {
                    container.blockEntity.setMaxSize((byte) (container.blockEntity.getMaxSize() + 1));
                    textField.setText(container.blockEntity.getMaxSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(container.blockEntity.getMaxSize())).writeBlockPos(this.container.blockEntity.getPos())));
                }
            }

            if (check(mouseX, mouseY, this.x + 158, this.y + 63, ARROW_WIDTH, ARROW_HEIGHT)) {
                if (container.blockEntity.getMaxSize() > 1) {
                    container.blockEntity.setMaxSize((byte) (container.blockEntity.getMaxSize() - 1));
                    textField.setText(container.blockEntity.getMaxSize() + "");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "bubble_max"), new PacketByteBuf(Unpooled.buffer().writeByte(container.blockEntity.getMaxSize())).writeBlockPos(this.container.blockEntity.getPos())));
                }
            }
        }
        return false;
    }
}
