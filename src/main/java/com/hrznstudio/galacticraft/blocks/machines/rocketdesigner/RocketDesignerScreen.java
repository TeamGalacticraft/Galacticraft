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

package com.hrznstudio.galacticraft.blocks.machines.rocketdesigner;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.DefaultParts;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketDesignerScreen extends AbstractContainerScreen<RocketDesignerContainer> {

    public static final ContainerFactory<AbstractContainerScreen> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof RocketDesignerBlockEntity) {
            return new RocketDesignerScreen(syncId, player, (RocketDesignerBlockEntity) be);
        } else {
            return null;
        }
    };
    protected final Identifier TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ROCKET_DESIGNER_SCREEN));
    protected BlockPos blockPos;
    protected World world;
    protected RocketDesignerBlockEntity be;

    private static final int WHITE_BOX_X = 192;
    private static final int WHITE_BOX_Y = 166;

    private static final int RED_BOX_X = WHITE_BOX_X + 25;
    private static final int RED_BOX_Y = WHITE_BOX_Y + 25;

    private static final int GREEN_BOX_X = RED_BOX_X + 25;
    private static final int GREEN_BOX_Y = RED_BOX_Y + 25;

    private static final int BOX_WIDTH = 24;
    private static final int BOX_HEIGHT = 24;

    private static final int SELECTED_TAB_X = 0;
    private static final int SELECTED_TAB_Y = 166;

    private static final int DEFAULT_TAB_X = 33;
    private static final int DEFAULT_TAB_Y = 166;

    private static final int SELECTED_TAB_WIDTH = 32;
    private static final int SELECTED_TAB_HEIGHT = 26;

    private static final int DEFAULT_TAB_WIDTH = 28;
    private static final int DEFAULT_TAB_HEIGHT = 25;

    private static final int ARROW_X = 178;
    private static final int ARROW_Y = 166;

    private static final int ARROW_WIDTH = 6;
    private static final int ARROW_HEIGHT = 11;

    private static final int HOVERED_ARROW_X = 185;
    private static final int HOVERED_ARROW_Y = 166;

    private static final int BACK_ARROW_X = ARROW_X - ARROW_WIDTH;
    private static final int BACK_ARROW_Y = ARROW_Y - ARROW_HEIGHT;

    private static final int BACK_ARROW_WIDTH = -ARROW_WIDTH;
    private static final int BACK_ARROW_HEIGHT = -ARROW_HEIGHT;

    private static final int BACK_HOVERED_ARROW_X = HOVERED_ARROW_X - ARROW_WIDTH;
    private static final int BACK_HOVERED_ARROW_Y = HOVERED_ARROW_Y - ARROW_HEIGHT;

    private int page = 0;
    private int maxPage = 0;
    private RocketPartType OPEN_TAB = RocketPartType.CONE; //NULL IS SEARCH TAB

    private RocketDesignerScreen(int syncId, PlayerEntity playerEntity, RocketDesignerBlockEntity blockEntity) {
        super(new RocketDesignerContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name"));
        this.containerWidth = 323;
        this.containerHeight = 164;
        this.world = playerEntity.world;
        this.be = blockEntity;
    }

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderBackground();

        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        for (int i = 0; i < RocketPartType.values().length; i++) {
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            if (RocketPartType.values()[i] != OPEN_TAB) {
                blit(this.left - 27, this.top + 3 + (27 * i), DEFAULT_TAB_X, DEFAULT_TAB_Y, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT);
            } else {
                blit(this.left - 31 + 2, this.top + 3 + (27 * i), SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            }
            this.itemRenderer.renderGuiItem(new ItemStack(DefaultParts.getPartForType(RocketPartType.values()[i]).getBlockToRender()), (this.left - 31) + 13, this.top + 3 + ((27) * i) + 4);
        }

        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        if (OPEN_TAB == null) {
            blit(this.left - 31 + 2, this.top + 3 + ((27) * 5), SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
        } else {
            blit(this.left - 27, this.top + 3 + ((27) * 5), DEFAULT_TAB_X, DEFAULT_TAB_Y, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT);
        }
        this.itemRenderer.renderGuiItem(new ItemStack(Items.COMPASS), (this.left - 31) + 10, this.top + 3 + ((27) * 5) + 4);
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);

        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        int x = 0;
        int y = 0;
        if (OPEN_TAB != null) {
            for (int i = page * 25; i < Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size(); i++) {
                RocketPart part = Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).get(i);

                this.minecraft.getTextureManager().bindTexture(TEXTURE);
                blit(this.left + 9 + ((BOX_WIDTH + 2) * x), this.top + 9 + ((BOX_HEIGHT + 2) * y), WHITE_BOX_X, WHITE_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
                this.itemRenderer.renderGuiItem(new ItemStack(part.getBlockToRender().asItem() == Items.AIR ? Items.BARRIER : part.getBlockToRender().asItem()), this.left + 9 + 4 + ((BOX_WIDTH + 2) * x), this.top + 9 + 4 + ((BOX_HEIGHT + 2) * y));
                if (++x == 5) {
                    x = 0;
                    if (++y == 5) {
                        break;
                    }
                }
            }
        } else {

            for (int i = page * 25; i < Galacticraft.ROCKET_PARTS.getAllEntries().size(); i++) {
                RocketPart part = Galacticraft.ROCKET_PARTS.getAllEntries().get(i);

                this.minecraft.getTextureManager().bindTexture(TEXTURE);
                blit(this.left + 9 + ((BOX_WIDTH + 2) * x), this.top + 9 + ((BOX_HEIGHT + 2) * y), WHITE_BOX_X, WHITE_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
                this.itemRenderer.renderGuiItem(new ItemStack(part.getBlockToRender().asItem() == Items.AIR ? Items.BARRIER : part.getBlockToRender().asItem()), this.left + 9 + 4 + ((BOX_WIDTH + 2) * x), this.top + 9 + 4 + ((BOX_HEIGHT + 2) * y));
                if (++x == 5) {
                    x = 0;
                    if (++y == 5) {
                        break;
                    }
                }
            }
        }

        if (OPEN_TAB != null) {
            if (Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() > 25) {
                maxPage = (int) ((Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() / 25.0F) - ((Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() / 25.0F) % 1.0F)) - 1; //round down, index 0
            } else {
                page = 0;
            }
        } else {
            if (Galacticraft.ROCKET_PARTS.getAllEntries().size() > 25) {
                maxPage = (int) ((Galacticraft.ROCKET_PARTS.getAllEntries().size() / 25.0F) - ((Galacticraft.ROCKET_PARTS.getAllEntries().size() / 25.0F) % 1.0F)) - 1;
            } else {
                page = 0;
            }
        }

        if (maxPage > 0) {
            if (page < maxPage) {
                blit(this.left + 60, this.top + 145, ARROW_X, ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT);
            }

            if (page - 1 > 0) {
                blit(this.left + 40 - BACK_ARROW_HEIGHT, this.top + 145 - BACK_ARROW_WIDTH, BACK_ARROW_X, BACK_ARROW_Y, BACK_ARROW_WIDTH, BACK_ARROW_HEIGHT);
            }
        }


        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.CONE).getBlockToRender().asItem()), this.left + 156, this.top + 8);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BODY).getBlockToRender().asItem()), this.left + 156, this.top + 16 + 8);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.FIN).getBlockToRender().asItem()), this.left + 156, this.top + 32 + 8);

        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BOOSTER).getBlockToRender().asItem() == Items.AIR ? Items.BARRIER : this.be.getPart(RocketPartType.BOOSTER).getBlockToRender().asItem()), this.left + 225, this.top + 44);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BOTTOM).getBlockToRender().asItem()), this.left + 225, this.top + 44 + 16);

        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name").asFormattedString(), (this.width / 2), this.top + 6 - 15, Formatting.WHITE.getColorValue());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        for (int i = 0; i < RocketPartType.values().length; i++) {
            if (check(mouseX, mouseY, this.left - 27, this.top + 3 + ((27) * i), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                this.renderTooltip(new TranslatableText("ui.galacticraft-rewoven.part_type." + RocketPartType.values()[i].asString()).asString(), mouseX, mouseY); // caps
                break;
            }
        }

        if (check(mouseX, mouseY, this.left - 27, this.top + 3 + 135, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
            this.renderTooltip("Search", mouseX, mouseY);
        }

        super.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    public void blit(int i, int j, int k, int l, int m, int n) {
        blit(i, j, k, l, m, n, 512, 256); //!! if you need to use any other textures other than #TEXTURE use the other blit, specifying the tex size blitV;
    }

    @Override
    public boolean mouseDragged(double startX, double startY, int button, double diffX, double diffY) {
        if (button == 0) {
            if (this.left - startX < -256 && this.left - startX > -313 && this.top - startY < -9.0F && this.top - startY > -15.0F
                    && this.left - (startX + diffX) < -256 && this.left - (startX + diffX) > -313 && this.top - (startY + diffY) < -9.0F && this.top - (startY + diffY) > -15.0F) {
                return colourClick(startX + diffX, startY + diffY, button);
            }

        }
        return super.mouseDragged(startX, startY, button, diffX, diffY);
    }

    public boolean colourClick(double mouseX, double mouseY, int button) { //56 colour spaces
        if (button == 0) {
            if (this.left - mouseX < -256 && this.left - mouseX > -313 && this.top - mouseY < -9.0F && this.top - mouseY > -15.0F) {

            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        return super.mouseScrolled(double_1, double_2, double_3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("X: " + mouseX + " Y: " + mouseY);
        System.out.println("X (R): " + (this.left - mouseX) + " Y: " + (this.top - mouseY));
        return super.mouseClicked(mouseX, mouseY, button) | tabClick(mouseX, mouseY, button) | contentClick(mouseX, mouseY, button) | colourClick(mouseX, mouseY, button);
    }

    public boolean tabClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < RocketPartType.values().length; i++) {
                if (RocketPartType.values()[i] != OPEN_TAB) {
                    if (check(mouseX, mouseY, this.left - 27, this.top + 3 + ((27) * i), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                        OPEN_TAB = RocketPartType.values()[i];
                        page = 0;
                        return true;
                    }
                }
            }

            if (OPEN_TAB != null && check(mouseX, mouseY, this.left - 27, this.top + 3 + ((27) * 5), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                OPEN_TAB = null;
                page = 0;
            }

        }

        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean check(double mouseX, double mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseY >= buttonY && mouseX <= buttonX + buttonWidth && mouseY <= buttonY + buttonHeight;
    }

    public boolean contentClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = 0;
            int y = 0;
            if (OPEN_TAB != null) {
                for (int i = page * 25; i < Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size(); i++) {
                    RocketPart part = Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).get(i);
                    if (check(mouseX, mouseY, this.left + 9 + ((BOX_WIDTH + 2) * x), this.top + 9 + ((BOX_HEIGHT + 2) * y), BOX_WIDTH, BOX_HEIGHT)) {
                        this.be.setPart(part);
                        break;
                    }
                    if (++x == 5) {
                        x = 0;
                        if (++y == 5) {
                            break;
                        }
                    }
                }
            } else {
                for (int i = page * 25; i < Galacticraft.ROCKET_PARTS.getAllEntries().size(); i++) {
                    RocketPart part = Galacticraft.ROCKET_PARTS.getAllEntries().get(i);
                    if (check(mouseX, mouseY, this.left + 9 + ((BOX_WIDTH + 2) * x), this.top + 9 + ((BOX_HEIGHT + 2) * y), BOX_WIDTH, BOX_HEIGHT)) {
                        this.be.setPart(part);
                        break;
                    }
                    if (++x == 5) {
                        x = 0;
                        if (++y == 5) {
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }
}
