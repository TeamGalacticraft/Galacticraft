package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorScreen extends MachineContainerScreen {
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COLLECTOR_SCREEN));
    private static final Identifier CONFIG_TABS = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));
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
    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;
    private final OxygenCollectorBlockEntity collector;
    BlockPos blockPos;
    private int energyDisplayX = 0;
    private int energyDisplayY = 0;
    private int oxygenDisplayX = 0;
    private int oxygenDisplayY = 0;
    private World world;

    public OxygenCollectorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new OxygenCollectorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, playerEntity.world, blockPos, new TranslatableComponent("ui.galacticraft-rewoven.oxygen_collector.name"));
        this.blockPos = blockPos;
        this.world = playerEntity.world;
        this.containerHeight = 181;
        this.collector = (OxygenCollectorBlockEntity) world.getBlockEntity(blockPos);
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
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableComponent("block.galacticraft-rewoven.oxygen_collector").getText(), (this.width / 2), this.top + 5, ChatFormat.DARK_GRAY.getColor());
//        DrawableUtils.drawCenteredString(this.minecraft.textRenderer,
//                new TranslatableComponent("ui.galacticraft-rewoven.machine.status",
//                        new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.active").applyFormat(ChatFormat.GREEN)
//                ).getText(), this.left + 90, this.top + 28, ChatFormat.DARK_GRAY.getTextColor());
        String statusText = new TranslatableComponent("ui.galacticraft-rewoven.machine.status").getText();


        int statusX = this.left + 38;
        int statusY = this.top + 64;

        minecraft.textRenderer.draw(statusText, statusX, statusY, ChatFormat.DARK_GRAY.getColor());

        String status = collector.status == CollectorStatus.COLLECTING ? "ui.galacticraft-rewoven.machinestatus.collecting"
                : collector.status == CollectorStatus.NOT_ENOUGH_LEAVES ? "ui.galacticraft-rewoven.machinestatus.not_enough_leaves"
                : "ui.galacticraft-rewoven.machinestatus.inactive";
        minecraft.textRenderer.draw(new TranslatableComponent(status).getText(), statusX + minecraft.textRenderer.getStringWidth(statusText), statusY, collector.status.getTextColor());

        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableComponent("ui.galacticraft-rewoven.machine.collecting", collector.lastCollectAmount).getText(), (this.width / 2) + 10, statusY + 12, ChatFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = (float) ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy();
        float maxEnergy = (float) ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        float currentOxygen = (float) ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getCurrentEnergy();
        float maxOxygen = (float) ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getMaxEnergy();
        float oxygenScale = (currentOxygen / maxOxygen);

        //this.drawTexturedReact(...)
//        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.minecraft.getTextureManager().bindTexture(OVERLAY);
        this.blit(energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.blit(energyDisplayX, (energyDisplayY - (int) (OVERLAY_HEIGHT * energyScale)) + OVERLAY_HEIGHT, ENERGY_X, ENERGY_Y, OVERLAY_WIDTH, (int) (OVERLAY_HEIGHT * energyScale));

        this.blit(oxygenDisplayX, oxygenDisplayY, OXYGEN_DIMMED_X, OXYGEN_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.blit(oxygenDisplayX, (oxygenDisplayY - (int) (OVERLAY_HEIGHT * oxygenScale)) + OVERLAY_HEIGHT, OXYGEN_X, OXYGEN_Y, OVERLAY_WIDTH, (int) (OVERLAY_HEIGHT * oxygenScale));
//        this.blit((energyDisplayY - (int) (OVERLAY_WIDTH * energyScale)) + OVERLAY_WIDTH, energyDisplayY, ENERGY_X, ENERGY_Y, (int) (OVERLAY_WIDTH * energyScale), (OVERLAY_HEIGHT));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + OVERLAY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + OVERLAY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
//            toolTipLines.add(new TranslatableComponent("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(ChatFormat.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableComponent("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy()).setStyle(new Style().setColor(ChatFormat.BLUE))).getFormattedText() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableComponent("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy())).getFormattedText() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= oxygenDisplayX && mouseX <= oxygenDisplayX + OVERLAY_WIDTH && mouseY >= oxygenDisplayY && mouseY <= oxygenDisplayY + OVERLAY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
//            toolTipLines.add(new TranslatableComponent("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(ChatFormat.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableComponent("ui.galacticraft-rewoven.machine.current_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getCurrentEnergy()).setStyle(new Style().setColor(ChatFormat.BLUE))).getFormattedText() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableComponent("ui.galacticraft-rewoven.machine.max_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).getOxygen().getMaxEnergy())).getFormattedText() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.left - 22 && mouseX <= this.left && mouseY >= this.top + 21 && mouseY <= this.top + (22 + 21)) {
            this.renderTooltip("\u00A77" + new TranslatableComponent("ui.galacticraft-rewoven.tabs.side_config").getText(), mouseX, mouseY);
        }
    }
}
