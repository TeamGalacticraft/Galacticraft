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
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.api.rocket.RocketParts;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

    private static final int RED_COLOUR_X = 62;
    private static final int RED_COLOUR_Y = 182;

    private static final int GREEN_COLOUR_X = 118;
    private static final int GREEN_COLOUR_Y = 182;

    private static final int BLUE_COLOUR_X = 62;
    private static final int BLUE_COLOUR_Y = 187;

    private static final int ALPHA_X = 118;
    private static final int ALPHA_Y = 187;

    private static final int COLOUR_PICKER_HEIGHT = 5;

    private static final int RED_END_COLOUR_X = 116;
    private static final int RED_END_COLOUR_Y = 182;

    private static final int GREEN_END_COLOUR_X = 172;
    private static final int GREEN_END_COLOUR_Y = 182;

    private static final int BLUE_END_COLOUR_X = 116;
    private static final int BLUE_END_COLOUR_Y = 187;

    private static final int ALPHA_END_X = 172;
    private static final int ALPHA_END_Y = 187;

    private static final int COLOUR_PICKER_END_WIDTH = 2;
    private static final int COLOUR_PICKER_END_HEIGHT = 5;

    private int page = 0;
    private int maxPage = 0;
    private RocketPartType OPEN_TAB = RocketPartType.CONE;

    private final RocketEntity entity;

    private RocketDesignerScreen(int syncId, PlayerEntity playerEntity, RocketDesignerBlockEntity blockEntity) {
        super(new RocketDesignerContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name"));
        this.containerWidth = 323;
        this.containerHeight = 164;
        this.world = playerEntity.world;
        this.be = blockEntity;
        this.entity = new RocketEntity(GalacticraftEntityTypes.ROCKET, world);
    }

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderBackground();

        GuiLighting.disable();
        GlStateManager.disableDepthTest();

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);

        GuiLighting.enableForItems();

        for (int i = 0; i < RocketPartType.values().length; i++) {
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            if (RocketPartType.values()[i] != OPEN_TAB) {
                blit(this.left - 27, this.top + 3 + (27 * i), DEFAULT_TAB_X, DEFAULT_TAB_Y, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT);
            } else {
                blit(this.left - 31 + 2, this.top + 3 + (27 * i), SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            }
            this.itemRenderer.renderGuiItem(new ItemStack(RocketParts.getPartToRenderForType(RocketPartType.values()[i]).getDesignerItem()), (this.left - 31) + 13, this.top + 3 + ((27) * i) + 4);
        }
        GuiLighting.disable();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);

        GuiLighting.disable();
        GuiLighting.enableForItems();
        GlStateManager.disableDepthTest();

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        int x = 0;
        int y = 0;
        for (int i = page * 25; i < Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size(); i++) {
            RocketPart part = Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).get(i);

            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            blit(this.left + 9 + ((BOX_WIDTH + 2) * x), this.top + 9 + ((BOX_HEIGHT + 2) * y), WHITE_BOX_X, WHITE_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
            this.itemRenderer.renderGuiItem(new ItemStack(part.getDesignerItem().asItem()), this.left + 13 + ((BOX_WIDTH + 2) * x), this.top + 13 + ((BOX_HEIGHT + 2) * y));
            if (++x == 5) {
                x = 0;
                if (++y == 5) {
                    break;
                }
            }
        }

        GuiLighting.disable();

        if (Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() > 25) {
            maxPage = (int) ((Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() / 25.0F) - ((Galacticraft.ROCKET_PARTS.getPartsForType(OPEN_TAB).size() / 25.0F) % 1.0F)) - 1; //round down, index 0
        } else {
            page = 0;
        }


        if (maxPage > 0) {
            if (page < maxPage) {
                blit(this.left + 60, this.top + 145, ARROW_X, ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT);
            }

            if (page - 1 > 0) {
                blit(this.left + 40 - BACK_ARROW_HEIGHT, this.top + 145 - BACK_ARROW_WIDTH, BACK_ARROW_X, BACK_ARROW_Y, BACK_ARROW_WIDTH, BACK_ARROW_HEIGHT);
            }
        }

        GuiLighting.enableForItems();

        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.CONE).getDesignerItem().asItem()), this.left + 156, this.top + 8);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BODY).getDesignerItem().asItem()), this.left + 156, this.top + 24);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.FIN).getDesignerItem().asItem()), this.left + 156, this.top + 40);

        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.UPGRADE).getDesignerItem().asItem()), this.left + 225, this.top + 26);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BOOSTER).getDesignerItem().asItem()), this.left + 225, this.top + 44);
        this.itemRenderer.renderGuiItem(new ItemStack(this.be.getPart(RocketPartType.BOTTOM).getDesignerItem().asItem()), this.left + 225, this.top + 60);

        GuiLighting.disable();

        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        int red = (int) (56.0F * (this.be.getRed() / 255.0F));
        if (red >= 3 && red != 255) {
            this.blit(this.left + (257 + red - 2), this.top + 9, RED_END_COLOUR_X, RED_END_COLOUR_Y, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            red -= 2;
        }

        this.blit(this.left + 257, this.top + 9, RED_COLOUR_X, RED_COLOUR_Y, red, COLOUR_PICKER_HEIGHT);

        int green = (int) (56.0F * (this.be.getGreen() / 255.0F));
        if (green >= 3 && green != 255) {
            this.blit(this.left + (257 + green - 2), this.top + 19, GREEN_END_COLOUR_X, GREEN_END_COLOUR_Y, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            green -= 2;
        }

        this.blit(this.left + 257, this.top + 19, GREEN_COLOUR_X, GREEN_COLOUR_Y, green, COLOUR_PICKER_HEIGHT);

        int blue = (int) (56.0F * (this.be.getBlue() / 255.0F));
        if (blue >= 3 && blue != 255) {
            this.blit(this.left + (257 + blue - 2), this.top + 29, BLUE_END_COLOUR_X, BLUE_END_COLOUR_Y, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            blue -= 2;
        }

        this.blit(this.left + 257, this.top + 29, BLUE_COLOUR_X, BLUE_COLOUR_Y, blue, COLOUR_PICKER_HEIGHT);

        int alpha = (int) (56.0F * (this.be.getAlpha() / 255.0F));
        if (alpha >= 3 && alpha != 255) {
            this.blit(this.left + (257 + alpha - 2), this.top + 39, ALPHA_END_X, ALPHA_END_Y, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            alpha -= 2;
        }

        this.blit(this.left + 257, this.top + 39, ALPHA_X, ALPHA_Y, alpha, COLOUR_PICKER_HEIGHT);

        GuiLighting.enable();
        this.drawEntity(this.left + 172 + 24, this.top + 64);

        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name").asFormattedString(), (this.width / 2), this.top + 6 - 15, Formatting.WHITE.getColorValue());

        DrawableUtils.drawString(this.minecraft.textRenderer, "R", this.left + 245 + 3, this.top + 8, Formatting.RED.getColorValue());
        DrawableUtils.drawString(this.minecraft.textRenderer, "G", this.left + 245 + 3, this.top + 18, Formatting.GREEN.getColorValue());
        DrawableUtils.drawString(this.minecraft.textRenderer, "B", this.left + 245 + 3, this.top + 28, Formatting.BLUE.getColorValue());
        DrawableUtils.drawString(this.minecraft.textRenderer, "A", this.left + 245 + 3, this.top + 38, Formatting.WHITE.getColorValue());

        DrawableUtils.drawString(this.minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.rocket_info").asFormattedString(), this.left + 245, this.top + 62 - 9, Formatting.DARK_GRAY.getColorValue());
        DrawableUtils.drawString(this.minecraft.textRenderer, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.tier", this.entity.getTier()).asFormattedString(), this.left + 245, this.top + 62, Formatting.DARK_GRAY.getColorValue());

        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        for (int i = 0; i < RocketPartType.values().length; i++) {
            if (check(mouseX, mouseY, this.left - 27, this.top + 3 + ((27) * i), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                this.renderTooltip(new TranslatableText("ui.galacticraft-rewoven.part_type." + RocketPartType.values()[i].asString()).asString(), mouseX, mouseY);
                break;
            }
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
            if (this.left - startX < -256 && this.left - startX > -313 && this.top - startY < -9.0F && this.top - startY > -15.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 0);
            }

            if (this.left - startX < -256 && this.left - startX > -313 && this.top - startY < -19.0F && this.top - startY > -25.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 1);
            }

            if (this.left - startX < -256 && this.left - startX > -313 && this.top - startY < -29.0F && this.top - startY > -35.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 2);
            }

            if (this.left - startX < -256 && this.left - startX > -313 && this.top - startY < -39.0F && this.top - startY > -45.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 3);
            }

        }
        return super.mouseDragged(startX, startY, button, diffX, diffY);
    }

    public boolean colourClick(double mouseX, double mouseY, int button, byte b) { //56 colour spaces
        if (button == 0) {
            if (b != -1) {
                if (b == 0) {
                    int r = (int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (r > 255) {
                        r = 255;
                    } else if (r < 0) {
                        r = 0;
                    }
                    this.be.setRed(r);
                } else if (b == 1) {
                    int g = (int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (g > 255) {
                        g = 255;
                    } else if (g < 0) {
                        g = 0;
                    }
                    this.be.setGreen(g);
                } else if (b == 2) {
                    int blue = (int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (blue > 255) {
                        blue = 255;
                    } else if (blue < 0) {
                        blue = 0;
                    }
                    this.be.setBlue(blue);
                } else {
                    int a = (int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (a > 255) {
                        a = 255;
                    } else if (a < 0) {
                        a = 0;
                    }
                    this.be.setAlpha(a);
                }
            } else {
                if (this.left - mouseX < -256.0F && this.left - mouseX > -313.0F && this.top - mouseY < -9.0F && this.top - mouseY > -15.0F) {
                    this.be.setRed((int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.left - mouseX < -256.0F && this.left - mouseX > -313.0F && this.top - mouseY < -19.0F && this.top - mouseY > -25.0F) {
                    this.be.setGreen((int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.left - mouseX < -256.0F && this.left - mouseX > -313.0F && this.top - mouseY < -29.0F && this.top - mouseY > -35.0F) {
                    this.be.setBlue((int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.left - mouseX < -256.0F && this.left - mouseX > -313.0F && this.top - mouseY < -39.0F && this.top - mouseY > -45.0F) {
                    this.be.setAlpha((int) (((((this.left - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }
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
        return super.mouseClicked(mouseX, mouseY, button) | tabClick(mouseX, mouseY, button) | contentClick(mouseX, mouseY, button) | colourClick(mouseX, mouseY, button, (byte) -1);
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
        }

        return false;
    }

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

    public void drawEntity(int x, int y) {

        for (RocketPartType type : RocketPartType.values()) {
            this.entity.setPart(this.be.getPart(type));
        }
        this.entity.setColor(this.be.getRed(), this.be.getGreen(), this.be.getBlue(), this.be.getAlpha());

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, 50.0F);
        GlStateManager.scalef((float) (-10), (float) -10, (float) 10);
        GuiLighting.enable();
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GuiLighting.enable();
        GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-((float) Math.atan(this.top + 25 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.yaw = 225;
        entity.pitch = 0;
        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.method_3945(180.0F);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        entityRenderDispatcher.setRenderShadows(true);
        GlStateManager.popMatrix();
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
}
