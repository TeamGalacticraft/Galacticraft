package com.hrznstudio.galacticraft.api.screen;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineContainerScreen extends AbstractContainerScreen {

    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
    private static final int BUTTON_OFF_X = 0;
    private static final int BUTTON_OFF_Y = 240;
    private static final int BUTTON_ON_X = 0;
    private static final int BUTTON_ON_Y = 224;
    private static final int BUTTONS_WIDTH = 16;
    private static final int BUTTONS_HEIGHT = 16;
    private static final int REDSTONE_TORCH_OFF_X = 224;
    private static final int REDSTONE_TORCH_OFF_Y = 62;
    private static final int LOCK_OWNER_X = 208;
    private static final int LOCK_OWNER_Y = 48;
    private static final int LOCK_PARTY_X = 224;
    private static final int LOCK_PARTY_Y = 48;
    private static final int LOCK_PUBLIC_X = 208;
    private static final int LOCK_PUBLIC_Y = 64;
    private static final int ICONS_WIDTH = 16;
    private static final int ICONS_HEIGHT = 16;
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
    private static final int SECURITY_PANEL_WIDTH = 99;
    private static final int SECURITY_PANEL_HEIGHT = 91;
    private final BlockPos pos;
    private final World world;
    private boolean IS_REDSTONE_OPEN = false;
    private String selectedRedstoneOption = "DISABLED"; //0 = disabled (redstone doesn't matter), 1 = off (if redstone is off, the machine is on), 2 = on (if redstone is on, the machine turns off)
    private boolean IS_CONFIG_OPEN = false;
    private boolean IS_SECURITY_OPEN = false;

    private int selectedSecurityOption; //0 = owner only, 1 = space race party only, 2 = public access

    private SideOption[] sideOptions = null;

    public MachineContainerScreen(Container container, PlayerInventory playerInventory, World world, BlockPos pos, TranslatableComponent textComponent) {
        super(container, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals("NOT_SET")) {
                ((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).owner = "";
            }
            if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty && ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic) {
                Galacticraft.logger.fatal("The selected security option is both 'party' and 'public'!");
                Galacticraft.logger.fatal("The option has been automatically reset to public");
                ((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).isParty = false;
            }
            if (((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).isParty) {
                selectedSecurityOption = 1;
            } else if (((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).isPublic) {
                selectedSecurityOption = 2;
            } else if (!((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).owner.equals("") || !((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).owner.isEmpty()) {
                selectedSecurityOption = 0;
            } else {
                (((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(pos)).isPublic) = true;
            }

            this.selectedRedstoneOption = ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).redstoneOption;

            this.sideOptions = ConfigurableElectricMachineBlock.optionsToArray(world.getBlockState(pos));
        }
    }

    public void drawConfigTabs() {
        if (IS_REDSTONE_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH, this.top + 3, REDSTONE_PANEL_X, REDSTONE_PANEL_Y, REDSTONE_PANEL_WIDTH, REDSTONE_PANEL_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE), this.left - REDSTONE_PANEL_WIDTH + 6, this.top + 7);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.left - REDSTONE_PANEL_WIDTH + 23, this.top + 12, ChatFormat.GRAY.getColor());

            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

            switch (selectedRedstoneOption) {
                case "DISABLED":
                    this.blit(this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                    break;
                case "OFF":
                    this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                    break;
                case "ON":
                    this.blit(this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
                    break;
                default:
                    Galacticraft.logger.fatal("The selected redstone sideOptions option is not valid!");
                    Galacticraft.logger.fatal("The option has been automatically reset to 'ignore redstone'");
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("DISABLED")));
                    selectedRedstoneOption = "DISABLED";
                    break;
            }

            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.GUNPOWDER), this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 26);
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 23, REDSTONE_TORCH_OFF_X, REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE_TORCH), this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 25 - 2);
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            this.blit(this.left - REDSTONE_TAB_WIDTH, this.top + 3, REDSTONE_TAB_X, REDSTONE_TAB_Y, REDSTONE_TAB_WIDTH, REDSTONE_TAB_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.REDSTONE), this.left - REDSTONE_TAB_WIDTH + 4, this.top + 6);
        }
        if (IS_CONFIG_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left - CONFIG_PANEL_WIDTH, this.top + 26, CONFIG_PANEL_X, CONFIG_PANEL_Y, CONFIG_PANEL_WIDTH, CONFIG_PANEL_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 21, this.top + 49, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 43, this.top + 49, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left - REDSTONE_PANEL_WIDTH + 65, this.top + 49, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.left - REDSTONE_PANEL_WIDTH + 6, this.top + 29);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.left - REDSTONE_PANEL_WIDTH + 23, this.top + 33, ChatFormat.GRAY.getColor());
        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            if (!IS_REDSTONE_OPEN) {
                this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 26, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.left - CONFIG_TAB_WIDTH + 4, this.top + 26 + 3);
            } else {
                this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 96, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                this.minecraft.getItemRenderer().renderGuiItem(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.left - CONFIG_TAB_WIDTH + 4, this.top + 96 + 3);
            }
        }
        if (IS_SECURITY_OPEN) {
            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left + 176, this.top + 3, SECURITY_PANEL_X, SECURITY_PANEL_Y, SECURITY_PANEL_WIDTH, SECURITY_PANEL_HEIGHT);
            this.blit(this.left + 176 + 4, this.top + 6, LOCK_PARTY_X, LOCK_PARTY_Y, ICONS_WIDTH, ICONS_HEIGHT);
            this.drawString(this.minecraft.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.security_config"), this.left + 176 + 20, this.top + 12, ChatFormat.GRAY.getColor());

            this.minecraft.getTextureManager().bindTexture(PANELS_TEXTURE);
            this.blit(this.left + 174 + 21, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left + 174 + 43, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left + 174 + 65, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

            if (selectedSecurityOption == 0) {
                this.blit(this.left + 174 + 21, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else if (selectedSecurityOption == 1) {
                this.blit(this.left + 174 + 43, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else if (selectedSecurityOption == 2) {
                this.blit(this.left + 174 + 65, this.top + 26, BUTTON_ON_X, BUTTON_ON_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            } else {
                Galacticraft.logger.fatal("The selected security sideOptions option is not valid!");
                if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic = true;
                    ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty = false;
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString(this.playerInventory.player.getUuidAsString() + "_Public")));
                    selectedSecurityOption = 2;
                    Galacticraft.logger.fatal("The option has been reset to Public");
                }
            }

            this.blit(this.left + 174 + 21, this.top + 27 - 2, LOCK_OWNER_X, LOCK_OWNER_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left + 174 + 43, this.top + 27 - 2, LOCK_PARTY_X, LOCK_PARTY_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);
            this.blit(this.left + 174 + 65, this.top + 27 - 2, LOCK_PUBLIC_X, LOCK_PUBLIC_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

        } else {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            this.blit(this.left + 176, this.top + 3, SECURITY_TAB_X, SECURITY_TAB_Y, SECURITY_TAB_WIDTH, SECURITY_TAB_HEIGHT);
        }
    }

    public boolean checkTabsClick(double mouseX, double mouseY, int button) {
        if (!IS_REDSTONE_OPEN) {
            if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 3) && button == 0) {
                IS_REDSTONE_OPEN = true;
                IS_CONFIG_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else {
            if (mouseX >= this.left - REDSTONE_PANEL_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 3) && button == 0) {
                IS_REDSTONE_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }

            if (mouseX >= (this.left - 78) && mouseX <= (this.left - 78) + 19 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {
                if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    this.selectedRedstoneOption = "DISABLED";
                    ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).redstoneOption = "DISABLED";
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "redstone_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("DISABLED")));
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            if (mouseX >= (this.left - 78) + 22 && mouseX <= (this.left - 78) + 41 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {
                if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    this.selectedRedstoneOption = "OFF"; //r=o
                    ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).redstoneOption = "OFF";
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "redstone_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("OFF")));
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            if (mouseX >= (this.left - 78) + 44 && mouseX <= (this.left - 78) + 63 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {
                if (this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).redstoneOption = "ON";
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "redstone_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString("ON")));
                    this.selectedRedstoneOption = "ON";
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }

        if (!IS_CONFIG_OPEN) {
            if (IS_REDSTONE_OPEN) {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 96 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 96) && button == 0) {
                    IS_REDSTONE_OPEN = false;
                    IS_CONFIG_OPEN = true;
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            } else {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 26 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 26) && button == 0) {
                    IS_REDSTONE_OPEN = false;
                    IS_CONFIG_OPEN = true;
                    this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        } else {
            if (mouseX >= this.left - REDSTONE_PANEL_WIDTH && mouseX <= this.left && mouseY >= this.top + 26 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 26) && button == 0) {
                IS_CONFIG_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (!IS_SECURITY_OPEN) {
            if (mouseX >= this.left - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.left + 176 + 21 && mouseY >= this.top + 3 && mouseY <= this.top + (SECURITY_TAB_HEIGHT + 3) && button == 0) {
                IS_SECURITY_OPEN = true;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else {
            if (mouseX >= this.left - SECURITY_PANEL_WIDTH + 176 + 21 && mouseX <= this.left + 176 + 21 && mouseY >= this.top + 3 && mouseY <= this.top + (SECURITY_TAB_HEIGHT + 3) && button == 0) {
                IS_SECURITY_OPEN = false;
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }

            this.blit(this.left + 174 + 21, this.top + 26, BUTTON_OFF_X, BUTTON_OFF_Y, BUTTONS_WIDTH, BUTTONS_HEIGHT);

            //273 = r -> s

            if (mouseX >= (this.left - 78) + 273 && mouseX <= (this.left - 78) + 19 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {
                if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.isEmpty() || ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(this.playerInventory.player.getUuidAsString()) || ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals("")) {
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner = this.playerInventory.player.getUuidAsString();
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty = false;
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic = false;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString(this.playerInventory.player.getUuidAsString())));
                        this.selectedSecurityOption = 0;
                        this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
            if (mouseX >= (this.left - 78) + 22 + 273 && mouseX <= (this.left - 78) + 41 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {

                if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(playerInventory.player.getUuidAsString()) || (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.isEmpty() || ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(""))) {
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner = playerInventory.player.getUuidAsString();
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty = true;
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic = false;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString(this.playerInventory.player.getUuidAsString() + "_Party")));
                        this.selectedSecurityOption = 1;
                        this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
            if (mouseX >= (this.left - 78) + 44 + 273 && mouseX <= (this.left - 78) + 63 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41 && button == 0) {
                if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                    if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals("") || ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.isEmpty() || ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(this.playerInventory.player.getUuidAsString())) {
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner = this.playerInventory.player.getUuidAsString();
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty = false;
                        ((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic = true;
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security_update"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeString(this.playerInventory.player.getUuidAsString() + "_Public")));
                        this.selectedSecurityOption = 2;
                        this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            if (!((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic) {
                if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty) {
                    DrawableUtils.drawCenteredString(this.minecraft.textRenderer, "\u00A7l" + new TranslatableComponent("Team stuff pending...").getText(), (this.width / 2), this.top + 50, ChatFormat.DARK_RED.getColor());
                    //TODO space race stuffs
                    return;
                }
                if ((!((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.isEmpty() || !((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals("")) && !((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(this.playerInventory.player.getUuidAsString())) {
                    DrawableUtils.drawCenteredString(this.minecraft.textRenderer, "\u00A7l" + new TranslatableComponent("ui.galacticraft-rewoven.not_your_machine").getText(), (this.width / 2), this.top + 50, ChatFormat.DARK_RED.getColor());
                    return;
                }
            }

        }
        super.render(int_1, int_2, float_1);
    }

    protected void drawTabTooltips(int mouseX, int mouseY) {
        if (!IS_REDSTONE_OPEN) {
            if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (22 + 3)) {
                this.renderTooltip("\u00A77" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config").getText(), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.left - 78) && mouseX <= (this.left - 78) + 19 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore").getText(), mouseX, mouseY);
            }
            if (mouseX >= (this.left - 78) + 22 && mouseX <= (this.left - 78) + 41 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off").getText(), mouseX, mouseY);
            }
            if (mouseX >= (this.left - 78) + 44 && mouseX <= (this.left - 78) + 63 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on").getText(), mouseX, mouseY);
            }
        }
        if (!IS_CONFIG_OPEN) {
            if (IS_REDSTONE_OPEN) {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 96 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 96)) {
                    //TODO this is just a marker - TODO remove this?
                }
            } else {
                if (mouseX >= this.left - REDSTONE_TAB_WIDTH && mouseX <= this.left && mouseY >= this.top + 26 && mouseY <= this.top + (REDSTONE_TAB_HEIGHT + 26)) {

                }
            }
        }
        if (!IS_SECURITY_OPEN) {
            if (mouseX >= this.left - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.left + 176 + 21 && mouseY >= this.top + 3 && mouseY <= this.top + (SECURITY_TAB_HEIGHT + 3)) {
                this.renderTooltip("\u00A77" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config").getText(), mouseX, mouseY);
            }
        } else {
            if (mouseX >= (this.left - 78) + 273 && mouseX <= (this.left - 78) + 19 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.private").getText(), mouseX, mouseY);
            }
            if (mouseX >= (this.left - 78) + 22 + 273 && mouseX <= (this.left - 78) + 41 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.space_race").getText(), 22), mouseX, mouseY);
            }
            if (mouseX >= (this.left - 78) + 44 + 273 && mouseX <= (this.left - 78) + 63 + 273 - 3 && mouseY >= this.top + 26 && mouseY <= this.top + 41) {
                this.renderTooltip("\u00A7f" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.public").getText(), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (this.world.getBlockEntity(pos) != null && this.world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
            if (!((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isPublic) {
                if (((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).isParty) {
                    //TODO space race stuffs
                    return false;
                }

                if ((!((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.isEmpty() || !((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals("")) && !((ConfigurableElectricMachineBlockEntity) this.world.getBlockEntity(pos)).owner.equals(this.playerInventory.player.getUuidAsString())) {
                    return false;
                }
            }

        }
        return this.checkTabsClick(double_1, double_2, int_1) || super.mouseClicked(double_1, double_2, int_1);
    }
}
