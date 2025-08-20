package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.entity.BubbleAirlockControllerBlockEntity;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityAccessPayload;
import dev.galacticraft.mod.screen.BubbleAirlockControllerMenu;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BubbleAirlockControllerScreen extends MachineScreen<BubbleAirlockControllerBlockEntity, BubbleAirlockControllerMenu> {
    private IconButton publicBtn, teamBtn, privateBtn;

    private static final ResourceLocation MACHINELIB_PANELS = ResourceLocation.fromNamespaceAndPath("machinelib", "textures/gui/machine_panels.png");
    private static final int TEX_W = 256, TEX_H = 256;

    private static final int BTN_U = 0;
    private static final int BTN_V_NORMAL   = 196;
    private static final int BTN_V_HOVER    = 216;
    private static final int BTN_V_SELECTED = 236;
    private static final int BTN_W = 20, BTN_H = 20;

    private static final int PUB_U = 208, PUB_V = 49,  PUB_W = 15, PUB_H = 15;
    private static final int TEAM_U = 210, TEAM_V = 71, TEAM_W = 12, TEAM_H = 14;
    private static final int PRIV_U = 231, PRIV_V = 49,  PRIV_W = 10, PRIV_H = 14;

    private static final int ACCESS_BTN_SIZE = 20;
    private static final int ACCESS_BTN_GAP = 6;

    private static class IconButton extends AbstractButton {
        @FunctionalInterface interface PressHandler { void onPress(IconButton b); }
        private final ResourceLocation texture;
        private final int iconU, iconV, iconW, iconH;
        private final PressHandler handler;
        private boolean selected;

        IconButton(int x, int y, int size, ResourceLocation texture, int iconU, int iconV, int iconW, int iconH, PressHandler handler, Component tooltipText) {
            super(x, y, size, size, Component.empty());
            this.texture = texture; this.iconU = iconU; this.iconV = iconV; this.iconW = iconW; this.iconH = iconH;
            this.handler = handler;
            if (!tooltipText.getString().isEmpty()) this.setTooltip(Tooltip.create(tooltipText));
        }
        void setSelected(boolean sel) { this.selected = sel; }
        @Override public void onPress() { handler.onPress(this); }
        @Override protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
            final int v = this.selected ? BTN_V_SELECTED : (this.isHovered() ? BTN_V_HOVER : BTN_V_NORMAL);
            g.blit(texture, getX(), getY(), BTN_U, v, BTN_W, BTN_H, TEX_W, TEX_H);
            int ix = getX() + (this.width  - iconW) / 2 + 1;
            int iy = getY() + (this.height - iconH) / 2;
            g.blit(texture, ix, iy, iconU, iconV, iconW, iconH, TEX_W, TEX_H);
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput out) {}
    }

    public BubbleAirlockControllerScreen(BubbleAirlockControllerMenu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.AIRLOCK_CONTROLLER_SCREEN);
    }

    @Override protected void init() {
        super.init();
        this.imageHeight = 171;
        this.titleLabelX = 90;

        ProximityAccess initial = this.menu.access != null ? this.menu.access : ProximityAccess.PUBLIC;

        this.publicBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, PUB_U,  PUB_V,  PUB_W,  PUB_H, b -> {
            this.menu.access = ProximityAccess.PUBLIC;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PUBLIC));
            updateSelection(ProximityAccess.PUBLIC);
        }, Component.literal("Public"));

        this.teamBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, TEAM_U, TEAM_V, TEAM_W, TEAM_H, b -> {
            this.menu.access = ProximityAccess.TEAM;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.TEAM));
            updateSelection(ProximityAccess.TEAM);
        }, Component.literal("Team"));

        this.privateBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, PRIV_U, PRIV_V, PRIV_W, PRIV_H, b -> {
            this.menu.access = ProximityAccess.PRIVATE;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PRIVATE));
            updateSelection(ProximityAccess.PRIVATE);
        }, Component.literal("Private"));

        this.addRenderableWidget(publicBtn);
        this.addRenderableWidget(teamBtn);
        this.addRenderableWidget(privateBtn);
        updateSelection(initial);
        layoutAccessButtons();
    }

    private void updateSelection(ProximityAccess a) {
        publicBtn.setSelected(a == ProximityAccess.PUBLIC);
        teamBtn.setSelected(a == ProximityAccess.TEAM);
        privateBtn.setSelected(a == ProximityAccess.PRIVATE);
    }

    @Override protected void repositionElements() {
        super.repositionElements();
        layoutAccessButtons();
    }

    private void layoutAccessButtons() {
        int centerX = this.leftPos + (this.imageWidth / 2);
        int centerY = this.topPos + 40;
        int total = ACCESS_BTN_SIZE * 3 + ACCESS_BTN_GAP * 2;
        int startX = centerX - total / 2;
        int y = centerY - ACCESS_BTN_SIZE / 2;
        this.publicBtn.setPosition(startX, y);
        this.teamBtn.setPosition(startX + ACCESS_BTN_SIZE + ACCESS_BTN_GAP, y);
        this.privateBtn.setPosition(startX + (ACCESS_BTN_SIZE + ACCESS_BTN_GAP) * 2, y);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        AirlockState enabled = this.menu.state;
        Component label; int color;
        if (enabled.equals(AirlockState.ALL))      { label = Component.translatable(Translations.Ui.AIRLOCK_ENABLED);  color = ChatFormatting.DARK_GREEN.getColor(); }
        else if (enabled.equals(AirlockState.PARTIAL)) { label = Component.translatable(Translations.Ui.AIRLOCK_PARTIAL); color = ChatFormatting.DARK_PURPLE.getColor(); }
        else                                       { label = Component.translatable(Translations.Ui.AIRLOCK_DISABLED); color = ChatFormatting.RED.getColor(); }
        drawCenteredString(g, this.font, label, this.leftPos + 90, this.topPos + 18, color, false);
    }

    private void drawCenteredString(GuiGraphics g, Font font, Component text, int centerX, int y, int color, boolean shadow) {
        g.drawString(font, text, centerX - font.width(text) / 2, y, color, shadow);
    }

    @Override protected void drawTitle(@NotNull GuiGraphics graphics) {
        drawCenteredString(graphics, this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }
}