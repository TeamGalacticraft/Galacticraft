package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.screen.ElectricFurnaceScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElectricFurnaceScreen extends MachineHandledScreen<ElectricFurnaceScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ELECTRIC_FURNACE_SCREEN));

    private static final int ARROW_X = 78;
    private static final int ARROW_Y = 24;

    private static final int LIT_ARROW_X = 176;
    private static final int LIT_ARROW_Y = 0;

    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;

    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler screenHandler, PlayerInventory playerInventory, Text title) {
        super(screenHandler, playerInventory, screenHandler.blockEntity.getWorld(), screenHandler.blockEntity.getPos(), title);
        addWidget(new CapacitorWidget(screenHandler.blockEntity.getCapacitor(), 8, 29, 48, this::getEnergyTooltipLines, screenHandler.blockEntity::getStatus));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (handler.blockEntity.maxCookTime != 0 && handler.blockEntity.cookTime != 0) {
            double scale = ((double)handler.blockEntity.cookTime) / ((double)handler.blockEntity.maxCookTime);

            this.drawTexture(matrices, this.x + ARROW_X, this.y + ARROW_Y, LIT_ARROW_X, LIT_ARROW_Y, (int) (((double)ARROW_WIDTH) * scale), ARROW_HEIGHT);
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }
}
