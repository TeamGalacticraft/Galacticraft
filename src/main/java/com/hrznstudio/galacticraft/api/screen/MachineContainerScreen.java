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

package com.hrznstudio.galacticraft.api.screen;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer.MachineContainerConstructor;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineContainerScreen<C extends MachineContainer<?>> extends HandledScreen<C> {
    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));

    public static final int SECURITY_PANEL_WIDTH = 99;
    public static final int SECURITY_PANEL_HEIGHT = 91;

    private static final int BUTTON_OFF_X = 0;
    private static final int BUTTON_OFF_Y = 240;

    private static final int BUTTON_ON_X = 0;
    private static final int BUTTON_ON_Y = 224;

    private static final int BUTTONS_WIDTH = 16;
    private static final int BUTTONS_HEIGHT = 16;

    private static final int LOCK_OWNER_X = 208;
    private static final int LOCK_OWNER_Y = 48;

    private static final int LOCK_PARTY_X = 224;
    private static final int LOCK_PARTY_Y = 48;

    private static final int LOCK_PUBLIC_X = 208;
    private static final int LOCK_PUBLIC_Y = 64;

    private static final int ICONS_WIDTH = 16;
    private static final int ICONS_HEIGHT = 16;

    private static final int REDSTONE_TORCH_OFF_X = 224;
    private static final int REDSTONE_TORCH_OFF_Y = 62;

    private static final int REDSTONE_TAB_X = 0;
    private static final int REDSTONE_TAB_Y = 46;

    private static final int REDSTONE_TAB_WIDTH = 22;
    private static final int REDSTONE_TAB_HEIGHT = 22;

    private static final int REDSTONE_PANEL_X = 0;
    private static final int REDSTONE_PANEL_Y = 0;

    private static final int REDSTONE_PANEL_WIDTH = 99;
    private static final int REDSTONE_PANEL_HEIGHT = 91;

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;

    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    private static final int CONFIG_PANEL_X = 0;
    private static final int CONFIG_PANEL_Y = 93;

    private static final int CONFIG_PANEL_WIDTH = 99;
    private static final int CONFIG_PANEL_HEIGHT = 91;

    private static final int SECURITY_TAB_X = 23;
    private static final int SECURITY_TAB_Y = 23;

    private static final int SECURITY_TAB_WIDTH = 22;
    private static final int SECURITY_TAB_HEIGHT = 22;

    private static final int SECURITY_PANEL_X = 101;
    private static final int SECURITY_PANEL_Y = 0;

    private final BlockPos pos;
    private final World world;

    private boolean IS_REDSTONE_OPEN = false;
    public boolean IS_SECURITY_OPEN = false;
    private boolean IS_CONFIG_OPEN = false;

    private SideOption[] sideOptions; //Front, Back, Right, Left, Up, Down

    public MachineContainerScreen(C container, PlayerInventory playerInventory, World world, BlockPos pos, Text textComponent) {
        super(container, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            ConfigurableElectricMachineBlockEntity entity = ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos));
            assert entity != null;

            ConfigurableElectricMachineBlockEntity.SecurityInfo security = entity.getSecurity();
            if (!security.hasOwner()) {
                security.setOwner(this.playerInventory.player);
                security.setPublicity(ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
                sendSecurityUpdate(entity);
            } else if (security.getOwner().equals(playerInventory.player.getUuid())
                    && !security.getUsername().equals(playerInventory.player.getEntityName())) {
                security.setUsername(playerInventory.player.getEntityName());
                sendSecurityUpdate(entity);
            }

            this.sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
        } else {
            throw new IllegalStateException("This isn't a configurable be!");
        }
    }

    public static <T extends ConfigurableElectricMachineBlockEntity> ContainerFactory<HandledScreen> createFactory(
            Class<T> machineClass, MachineContainerConstructor<? extends MachineContainerScreen<?>, T> constructor) {
        return (syncId, id, player, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            BlockEntity be = player.world.getBlockEntity(pos);
            if (machineClass.isInstance(be)) {
                return constructor.create(syncId, player, machineClass.cast(be));
            } else {
                return null;
            }
        };
    }

    private void sendSecurityUpdate(ConfigurableElectricMachineBlockEntity entity) {
        if (this.playerInventory.player.getUuid().equals(entity.getSecurity().getOwner()) || !entity.getSecurity().hasOwner()) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"),
                    new PacketByteBuf(Unpooled.buffer())
                            .writeBlockPos(pos)
                            .writeEnumConstant(entity.getSecurity().getPublicity())
            ));
        } else {
            Galacticraft.logger.error("Tried to send security update when not the owner!");
        }
    }

    private void sendRedstoneUpdate(ConfigurableElectricMachineBlockEntity entity) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "redstone_update"),
                new PacketByteBuf(Unpooled.buffer())
                        .writeBlockPos(pos)
                        .writeEnumConstant(entity.getRedstoneState())
        ));
    }

    public void drawConfigTabs(MatrixStack stack) {
        if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            ConfigurableElectricMachineBlockEntity entity = (ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos);
            assert entity != null;

            ConfigurableElectricMachineBlockEntity.SecurityInfo security = entity.getSecurity();

            if (IS_REDSTONE_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH, this.y + 3, REDSTONE_PANEL_X, REDSTONE_PANEL_Y, REDSTONE_PANEL_WIDTH, REDSTONE_PANEL_HEIGHT);
                this.client.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE), this.x - REDSTONE_PANEL_WIDTH + 6, this.y + 7);
                this.drawString(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.x - REDSTONE_PANEL_WIDTH + 23, this.y + 12, Formatting.GRAY.getColorValue());

                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 21, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 65, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

                switch (entity.getRedstoneState()) {
                    case DISABLED:
                        this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 21, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                    case OFF:
                        this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                    case ON:
                        this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 65, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                }

                this.client.getItemRenderer().renderGuiItem(new ItemStack(Items.GUNPOWDER), this.x - REDSTONE_PANEL_WIDTH + 21, this.y + 26);
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43, this.y + 23, REDSTONE_TORCH_OFF_X, REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.client.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE_TORCH), this.x - REDSTONE_PANEL_WIDTH + 65, this.y + 25 - 2);
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_TAB_WIDTH, this.y + 3, REDSTONE_TAB_X, REDSTONE_TAB_Y, REDSTONE_TAB_WIDTH, REDSTONE_TAB_HEIGHT);
                this.client.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE), this.x - REDSTONE_TAB_WIDTH + 4, this.y + 6);
            }
            if (IS_CONFIG_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - CONFIG_PANEL_WIDTH, this.y + 26, CONFIG_PANEL_X, CONFIG_PANEL_Y, CONFIG_PANEL_WIDTH, CONFIG_PANEL_HEIGHT);

                //Front, Back, Right, Left, top, bottom

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 3, getXForOption(sideOptions[4]), getYForOption(sideOptions[4]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //TOP - Top

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 21 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions[2]), getYForOption(sideOptions[2]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE LEFT - right
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions[0]), getYForOption(sideOptions[0]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE LEFT-CENTER - Front
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 65 - 6 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions[3]), getYForOption(sideOptions[3]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE RIGHT-CENTER - left
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 87 - 9 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions[1]), getYForOption(sideOptions[1]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //RIGHT - Back

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 36 + 3, getXForOption(sideOptions[5]), getYForOption(sideOptions[5]), BUTTONS_WIDTH, BUTTONS_HEIGHT); //BOTTOM - BOTTOM

                this.client.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - REDSTONE_PANEL_WIDTH + 6, this.y + 29);
                this.drawString(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.x - REDSTONE_PANEL_WIDTH + 23, this.y + 33, Formatting.GRAY.getColorValue());
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                if (!IS_REDSTONE_OPEN) {
                    this.drawTexture(stack, this.x - CONFIG_TAB_WIDTH, this.y + 26, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                    this.client.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - CONFIG_TAB_WIDTH + 4, this.y + 26 + 3);
                } else {
                    this.drawTexture(stack, this.x - CONFIG_TAB_WIDTH, this.y + 96, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                    this.client.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - CONFIG_TAB_WIDTH + 4, this.y + 96 + 3);
                }
            }
            if (IS_SECURITY_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x + 176, this.y + 3, SECURITY_PANEL_X, SECURITY_PANEL_Y, SECURITY_PANEL_WIDTH, SECURITY_PANEL_HEIGHT);
                this.drawTexture(stack, this.x + 176 + 4, this.y + 6, LOCK_PARTY_X, LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                this.drawString(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.security_config"), this.x + 176 + 20, this.y + 12, Formatting.GRAY.getColorValue());

                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 43, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 65, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

                switch (security.getPublicity()) {
                    case PRIVATE:
                        this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                    case SPACE_RACE:
                        this.drawTexture(stack, this.x + 174 + 43, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                    case PUBLIC:
                        this.drawTexture(stack, this.x + 174 + 65, this.y + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                        break;
                }

                this.drawTexture(stack, this.x + 174 + 21, this.y + 27, LOCK_OWNER_X, LOCK_OWNER_Y + 2, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 43, this.y + 27, LOCK_PARTY_X, LOCK_PARTY_Y + 2, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                this.drawTexture(stack, this.x + 174 + 65, this.y + 27 - 2, LOCK_PUBLIC_X, LOCK_PUBLIC_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                this.drawTexture(stack, this.x + 176, this.y + 5, SECURITY_TAB_X, SECURITY_TAB_Y, SECURITY_TAB_WIDTH, SECURITY_TAB_HEIGHT);
            }
        }
    }

    public boolean checkTabsClick(MatrixStack stack, double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                ConfigurableElectricMachineBlockEntity entity = (ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos);
                assert entity != null;
                if (!IS_REDSTONE_OPEN) {
                    if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + REDSTONE_TAB_HEIGHT + 3) {
                        IS_REDSTONE_OPEN = true;
                        IS_CONFIG_OPEN = false;
                        playButtonSound();
                        return true;
                    }
                } else {
                    if (mouseX >= this.x - REDSTONE_PANEL_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + REDSTONE_TAB_HEIGHT + 3) {
                        IS_REDSTONE_OPEN = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= this.x - 78 && mouseX <= this.x - 78 + 19 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstoneState(ConfigurableElectricMachineBlockEntity.RedstoneState.DISABLED);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 22 && mouseX <= this.x - 78 + 41 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstoneState(ConfigurableElectricMachineBlockEntity.RedstoneState.OFF);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 44 && mouseX <= this.x - 78 + 63 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstoneState(ConfigurableElectricMachineBlockEntity.RedstoneState.ON);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                }

                if (!IS_CONFIG_OPEN) {
                    if (IS_REDSTONE_OPEN) {
                        if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 96 && mouseY <= this.y + REDSTONE_TAB_HEIGHT + 96) {
                            IS_REDSTONE_OPEN = false;
                            IS_CONFIG_OPEN = true;
                            playButtonSound();
                            return true;
                        }
                    } else {
                        if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + REDSTONE_TAB_HEIGHT + 26) {
                            IS_REDSTONE_OPEN = false;
                            IS_CONFIG_OPEN = true;
                            playButtonSound();
                            return true;
                        }
                    }
                } else {
                    if (mouseX >= this.x - REDSTONE_PANEL_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + REDSTONE_TAB_HEIGHT + 26) {
                        IS_CONFIG_OPEN = false;
                        playButtonSound();
                        return true;
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.NORTH)) {
                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("north", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("north", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("north", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("north" + "," + newState.get(EnumProperty.of("north", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 + 19 + 19 && (mouseX + 48) - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.SOUTH)) {

                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("south", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("south", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("south", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("south" + "," + newState.get(EnumProperty.of("south", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.EAST)) {

                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("east", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("east", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("east", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("east" + "," + newState.get(EnumProperty.of("east", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.WEST)) {

                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("west", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("west", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("west", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("west" + "," + newState.get(EnumProperty.of("west", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.UP)) {

                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("up", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("up", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("up", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("up" + "," + newState.get(EnumProperty.of("up", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock && !((ConfigurableElectricMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.DOWN)) {

                            BlockState state = this.world.getBlockState(pos);
                            state.get(EnumProperty.of("down", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())));
                            BlockState newState = state.with(EnumProperty.of("down", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())),
                                    state.get(EnumProperty.of("down", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
                                            .nextValidOption(state.getBlock()));

                            this.world.setBlockState(pos, newState);
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("down" + "," + newState.get(EnumProperty.of("down", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))).name())));
                            sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
                            playButtonSound();
                            return true;
                        }
                    }
                }

                if (!IS_SECURITY_OPEN) {
                    if (mouseX >= this.x - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + SECURITY_TAB_HEIGHT + 3) {
                        IS_SECURITY_OPEN = true;
                        playButtonSound();
                        return true;
                    }
                } else {
                    ConfigurableElectricMachineBlockEntity.SecurityInfo security = entity.getSecurity();
                    if (mouseX >= this.x - SECURITY_PANEL_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + SECURITY_TAB_HEIGHT + 3) {
                        IS_SECURITY_OPEN = false;
                        playButtonSound();
                        return true;
                    }

                    this.drawTexture(stack, this.x + 174 + 21, this.y + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

                    //273 = r -> s

                    if (mouseX >= this.x - 78 + 273 && mouseX <= this.x - 78 + 19 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= (this.x - 78) + 22 + 273 && mouseX <= (this.x - 78) + 41 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE);
                            sendSecurityUpdate(entity);
                            playButtonSound();
                            return true;
                        }
                    }
                    if (mouseX >= this.x - 78 + 44 + 273 && mouseX <= this.x - 78 + 63 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        if (security.getOwner().equals(this.playerInventory.player.getUuid())) {
                            security.setUsername(this.playerInventory.player.getName().asString());
                            security.setPublicity(ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.PUBLIC);
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

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            ConfigurableElectricMachineBlockEntity.SecurityInfo security = ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).getSecurity();
            switch (security.getPublicity()) {
                case PRIVATE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, "\u00A7l" + new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.not_your_machine").asString(), (this.width / 2), this.y + 50, Formatting.DARK_RED.getColorValue());
                        return;
                    }
                case SPACE_RACE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, "\u00A7l" + new TranslatableText("Team stuff pending...").asString(), (this.width / 2), this.y + 50, Formatting.DARK_RED.getColorValue());
                        return;
                    }
                default:
                    break;
            }
        }

        this.drawConfigTabs(stack);
        super.render(stack, mouseX, mouseY, delta);
    }

    protected void drawTabTooltips(MatrixStack stack, int mouseX, int mouseY) {
        if (!IS_REDSTONE_OPEN) {
            if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + (22 + 3)) {
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
        if (!IS_CONFIG_OPEN) {
            if (IS_REDSTONE_OPEN) {
                if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 96 && mouseY <= this.y + (REDSTONE_TAB_HEIGHT + 96)) {
                    this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
                }
            } else {
                if (mouseX >= this.x - REDSTONE_TAB_WIDTH && mouseX <= this.x && mouseY >= this.y + 26 && mouseY <= this.y + (REDSTONE_TAB_HEIGHT + 26)) {
                    this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
                }
            }
        } else {
            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.north").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[0].getFormattedName()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.south").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[1].getFormattedName()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.east").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[2].getFormattedName()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.west").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[3].getFormattedName()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.up").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[4].getFormattedName()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                this.renderTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.down").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Text[]{this.sideOptions[5].getFormattedName()}), mouseX, mouseY);
            }
        }
        if (!IS_SECURITY_OPEN) {
            if (mouseX >= this.x - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + (SECURITY_TAB_HEIGHT + 3)) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.x - 78) + 273 && mouseX <= (this.x - 78) + 19 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.private").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 22 + 273 && mouseX <= (this.x - 78) + 41 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, this.client.textRenderer.wrapLines(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.space_race", "[TEAM NAME]\u00a7r").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), 150), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 44 + 273 && mouseX <= (this.x - 78) + 63 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.public").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
        }
    }

    public boolean isAllowed() {
        if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            ConfigurableElectricMachineBlockEntity.SecurityInfo security = ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).getSecurity();
            switch (security.getPublicity()) {
                case PRIVATE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        return false;
                    }
                case SPACE_RACE:
                    if (!this.playerInventory.player.getUuid().equals(security.getOwner())) {
                        return false;
                    }
                default:
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (isAllowed()) {
            return this.checkTabsClick(new MatrixStack(), double_1, double_2, int_1) || super.mouseClicked(double_1, double_2, int_1);
        } else {
            return false;
        }
    }

    private int getXForOption(SideOption option) {
        switch (option) {
            case DEFAULT:
                return 224; //240
            case POWER_INPUT:
                return 206; //0
            case POWER_OUTPUT:
                return 206; //17
            case OXYGEN_INPUT:
                return 240; //0
            case OXYGEN_OUTPUT:
                return 240; //17 223,0
            case FLUID_INPUT:
                return 223; //0
            case FLUID_OUTPUT:
                return 223; //17
            default:
                return 0;
        }
    }

    private int getYForOption(SideOption option) {
        switch (option) {
            case DEFAULT:
                return 240; //224
            case POWER_INPUT:
                return 0; //206
            case POWER_OUTPUT:
                return 17; //206
            case OXYGEN_INPUT:
                return 0; //240
            case OXYGEN_OUTPUT:
                return 17; //240
            case FLUID_INPUT:
                return 0; //223
            case FLUID_OUTPUT:
                return 17; //223
            default:
                return 0;
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        if (isAllowed()) {
            super.drawMouseoverTooltip(stack, mouseX, mouseY);
            drawTabTooltips(stack, mouseX, mouseY);
        }
    }
    
    private void playButtonSound() {
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
