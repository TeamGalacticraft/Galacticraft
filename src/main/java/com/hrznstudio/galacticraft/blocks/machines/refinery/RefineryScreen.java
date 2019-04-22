package com.hrznstudio.galacticraft.blocks.machines.refinery;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RefineryScreen extends ContainerScreen {

    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COLLECTOR_SCREEN));
    private static final Identifier CONFIG_TABS = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));

    private static final int OVERLAY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int OVERLAY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;

    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    private final RefineryBlockEntity refinery;
    BlockPos blockPos;

    private int energyDisplayX = 0;
    private int energyDisplayY = 0;
    private int oxygenDisplayX = 0;
    private int oxygenDisplayY = 0;

    private World world;

    public RefineryScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new OxygenCollectorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, new TranslatableTextComponent("ui.galacticraft-rewoven.oxygen_collector.name"));
        this.blockPos = blockPos;
        this.world = playerEntity.world;
        this.containerHeight = 181;
        this.refinery = (RefineryBlockEntity) world.getBlockEntity(blockPos);
    }

    @Override
    protected void drawBackground(float v, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.left;
        int topPos = this.top;

        energyDisplayX = leftPos + 11;
        energyDisplayY = topPos + 18;
        oxygenDisplayX = leftPos + 33;
        oxygenDisplayY = topPos + 18;

        //this.drawTexturedRect(...)
        this.blit(leftPos, topPos, 0, 0, this.containerWidth, this.containerHeight);
        this.drawEnergyBufferBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableTextComponent("block.galacticraft-rewoven.refinery").getText(), (this.width / 2), this.top + 5, TextFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawConfigTabs() {
        this.minecraft.getTextureManager().bindTexture(CONFIG_TABS);
        this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 3, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = (float) ((RefineryBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy();
        float maxEnergy = (float) ((RefineryBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        this.minecraft.getTextureManager().bindTexture(OVERLAY);
        this.blit(energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.blit(energyDisplayX, (energyDisplayY - (int) (OVERLAY_HEIGHT * energyScale)) + OVERLAY_HEIGHT, ENERGY_X, ENERGY_Y, OVERLAY_WIDTH, (int) (OVERLAY_HEIGHT * energyScale));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + OVERLAY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + OVERLAY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
//            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy()).setStyle(new Style().setColor(TextFormat.BLUE))).getFormattedText() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy())).getFormattedText() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= oxygenDisplayX && mouseX <= oxygenDisplayX + OVERLAY_WIDTH && mouseY >= oxygenDisplayY && mouseY <= oxygenDisplayY + OVERLAY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
//            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.current_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getCurrentEnergy()).setStyle(new Style().setColor(TextFormat.BLUE))).getFormattedText() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.max_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getMaxEnergy())).getFormattedText() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.left - 22 && mouseX <= this.left && mouseY >= this.top + 21 && mouseY <= this.top + (22 + 21)) {
            this.renderTooltip("\u00A77" + new TranslatableTextComponent("ui.galacticraft-rewoven.tabs.side_config").getText(), mouseX, mouseY);
        }
    }
}
