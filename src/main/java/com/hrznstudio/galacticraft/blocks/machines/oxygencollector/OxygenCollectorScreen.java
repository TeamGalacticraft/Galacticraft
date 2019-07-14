package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorScreen extends MachineContainerScreen<OxygenCollectorContainer> {
    public static final ContainerFactory<AbstractContainerScreen> FACTORY = createFactory(OxygenCollectorBlockEntity.class, OxygenCollectorScreen::new);

    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COLLECTOR_SCREEN));
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

    private int energyDisplayX = 0;
    private int energyDisplayY = 0;
    private int oxygenDisplayX = 0;
    private int oxygenDisplayY = 0;
    private World world;

    public OxygenCollectorScreen(int syncId, PlayerEntity playerEntity, OxygenCollectorBlockEntity blockEntity) {
        super(new OxygenCollectorContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, playerEntity.world, blockEntity.getPos(), new TranslatableText("ui.galacticraft-rewoven.oxygen_collector.name"));
        this.world = playerEntity.world;
        this.containerHeight = 181;
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
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_collector").asFormattedString(), (this.width / 2), this.top + 5, Formatting.DARK_GRAY.getColorValue());
//        DrawableUtils.drawCenteredString(this.minecraft.textRenderer,
//                new TranslatableText("ui.galacticraft-rewoven.machine.status",
//                        new TranslatableText("ui.galacticraft-rewoven.machinestatus.active").applyFormat(Formatting.GREEN)
//                ).asString(), this.left + 90, this.top + 28, Formatting.DARK_GRAY.getTextColor());
        String statusText = new TranslatableText("ui.galacticraft-rewoven.machine.status").asFormattedString();


        int statusX = this.left + 38;
        int statusY = this.top + 64;

        minecraft.textRenderer.draw(statusText, statusX, statusY, Formatting.DARK_GRAY.getColorValue());

        String status = container.blockEntity.status == CollectorStatus.COLLECTING ? "ui.galacticraft-rewoven.machinestatus.collecting"
                : container.blockEntity.status == CollectorStatus.NOT_ENOUGH_LEAVES ? "ui.galacticraft-rewoven.machinestatus.not_enough_leaves"
                : "ui.galacticraft-rewoven.machinestatus.inactive";
        minecraft.textRenderer.draw(new TranslatableText(status).asFormattedString(), statusX + minecraft.textRenderer.getStringWidth(statusText), statusY, container.blockEntity.status.getTextColor());

        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.machine.collecting", container.lastCollectAmount.get()).asFormattedString(), (this.width / 2) + 10, statusY + 12, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = container.energy.get();
        float maxEnergy = container.getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        float currentOxygen = container.oxygen.get();
        float maxOxygen = OxygenCollectorBlockEntity.MAX_OXYGEN;
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
//            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(Formatting.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(container.energy.get()).setStyle(new Style().setColor(Formatting.BLUE))).asFormattedString() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableText("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(container.getMaxEnergy())).asFormattedString() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= oxygenDisplayX && mouseX <= oxygenDisplayX + OVERLAY_WIDTH && mouseY >= oxygenDisplayY && mouseY <= oxygenDisplayY + OVERLAY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
//            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status", ((OxygenCollectorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(Formatting.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableText("ui.galacticraft-rewoven.machine.current_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(container.oxygen.get()).setStyle(new Style().setColor(Formatting.BLUE))).asFormattedString() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableText("ui.galacticraft-rewoven.machine.max_oxygen", GalacticraftEnergy.GALACTICRAFT_OXYGEN.getDisplayAmount(OxygenCollectorBlockEntity.MAX_OXYGEN)).asFormattedString() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.left - 22 && mouseX <= this.left && mouseY >= this.top + 21 && mouseY <= this.top + (22 + 21)) {
            this.renderTooltip("\u00A77" + new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").asFormattedString(), mouseX, mouseY);
        }
    }
}
