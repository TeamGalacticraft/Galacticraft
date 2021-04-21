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

package dev.galacticraft.mod.api.screen;

import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.misc.Reference;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.api.block.ConfiguredMachineFace;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.client.gui.widget.machine.AbstractWidget;
import dev.galacticraft.mod.client.model.MachineBakedModel;
import dev.galacticraft.mod.item.GalacticraftItems;
import dev.galacticraft.mod.screen.MachineScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.screen.tank.Tank;
import dev.galacticraft.mod.util.ColorUtil;
import dev.galacticraft.mod.util.DrawableUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public abstract class MachineHandledScreen<C extends MachineScreenHandler<? extends MachineBlockEntity>> extends HandledScreen<C> implements DrawableUtil {
    private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
    private static final ItemStack UNLIT_TORCH = new ItemStack(GalacticraftItems.UNLIT_TORCH);
    private static final ItemStack REDSTONE_TORCH = new ItemStack(Items.REDSTONE_TORCH);
    private static final ItemStack WRENCH = new ItemStack(GalacticraftItems.STANDARD_WRENCH);
    private static final ItemStack ALUMINUM_WIRE = new ItemStack(GalacticraftBlock.ALUMINUM_WIRE);

    public static final int PANEL_WIDTH = 100;
    public static final int PANEL_HEIGHT = 93;
    public static final int TAB_WIDTH = 22;
    public static final int TAB_HEIGHT = 22;

    private static final int SPACING = 4;

    private static final int BUTTON_U = 0;
    private static final int BUTTON_V = 208;
    private static final int BUTTON_HOVERED_V = 224;
    private static final int BUTTON_PRESSED_V = 240;
    private static final int BUTTON_WIDTH = 16;
    private static final int BUTTON_HEIGHT = 16;

    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    private static final int ICON_LOCK_PRIVATE_U = 221;
    private static final int ICON_LOCK_PRIVATE_V = 47;

    private static final int ICON_LOCK_PARTY_U = 204;
    private static final int ICON_LOCK_PARTY_V = 64;

    private static final int ICON_LOCK_PUBLIC_U = 204;
    private static final int ICON_LOCK_PUBLIC_V = 47;

    private static final int TAB_REDSTONE_U = 203;
    private static final int TAB_REDSTONE_V = 0;

    private static final int TAB_CONFIG_U = 203;
    private static final int TAB_CONFIG_V = 23;

    private static final int TAB_STATS_U = 226;
    private static final int TAB_STATS_V = 0;

    private static final int TAB_SECURITY_U = 226;
    private static final int TAB_SECURITY_V = 23;

    private static final int PANEL_REDSTONE_U = 0;
    private static final int PANEL_REDSTONE_V = 0;

    private static final int PANEL_CONFIG_U = 0;
    private static final int PANEL_CONFIG_V = 93;

    private static final int PANEL_STATS_U = 101;
    private static final int PANEL_STATS_V = 0;

    private static final int PANEL_SECURITY_U = 101;
    private static final int PANEL_SECURITY_V = 93;

    public static final int PANEL_ICON_X = 3;
    public static final int PANEL_ICON_Y = 3;

    public static final int PANEL_TITLE_X = 19;
    public static final int PANEL_TITLE_Y = 7;

    private static final int REDSTONE_IGNORE_X = 18;
    private static final int REDSTONE_IGNORE_Y = 30;
    
    private static final int REDSTONE_LOW_X = 43;
    private static final int REDSTONE_LOW_Y = 30;

    private static final int REDSTONE_HIGH_X = 68;
    private static final int REDSTONE_HIGH_Y = 30;

    private static final int SECURITY_PUBLIC_X = 18;
    private static final int SECURITY_PUBLIC_Y = 30;

    private static final int SECURITY_TEAM_X = 43;
    private static final int SECURITY_TEAM_Y = 30;

    private static final int SECURITY_PRIVATE_X = 68;
    private static final int SECURITY_PRIVATE_Y = 30;

    private static final int TOP_FACE_X = 33;
    private static final int TOP_FACE_Y = 26;

    private static final int LEFT_FACE_X = 52;
    private static final int LEFT_FACE_Y = 45;

    private static final int FRONT_FACE_X = 33;
    private static final int FRONT_FACE_Y = 45;

    private static final int RIGHT_FACE_X = 14;
    private static final int RIGHT_FACE_Y = 45;

    private static final int BACK_FACE_X = 71;
    private static final int BACK_FACE_Y = 45;

    private static final int BOTTOM_FACE_X = 33;
    private static final int BOTTOM_FACE_Y = 64;

    private static final int OWNER_FACE_X = 6;
    private static final int OWNER_FACE_Y = 20;

    private static final int OWNER_FACE_WIDTH = 32;
    private static final int OWNER_FACE_HEIGHT = 32;
    private static final int PANEL_UPPER_HEIGHT = 20;

    private static final int REDSTONE_STATE_TEXT_X = 11;
    private static final int REDSTONE_STATE_TEXT_Y = 53;

    private static final int REDSTONE_STATUS_TEXT_X = 11;
    private static final int REDSTONE_STATUS_TEXT_Y = 57; //add fontheight

    private static final int SECURITY_STATE_TEXT_X = 11;
    private static final int SECURITY_STATE_TEXT_Y = 53;

    protected final BlockPos pos;
    protected final World world;
    protected Tank focusedTank = null;

    private final List<AbstractWidget> widgets = new LinkedList<>();

    private @NotNull Identifier ownerSkin = new Identifier("textures/entity/steve.png");
    private final MachineBakedModel.SpriteProvider spriteProvider;
    private final List<Text> tooltipCache = new LinkedList<>();

    public MachineHandledScreen(C handler, PlayerInventory playerInventory, World world, BlockPos pos, Text textComponent) {
        super(handler, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        this.spriteProvider = MachineBakedModel.SPRITE_PROVIDERS.getOrDefault(handler.machine.getCachedState() == null ? world.getBlockState(pos).getBlock() : handler.machine.getCachedState().getBlock(), MachineBakedModel.SpriteProvider.DEFAULT);

        MinecraftClient.getInstance().getSkinProvider().loadSkin(handler.machine.getSecurity().getOwner(), (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN && identifier != null) {
                MachineHandledScreen.this.ownerSkin = identifier;
            }
        }, true);
    }

    @NotNull
    protected Collection<? extends Text> getEnergyTooltipLines() {
        return Collections.emptyList();
    }

    public void drawConfigTabs(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DiffuseLighting.disable();
        assert this.client != null;
        if (this.handler.machine != null) {
            final MachineBlockEntity machine = this.handler.machine;
            boolean secondary = false;
            this.client.getTextureManager().bindTexture(Constant.ScreenTexture.MACHINE_CONFIG_PANELS);
            for (Tab tab : Tab.values()) { // 0, 1, 2, 3
                if (secondary) matrices.translate(0, SPACING, 0);
                this.drawTexture(matrices, this.x + (tab.isLeft() ? tab.isOpen() ? -PANEL_WIDTH : -22 : this.backgroundWidth), this.y + (secondary ? Tab.values()[tab.ordinal() - 1].isOpen() ? PANEL_HEIGHT : TAB_HEIGHT : 0) + SPACING, tab.getU(), tab.getV(), tab.isOpen() ? PANEL_WIDTH : TAB_WIDTH, tab.isOpen() ? PANEL_HEIGHT : TAB_HEIGHT);
                if (secondary) matrices.translate(0, -SPACING, 0);
                secondary = !secondary;
            }
            matrices.push();
            matrices.translate(this.x, this.y, 0);

            if (Tab.REDSTONE.isOpen()) {
                matrices.push();
                matrices.translate(-PANEL_WIDTH, SPACING, 0);
                this.drawButton(matrices, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, mouseX + PANEL_WIDTH - this.x, mouseY - SPACING - this.y, delta, machine.getRedstoneInteraction() == RedstoneInteractionType.IGNORE);
                this.drawButton(matrices, REDSTONE_LOW_X, REDSTONE_LOW_Y, mouseX + PANEL_WIDTH - this.x, mouseY - SPACING - this.y, delta, machine.getRedstoneInteraction() == RedstoneInteractionType.LOW);
                this.drawButton(matrices, REDSTONE_HIGH_X, REDSTONE_HIGH_Y, mouseX + PANEL_WIDTH - this.x, mouseY - SPACING - this.y, delta, machine.getRedstoneInteraction() == RedstoneInteractionType.HIGH);
                this.renderItemIcon(matrices, PANEL_ICON_X, PANEL_ICON_Y, REDSTONE);
                this.renderItemIcon(matrices, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, REDSTONE);
                this.renderItemIcon(matrices, REDSTONE_LOW_X, REDSTONE_LOW_Y - 2, UNLIT_TORCH);
                this.renderItemIcon(matrices, REDSTONE_HIGH_X, REDSTONE_HIGH_Y - 2, REDSTONE_TORCH);

                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.redstone")
                        .setStyle(Constant.Text.GRAY_STYLE), PANEL_TITLE_X, PANEL_TITLE_Y, ColorUtil.WHITE);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.redstone.state",
                        machine.getRedstoneInteraction().getName()).setStyle(Constant.Text.DARK_GRAY_STYLE), REDSTONE_STATE_TEXT_X, REDSTONE_STATE_TEXT_Y, ColorUtil.WHITE);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.redstone.status",
                        !machine.disabled() ? new TranslatableText("ui.galacticraft.machine.redstone.status.enabled").setStyle(Constant.Text.GREEN_STYLE)
                                : new TranslatableText("ui.galacticraft.machine.redstone.status.disabled").setStyle(Constant.Text.DARK_RED_STYLE))
                        .setStyle(Constant.Text.DARK_GRAY_STYLE), REDSTONE_STATUS_TEXT_X, REDSTONE_STATUS_TEXT_Y + this.textRenderer.fontHeight, ColorUtil.WHITE);

                matrices.pop();
            }
            if (Tab.CONFIGURATION.isOpen()) {
                matrices.push();
                matrices.translate(-PANEL_WIDTH, TAB_HEIGHT + SPACING + SPACING, 0);
                this.renderItemIcon(matrices, PANEL_ICON_X, PANEL_ICON_Y, WRENCH);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.configuration")
                        .setStyle(Constant.Text.GRAY_STYLE), PANEL_TITLE_X, PANEL_TITLE_Y, ColorUtil.WHITE);

                this.client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
                this.drawMachineFace(matrices, TOP_FACE_X, TOP_FACE_Y, machine, BlockFace.TOP);
                this.drawMachineFace(matrices, LEFT_FACE_X, LEFT_FACE_Y, machine, BlockFace.LEFT);
                this.drawMachineFace(matrices, FRONT_FACE_X, FRONT_FACE_Y, machine, BlockFace.FRONT);
                this.drawMachineFace(matrices, RIGHT_FACE_X, RIGHT_FACE_Y, machine, BlockFace.RIGHT);
                this.drawMachineFace(matrices, BACK_FACE_X, BACK_FACE_Y, machine, BlockFace.BACK);
                this.drawMachineFace(matrices, BOTTOM_FACE_X, BOTTOM_FACE_Y, machine, BlockFace.BOTTOM);
                matrices.pop();
            }
            if (Tab.STATS.isOpen()) {
                matrices.push();
                matrices.translate(this.backgroundWidth, SPACING, 0);
                this.renderItemIcon(matrices, PANEL_ICON_X, PANEL_ICON_Y, ALUMINUM_WIRE);
                this.client.getTextureManager().bindTexture(this.ownerSkin);
                drawTexture(matrices, OWNER_FACE_X, OWNER_FACE_Y, OWNER_FACE_WIDTH, OWNER_FACE_HEIGHT, 8, 8, 8, 8, 64, 64);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.stats")
                        .setStyle(Constant.Text.GREEN_STYLE), PANEL_TITLE_X, PANEL_TITLE_Y, ColorUtil.WHITE);
                List<OrderedText> text = this.textRenderer.wrapLines(new TranslatableText((machine.getCachedState() != null ? machine.getCachedState()
                        : this.handler.machine.getCachedState()).getBlock().getTranslationKey()), 64);
                int offsetY = 0;
                for (OrderedText orderedText : text) {
                    this.textRenderer.draw(matrices, orderedText, 40, 22 + offsetY, ColorUtil.WHITE);
                    offsetY += this.textRenderer.fontHeight + 2;
                }
//                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.machine.stats.gjt", "N/A")
//                        .setStyle(Constants.Text.GRAY_STYLE), 11, 54, ColorUtils.WHITE);
                //                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.machine.stats.todo", "N/A")
//                        .setStyle(Constants.Text.GRAY_STYLE), 11, 54, ColorUtils.WHITE);
                matrices.pop();
            }

            if (Tab.SECURITY.isOpen()) {
                matrices.push();
                matrices.translate(this.backgroundWidth, TAB_HEIGHT + SPACING + SPACING, 0);
                this.client.getTextureManager().bindTexture(Constant.ScreenTexture.MACHINE_CONFIG_PANELS);
                this.drawTexture(matrices, PANEL_ICON_X, PANEL_ICON_Y, ICON_LOCK_PRIVATE_U, ICON_LOCK_PRIVATE_V, ICON_WIDTH, ICON_HEIGHT);

                this.drawButton(matrices, SECURITY_PUBLIC_X, SECURITY_PUBLIC_Y, mouseX - this.backgroundWidth - this.x, mouseY - (TAB_HEIGHT + SPACING + SPACING) - this.y, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.PUBLIC || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawButton(matrices, SECURITY_TEAM_X, SECURITY_TEAM_Y, mouseX - this.backgroundWidth - this.x, mouseY - (TAB_HEIGHT + SPACING + SPACING) - this.y, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.TEAM || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawButton(matrices, SECURITY_PRIVATE_X, SECURITY_PRIVATE_Y, mouseX - this.backgroundWidth - this.x, mouseY - (TAB_HEIGHT + SPACING + SPACING) - this.y, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.PRIVATE || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawTexture(matrices, SECURITY_PUBLIC_X, SECURITY_PUBLIC_Y, ICON_LOCK_PRIVATE_U, ICON_LOCK_PRIVATE_V, ICON_WIDTH, ICON_HEIGHT);
                this.drawTexture(matrices, SECURITY_TEAM_X, SECURITY_TEAM_Y, ICON_LOCK_PARTY_U, ICON_LOCK_PARTY_V, ICON_WIDTH, ICON_HEIGHT);
                this.drawTexture(matrices, SECURITY_PRIVATE_X, SECURITY_PRIVATE_Y, ICON_LOCK_PUBLIC_U, ICON_LOCK_PUBLIC_V, ICON_WIDTH, ICON_HEIGHT);

                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.security")
                        .setStyle(Constant.Text.GRAY_STYLE), PANEL_TITLE_X, PANEL_TITLE_Y, ColorUtil.WHITE);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.security.state",
                        machine.getSecurity().getAccessibility().getName()).setStyle(Constant.Text.GRAY_STYLE), SECURITY_STATE_TEXT_X, SECURITY_STATE_TEXT_Y, ColorUtil.WHITE);
//                assert machine.getSecurity().getOwner() != null;
//                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft.machine.security.owned_by", machine.getSecurity().getOwner().getName())
//                        .setStyle(Constants.Text.GRAY_STYLE), SECURITY_STATE_TEXT_X, SECURITY_STATE_TEXT_Y + this.textRenderer.fontHeight + 4, ColorUtils.WHITE);

                matrices.pop();
            }
            matrices.pop();
        }
    }

    private void drawMachineFace(MatrixStack matrices, int x, int y, MachineBlockEntity machine, BlockFace face) {
        drawSprite(matrices, x, y, 0, 16, 16, MachineBakedModel.getSprite(face, machine, null, this.spriteProvider, machine.getConfiguration().getSideConfiguration().get(face).getAutomationType()));
    }

    private void renderItemIcon(MatrixStack matrices, int x, int y, ItemStack stack) {
        assert this.client != null;
        BakedModel model = this.itemRenderer.getHeldItemModel(stack, this.world, this.playerInventory.player);
        matrices.push();
        AbstractTexture blockAtlasTexture = this.client.getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        assert blockAtlasTexture != null;
        blockAtlasTexture.setFilter(false, false);
        blockAtlasTexture.bindTexture();
        matrices.translate(x + 8, y + 8, 100.0F + this.getZOffset());
        matrices.scale(16, -16, 16);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        this.itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrices.pop();
    }

    public void drawButton(MatrixStack matrices, int x, int y, double mouseX, double mouseY, float delta, boolean pressed) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(Constant.ScreenTexture.MACHINE_CONFIG_PANELS);
        if (pressed) {
            this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_PRESSED_V, BUTTON_WIDTH, BUTTON_HEIGHT);
            return;
        }
        if (this.check(mouseX, mouseY, x, y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_HOVERED_V, BUTTON_WIDTH, BUTTON_HEIGHT);
        } else {
            this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_V, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    public boolean checkTabsClick(double mouseX, double mouseY, int button) {
        assert this.client != null;
        assert this.handler.machine != null;

        final double mX = mouseX, mY = mouseY;
        final MachineBlockEntity machine = this.handler.machine;
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        if (Tab.REDSTONE.isOpen()) {
            mouseX += PANEL_WIDTH;
            mouseY -= SPACING;
            if (this.check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_UPPER_HEIGHT)) {
                Tab.REDSTONE.click();
                return true;
            }
            if (this.check(mouseX, mouseY, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.setRedstone(RedstoneInteractionType.IGNORE);
                this.playButtonSound();
                return true;
            }
            if (this.check(mouseX, mouseY, REDSTONE_LOW_X, REDSTONE_LOW_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.setRedstone(RedstoneInteractionType.LOW);
                this.playButtonSound();
                return true;
            }
            if (this.check(mouseX, mouseY, REDSTONE_HIGH_X, REDSTONE_HIGH_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.setRedstone(RedstoneInteractionType.HIGH);
                this.playButtonSound();
                return true;
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (this.check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT)) {
                    return true;
                }
            }
        } else {
            mouseX += TAB_WIDTH;
            mouseY -= SPACING;
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                Tab.REDSTONE.click();
                return true;
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX += PANEL_WIDTH;
            mouseY -= TAB_HEIGHT + SPACING + SPACING;
            if (this.check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_UPPER_HEIGHT)) {
                Tab.CONFIGURATION.click();
                return true;
            }
            if (button >= GLFW.GLFW_MOUSE_BUTTON_LEFT && button <= GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                if (this.check(mouseX, mouseY, TOP_FACE_X, TOP_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.TOP, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, LEFT_FACE_X, LEFT_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.LEFT, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, FRONT_FACE_X, FRONT_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.FRONT, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, RIGHT_FACE_X, RIGHT_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.RIGHT, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, BACK_FACE_X, BACK_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.BACK, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, BOTTOM_FACE_X, BOTTOM_FACE_Y, 16, 16)) {
                    SideConfigurationAction.VALUES[button].update(this.client.player, machine, BlockFace.BOTTOM, Screen.hasShiftDown(), Screen.hasControlDown());
                    this.playButtonSound();
                    return true;
                }
            }
        } else {
            mouseX += TAB_WIDTH;
            if (Tab.REDSTONE.isOpen()) {
                mouseY -= PANEL_HEIGHT + SPACING + SPACING;
            } else {
                mouseY -= TAB_HEIGHT + SPACING + SPACING;
            }
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                Tab.CONFIGURATION.click();
                return true;
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        mouseX -= this.backgroundWidth;
        mouseY -= SPACING;
        if (Tab.STATS.isOpen()) {
            if (this.check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_UPPER_HEIGHT)) {
                Tab.STATS.click();
                return true;
            }
        } else {
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                Tab.STATS.click();
                return true;
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        mouseX -= this.backgroundWidth;
        if (Tab.SECURITY.isOpen()) {
            mouseY -= TAB_HEIGHT + SPACING + SPACING;
            if (this.check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_UPPER_HEIGHT)) {
                Tab.SECURITY.click();
                return true;
            }

            if (machine.getSecurity().isOwner(this.playerInventory.player)) {
                if (this.check(mouseX, mouseY, SECURITY_PRIVATE_X, SECURITY_PRIVATE_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.setAccessibility(SecurityInfo.Accessibility.PRIVATE);
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, SECURITY_TEAM_X, SECURITY_TEAM_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.setAccessibility(SecurityInfo.Accessibility.TEAM);
                    this.playButtonSound();
                    return true;
                }
                if (this.check(mouseX, mouseY, SECURITY_PUBLIC_X, SECURITY_PUBLIC_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.setAccessibility(SecurityInfo.Accessibility.PUBLIC);
                    this.playButtonSound();
                    return true;
                }
            }
        } else {
            if (Tab.STATS.isOpen()) {
                mouseY -= PANEL_HEIGHT + SPACING + SPACING;
            } else {
                mouseY -= TAB_HEIGHT + SPACING + SPACING;
            }
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                Tab.SECURITY.click();
            }
        }
        return false;
    }

    protected void setAccessibility(SecurityInfo.Accessibility accessibility) {
        this.handler.machine.getSecurity().setAccessibility(accessibility);
        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "security_config"), new PacketByteBuf(ByteBufAllocator.DEFAULT.buffer((Long.SIZE / Byte.SIZE) + 1).writeByte(accessibility.ordinal())));
    }

    protected void setRedstone(RedstoneInteractionType redstone) {
        this.handler.machine.setRedstone(redstone);
        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "redstone_config"), new PacketByteBuf(ByteBufAllocator.DEFAULT.buffer((Long.SIZE / Byte.SIZE) + 1).writeByte(redstone.ordinal())));
    }

    protected void drawTabTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        final MachineBlockEntity machine = this.handler.machine;
        final int mX = mouseX, mY = mouseY;
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        if (Tab.REDSTONE.isOpen()) {
            mouseX += PANEL_WIDTH;
            mouseY -= SPACING;
            if (this.check(mouseX, mouseY, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneInteractionType.IGNORE.getName(), mX, mY);
            }
            if (this.check(mouseX, mouseY, REDSTONE_LOW_X, REDSTONE_LOW_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneInteractionType.LOW.getName(), mX, mY);
            }
            if (this.check(mouseX, mouseY, REDSTONE_HIGH_X, REDSTONE_HIGH_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneInteractionType.HIGH.getName(), mX, mY);
            }
        } else {
            mouseX += TAB_WIDTH;
            mouseY -= SPACING;
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.redstone").setStyle(Constant.Text.RED_STYLE), mX, mY);
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX += PANEL_WIDTH;
            mouseY -= TAB_HEIGHT + SPACING + SPACING;
            if (this.check(mouseX, mouseY, TOP_FACE_X, TOP_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.TOP, mX, mY);
            }
            if (this.check(mouseX, mouseY, LEFT_FACE_X, LEFT_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.LEFT, mX, mY);
            }
            if (this.check(mouseX, mouseY, FRONT_FACE_X, FRONT_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.FRONT, mX, mY);
            }
            if (this.check(mouseX, mouseY, RIGHT_FACE_X, RIGHT_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.RIGHT, mX, mY);
            }
            if (this.check(mouseX, mouseY, BACK_FACE_X, BACK_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.BACK, mX, mY);
            }
            if (this.check(mouseX, mouseY, BOTTOM_FACE_X, BOTTOM_FACE_Y, 16, 16)) {
                this.renderFaceTooltip(matrices, BlockFace.BOTTOM, mX, mY);
            }
        } else {
            mouseX += TAB_WIDTH;
            if (Tab.REDSTONE.isOpen()) {
                mouseY -= PANEL_HEIGHT + SPACING;
            } else {
                mouseY -= TAB_HEIGHT + SPACING;
            }
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.configuration").setStyle(Constant.Text.BLUE_STYLE), mX, mY);
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        mouseX -= this.backgroundWidth;
        mouseY -= SPACING;
        if (Tab.STATS.isOpen()) {
            if (this.check(mouseX, mouseY, OWNER_FACE_X, OWNER_FACE_Y, OWNER_FACE_WIDTH, OWNER_FACE_HEIGHT)) {
                assert machine.getSecurity().getOwner() != null;
                this.renderTooltip(matrices, new LiteralText(machine.getSecurity().getOwner().getName()), mX, mY);
            }
        } else {
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.stats").setStyle(Constant.Text.YELLOW_STYLE), mX, mY);
            }
        }
        mouseX = mX - this.x;
        mouseY = mY - this.y;
        if (Tab.SECURITY.isOpen()) {
            mouseX -= this.backgroundWidth;
            mouseY -= TAB_HEIGHT + SPACING + SPACING;

            if (machine.getSecurity().isOwner(this.playerInventory.player)) {
                if (this.check(mouseX, mouseY, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.PRIVATE.getName(), mX, mY);
                }
                if (this.check(mouseX, mouseY, REDSTONE_LOW_X, REDSTONE_LOW_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.TEAM.getName(), mX, mY);
                }
                if (this.check(mouseX, mouseY, REDSTONE_HIGH_X, REDSTONE_HIGH_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.PUBLIC.getName(), mX, mY);
                }
            } else {
                if (this.check(mouseX, mouseY, REDSTONE_IGNORE_X, REDSTONE_IGNORE_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    || check(mouseX, mouseY, REDSTONE_LOW_X, REDSTONE_LOW_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                    || check(mouseX, mouseY, REDSTONE_HIGH_X, REDSTONE_HIGH_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.security.access_denied"), mX, mY);
                }
            }
        } else {
            mouseX -= this.backgroundWidth;
            if (Tab.STATS.isOpen()) {
                mouseY -= PANEL_HEIGHT + SPACING + SPACING;
            } else {
                mouseY -= TAB_HEIGHT + SPACING + SPACING;
            }
            if (this.check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.security").setStyle(Constant.Text.BLUE_STYLE), mX, mY);
            }
        }
    }

    protected void renderFaceTooltip(MatrixStack matrices, BlockFace face, int mouseX, int mouseY) {
        tooltipCache.add(face.getName());
        ConfiguredMachineFace configuredFace = this.handler.machine.getConfiguration().getSideConfiguration().get(face);
        if (configuredFace.getAutomationType() != AutomationType.NONE) {
            tooltipCache.add(configuredFace.getAutomationType().getFormattedName());
        }
        if (configuredFace.getMatching() != null) {
            if (configuredFace.getMatching().left().isPresent()) {
                tooltipCache.add(new TranslatableText("ui.galacticraft.machine.configuration.matches", new LiteralText(String.valueOf(configuredFace.getMatching().left().get())).setStyle(Constant.Text.AQUA_STYLE)).setStyle(Constant.Text.GRAY_STYLE));
            } else {
                tooltipCache.add(new TranslatableText("ui.galacticraft.machine.configuration.matches", configuredFace.getMatching().right().get().getName()).setStyle(Constant.Text.GRAY_STYLE));
            }
        }
        this.renderTooltip(matrices, tooltipCache, mouseX, mouseY);

        tooltipCache.clear();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        if (this.handler.machine == null || !this.handler.machine.getSecurity().hasAccess(handler.player)) {
            this.onClose();
            return;
        }

        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.translate(this.x, this.y, 0);
        for (AbstractWidget widget : this.widgets) {
            widget.render(matrices, mouseX - this.x, mouseY - this.y, delta);
        }
        matrices.pop();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.drawConfigTabs(matrices, mouseX, mouseY, delta);
        this.drawTanks(matrices, mouseX, mouseY, delta);
        this.handleSlotHighlight(matrices, mouseX, mouseY, delta);
    }

    protected void drawTanks(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        Int2IntArrayMap color = getTankColor(matrices, mouseX, mouseY);
        color.defaultReturnValue(-1);
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        this.focusedTank = null;
        for (Tank tank : this.handler.tanks) {
            tank.render(matrices, this.client, this.world, this.pos, mouseX - this.x, mouseY - this.y, color.get(tank.index) != -1, color);
            if (tank.isHoveredOverTank(mouseX - this.x, mouseY - this.y)) {
                this.focusedTank = tank;
                tank.renderHighlight(matrices, this.client, this.world, this.pos, mouseX - this.x, mouseY - this.y);
            }
        }
        for (Tank tank : this.handler.tanks) {
            tank.drawTooltip(matrices, this.client, this.world, this.pos, mouseX - this.x, mouseY - this.y);
        }
        matrices.pop();

        color = getItemColor(matrices, mouseX, mouseY);;
        color.defaultReturnValue(-1);
        for (Slot slot : this.handler.slots) {
            if (slot.inventory instanceof InventoryFixedWrapper && ((InventoryFixedWrapper) slot.inventory).getWrapped() == this.handler.machine.getInventory()) {
                if (color.get(slot.index) != -1) {
                    RenderSystem.disableDepthTest();
                    int c = color.get(slot.index);
                    int r = (c >> 16 & 255);
                    int g = (c >> 8 & 255);
                    int b = (c & 255);
                    c = 80; c <<= 8; c += r; c <<= 8; c += g; c <<= 8; c += b;
                    RenderSystem.colorMask(true, true, true, false);
                    fillGradient(matrices, this.x + slot.x, this.y + slot.y, this.x + slot.x + 16, this.y + slot.y + 16, c, c);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }

    protected Int2IntArrayMap getTankColor(MatrixStack matrices, int mouseX, int mouseY) {
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX -= this.x - PANEL_WIDTH;
            mouseY -= this.y + TAB_HEIGHT + SPACING;
            Int2IntArrayMap out = new Int2IntArrayMap();
            if (this.check(mouseX, mouseY, TOP_FACE_X, TOP_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.TOP).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.TOP).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            if (this.check(mouseX, mouseY, LEFT_FACE_X, LEFT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.LEFT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.LEFT).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            if (this.check(mouseX, mouseY, FRONT_FACE_X, FRONT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.FRONT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.FRONT).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            if (this.check(mouseX, mouseY, RIGHT_FACE_X, RIGHT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.RIGHT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.RIGHT).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            if (this.check(mouseX, mouseY, BACK_FACE_X, BACK_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.BACK).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BACK).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            if (this.check(mouseX, mouseY, BOTTOM_FACE_X, BOTTOM_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.BOTTOM).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BOTTOM).getMatching(this.handler.machine.getFluidInv()));
                groupFluid(out, list);
            }
            return out;
        }
        return new Int2IntArrayMap();
    }

    protected Int2IntArrayMap getItemColor(MatrixStack matrices, int mouseX, int mouseY) {
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX -= this.x - PANEL_WIDTH;
            mouseY -= this.y + TAB_HEIGHT + SPACING;
            Int2IntArrayMap out = new Int2IntArrayMap();
            if (this.check(mouseX, mouseY, TOP_FACE_X, TOP_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.TOP).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.TOP).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            if (this.check(mouseX, mouseY, LEFT_FACE_X, LEFT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.LEFT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.LEFT).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            if (this.check(mouseX, mouseY, FRONT_FACE_X, FRONT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.FRONT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.FRONT).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            if (this.check(mouseX, mouseY, RIGHT_FACE_X, RIGHT_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.RIGHT).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.RIGHT).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            if (this.check(mouseX, mouseY, BACK_FACE_X, BACK_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.BACK).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BACK).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            if (this.check(mouseX, mouseY, BOTTOM_FACE_X, BOTTOM_FACE_Y, 16, 16) && this.handler.machine.getIOConfig().get(BlockFace.BOTTOM).getMatching() != null) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BOTTOM).getMatching(this.handler.machine.getInventory()));
                groupItem(out, list);
            }
            return out;
        }
        return new Int2IntArrayMap();
    }

    private void groupFluid(Int2IntMap out, IntList list) {
        for (Tank tank : this.handler.tanks) {
            if (list.contains(tank.index)) {
                out.put(tank.index, this.handler.machine.getFluidInv().getTypes()[tank.index].getColor().getRgb());
            }
        }
    }

    private void groupItem(Int2IntMap out, IntList list) {
        for (Slot slot : this.handler.slots) {
            if (list.contains(slot.index)) {
                out.put(slot.index, this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb());
            }
        }
    }

    protected void handleSlotHighlight(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX -= PANEL_WIDTH + this.x;
            mouseY -= this.y + TAB_HEIGHT + SPACING;
            if (this.check(mouseX, mouseY, TOP_FACE_X, TOP_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.TOP).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
            if (this.check(mouseX, mouseY, LEFT_FACE_X, LEFT_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.LEFT).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
            if (this.check(mouseX, mouseY, FRONT_FACE_X, FRONT_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.FRONT).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
            if (this.check(mouseX, mouseY, RIGHT_FACE_X, RIGHT_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.RIGHT).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
            if (this.check(mouseX, mouseY, BACK_FACE_X, BACK_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BACK).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
            if (this.check(mouseX, mouseY, BOTTOM_FACE_X, BOTTOM_FACE_Y, 16, 16)) {
                IntList list = new IntArrayList(this.handler.machine.getIOConfig().get(BlockFace.BOTTOM).getMatching(this.handler.machine.getInventory()));
                groupStack(matrices, list);
            }
        }
    }

    private void groupStack(MatrixStack matrices, IntList list) {
        for (Slot slot : this.handler.slots) {
            if (list.contains(slot.index)) {
                drawSlotOverlay(matrices, slot);
            }
        }
    }

    private void drawSlotOverlay(MatrixStack matrices, Slot slot) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder,
                slot.x - 1, slot.y - 1,
                slot.x - 1, slot.y + 17,
                this.getZOffset(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb());
        tessellator.draw();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder,
                slot.x - 1, slot.y + 17,
                slot.x + 17, slot.y - 1,
                this.getZOffset(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb());
        tessellator.draw();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder,
                slot.x + 17, slot.y + 17,
                slot.x + 17, slot.y - 1,
                this.getZOffset(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb());
        tessellator.draw();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder,
                slot.x + 17, slot.y - 1,
                slot.x - 1, slot.y - 1,
                this.getZOffset(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb(),
                this.handler.machine.getInventory().getTypes()[slot.index].getColor().getRgb());
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAllowed()) {
            for (AbstractWidget widget : widgets) {
                widget.mouseClicked(mouseX - this.x, mouseY - this.y, button);
            }
            boolean tankMod = false;
            if (this.focusedTank != null && button == 0) {
                tankMod = this.focusedTank.acceptStack(new Reference<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        return MachineHandledScreen.this.playerInventory.getCursorStack();
                    }

                    @Override
                    public boolean set(ItemStack value) {
                        MachineHandledScreen.this.playerInventory.setCursorStack(value);
                        return true;
                    }

                    @Override
                    public boolean isValid(ItemStack value) {
                        return true;
                    }
                }, new FixedInventoryVanillaWrapper(this.playerInventory).getInsertable());
            }
            return this.checkTabsClick(mouseX, mouseY, button) | super.mouseClicked(mouseX, mouseY, button) | tankMod;
        } else {
            return false;
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (isAllowed()) {
            super.drawMouseoverTooltip(matrices, mouseX, mouseY);
            drawTabTooltips(matrices, mouseX, mouseY);
            matrices.push();
            matrices.translate(this.x, this.y, 0);
            for (AbstractWidget widget : widgets) {
                widget.drawMouseoverTooltip(matrices, mouseX - this.x, mouseY - this.y);
            }
            matrices.pop();
        }
    }

    public boolean isAllowed() {
        if (this.handler.machine != null) {
            return handler.machine.getSecurity().hasAccess(playerInventory.player);
        }
        return false;
    }

    private void playButtonSound() {
        assert this.client != null;
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

    @Nullable
    @Contract("null -> null")
    public <T extends AbstractWidget> T addWidget(@Nullable T widget) {
        if (widget == null) return null;
        this.widgets.add(widget);
        return widget;
    }

    public static void drawTextureColor(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth, int red, int green, int blue, int alpha) {
        drawTextureColor(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight, red, green, blue, alpha);
    }

    private static void drawTextureColor(MatrixStack matrices, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight, int red, int green, int blue, int alpha) {
        drawTexturedQuadColor(matrices.peek().getModel(), x0, x1, y0, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight, red, green, blue, alpha);
    }

    private static void drawTexturedQuadColor(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, int red, int green, int blue, int alwha) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).color(red, green, blue, 255).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).color(red, green, blue, 255).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).color(red, green, blue, 255).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).color(red, green, blue, 255).texture(u0, v0).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    public enum Tab {
        REDSTONE(TAB_REDSTONE_U, TAB_REDSTONE_V, PANEL_REDSTONE_U, PANEL_REDSTONE_V, true),
        CONFIGURATION(TAB_CONFIG_U, TAB_CONFIG_V, PANEL_CONFIG_U, PANEL_CONFIG_V, true),
        STATS(TAB_STATS_U, TAB_STATS_V, PANEL_STATS_U, PANEL_STATS_V, false),
        SECURITY(TAB_SECURITY_U, TAB_SECURITY_V, PANEL_SECURITY_U, PANEL_SECURITY_V, false);

        private final int tabU;
        private final int tabV;
        private final int panelU;
        private final int panelV;
        private final boolean left;
        private boolean open = false;

        Tab(int tabU, int tabV, int panelU, int panelV, boolean left) {
            this.tabU = tabU;
            this.tabV = tabV;
            this.panelU = panelU;
            this.panelV = panelV;
            this.left = left;
        }

        public int getU() {
            return open ? this.panelU : this.tabU;
        }

        public boolean isLeft() {
            return left;
        }

        public int getV() {
            return open ? this.panelV : this.tabV;
        }

        public boolean isOpen() {
            return open;
        }

        public void click() {
            this.open = !this.open;
            if (this.open) {
                Tab.values()[this.ordinal() + 1 - this.ordinal() % 2 * 2].open = false;
            }
        }
    }

    private enum SideConfigurationAction {
        CHANGE_TYPE((player, machine, face, back, reset) -> {
            ConfiguredMachineFace sideOption = machine.getIOConfig().get(face);
            if (reset) {
                sideOption.setOption(AutomationType.NONE);
                return;
            }
            List<AutomationType> types = ConfiguredMachineFace.getValidTypes(machine);
            int i = types.indexOf(sideOption.getAutomationType());
            if (!back) {
                if (++i == types.size()) i = 0;
            } else {
                if (i == 0) i = types.size();
                i--;
            }
            sideOption.setOption(types.get(i));
            sideOption.setMatching(null);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByte(face.ordinal()).writeBoolean(false).writeByte(sideOption.getAutomationType().ordinal());
            ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "side_config"), buf);

        }), //LEFT
        CHANGE_MATCH((player, machine, face, back, reset) -> {
            ConfiguredMachineFace sideOption = machine.getIOConfig().get(face);
            if (sideOption.getAutomationType().isEnergy() || sideOption.getAutomationType() == AutomationType.NONE) return;
            if (reset) {
                sideOption.setMatching(null);
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeByte(face.ordinal()).writeBoolean(true).writeBoolean(false).writeInt(-1);
                ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "side_config"), buf);
                return;
            }
            SlotType[] slotTypes = sideOption.getAutomationType().getAutomatable(machine).getTypes();
            slotTypes = Arrays.copyOf(slotTypes, slotTypes.length);
            int s = 0;
            for (int i = 0; i < slotTypes.length; i++) {
                if (!slotTypes[i].getType().canPassAs(sideOption.getAutomationType())) {
                    slotTypes[i] = null;
                    s++;
                }
            }
            if (s > 0) {
                SlotType[] tmp = new SlotType[slotTypes.length - s];
                s = 0;
                for (int i = 0; i < slotTypes.length; i++) {
                    if (slotTypes[i] == null) {
                        s++;
                    } else {
                        tmp[i - s] = slotTypes[i];
                    }
                }
                slotTypes = tmp;
            }
            int i = 0;
            if (sideOption.getMatching() != null && sideOption.getMatching().right().isPresent()) {
                SlotType slotType = sideOption.getMatching().right().get();
                for (; i < slotTypes.length; i++) {
                    if (slotTypes[i] == slotType) break;
                }
                if (back) i--;
                else i++;
            }

            if (i == slotTypes.length) i = 0;
            if (i == -1) i = slotTypes.length - 1;
            sideOption.setMatching(Either.right(slotTypes[i]));
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByte(face.ordinal()).writeBoolean(true).writeBoolean(false).writeInt(SlotType.SLOT_TYPES.getRawId(slotTypes[i]));
            ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "side_config"), buf);
        }), //RIGHT
        CHANGE_MATCH_SLOT((player, machine, face, back, reset) -> {
            ConfiguredMachineFace sideOption = machine.getIOConfig().get(face);
            if (sideOption.getAutomationType().isEnergy() || sideOption.getAutomationType() == AutomationType.NONE) return;
            if (reset) {
                sideOption.setMatching(null);
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeByte(face.ordinal()).writeBoolean(true).writeBoolean(true).writeInt(-1);
                ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "side_config"), buf);
                return;
            }
            int i = 0;
            IntList list = null;
            if (sideOption.getMatching() != null && sideOption.getMatching().left().isPresent()) {
                i = sideOption.getMatching().left().get();
                sideOption.setMatching(null);
                list = new IntArrayList(sideOption.getMatching(sideOption.getAutomationType().getAutomatable(machine)));
                i = list.indexOf(i);
            }
            if (list == null)
                list = new IntArrayList(sideOption.getMatching(sideOption.getAutomationType().getAutomatable(machine)));

            if (!back) {
                if (++i == list.size()) i = 0;
            } else {
                if (i == 0) i = list.size();
                i--;
            }
            sideOption.setMatching(Either.left(list.getInt(i)));

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeByte(face.ordinal());
            buf.writeBoolean(true).writeBoolean(true);
            buf.writeInt(i);
            ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "side_config"), buf);
        }); //MID

        static final SideConfigurationAction[] VALUES = SideConfigurationAction.values();
        private final SecurityUpdater updater;

        SideConfigurationAction(SecurityUpdater updater) {
            this.updater = updater;
        }

        void update(ClientPlayerEntity player, MachineBlockEntity machine, BlockFace face, boolean back, boolean reset) {
            updater.update(player, machine, face, back, reset);
        }

        @FunctionalInterface
        interface SecurityUpdater {
            void update(ClientPlayerEntity player, MachineBlockEntity machine, BlockFace face, boolean back, boolean reset);
        }
    }
}
