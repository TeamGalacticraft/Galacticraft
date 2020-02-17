/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.machines.rocketassembler;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketAssemblerScreen extends AbstractContainerScreen<RocketAssemblerContainer> {

    public static final int SELECTED_TAB_X = 324;
    public static final int SELECTED_TAB_Y = 4;
    public static final int SELECTED_TAB_WIDTH = 32;
    public static final int SELECTED_TAB_HEIGHT = 25;

    public static final int TAB_X = 325;
    public static final int TAB_Y = 30;
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 25;

    public static final int RED_BOX_X = 324;
    public static final int RED_BOX_Y = 56;
    public static final int RED_BOX_WIDTH = 24;
    public static final int RED_BOX_HEIGHT = 24;

    public static final int GREEN_BOX_X = 324;
    public static final int GREEN_BOX_Y = 81;
    public static final int GREEN_BOX_WIDTH = 24;
    public static final int GREEN_BOX_HEIGHT = 24;

    public static final int ARROW_X = 324;
    public static final int ARROW_Y = 106;
    public static final int ARROW_WIDTH = 6;
    public static final int ARROW_HEIGHT = 10;

    public static final int SELECTED_ARROW_X = 324;
    public static final int SELECTED_ARROW_Y = 117;
    public static final int SELECTED_ARROW_WIDTH = 6;
    public static final int SELECTED_ARROW_HEIGHT = 10;

    public static final int BACK_ARROW_X = ARROW_X + ARROW_WIDTH;
    public static final int BACK_ARROW_Y = ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_ARROW_WIDTH = -ARROW_WIDTH;
    public static final int BACK_ARROW_HEIGHT = -ARROW_HEIGHT;

    public static final int BACK_SELECTED_ARROW_X = SELECTED_ARROW_X + ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_Y = SELECTED_ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_SELECTED_ARROW_WIDTH = -SELECTED_ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_HEIGHT = -SELECTED_ARROW_HEIGHT;

    public static final int BACK_ARROW_OFFSET_X = ARROW_WIDTH;
    public static final int BACK_ARROW_OFFSET_Y = ARROW_HEIGHT;

    public static final ContainerFactory<AbstractContainerScreen> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof RocketAssemblerBlockEntity) {
            return new RocketAssemblerScreen(syncId, player, (RocketAssemblerBlockEntity) be);
        } else {
            return null;
        }
    };

    protected final Identifier TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ROCKET_ASSEMBLER_SCREEN));
    private World world;
    private BlockEntity blockEntity;
    private boolean landerTabSelected = false;

    private RocketAssemblerScreen(int syncId, PlayerEntity playerEntity, RocketAssemblerBlockEntity blockEntity) {
        super(new RocketAssemblerContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name"));
        this.containerWidth = 323;
        this.containerHeight = 175;
        this.world = playerEntity.world;
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        blit(this.left, this.top, 0, 0, containerWidth, containerHeight);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        GuiLighting.disable();

        if (landerTabSelected) {
            
        }
    }

    @Override
    protected void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void blit(int x, int y, int u, int v, int width, int height) {
        blit(x, y, u, v, width, height, 512, 256);
    }
}
