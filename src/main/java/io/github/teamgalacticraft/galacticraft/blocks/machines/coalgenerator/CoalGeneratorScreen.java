package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.tgcutils.api.drawable.DrawableUtils;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorScreen extends ContainerScreen {

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.COAL_GENERATOR_SCREEN));

    private static final int ENERGY_X = 0;
    private static final int ENERGY_Y = 167;
    private static final int ENERGY_WIDTH = 12;
    private static final int ENERGY_HEIGHT = 40;
    private int energyDisplayX = 0;
    private int energyDisplayY = 0;

    BlockPos blockPos;
    private World world;

    public CoalGeneratorScreen(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(new CoalGeneratorContainer(syncId, blockPos, playerEntity), playerEntity.inventory, new TranslatableTextComponent("ui.galacticraft-fabric.coal_generator.name"));
        this.blockPos = blockPos;
        this.world = playerEntity.world;
    }

    @Override
    protected void drawBackground(float v, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.left;
        int topPos = this.top;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 9;

        //this.drawTexturedReact(...)
        this.blit(leftPos, topPos, 0, 0, this.containerWidth, this.containerHeight);
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        this.drawEnergyBufferBar();
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, I18n.translate("block.galacticraft-fabric.coal_generator_block"), (this.width / 2),this.top + 5, TextFormat.DARK_GRAY.getColor());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = (float)((CoalGeneratorBlockEntity)world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy();
        float maxEnergy = (float)((CoalGeneratorBlockEntity)world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        //this.drawTexturedReact(...)
        this.blit(energyDisplayX, (energyDisplayY - (int)(ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int)(ENERGY_HEIGHT * energyScale));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        if(mouseX >= energyDisplayX && mouseX <= energyDisplayX + ENERGY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + ENERGY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-fabric.machine.status", ((CoalGeneratorBlockEntity)world.getBlockEntity(blockPos)).status.toString()).getText());
            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-fabric.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(((CoalGeneratorBlockEntity)world.getBlockEntity(blockPos)).getEnergy().getCurrentEnergy())).getText());
            toolTipLines.add(new TranslatableTextComponent("ui.galacticraft-fabric.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(((CoalGeneratorBlockEntity)world.getBlockEntity(blockPos)).getEnergy().getMaxEnergy())).getText());

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
    }
}
