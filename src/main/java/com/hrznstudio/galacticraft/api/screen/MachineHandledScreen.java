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

package com.hrznstudio.galacticraft.api.screen;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.client.gui.widget.machine.AbstractWidget;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public abstract class MachineHandledScreen<C extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> extends AbstractContainerScreen<C> {
    public static final ResourceLocation TABS_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final ResourceLocation PANELS_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
    public static final ResourceLocation OVERLAY = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

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
    protected final Level world;
    private final List<AbstractWidget> widgets = new LinkedList<>();

    public boolean securityOpen = false;
    public boolean redstoneOpen = false;
    public boolean configOpen = false;

    private final Map<BlockFace, SideOption> config = new EnumMap<>(BlockFace.class);

    public MachineHandledScreen(C screenHandler, Inventory playerInventory, Level world, BlockPos pos, Component textComponent) {
        super(screenHandler, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        ConfigurableMachineBlockEntity.SecurityInfo security = this.menu.machine.getSecurity();
        if (!security.hasOwner()) {
            security.setOwner(this.inventory.player);
            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
            sendSecurityUpdate(this.menu.machine);
        } else if (security.getOwner().equals(playerInventory.player.getUUID())
                && !security.getUsername().equals(playerInventory.player.getScoreboardName())) {
            security.setUsername(playerInventory.player.getScoreboardName());
            sendSecurityUpdate(this.menu.machine);
        }

        for (BlockFace face : Constants.Misc.BLOCK_FACES) {
            config.put(face, screenHandler.machine.getSideConfiguration().get(face).getOption());
        }
    }

    private void sendSecurityUpdate(ConfigurableMachineBlockEntity entity) {
        if (this.inventory.player.getUUID().equals(entity.getSecurity().getOwner()) || !entity.getSecurity().hasOwner()) {
            ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "security"),
                    new FriendlyByteBuf(Unpooled.buffer())
                            .writeBlockPos(pos)
                            .writeEnum(entity.getSecurity().getPublicity())
            );
        } else {
            Galacticraft.logger.error("Tried to send security update when not the owner!");
        }
    }

    @NotNull
    protected Collection<? extends Component> getEnergyTooltipLines() {
        return Collections.emptyList();
    }

    public void drawConfigTabs(PoseStack matrices, int mouseX, int mouseY) {
        Lighting.turnOff();
        if (this.menu.machine != null) {
            final ConfigurableMachineBlockEntity entity = this.menu.machine;

            if (redstoneOpen) {
                this.minecraft.getTextureManager().bind(PANELS_TEXTURE);
                this.blit(matrices, this.leftPos - PANEL_WIDTH, this.topPos + 3, PANEL_REDSTONE_X, PANEL_REDSTONE_Y, PANEL_WIDTH, PANEL_HEIGHT);

                this.blit(matrices, this.leftPos - PANEL_WIDTH + 21, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos - PANEL_WIDTH + 21 + 22, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos - PANEL_WIDTH + 21 + 44, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                switch (entity.getRedstone()) {
                    case IGNORE:
                        this.blit(matrices, this.leftPos - PANEL_WIDTH + 21, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case OFF:
                        this.blit(matrices, this.leftPos - PANEL_WIDTH + 43, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case ON:
                        this.blit(matrices, this.leftPos - PANEL_WIDTH + 65, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                }

                this.blit(matrices, this.leftPos - PANEL_WIDTH + 43, this.topPos + 23, ICON_REDSTONE_TORCH_OFF_X, ICON_REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                drawString(matrices, this.font, I18n.get("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.leftPos - PANEL_WIDTH + 23, this.topPos + 12, ChatFormatting.GRAY.getColor());
                Lighting.setupFor3DItems();
                this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.REDSTONE), this.leftPos - PANEL_WIDTH + 6, this.topPos + 7);
                this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.GUNPOWDER), this.leftPos - PANEL_WIDTH + 6 + 15, this.topPos + 26);
                this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.REDSTONE_TORCH), this.leftPos - PANEL_WIDTH + 6 + 15 + 15, this.topPos + 25 - 2);
                Lighting.setupForFlatItems();
            } else {
                this.minecraft.getTextureManager().bind(TABS_TEXTURE);
                this.blit(matrices, this.leftPos - TAB_WIDTH, this.topPos + 3, TAB_REDSTONE_X, TAB_REDSTONE_Y, TAB_WIDTH, TAB_HEIGHT);
                this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.REDSTONE), this.leftPos - TAB_WIDTH + 4, this.topPos + 6);
            }

            if (configOpen) {
                this.minecraft.getTextureManager().bind(PANELS_TEXTURE);
                this.blit(matrices, this.leftPos - PANEL_WIDTH, this.topPos + 26, PANEL_CONFIG_X, PANEL_CONFIG_Y, PANEL_WIDTH, PANEL_HEIGHT);

                { // CONFIGURABLE SIDE BUTTONS
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y - 18, getX(config.get(BlockFace.TOP), false), getY(config.get(BlockFace.TOP)), ICONS_WIDTH, ICONS_HEIGHT); //TOP - Top
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y + 18, getX(config.get(BlockFace.BOTTOM), false), getY(config.get(BlockFace.BOTTOM)), ICONS_WIDTH, ICONS_HEIGHT); //BOTTOM - BOTTOM
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.FRONT), true), getY(config.get(BlockFace.FRONT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER LEFT-CENTER - Front
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.LEFT), true), getY(config.get(BlockFace.LEFT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER RIGHT-CENTER - left
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.BACK), true), getY(config.get(BlockFace.BACK)), ICONS_WIDTH, ICONS_HEIGHT); //RIGHT - Back
                    this.blit(matrices, this.leftPos + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, getX(config.get(BlockFace.RIGHT), true), getY(config.get(BlockFace.RIGHT)), ICONS_WIDTH, ICONS_HEIGHT); //CENTER LEFT - right
                }

                if (hasShiftDown()) {
                    if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y - 18, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (config.get(BlockFace.TOP).isItem()) {
                            if (menu.machine.getSideConfiguration().getTopValue() != -1) {
                                font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getTopValue()), this.leftPos + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y - 18) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                            }
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y + 18, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (menu.machine.getSideConfiguration().getBottomValue() != -1) {
                            font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getBottomValue()), this.leftPos + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y + 18) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                        }
                        if (config.get(BlockFace.BOTTOM).isItem()) {
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X, this.topPos + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (menu.machine.getSideConfiguration().getFrontValue() != -1) {
                            font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getFrontValue()), this.leftPos + BASE_CONFIG_BUTTON_X + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                        }
                        if (config.get(BlockFace.FRONT).isItem()) {
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (menu.machine.getSideConfiguration().getLeftValue() != -1) {
                            font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getLeftValue()), this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                        }
                        if (config.get(BlockFace.LEFT).isItem()) {
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (menu.machine.getSideConfiguration().getBackValue() != -1) {
                            font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getBackValue()), this.leftPos + BASE_CONFIG_BUTTON_X + CONFIG_BUTTON_SPACING + CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                        }
                        if (config.get(BlockFace.BACK).isItem()) {
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    } else if (check(mouseX, mouseY, this.leftPos + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING, this.topPos + BASE_CONFIG_BUTTON_Y, ICONS_WIDTH, ICONS_HEIGHT)) {
                        if (menu.machine.getSideConfiguration().getRightValue() != -1) {
                            font.draw(matrices, String.valueOf(menu.machine.getSideConfiguration().getRightValue()), this.leftPos + BASE_CONFIG_BUTTON_X - CONFIG_BUTTON_SPACING + (ICONS_WIDTH / 2F), (this.topPos + BASE_CONFIG_BUTTON_Y) + (ICONS_HEIGHT / 2F), ChatFormatting.GOLD.getColor());
                        }
                        if (config.get(BlockFace.RIGHT).isItem()) {
                            for (Slot slot : menu.slots) {
                                if (slot.container != inventory) {
                                    this.font.draw(matrices, new TextComponent(String.valueOf(slot.index)), this.leftPos + slot.x + 10, this.topPos + slot.y + 8, ChatFormatting.GOLD.getColor());
                                }
                            }
                        }
                    }
                }

                this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.leftPos - PANEL_WIDTH + 6, this.topPos + 29);
                drawString(matrices, this.font, I18n.get("ui.galacticraft-rewoven.tabs.side_config"), this.leftPos - PANEL_WIDTH + 23, this.topPos + 33, ChatFormatting.GRAY.getColor());
            } else {
                this.minecraft.getTextureManager().bind(TABS_TEXTURE);
                if (!redstoneOpen) {
                    this.blit(matrices, this.leftPos - TAB_WIDTH, this.topPos + 26, TAB_CONFIG_X, TAB_CONFIG_Y, TAB_WIDTH, TAB_HEIGHT);
                    this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.leftPos - TAB_WIDTH + 4, this.topPos + 26 + 3);
                } else {
                    this.blit(matrices, this.leftPos - TAB_WIDTH, this.topPos + 96, TAB_CONFIG_X, TAB_CONFIG_Y, TAB_WIDTH, TAB_HEIGHT);
                    this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.leftPos - TAB_WIDTH + 4, this.topPos + 96 + 3);
                }
            }
            if (securityOpen) {
                this.minecraft.getTextureManager().bind(PANELS_TEXTURE);
                this.blit(matrices, this.leftPos + 176, this.topPos + 3, PANEL_SECURITY_X, PANEL_SECURITY_Y, PANEL_WIDTH, PANEL_HEIGHT);
                this.blit(matrices, this.leftPos + 176 + 4, this.topPos + 6, ICON_LOCK_PARTY_X, ICON_LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos + 174 + 21, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos + 174 + 43, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos + 174 + 65, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                switch (entity.getSecurity().getPublicity()) {
                    case PRIVATE:
                        this.blit(matrices, this.leftPos + 174 + 21, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case SPACE_RACE:
                        this.blit(matrices, this.leftPos + 174 + 43, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                    case PUBLIC:
                        this.blit(matrices, this.leftPos + 174 + 65, this.topPos + 26, BUTTON_X, BUTTON_ON_Y, ICONS_WIDTH, ICONS_HEIGHT);
                        break;
                }

                this.blit(matrices, this.leftPos + 174 + 21, this.topPos + 27, ICON_LOCK_OWNER_X, ICON_LOCK_OWNER_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos + 174 + 43, this.topPos + 27, ICON_LOCK_PARTY_X, ICON_LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.blit(matrices, this.leftPos + 174 + 65, this.topPos + 27 - 2, ICON_LOCK_PUBLIC_X, ICON_LOCK_PUBLIC_Y, ICONS_WIDTH, ICONS_HEIGHT);

                drawString(matrices, this.font, I18n.get("ui.galacticraft-rewoven.tabs.security_config"), this.leftPos + 176 + 20, this.topPos + 12, ChatFormatting.GRAY.getColor());
            } else {
                this.minecraft.getTextureManager().bind(TABS_TEXTURE);
                this.blit(matrices, this.leftPos + 176, this.topPos + 5, TAB_SECURITY_X, TAB_SECURITY_Y, TAB_WIDTH, TAB_HEIGHT);
            }
        }
    }

    public boolean checkTabsClick(PoseStack matrices, double mouseX, double mouseY, int button) {
        if (button != 3) {
            if (this.menu.machine != null) {
                ConfigurableMachineBlockEntity entity = this.menu.machine;
                if (!redstoneOpen) {
                    if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 3 && mouseY <= this.topPos + TAB_HEIGHT + 3) {
                        redstoneOpen = true;
                        configOpen = false;
                        playButtonSound();
                        return true;
                    }
                } else {
                    if (mouseX >= this.leftPos - PANEL_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 3 && mouseY <= this.topPos + TAB_HEIGHT + 3) {
                        redstoneOpen = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= this.leftPos - 78 && mouseX <= this.leftPos - 78 + 19 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.IGNORE);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.leftPos - 78 + 22 && mouseX <= this.leftPos - 78 + 41 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.OFF);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.leftPos - 78 + 44 && mouseX <= this.leftPos - 78 + 63 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        entity.setRedstone(ConfigurableMachineBlockEntity.RedstoneState.ON);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                }

                if (!configOpen) {
                    if (redstoneOpen) {
                        if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 96 && mouseY <= this.topPos + TAB_HEIGHT + 96) {
                            redstoneOpen = false;
                            configOpen = true;
                            playButtonSound();
                            return true;
                        }
                    } else {
                        if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 26 && mouseY <= this.topPos + TAB_HEIGHT + 26) {
                            redstoneOpen = false;
                            configOpen = true;
                            playButtonSound();
                            return true;
                        }
                    }
                } else {
                    if (mouseX >= this.leftPos - PANEL_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 26 && mouseY <= this.topPos + TAB_HEIGHT + 26) {
                        configOpen = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.FRONT)) {
                            updateSides(button, BlockFace.FRONT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 + 19 + 19 && (mouseX + 48) - 19 - 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.BACK)) {
                            updateSides(button, BlockFace.BACK);
                            return true;
                        }
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 - 19 && mouseX + 48 + 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.RIGHT)) {
                            updateSides(button, BlockFace.RIGHT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 + 19 && mouseX + 48 - 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.LEFT)) {
                            updateSides(button, BlockFace.LEFT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 && mouseY <= this.topPos + 68) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.TOP)) {
                            updateSides(button, BlockFace.TOP);
                            return true;
                        }
                    }

                    if (mouseX >= (this.leftPos - PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 + 18 && mouseY <= this.topPos + 68 + 18 + 18) {
                        if (!this.menu.machine.getLockedFaces().contains(BlockFace.BOTTOM)) {
                            updateSides(button, BlockFace.BOTTOM);
                            return true;
                        }
                    }
                }

                if (!securityOpen) {
                    if (entity.getSecurity().isOwner(inventory.player) && mouseX >= this.leftPos - TAB_WIDTH + 176 + 21 && mouseX <= this.leftPos + 176 + 21 && mouseY >= this.topPos + 3 && mouseY <= this.topPos + TAB_HEIGHT + 3) {
                        securityOpen = true;
                        playButtonSound();
                        return true;
                    }
                } else if (entity.getSecurity().isOwner(inventory.player)) {
                    ConfigurableMachineBlockEntity.SecurityInfo security = entity.getSecurity();
                    if (mouseX >= this.leftPos - PANEL_WIDTH + 176 + 21 && mouseX <= this.leftPos + 176 + 21 && mouseY >= this.topPos + 3 && mouseY <= this.topPos + TAB_HEIGHT + 3) {
                        securityOpen = false;
                        playButtonSound();
                        return true;
                    }

                    this.blit(matrices, this.leftPos + 174 + 21, this.topPos + 26, BUTTON_X, BUTTON_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);

                    //273 = r -> s

                    if (mouseX >= this.leftPos - 78 + 273 && mouseX <= this.leftPos - 78 + 19 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        if (security.getOwner().equals(this.inventory.player.getUUID())) {
                            security.setUsername(this.inventory.player.getName().getContents());
                            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= (this.leftPos - 78) + 22 + 273 && mouseX <= (this.leftPos - 78) + 41 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        if (security.getOwner().equals(this.inventory.player.getUUID())) {
                            security.setUsername(this.inventory.player.getName().getContents());
                            security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= this.leftPos - 78 + 44 + 273 && mouseX <= this.leftPos - 78 + 63 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                        if (security.getOwner().equals(this.inventory.player.getUUID())) {
                            security.setUsername(this.inventory.player.getName().getContents());
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

    protected void drawTabTooltips(PoseStack matrices, int mouseX, int mouseY) {
        assert this.minecraft != null;
        if (!redstoneOpen) {
            if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 3 && mouseY <= this.topPos + (22 + 3)) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config").setStyle(Constants.Styles.GRAY_STYLE), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.leftPos - 78) && mouseX <= (this.leftPos - 78) + 19 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.leftPos - 78) + 22 && mouseX <= (this.leftPos - 78) + 41 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.leftPos - 78) + 44 && mouseX <= (this.leftPos - 78) + 63 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
            }
        }
        if (!configOpen) {
            if (redstoneOpen) {
                if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 96 && mouseY <= this.topPos + (TAB_HEIGHT + 96)) {
                    this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
                }
            } else {
                if (mouseX >= this.leftPos - TAB_WIDTH && mouseX <= this.leftPos && mouseY >= this.topPos + 26 && mouseY <= this.topPos + (TAB_HEIGHT + 26)) {
                    this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
                }
            }
        } else {
            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.north").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.FRONT).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }

            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {//Front, Back, Right, Left, Up, Down
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.south").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.BACK).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }

            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.west").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.RIGHT).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }

            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 && mouseY <= this.topPos + 68 + 18) {
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.east").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.LEFT).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }

            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 && mouseY <= this.topPos + 68) {
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.up").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.TOP).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }

            if (mouseX >= this.leftPos - PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.leftPos && mouseY >= this.topPos + 49 + 3 + 18 + 18 && mouseY <= this.topPos + 68 + 18 + 18) {
                this.renderTooltip(matrices, Lists.asList(new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config.down").setStyle(Constants.Styles.GRAY_STYLE).getVisualOrderText(), new FormattedCharSequence[]{this.config.get(BlockFace.BOTTOM).getFormattedName().getVisualOrderText()}), mouseX, mouseY);
            }
        }
        if (!securityOpen) {
            if (mouseX >= this.leftPos - TAB_WIDTH + 176 + 21 && mouseX <= this.leftPos + 176 + 21 && mouseY >= this.topPos + 3 && mouseY <= this.topPos + (TAB_HEIGHT + 3)) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config").setStyle(Constants.Styles.GRAY_STYLE), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.leftPos - 78) + 273 && mouseX <= (this.leftPos - 78) + 19 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.private").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.leftPos - 78) + 22 + 273 && mouseX <= (this.leftPos - 78) + 41 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, this.font.split(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.space_race", "[TEAM NAME]\u00a7r").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), 150), mouseX, mouseY);
            }
            if (mouseX >= (this.leftPos - 78) + 44 + 273 && mouseX <= (this.leftPos - 78) + 63 + 273 - 3 && mouseY >= this.topPos + 26 && mouseY <= this.topPos + 41) {
                this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.public").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        assert this.minecraft != null;
        if (this.menu.machine != null) {
            ConfigurableMachineBlockEntity.SecurityInfo security = this.menu.machine.getSecurity();
            switch (security.getPublicity()) {
                case PRIVATE:
                    if (!this.inventory.player.getUUID().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(matrices, this.font, "\u00A7l" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.not_your_machine").getContents(), (this.width / 2), this.topPos + 50, ChatFormatting.DARK_RED.getColor());
                        return;
                    }
                case SPACE_RACE:
                    if (!this.inventory.player.getUUID().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(matrices, this.font, "\u00A7l" + new TranslatableComponent("Space race system WIP").getContents(), (this.width / 2), this.topPos + 50, ChatFormatting.DARK_RED.getColor());
                        return;
                    }
                default:
                    break;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
        this.drawConfigTabs(matrices, mouseX, mouseY);
        matrices.pushPose();
        matrices.translate(this.leftPos, this.topPos, 0);
        for (AbstractWidget widget : this.widgets) {
            widget.render(matrices, mouseX - this.leftPos, mouseY - this.topPos, delta);
        }
        matrices.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAllowed()) {
            for (AbstractWidget widget : widgets) {
                widget.mouseClicked(mouseX - this.leftPos, mouseY - this.topPos, button);
            }
            return this.checkTabsClick(new PoseStack(), mouseX, mouseY, button) | super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }

    @Override
    protected void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
        if (isAllowed()) {
            super.renderTooltip(matrices, mouseX, mouseY);
            drawTabTooltips(matrices, mouseX, mouseY);
            matrices.pushPose();
            matrices.translate(this.leftPos, this.topPos, 0);
            for (AbstractWidget widget : widgets) {
                widget.drawMouseoverTooltip(matrices, mouseX - this.leftPos, mouseY - this.topPos);
            }
            matrices.popPose();
        }
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isAllowed() {
        if (this.menu.machine != null) {
            return menu.machine.getSecurity().hasAccess(inventory.player);
        }
        return false;
    }

    private void updateSides(int btn, BlockFace face) {
        if (!Screen.hasShiftDown()) {
            SideOption next;
            if (btn == 1) {
                next = menu.machine.getSideConfiguration().get(face).getOption().prevValidOption(menu.machine);
            } else {
                next = menu.machine.getSideConfiguration().get(face).getOption().nextValidOption(menu.machine);
            }
            menu.machine.getSideConfiguration().set(face, next);
            sendSideConfigUpdate(face, true, next, false);
            config.replace(face, next);
        } else {
            if (btn != 1) {
                menu.machine.getSideConfiguration().increment(face);
                sendSideConfigUpdate(face, false, null, true);
            } else {
                menu.machine.getSideConfiguration().decrement(face);
                sendSideConfigUpdate(face, false ,null, false);
            }
        }
        playButtonSound();
    }

    private void sendRedstoneUpdate(ConfigurableMachineBlockEntity entity) {
        ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "redstone"),
                new FriendlyByteBuf(Unpooled.buffer())
                        .writeBlockPos(pos)
                        .writeEnum(entity.getRedstone())
        );
    }

    private void playButtonSound() {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Contract("_, true, null, _ -> fail;")
    private void sendSideConfigUpdate(@NotNull BlockFace face, boolean optionChange, @Nullable SideOption option, boolean positive) {
        if (menu.machine.getSecurity().hasAccess(inventory.player)) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBlockPos(menu.machine.getBlockPos());
            buf.writeBoolean(optionChange);
            if (optionChange) {
                buf.writeEnum(face);
                buf.writeEnum(option);
            } else {
                buf.writeBoolean(positive);
                buf.writeEnum(face);
            }
            ClientPlayNetworking.send(new ResourceLocation(Constants.MOD_ID, "side_config"), buf);
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
        if (option.isItem()) {
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
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseScrolled(mouseX - this.leftPos, mouseY - this.topPos, amount);
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
            widget.mouseMoved(mouseX - this.leftPos, mouseY - this.topPos);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseDragged(mouseX - this.leftPos, mouseY - this.topPos, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || b;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseReleased(mouseX - this.leftPos, mouseY - this.topPos, button);
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
