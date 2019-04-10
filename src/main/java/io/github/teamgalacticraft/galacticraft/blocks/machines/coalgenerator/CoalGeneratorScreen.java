package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
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

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorScreen extends ContainerScreen {

    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.COAL_GENERATOR_SCREEN));
    private static final Identifier CONFIG_TABS = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));

    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;
    private static final int ENERGY_DIMMED_WIDTH= Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_DIMMED_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private int energyDisplayX = 0;
    private int energyDisplayY = 0;

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    BlockPos blockPos;
    private World world;

    public CoalGeneratorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new CoalGeneratorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, new TranslatableTextComponent("ui.galacticraft-rewoven.coal_generator.name"));
        this.blockPos = blockPos;
        this.world = playerEntity.world;
        this.containerHeight = 176;
    }

    @Override
    protected void drawBackground(float v, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.left;
        int topPos = this.top;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 18;

        //this.drawTexturedRect(...)
        this.blit(leftPos, topPos - 10, 0, 0, this.containerWidth, this.containerHeight);
        this.drawEnergyBufferBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableTextComponent("block.galacticraft-rewoven.coal_generator_block").getText(), (this.width / 2), this.top + 5, TextFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawConfigTabs() {
        this.minecraft.getTextureManager().bindTexture(CONFIG_TABS);
        this.blit(this.left - CONFIG_TAB_WIDTH, this.top - 7, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = (float) ((CoalGeneratorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy();
        float maxEnergy = (float) ((CoalGeneratorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        //this.drawTexturedReact(...)
        this.minecraft.getTextureManager().bindTexture(OVERLAY);
        this.blit(energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, ENERGY_DIMMED_WIDTH, ENERGY_DIMMED_HEIGHT);
        this.blit(energyDisplayX, (energyDisplayY - (int) (ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int) (ENERGY_HEIGHT * energyScale));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + ENERGY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + ENERGY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-rewoven.machine.status", ((CoalGeneratorBlockEntity) world.getBlockEntity(blockPos)).status.toString()).setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText());
            toolTipLines.add("\u00A76" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(((CoalGeneratorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy()).setStyle(new Style().setColor(TextFormat.BLUE))).getFormattedText() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableTextComponent("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(((CoalGeneratorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy())).getFormattedText() + "\u00A7r");

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.left - 22 && mouseX <= this.left && mouseY >= this.top + 3 && mouseY <= this.top + (22 + 3)) {
            this.renderTooltip("\u00A77" + new TranslatableTextComponent("ui.galacticraft-rewoven.tabs.side_config").getText(), mouseX, mouseY);
        }
    }
}
