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

package com.hrznstudio.galacticraft.api.screen;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.client.gui.widget.machine.AbstractWidget;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public abstract class MachineHandledScreen<C extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> extends HandledScreen<C> {
    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
    public static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

    public static final int PANEL_WIDTH = 99;
    public static final int PANEL_HEIGHT = 91;
    public static final int TAB_WIDTH = 22;
    public static final int TAB_HEIGHT = 22;

    private static final int BUTTON_X = 0;
    private static final int BUTTON_OFF_Y = 240;
    private static final int BUTTON_ON_Y = 224;

    private static final int ICONS_WIDTH = 16;
    private static final int ICONS_HEIGHT = 16;

    private static final int ICON_LOCK_OWNER_X = 208;
    private static final int ICON_LOCK_OWNER_Y = 48;

    private static final int ICON_LOCK_PARTY_X = 224;
    private static final int ICON_LOCK_PARTY_Y = 48;

    private static final int ICON_LOCK_PUBLIC_X = 208;
    private static final int ICON_LOCK_PUBLIC_Y = 64;

    private static final int ICON_REDSTONE_TORCH_OFF_X = 224;
    private static final int ICON_REDSTONE_TORCH_OFF_Y = 62;

    private static final int TAB_REDSTONE_X = 0;
    private static final int TAB_REDSTONE_Y = 46;

    private static final int TAB_CONFIG_X = 0;
    private static final int TAB_CONFIG_Y = 69;

    private static final int TAB_SECURITY_X = 23;
    private static final int TAB_SECURITY_Y = 23;

    private static final int PANEL_REDSTONE_X = 0;
    private static final int PANEL_REDSTONE_Y = 0;

    private static final int PANEL_CONFIG_X = 0;
    private static final int PANEL_CONFIG_Y = 93;

    private static final int PANEL_SECURITY_X = 101;
    private static final int PANEL_SECURITY_Y = 0;

    private static final int CONFIG_BUTTON_SPACING = 19;
    private static final int BASE_CONFIG_BUTTON_X = (-PANEL_WIDTH) + 35;
    private static final int BASE_CONFIG_BUTTON_Y = 70;

    protected final BlockPos pos;
    protected final World world;
    private final List<AbstractWidget> widgets = new LinkedList<>();

    public boolean securityOpen = false;
    public boolean redstoneOpen = false;
    public boolean configOpen = false;

    private final Map<BlockFace, SideOption> config = new EnumMap<>(BlockFace.class);

    public MachineHandledScreen(C screenHandler, PlayerInventory playerInventory, World world, BlockPos pos, Text textComponent) {
        super(screenHandler, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        ConfigurableMachineBlockEntity.SecurityInfo security = this.handler.blockEntity.getSecurity();
        if (!security.hasOwner()) {
            security.setOwner(this.playerInventory.player);
            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
            sendSecurityUpdate(this.handler.blockEntity);
        } else if (security.getOwner().equals(playerInventory.player.getUuid())
                && !security.getUsername().equals(playerInventory.player.getEntityName())) {
            security.setUsername(playerInventory.player.getEntityName());
            sendSecurityUpdate(this.handler.blockEntity);
        }

        for (BlockFace face : BlockFace.values()) {
            config.put(face, screenHandler.blockEntity.getSideConfigInfo().get(face).getOption());
        }
    }

    private void sendSecurityUpdate(ConfigurableMachineBlockEntity entity) {
        if (this.playerInventory.player.getUuid().equals(entity.getSecurity().getOwner()) || !entity.getSecurity().hasOwner()) {
            ClientPlayNetworking.send(new Identifier(Constants.MOD_ID, "security"),
                    new PacketByteBuf(Unpooled.buffer())
                            .writeBlockPos(pos)
                            .writeEnumConstant(entity.getSecurity().getPublicity())
            );
        } else {
            Galacticraft.logger.error("Tried to send security update when not the owner!");
        }
    }

    @NotNull
    protected Collection<? extends Text> getEnergyTooltipLines() {
        return Collections.emptyList();
    }

    public void drawConfigTabs(MatrixStack stack, int mouseX, int mouseY) {
        DiffuseLighting.disable();
        if (this.handler.blockEntity != null) {
            final ConfigurableMachineBlockEntity entity = this.handler.blockEntity;

            if (redstoneOpen) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - PANEL_WIDTH, this.y + 3, PANEL_REDSTONE_X, PANEL_REDSTONE_Y, PANEL_WIDTH, PANEL_HEIGHT);

                this.drawTexture(stack, this.x - PANEL_WIDTH + 21, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x - PANEL_WIDTH + 21 + 22, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x - PANEL_WIDTH + 21 + 44, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                switch (entity.getRedstone()) {
                    case IGNORE:
                        this.drawTexture(stack, this.x - PANEL_WIDTH + 21, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case OFF:
                        this.drawTexture(stack, this.x - PANEL_WIDTH + 43, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case ON:
                        this.drawTexture(stack, this.x - PANEL_WIDTH + 65, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                }

                this.drawTexture(stack, this.x - PANEL_WIDTH + 43, this.y + 23, ICON_REDSTONE_TORCH_OFF_X, ICON_REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.x - PANEL_WIDTH + 23, this.y + 12, Formatting.GRAY.getColorValue());
                DiffuseLighting.enableGuiDepthLighting();
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE), this.x - PANEL_WIDTH + 6, this.y + 7);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.GUNPOWDER), this.x - PANEL_WIDTH + 6 + 15, this.y + 26);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE_TORCH), this.x - PANEL_WIDTH + 6 + 15 + 15, this.y + 25 - 2);
                DiffuseLighting.disableGuiDepthLighting();
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                this.drawTexture(stack, this.x - TAB_WIDTH, this.y + 3, TAB_REDSTONE_X, TAB_REDSTONE_Y, TAB_WIDTH, TAB_HEIGHT);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE), this.x - TAB_WIDTH + 4, this.y + 6);
            }

            if (configOpen) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - PANEL_WIDTH, this.y + 26, PANEL_CONFIG_X, PANEL_CONFIG_Y, PANEL_WIDTH, PANEL_HEIGHT);

                { // CONFIGURABLE SIDE BUTTONS
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y - 18, getX(config.get(BlockFace.TOP), false), getY(config.get(BlockFace.TOP)), ICONS_WIDTH, ICONS_HEIGHT); //TOP - Top
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y + 18, getX(config.get(BlockFace.BOTTOM), false), getY(config.get(BlockFace.BOTTOM)), ICONS_WIDTH, ICONS_HEIGHT); //BOTTOM - BOTTOM
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.FRONT), true), getY(config.get(BlockFace.FRONT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER LEFT-CENTER - Front
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.LEFT), true), getY(config.get(BlockFace.LEFT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER RIGHT-CENTER - left
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.BACK), true), getY(config.get(BlockFace.BACK)), ICONS_WIDTH, ICONS_HEIGHT); //RIGHT - Back
                    this.drawTexture(stack, this.x + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.RIGHT), true), getY(config.get(BlockFace.RIGHT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER LEFT - right
                }

                if (hasShiftDown()) {
                    if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y - 18, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (config.get(BlockFace.TOP).isItem()) {
                            if (handler.blockEntity.getSideConfigInfo().getTopValue() != -1) {
                                textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getTopValue()), this.x + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y - 18) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                            }
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y + 18, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (handler.blockEntity.getSideConfigInfo().getBottomValue() != -1) {
                            textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getBottomValue()), this.x + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y + 18) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                        }
                        if (config.get(BlockFace.BOTTOM).isItem()) {
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X, this.y + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (handler.blockEntity.getSideConfigInfo().getFrontValue() != -1) {
                            textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getFrontValue()), this.x + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                        }
                        if (config.get(BlockFace.FRONT).isItem()) {
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (handler.blockEntity.getSideConfigInfo().getLeftValue() != -1) {
                            textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getLeftValue()), this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                        }
                        if (config.get(BlockFace.LEFT).isItem()) {
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (handler.blockEntity.getSideConfigInfo().getBackValue() != -1) {
                            textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getBackValue()), this.x + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                        }
                        if (config.get(BlockFace.BACK).isItem()) {
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.x + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING, this.y + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (handler.blockEntity.getSideConfigInfo().getRightValue() != -1) {
                            textRenderer.draw(stack, String.valueOf(handler.blockEntity.getSideConfigInfo().getRightValue()), this.x + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.y + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), Formatting.GOLD.getColorValue());
                        }
                        if (config.get(BlockFace.RIGHT).isItem()) {
                            for (Slot slot : handler.slots) {
                                if (slot.inventory != playerInventory) {
                                    this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x + 10, this.y + slot.y + 8, Formatting.GOLD.getColorValue());
                                }
                            }
                        }
                    }
                }

                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - PANEL_WIDTH + 6, this.y + 29);
                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.x - PANEL_WIDTH + 23, this.y + 33, Formatting.GRAY.getColorValue());
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                if (!redstoneOpen) {
                    this.drawTexture(stack, this.x - TAB_WIDTH, this.y + 26, TAB_CONFIG_X, TAB_CONFIG_Y, TAB_WIDTH, TAB_HEIGHT);
                    this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - TAB_WIDTH + 4, this.y + 26 + 3);
                } else {
                    this.drawTexture(stack, this.x - TAB_WIDTH, this.y + 96, TAB_CONFIG_X, TAB_CONFIG_Y, TAB_WIDTH, TAB_HEIGHT);
                    this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - TAB_WIDTH + 4, this.y + 96 + 3);
                }
            }
            if (securityOpen) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x + 176, this.y + 3, PANEL_SECURITY_X, PANEL_SECURITY_Y, PANEL_WIDTH, PANEL_HEIGHT);
                this.drawTexture(stack, this.x + 176 + 4, this.y + 6, ICON_LOCK_PARTY_X, ICON_LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 43, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 65, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                switch (entity.getSecurity().getPublicity()) {
                    case PRIVATE:
                        this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case SPACE_RACE:
                        this.drawTexture(stack, this.x + 174 + 43, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case PUBLIC:
                        this.drawTexture(stack, this.x + 174 + 65, this.y + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                }

                this.drawTexture(stack, this.x + 174 + 21, this.y + 27, ICON_LOCK_OWNER_X, ICON_LOCK_OWNER_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 43, this.y + 27, ICON_LOCK_PARTY_X, ICON_LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 65, this.y + 27 - 2, ICON_LOCK_PUBLIC_X, ICON_LOCK_PUBLIC_Y, ICONS_WIDTH, ICONS_HEIGHT);

                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.security_config"), this.x + 176 + 20, this.y + 12, Formatting.GRAY.getColorValue());
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                this.drawTexture(stack, this.x + 176, this.y + 5, TAB_SECURITY_X, TAB_SECURITY_Y, TAB_WIDTH, TAB_HEIGHT);
            }
        }
    }

    public boolean checkTabsClick(MatrixStack stack, double mouseX, double mouseY, int button) {
        if (button != 3) {
            if (this.handler.blockEntity != null) {
                ConfigurableMachineBlockEntity entity = this.handler.blockEntity;
                if (!redstoneOpen) {
                    if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + TAB_HEIGHT + 3) {
                        redstoneOpen = true;
                        configOpen = false;
                        playButtonSound();
                        return true;
                    }
                } else {
                    if (mouseX >= this.x - PANEL_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + TAB_HEIGHT + 3) {
                        redstoneOpen = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= this.x - 78 && mouseX <= this.x - 78 + 19 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.IGNORE);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 22 && mouseX <= this.x - 78 + 41 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.OFF);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 44 && mouseX <= this.x - 78 + 63 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.ON);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                }

                if (!configOpen) {
                    if (redstoneOpen) {
                        if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 96 && mouseY <= this.y + TAB_HEIGHT + 96) {
                            redstoneOpen = false;
                            configOpen = true;
                            playButtonSound();
                            return true;
                        }
                    } else {
                        if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + TAB_HEIGHT + 26) {
                            redstoneOpen = false;
                            configOpen = true;
                            playButtonSound();
                            return true;
                        }
                    }
                } else {
                    if (mouseX >= this.x - PANEL_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + TAB_HEIGHT + 26) {
                        configOpen = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.FRONT)) {
                            updateSides(button, BlockFace.FRONT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 + 19 + 19 && (mouseX + 48) - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.BACK)) {
                            updateSides(button, BlockFace.BACK);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.RIGHT)) {
                            updateSides(button, BlockFace.RIGHT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.LEFT)) {
                            updateSides(button, BlockFace.LEFT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.TOP)) {
                            updateSides(button, BlockFace.TOP);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                        if (!this.handler.blockEntity.getNonConfigurableSides().contains(BlockFace.BOTTOM)) {
                            updateSides(button, BlockFace.BOTTOM);
                            return true;
                        }
                    }
                }

                if (!securityOpen) {
                    if (entity.getSecurity().isOwner(playerInventory.player) && mouseX >= this.x - TAB_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + TAB_HEIGHT + 3) {
                        securityOpen = true;
                        playButtonSound();
                        return true;
                    }
                } else if (entity.getSecurity().isOwner(playerInventory.player)) {
                    ConfigurableMachineBlockEntity.SecurityInfo security = entity.getSecurity();
                    if (mouseX >= this.x - PANEL_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + TAB_HEIGHT + 3) {
                        securityOpen = false;
                        playButtonSound();
                        return true;
                    }

                    this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                    //273 = r -> s

                    if (mouseX >= this.x - 78 + 273 && mouseX <= this.x - 78 + 19 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= (this.x - 78) + 22 + 273 && mouseX <= (this.x - 78) + 41 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= this.x - 78 + 44 + 273 && mouseX <= this.x - 78 + 63 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PUBLIC);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected void drawTabTooltips(MatrixStack stack, int mouseX, int mouseY) {
        assert this.client != null;
        if (!redstoneOpen) {
            if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + (22 + 3)) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.x - 78) && mouseX <= (this.x - 78) + 19 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 22 && mouseX <= (this.x - 78) + 41 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 44 && mouseX <= (this.x - 78) + 63 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
        }
        if (!configOpen) {
            if (redstoneOpen) {
                if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 96 && mouseY <= this.y + (TAB_HEIGHT + 96)) {
                    this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
                }
            } else {
                if (mouseX >= this.x - TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + (TAB_HEIGHT + 26)) {
                    this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
                }
            }
        } else {
            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.north").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.FRONT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {//Front, Back, Right, Left, Up, Down
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.south").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.BACK).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.west").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.RIGHT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.east").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.LEFT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.up").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.TOP).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.down").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.config.get(BlockFace.BOTTOM).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }
        }
        if (!securityOpen) {
            if (mouseX >= this.x - TAB_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + (TAB_HEIGHT + 3)) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.x - 78) + 273 && mouseX <= (this.x - 78) + 19 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.private").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 22 + 273 && mouseX <= (this.x - 78) + 41 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderOrderedTooltip(stack, this.client.textRenderer.wrapLines(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.space_race", "[TEAM NAME]\u00a7r").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), 150), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 44 + 273 && mouseX <= (this.x - 78) + 63 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.public").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        if (this.handler.blockEntity != null) {
            ConfigurableMachineBlockEntity.SecurityInfo security = this.handler.blockEntity.getSecurity();
            switch (security.getPublicity()) {
                case PRIVATE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, "\u00A7l" + new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.not_your_machine").asString(), (this.width / 2), this.y + 50, Formatting.DARK_RED.getColorValue());
                        return;
                    }
                case SPACE_RACE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, "\u00A7l" + new TranslatableText("Space race system WIP").asString(), (this.width / 2), this.y + 50, Formatting.DARK_RED.getColorValue());
                        return;
                    }
                default:
                    break;
            }
        }

        super.render(stack, mouseX, mouseY, delta);
        this.drawConfigTabs(stack, mouseX, mouseY);
        stack.push();
        stack.translate(this.x, this.y, 0);
        for (AbstractWidget widget : this.widgets) {
            widget.render(stack, mouseX - this.x, mouseY - this.y, delta);
        }
        stack.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAllowed()) {
            for (AbstractWidget widget : widgets) {
                widget.mouseClicked(mouseX - this.x, mouseY - this.y, button);
            }
            return this.checkTabsClick(new MatrixStack(), mouseX, mouseY, button) | super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        if (isAllowed()) {
            super.drawMouseoverTooltip(stack, mouseX, mouseY);
            drawTabTooltips(stack, mouseX, mouseY);
            stack.push();
            stack.translate(this.x, this.y, 0);
            for (AbstractWidget widget : widgets) {
                widget.drawMouseoverTooltip(stack, mouseX - this.x, mouseY - this.y);
            }
            stack.pop();
        }
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isAllowed() {
        if (this.handler.blockEntity != null) {
            return handler.blockEntity.getSecurity().hasAccess(playerInventory.player);
        }
        return false;
    }

    private void updateSides(int btn, BlockFace face) {
        if (!Screen.hasShiftDown()) {
            SideOption next;
            if (btn == 1) {
                next = handler.blockEntity.getSideConfigInfo().get(face).getOption().prevValidOption(handler.blockEntity);
            } else {
                next = handler.blockEntity.getSideConfigInfo().get(face).getOption().nextValidOption(handler.blockEntity);
            }
            handler.blockEntity.getSideConfigInfo().set(face, next);
            sendSideConfigUpdate(face, true, next, false);
            config.replace(face, next);
        } else {
            if (btn != 1) {
                handler.blockEntity.getSideConfigInfo().increment(face);
                sendSideConfigUpdate(face, false, null, true);
            } else {
                handler.blockEntity.getSideConfigInfo().decrement(face);
                sendSideConfigUpdate(face, false ,null, false);
            }
        }
        playButtonSound();
    }

    private void sendRedstoneUpdate(ConfigurableMachineBlockEntity entity) {
        ClientPlayNetworking.send(new Identifier(Constants.MOD_ID, "redstone"),
                new PacketByteBuf(Unpooled.buffer())
                        .writeBlockPos(pos)
                        .writeEnumConstant(entity.getRedstone())
        );
    }

    private void playButtonSound() {
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Contract("_, true, null, _ -> fail;")
    private void sendSideConfigUpdate(@NotNull BlockFace face, boolean optionChange, @Nullable SideOption option, boolean positive) {
        if (handler.blockEntity.getSecurity().hasAccess(playerInventory.player)) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(handler.blockEntity.getPos());
            buf.writeBoolean(optionChange);
            if (optionChange) {
                buf.writeEnumConstant(face);
                buf.writeEnumConstant(option);
            } else {
                buf.writeBoolean(positive);
                buf.writeEnumConstant(face);
            }
            ClientPlayNetworking.send(new Identifier(Constants.MOD_ID, "side_config"), buf);
        } else {
            Galacticraft.logger.error("Tried to send side update when not trusted!");
        }
    }

    private int getX(SideOption option, boolean side) {
        if (option == SideOption.DEFAULT) {
            return !side ? 224 : 240;
        }
        if (option.isEnergy()) {
            return 206;
        }
        if (option.isOxygen() || option.isItem()) {
            return 240;
        }
        if (option.isFluid()) {
            return 223;
        }
        return 0;
    }

    private int getY(SideOption option) {
        switch (option) {
            case DEFAULT:
                return 240; //224
            case POWER_INPUT:
                return 0; //206
            case POWER_OUTPUT:
                return 17; //206
//            case OXYGEN_INPUT:
//                return 0; //240
//            case OXYGEN_OUTPUT:
//                return 17; //240
            case FLUID_INPUT:
                return 0; //223
            case FLUID_OUTPUT:
                return 17; //223
            case ITEM_OUTPUT:
                return 68;
            case ITEM_INPUT:
                return 51;
            default:
                return 0;
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseScrolled(mouseX - this.x, mouseY - this.y, amount);
        }
        return b;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.keyReleased(keyCode, scanCode, modifiers);
        }
        return b;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.charTyped(chr, keyCode);
        }
        return b;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (AbstractWidget widget : widgets) {
            widget.mouseMoved(mouseX - this.x, mouseY - this.y);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseDragged(mouseX - this.x, mouseY - this.y, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || b;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseReleased(mouseX - this.x, mouseY - this.y, button);
        }
        return super.mouseReleased(mouseX, mouseY, button) || b;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || b;
    }

    public <T extends AbstractWidget> T addWidget(T widget) {
        this.widgets.add(widget);
        return widget;
    }
}
