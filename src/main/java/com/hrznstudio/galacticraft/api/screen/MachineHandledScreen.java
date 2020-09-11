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
 *
 */

package com.hrznstudio.galacticraft.api.screen;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock.BlockFace;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public abstract class MachineHandledScreen<C extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> extends HandledScreen<C> {
    public static final Identifier TABS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
    public static final Identifier PANELS_TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_PANELS));
    public static final int SECURITY_PANEL_WIDTH = 99;
    public static final int SECURITY_PANEL_HEIGHT = 91;
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
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

    protected final BlockPos pos;
    protected final World world;
    public boolean IS_SECURITY_OPEN = false;
    private boolean IS_REDSTONE_OPEN = false;
    private boolean IS_CONFIG_OPEN = false;

    private final Map<BlockFace, SideOption> sideOptions = new EnumMap<>(BlockFace.class); //Front, Back, Right, Left, Up, Down

    public MachineHandledScreen(C screenHandler, PlayerInventory playerInventory, World world, BlockPos pos, Text textComponent) {
        super(screenHandler, playerInventory, textComponent);
        assert isAllowed();
        this.pos = pos;
        this.world = world;

        if (this.handler.blockEntity != null) {
            ConfigurableMachineBlockEntity entity = this.handler.blockEntity;

            ConfigurableMachineBlockEntity.SecurityInfo security = entity.getSecurity();
            if (!security.hasOwner()) {
                security.setOwner(this.playerInventory.player);
                security.setPublicity(ConfigurableMachineBlockEntity.SecurityInfo.Publicity.PRIVATE);
                sendSecurityUpdate(entity);
            } else if (security.getOwner().equals(playerInventory.player.getUuid())
                    && !security.getUsername().equals(playerInventory.player.getEntityName())) {
                security.setUsername(playerInventory.player.getEntityName());
                sendSecurityUpdate(entity);
            }

            for (BlockFace face : BlockFace.values()) {
                sideOptions.put(face, ((ConfigurableMachineBlock) world.getBlockState(pos).getBlock()).getOption(world.getBlockState(pos), face));
            }
        }
    }

    private void sendSecurityUpdate(ConfigurableMachineBlockEntity entity) {
        if (this.playerInventory.player.getUuid().equals(entity.getSecurity().getOwner()) || !entity.getSecurity().hasOwner()) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "security"),
                    new PacketByteBuf(Unpooled.buffer())
                            .writeBlockPos(pos)
                            .writeEnumConstant(entity.getSecurity().getPublicity())
            ));
        } else {
            Galacticraft.logger.error("Tried to send security update when not the owner!");
        }
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    private void sendRedstoneUpdate(ConfigurableMachineBlockEntity entity) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "redstone"),
                new PacketByteBuf(Unpooled.buffer())
                        .writeBlockPos(pos)
                        .writeEnumConstant(entity.getRedstoneState())
        ));
    }

    protected void drawEnergyBufferBar(MatrixStack stack, int energyX, int energyY) {
        float height = Constants.TextureCoordinates.OVERLAY_HEIGHT;
        float currentEnergy = handler.energy.get();
        float maxEnergy = handler.getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(stack, energyX, energyY, Constants.TextureCoordinates.ENERGY_DARK_X, Constants.TextureCoordinates.ENERGY_DARK_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) height);
        this.drawTexture(stack, energyX, (int) ((energyY - (height * energyScale)) + height), Constants.TextureCoordinates.ENERGY_LIGHT_X, Constants.TextureCoordinates.ENERGY_LIGHT_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) (height * energyScale));
    }

    private void sendSideConfigUpdate(BlockFace face, SideOption option, boolean numChange, boolean positive) {
        assert this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock;
        assert this.handler.blockEntity.validSideOptions().contains(option);
        if (((ConfigurableMachineBlockEntity) this.world.getBlockEntity(pos)).getSecurity().hasAccess(playerInventory.player)) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "side_config"),
                    new PacketByteBuf(new PacketByteBuf(Unpooled.buffer())
                            .writeBlockPos(pos)
                            .writeEnumConstant(face.toDirection(Direction.NORTH))
                            .writeEnumConstant(option)
                            .writeBoolean(numChange).writeBoolean(positive))
            ));
        } else {
            Galacticraft.logger.error("Tried to send side update when not trusted!");
        }
    }

    protected void drawEnergyTooltip(MatrixStack stack, int mouseX, int mouseY, int energyX, int energyY) {
        if (check(mouseX, mouseY, energyX, energyY, Constants.TextureCoordinates.OVERLAY_WIDTH, Constants.TextureCoordinates.OVERLAY_HEIGHT)) {
            List<Text> lines = new ArrayList<>();
            if (handler.blockEntity.getStatusForTooltip() != null) {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(this.handler.blockEntity.getStatusForTooltip().getText()));
            }

            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).append(GalacticraftEnergy.GALACTICRAFT_JOULES.getDisplayAmount(this.handler.energy.get()).setStyle(Style.EMPTY.withColor(Formatting.BLUE))));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_energy").setStyle(Style.EMPTY.withColor(Formatting.RED)).append(GalacticraftEnergy.GALACTICRAFT_JOULES.getDisplayAmount(this.handler.getMaxEnergy()).setStyle(Style.EMPTY.withColor(Formatting.BLUE))));
            lines.addAll(getEnergyTooltipLines());

            this.renderOrderedTooltip(stack, Lists.transform(lines, Text::asOrderedText), mouseX, mouseY);
        }
    }

    @NotNull
    protected Collection<? extends Text> getEnergyTooltipLines() {
        return new ArrayList<>();
    }

    public void drawConfigTabs(MatrixStack stack, int mouseX, int mouseY) {
        if (this.handler.blockEntity != null) {
            ConfigurableMachineBlockEntity entity = this.handler.blockEntity;

            ConfigurableMachineBlockEntity.SecurityInfo security = entity.getSecurity();
            DiffuseLighting.disable();
            if (IS_REDSTONE_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH, this.y + 3, REDSTONE_PANEL_X, REDSTONE_PANEL_Y, REDSTONE_PANEL_WIDTH, REDSTONE_PANEL_HEIGHT);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE), this.x - REDSTONE_PANEL_WIDTH + 6, this.y + 7);
                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.redstone_activation_config"), this.x - REDSTONE_PANEL_WIDTH + 23, this.y + 12, Formatting.GRAY.getColorValue());

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

                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.GUNPOWDER), this.x - REDSTONE_PANEL_WIDTH + 21, this.y + 26);
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43, this.y + 23, REDSTONE_TORCH_OFF_X, REDSTONE_TORCH_OFF_Y, ICONS_WIDTH, ICONS_HEIGHT);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE_TORCH), this.x - REDSTONE_PANEL_WIDTH + 65, this.y + 25 - 2);
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                this.drawTexture(stack, this.x - REDSTONE_TAB_WIDTH, this.y + 3, REDSTONE_TAB_X, REDSTONE_TAB_Y, REDSTONE_TAB_WIDTH, REDSTONE_TAB_HEIGHT);
                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(Items.REDSTONE), this.x - REDSTONE_TAB_WIDTH + 4, this.y + 6);
            }
            if (IS_CONFIG_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x - CONFIG_PANEL_WIDTH, this.y + 26, CONFIG_PANEL_X, CONFIG_PANEL_Y, CONFIG_PANEL_WIDTH, CONFIG_PANEL_HEIGHT);

                //Front, Back, Right, Left, top, bottom

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 3, getXForOption(sideOptions.get(BlockFace.TOP)), getYForOption(sideOptions.get(BlockFace.TOP)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //TOP - Top

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 21 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions.get(BlockFace.RIGHT)), getYForOption(sideOptions.get(BlockFace.RIGHT)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE LEFT - right
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions.get(BlockFace.FRONT)), getYForOption(sideOptions.get(BlockFace.FRONT)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE LEFT-CENTER - Front
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 65 - 6 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions.get(BlockFace.LEFT)), getYForOption(sideOptions.get(BlockFace.LEFT)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //MIDDLE RIGHT-CENTER - left
                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 87 - 9 - 5, this.y + 49 + 22 - 11 + 7 + 3, getXForOption(sideOptions.get(BlockFace.BACK)), getYForOption(sideOptions.get(BlockFace.BACK)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //RIGHT - Back

                this.drawTexture(stack, this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5, this.y + 49 + 36 + 3, getXForOption(sideOptions.get(BlockFace.BOTTOM)), getYForOption(sideOptions.get(BlockFace.BOTTOM)), BUTTONS_WIDTH, BUTTONS_HEIGHT); //BOTTOM - BOTTOM

                //TODO
                if (hasShiftDown()) {
                    if ((mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.FRONT).isItem())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.BACK).isItem())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.LEFT).isItem())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.RIGHT).isItem())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68 && sideOptions.get(BlockFace.TOP).isItem())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18 && sideOptions.get(BlockFace.BOTTOM).isItem())) {

                        for (Slot slot : handler.slots) {
                            if (slot.inventory != playerInventory) {
                                this.textRenderer.draw(stack, new LiteralText(String.valueOf(slot.id)), this.x + slot.x, this.y + slot.y, Formatting.BLUE.getColorValue());
                            }
                        }
                    } else if ((mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.FRONT).isFluid())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.BACK).isFluid())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.LEFT).isFluid())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.RIGHT).isFluid())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68 && sideOptions.get(BlockFace.TOP).isFluid())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18 && sideOptions.get(BlockFace.BOTTOM).isFluid())) {
                        //todo
                    } else if ((mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.FRONT).isOxygen())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.BACK).isOxygen())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.LEFT).isOxygen())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.RIGHT).isOxygen())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68 && sideOptions.get(BlockFace.TOP).isOxygen())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18 && sideOptions.get(BlockFace.BOTTOM).isOxygen())) {
                        //todo
                    } else if ((mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.FRONT).isEnergy())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.BACK).isEnergy())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.LEFT).isEnergy())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18 && sideOptions.get(BlockFace.RIGHT).isEnergy())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68 && sideOptions.get(BlockFace.TOP).isEnergy())
                            || (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18 && sideOptions.get(BlockFace.BOTTOM).isEnergy())) {
                        //todo
                    }
                }

                this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - REDSTONE_PANEL_WIDTH + 6, this.y + 29);
                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.side_config"), this.x - REDSTONE_PANEL_WIDTH + 23, this.y + 33, Formatting.GRAY.getColorValue());
            } else {
                this.client.getTextureManager().bindTexture(TABS_TEXTURE);
                if (!IS_REDSTONE_OPEN) {
                    this.drawTexture(stack, this.x - CONFIG_TAB_WIDTH, this.y + 26, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                    this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - CONFIG_TAB_WIDTH + 4, this.y + 26 + 3);
                } else {
                    this.drawTexture(stack, this.x - CONFIG_TAB_WIDTH, this.y + 96, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
                    this.client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(GalacticraftItems.STANDARD_WRENCH), this.x - CONFIG_TAB_WIDTH + 4, this.y + 96 + 3);
                }
            }
            if (IS_SECURITY_OPEN) {
                this.client.getTextureManager().bindTexture(PANELS_TEXTURE);
                this.drawTexture(stack, this.x + 176, this.y + 3, SECURITY_PANEL_X, SECURITY_PANEL_Y, SECURITY_PANEL_WIDTH, SECURITY_PANEL_HEIGHT);
                this.drawTexture(stack, this.x + 176 + 4, this.y + 6, LOCK_PARTY_X, LOCK_PARTY_Y + 2, ICONS_WIDTH, ICONS_HEIGHT);
                drawStringWithShadow(stack, this.client.textRenderer, I18n.translate("ui.galacticraft-rewoven.tabs.security_config"), this.x + 176 + 20, this.y + 12, Formatting.GRAY.getColorValue());

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

    private void updateSides(int btn, BlockState state, Property<SideOption> prop, BlockFace face) {
        if (!Screen.hasShiftDown()) {
            SideOption next;
            if (btn == 1) {
                next = state.get(prop).prevValidOption(handler.blockEntity);
            } else {
                next = state.get(prop).nextValidOption(handler.blockEntity);
            }
            this.world.setBlockState(pos, state.with(prop, next));
            sendSideConfigUpdate(face, next, false, false);
            handler.blockEntity.getSideConfigInfo().setFrontOption(next);
        } else {
            if (btn != 1) {
                handler.blockEntity.getSideConfigInfo().increment(face);
                sendSideConfigUpdate(face, state.get(prop), true, true);
            } else {
                handler.blockEntity.getSideConfigInfo().decrement(face);
                sendSideConfigUpdate(face, state.get(prop), true, false);
            }
        }
        playButtonSound();
    }

    public boolean checkTabsClick(MatrixStack stack, double mouseX, double mouseY, int button) {
        if (button != 3) {
            if (this.handler.blockEntity != null) {
                ConfigurableMachineBlockEntity entity = this.handler.blockEntity;
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
                        entity.setRedstoneState(ConfigurableMachineBlockEntity.RedstoneState.DISABLED);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 22 && mouseX <= this.x - 78 + 41 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstoneState(ConfigurableMachineBlockEntity.RedstoneState.OFF);
                        sendRedstoneUpdate(entity);
                        playButtonSound();
                        return true;
                    }
                    if (mouseX >= this.x - 78 + 44 && mouseX <= this.x - 78 + 63 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                        entity.setRedstoneState(ConfigurableMachineBlockEntity.RedstoneState.ON);
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
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.NORTH)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("north", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.FRONT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 + 19 + 19 && (mouseX + 48) - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.SOUTH)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("south", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.BACK);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.EAST)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("east", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.RIGHT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.WEST)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("west", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.LEFT);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.UP)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("up", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.TOP);
                            return true;
                        }
                    }

                    if (mouseX >= (this.x - REDSTONE_PANEL_WIDTH + 43) - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                        if (this.world.getBlockState(pos).getBlock() instanceof ConfigurableMachineBlock && !((ConfigurableMachineBlock) this.world.getBlockState(pos).getBlock()).disabledSides().contains(Direction.DOWN)) {
                            EnumProperty<SideOption> prop = EnumProperty.of("down", SideOption.class, handler.blockEntity.validSideOptions());
                            updateSides(button, this.world.getBlockState(pos), prop, BlockFace.BOTTOM);
                            return true;
                        }
                    }
                }

                if (!IS_SECURITY_OPEN) {
                    if (entity.getSecurity().isOwner(playerInventory.player) && mouseX >= this.x - SECURITY_TAB_WIDTH + 176 + 21 && mouseX <= this.x + 176 + 21 && mouseY >= this.y + 3 && mouseY <= this.y + SECURITY_TAB_HEIGHT + 3) {
                        IS_SECURITY_OPEN = true;
                        playButtonSound();
                        return true;
                    }
                } else if (entity.getSecurity().isOwner(playerInventory.player)) {
                    ConfigurableMachineBlockEntity.SecurityInfo security = entity.getSecurity();
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

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
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
                        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, "\u00A7l" + new TranslatableText("Team stuff pending...").asString(), (this.width / 2), this.y + 50, Formatting.DARK_RED.getColorValue());
                        return;
                    }
                default:
                    break;
            }
        }

        this.drawConfigTabs(stack, mouseX, mouseY);
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
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.north").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.FRONT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 + 19 && mouseX + 48 - 19 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {//Front, Back, Right, Left, Up, Down
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.south").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.BACK).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 - 19 && mouseX + 48 + 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.west").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.RIGHT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 + 19 && mouseX + 48 - 19 <= this.x && mouseY >= this.y + 49 + 3 + 18 && mouseY <= this.y + 68 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.east").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.LEFT).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 && mouseY <= this.y + 68) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.up").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.TOP).getFormattedName().asOrderedText()}), mouseX, mouseY);
            }

            if (mouseX >= this.x - REDSTONE_PANEL_WIDTH + 43 - 3 - 5 && mouseX + 48 <= this.x && mouseY >= this.y + 49 + 3 + 18 + 18 && mouseY <= this.y + 68 + 18 + 18) {
                this.renderOrderedTooltip(stack, Lists.asList(new TranslatableText("ui.galacticraft-rewoven.tabs.side_config.down").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).asOrderedText(), new OrderedText[]{this.sideOptions.get(BlockFace.BOTTOM).getFormattedName().asOrderedText()}), mouseX, mouseY);
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
                this.renderOrderedTooltip(stack, this.client.textRenderer.wrapLines(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.space_race", "[TEAM NAME]\u00a7r").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), 150), mouseX, mouseY);
            }
            if (mouseX >= (this.x - 78) + 44 + 273 && mouseX <= (this.x - 78) + 63 + 273 - 3 && mouseY >= this.y + 26 && mouseY <= this.y + 41) {
                this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.public").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), mouseX, mouseY);
            }
        }
    }

    public boolean isAllowed() {
        if (this.handler.blockEntity != null) {
            return handler.blockEntity.getSecurity().hasAccess(playerInventory.player);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAllowed()) {
            return this.checkTabsClick(new MatrixStack(), mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }

    private int getXForOption(SideOption option) {
        if (option == SideOption.DEFAULT) return 224;
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
            case ITEM_OUTPUT:
                return 68;
            case ITEM_INPUT:
                return 51;
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

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {

    }
}
