package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlockEntity;
import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CircuitFabricatorScreen extends ContainerScreen {

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.CIRCUIT_FABRICATOR_SCREEN));
    private static final Identifier CONFIG_TABS = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.MACHINE_CONFIG_TABS));

    private static final int ENERGY_X = 0;
    private static final int ENERGY_Y = 167;
    private static final int ENERGY_WIDTH = 12;
    private static final int ENERGY_HEIGHT = 40;
    private int energyDisplayX = 0;
    private int energyDisplayY = 0;

    private static final int CONFIG_TAB_X = 0;
    private static final int CONFIG_TAB_Y = 69;
    private static final int CONFIG_TAB_WIDTH = 22;
    private static final int CONFIG_TAB_HEIGHT = 22;

    BlockPos blockPos;
    private World world;


    public CircuitFabricatorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new CircuitFabricatorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, new TranslatableTextComponent("ui.galacticraft-rewoven.circuit_fabricator.name"));
    }

    @Override
    protected void drawBackground(float v, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.left;
        int topPos = this.top;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 9;

        //this.drawTexturedRect(...)
        this.blit(leftPos, topPos, 0, 0, this.containerWidth, this.containerHeight);
        this.drawEnergyBufferBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, I18n.translate("block.galacticraft-rewoven.coal_generator_block"), (this.width / 2), this.top + 5, TextFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawConfigTabs() {
        this.minecraft.getTextureManager().bindTexture(CONFIG_TABS);
        this.blit(this.left - CONFIG_TAB_WIDTH, this.top + 3, CONFIG_TAB_X, CONFIG_TAB_Y, CONFIG_TAB_WIDTH, CONFIG_TAB_HEIGHT);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = (float) ((CircuitFabricatorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy();
        float maxEnergy = (float) ((CircuitFabricatorBlockEntity) world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        //this.drawTexturedReact(...)
        this.blit(energyDisplayX, (energyDisplayY - (int) (ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int) (ENERGY_HEIGHT * energyScale));
    }
}
