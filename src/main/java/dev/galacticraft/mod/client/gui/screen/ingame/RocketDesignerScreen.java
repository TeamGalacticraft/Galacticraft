/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.block.entity.RocketDesignerBlockEntity;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.RocketEntity;
import dev.galacticraft.mod.screen.RocketDesignerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketDesignerScreen extends AbstractContainerScreen<RocketDesignerScreenHandler> {
    protected RocketDesignerBlockEntity blockEntity;

    private static final int WHITE_BOX_U = 192;
    private static final int WHITE_BOX_V = 166;

    private static final int RED_BOX_U = WHITE_BOX_U + 25;
    private static final int RED_BOX_V = WHITE_BOX_V + 25;

    private static final int GREEN_BOX_U = RED_BOX_U + 25;
    private static final int GREEN_BOX_V = RED_BOX_V + 25;

    private static final int BOX_WIDTH = 24;
    private static final int BOX_HEIGHT = 24;

    private static final int SELECTED_TAB_U = 0;
    private static final int SELECTED_TAB_V = 166;

    private static final int DEFAULT_TAB_U = 33;
    private static final int DEFAULT_TAB_V = 166;

    private static final int SELECTED_TAB_WIDTH = 32;
    private static final int SELECTED_TAB_HEIGHT = 26;

    private static final int DEFAULT_TAB_WIDTH = 28;
    private static final int DEFAULT_TAB_HEIGHT = 25;

    private static final int ARROW_U = 178;
    private static final int ARROW_V = 166;

    private static final int ARROW_WIDTH = 6;
    private static final int ARROW_HEIGHT = 11;

    private static final int HOVERED_ARROW_U = 185;
    private static final int HOVERED_ARROW_V = 166;

    private static final int BACK_ARROW_U = ARROW_U - ARROW_WIDTH;
    private static final int BACK_ARROW_V = ARROW_V - ARROW_HEIGHT;

    private static final int BACK_ARROW_WIDTH = -ARROW_WIDTH;
    private static final int BACK_ARROW_HEIGHT = -ARROW_HEIGHT;

    private static final int BACK_HOVERED_ARROW_U = HOVERED_ARROW_U - ARROW_WIDTH;
    private static final int BACK_HOVERED_ARROW_V = HOVERED_ARROW_V - ARROW_HEIGHT;

    private static final int RED_COLOUR_U = 62;
    private static final int RED_COLOUR_V = 182;

    private static final int GREEN_COLOUR_U = 118;
    private static final int GREEN_COLOUR_V = 182;

    private static final int BLUE_COLOUR_U = 62;
    private static final int BLUE_COLOUR_V = 187;

    private static final int ALPHA_U = 118;
    private static final int ALPHA_V = 187;

    private static final int COLOUR_PICKER_HEIGHT = 5;

    private static final int RED_END_COLOUR_U = 116;
    private static final int RED_END_COLOUR_V = 182;

    private static final int GREEN_END_COLOUR_U = 172;
    private static final int GREEN_END_COLOUR_V = 182;

    private static final int BLUE_END_COLOUR_U = 116;
    private static final int BLUE_END_COLOUR_V = 187;

    private static final int ALPHA_END_U = 172;
    private static final int ALPHA_END_V = 187;

    private static final int COLOUR_PICKER_END_WIDTH = 2;
    private static final int COLOUR_PICKER_END_HEIGHT = 5;
    
    private static final RocketPartType[] ROCKET_PART_TYPES = RocketPartType.values();

    private int page = 0;
    private int maxPage = 0;
    private final List<RocketPart> validParts = new ArrayList<>();
    private RocketPartType currentType = RocketPartType.CONE;

    private final RocketEntity entity;

    public RocketDesignerScreen(RocketDesignerScreenHandler screenHandler, Inventory inv, Component title) {
        super(screenHandler, inv, title);
        this.imageWidth = 323;
        this.imageHeight = 164;
        this.blockEntity = screenHandler.designer;
        this.entity = new RocketEntity(GalacticraftEntityType.ROCKET, inv.player.level);
        this.validParts.addAll(GalacticraftRocketParts.getUnlockedParts(inv.player, currentType));
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);

        Lighting.setupFor3DItems();

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_DESIGNER_SCREEN);

        blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for (int i = 0; i < ROCKET_PART_TYPES.length; i++) {
            RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_DESIGNER_SCREEN);
            if (ROCKET_PART_TYPES[i] != currentType) {
                blit(matrices, this.leftPos - 27, this.topPos + 3 + (27 * i), DEFAULT_TAB_U, DEFAULT_TAB_V, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT);
            } else {
                blit(matrices, this.leftPos - 29, this.topPos + 3 + (27 * i), SELECTED_TAB_U, SELECTED_TAB_V, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            }
            matrices.pushPose();
            matrices.translate((this.leftPos - 31) + 13, this.topPos + 3 + ((27) * i) + 4, 0);
            GalacticraftRocketParts.getPartToRenderForType(this.blockEntity.getLevel().registryAccess(), ROCKET_PART_TYPES[i]).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        Lighting.setupFor3DItems();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_DESIGNER_SCREEN);

        int x = 0;
        int y = 0;
        for (int i = page * 25; i < validParts.size(); i++) {
            RocketPart part = validParts.get(i);

            RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_DESIGNER_SCREEN);
            blit(matrices, this.leftPos + 9 + ((BOX_WIDTH + 2) * x), this.topPos + 9 + ((BOX_HEIGHT + 2) * y), WHITE_BOX_U, WHITE_BOX_V, BOX_WIDTH, BOX_HEIGHT);

            if (part != null) {
                matrices.pushPose();
                matrices.translate(this.leftPos + 13 + ((BOX_WIDTH + 2) * x), this.topPos + 13 + ((BOX_HEIGHT + 2) * y), 0);
                RocketPartRendererRegistry.INSTANCE.getRenderer(RocketPart.getId(this.blockEntity.getLevel().registryAccess(), part)).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
                matrices.popPose();
            }
            if (++x == 5) {
                x = 0;
                if (++y == 5) {
                    break;
                }
            }
        }

        if (validParts.size() > 25) {
            maxPage = (int) ((validParts.size() / 25.0F) - ((validParts.size() / 25.0F) % 1.0F)) - 1; //round down, index 0
        } else {
            page = 0;
        }


        if (maxPage > 0) {
            if (page < maxPage) {
                blit(matrices, this.leftPos + 60, this.topPos + 145, ARROW_U, ARROW_V, ARROW_WIDTH, ARROW_HEIGHT);
            }

            if (page - 1 > 0) {
                blit(matrices, this.leftPos + 40 - BACK_ARROW_HEIGHT, this.topPos + 145 - BACK_ARROW_WIDTH, BACK_ARROW_U, BACK_ARROW_V, BACK_ARROW_WIDTH, BACK_ARROW_HEIGHT);
            }
        }
        ResourceLocation part = this.blockEntity.getPart(RocketPartType.CONE);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 8, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        part = this.blockEntity.getPart(RocketPartType.BODY);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 24, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        part = this.blockEntity.getPart(RocketPartType.FIN);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 40, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        part = this.blockEntity.getPart(RocketPartType.UPGRADE);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 26, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        part = this.blockEntity.getPart(RocketPartType.BOOSTER);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 44, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        part = this.blockEntity.getPart(RocketPartType.BOTTOM);
        if (part != null) {
            matrices.pushPose();
            matrices.translate(this.leftPos + 156, this.topPos + 60, 0);
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
            matrices.popPose();
        }
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_DESIGNER_SCREEN);

        int red = (int) (56.0F * (this.blockEntity.getRed() / 255.0F));
        if (red >= 3 && red != 255) {
            this.blit(matrices, this.leftPos + (257 + red - 2), this.topPos + 9, RED_END_COLOUR_U, RED_END_COLOUR_V, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            red -= 2;
        }

        this.blit(matrices, this.leftPos + 257, this.topPos + 9, RED_COLOUR_U, RED_COLOUR_V, red, COLOUR_PICKER_HEIGHT);

        int green = (int) (56.0F * (this.blockEntity.getGreen() / 255.0F));
        if (green >= 3 && green != 255) {
            this.blit(matrices, this.leftPos + (257 + green - 2), this.topPos + 19, GREEN_END_COLOUR_U, GREEN_END_COLOUR_V, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            green -= 2;
        }

        this.blit(matrices, this.leftPos + 257, this.topPos + 19, GREEN_COLOUR_U, GREEN_COLOUR_V, green, COLOUR_PICKER_HEIGHT);

        int blue = (int) (56.0F * (this.blockEntity.getBlue() / 255.0F));
        if (blue >= 3 && blue != 255) {
            this.blit(matrices, this.leftPos + (257 + blue - 2), this.topPos + 29, BLUE_END_COLOUR_U, BLUE_END_COLOUR_V, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            blue -= 2;
        }

        this.blit(matrices, this.leftPos + 257, this.topPos + 29, BLUE_COLOUR_U, BLUE_COLOUR_V, blue, COLOUR_PICKER_HEIGHT);

        int alpha = (int) (56.0F * (this.blockEntity.getAlpha() / 255.0F));
        if (alpha >= 3 && alpha != 255) {
            this.blit(matrices, this.leftPos + (257 + alpha - 2), this.topPos + 39, ALPHA_END_U, ALPHA_END_V, COLOUR_PICKER_END_WIDTH, COLOUR_PICKER_END_HEIGHT);
            alpha -= 2;
        }

        this.blit(matrices, this.leftPos + 257, this.topPos + 39, ALPHA_U, ALPHA_V, alpha, COLOUR_PICKER_HEIGHT);

        for (RocketPartType type : ROCKET_PART_TYPES) {
            if (this.blockEntity.getPart(type) != null) this.entity.setPart(this.blockEntity.getPart(type), type);
        }
        this.entity.setColor(this.blockEntity.getAlpha() << 24 | this.blockEntity.getRed() << 16 | this.blockEntity.getGreen() << 8 | this.blockEntity.getBlue());

        drawRocket(this.leftPos + 172 + 24, this.topPos + 64, 20, mouseX, mouseY, entity);

        drawCenteredString(matrices, this.minecraft.font, I18n.get("ui.galacticraft.rocket_designer.name"), (this.width / 2), this.topPos + 6 - 15, ChatFormatting.WHITE.getColor());

        minecraft.font.draw(matrices, "R", this.leftPos + 245 + 3, this.topPos + 8, ChatFormatting.RED.getColor());
        minecraft.font.draw(matrices, "G", this.leftPos + 245 + 3, this.topPos + 18, ChatFormatting.GREEN.getColor());
        minecraft.font.draw(matrices, "B", this.leftPos + 245 + 3, this.topPos + 28, ChatFormatting.BLUE.getColor());
        minecraft.font.draw(matrices, "A", this.leftPos + 245 + 3, this.topPos + 38, ChatFormatting.WHITE.getColor());

        minecraft.font.draw(matrices, Component.translatable("ui.galacticraft.rocket_designer.rocket_info").asString(), this.leftPos + 245, this.topPos + 62 - 9, ChatFormatting.DARK_GRAY.getColorValue());
//        client.textRenderer.draw(matrices, Component.translatable("ui.galacticraft.rocket_designer.tier", this.entity.getTier()).asString(), this.leftPos + 245, this.topPos + 62, Formatting.DARK_GRAY.getColorValue());

        this.renderTooltip(matrices, mouseX, mouseY);
    }

    public static void drawRocket(int x, int y, int size, float mouseX, float mouseY, RocketEntity entity) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        PoseStack matrices = RenderSystem.getModelViewStack();
        matrices.pushPose();
        matrices.translate(x, y, 1050.0D);
        matrices.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack2 = new PoseStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float i = entity.getYaw();
        float j = entity.getPitch();
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.setYaw(i);
        entity.setPitch(j);
        matrices.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        for (int i = 0; i < ROCKET_PART_TYPES.length; i++) {
            if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 3 + ((27) * i), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                this.renderTooltip(stack, Component.translatable("ui.galacticraft.part_type." + ROCKET_PART_TYPES[i].asString()), mouseX, mouseY);
                break;
            }
        }

        super.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double startX, double startY, int button, double diffX, double diffY) {
        if (button == 0) {
            if (this.leftPos - startX < -256 && this.leftPos - startX > -313 && this.topPos - startY < -9.0F && this.topPos - startY > -15.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 0);
            }

            if (this.leftPos - startX < -256 && this.leftPos - startX > -313 && this.topPos - startY < -19.0F && this.topPos - startY > -25.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 1);
            }

            if (this.leftPos - startX < -256 && this.leftPos - startX > -313 && this.topPos - startY < -29.0F && this.topPos - startY > -35.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 2);
            }

            if (this.leftPos - startX < -256 && this.leftPos - startX > -313 && this.topPos - startY < -39.0F && this.topPos - startY > -45.0F) {
                return colourClick(startX + diffX, startY + diffY, button, (byte) 3);
            }

        }
        return super.mouseDragged(startX, startY, button, diffX, diffY);
    }

    public boolean colourClick(double mouseX, double mouseY, int button, byte b) { //56 colour spaces
        if (button == 0) {
            if (b != -1) {
                if (b == 0) {
                    int r = (int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (r > 255) {
                        r = 255;
                    } else if (r < 0) {
                        r = 0;
                    }
                    this.blockEntity.setRedClient(r);
                } else if (b == 1) {
                    int g = (int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (g > 255) {
                        g = 255;
                    } else if (g < 0) {
                        g = 0;
                    }
                    this.blockEntity.setGreenClient(g);
                } else if (b == 2) {
                    int blue = (int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (blue > 255) {
                        blue = 255;
                    } else if (blue < 0) {
                        blue = 0;
                    }
                    this.blockEntity.setBlueClient(blue);
                } else {
                    int a = (int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255);
                    if (a > 255) {
                        a = 255;
                    } else if (a < 0) {
                        a = 0;
                    }
                    this.blockEntity.setAlphaClient(a);
                }
            } else {
                if (this.leftPos - mouseX < -256.0F && this.leftPos - mouseX > -313.0F && this.topPos - mouseY < -9.0F && this.topPos - mouseY > -15.0F) {
                    this.blockEntity.setRedClient((int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.leftPos - mouseX < -256.0F && this.leftPos - mouseX > -313.0F && this.topPos - mouseY < -19.0F && this.topPos - mouseY > -25.0F) {
                    this.blockEntity.setGreenClient((int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.leftPos - mouseX < -256.0F && this.leftPos - mouseX > -313.0F && this.topPos - mouseY < -29.0F && this.topPos - mouseY > -35.0F) {
                    this.blockEntity.setBlueClient((int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255));
                }

                if (this.leftPos - mouseX < -256.0F && this.leftPos - mouseX > -313.0F && this.topPos - mouseY < -39.0F && this.topPos - mouseY > -45.0F) {
                    this.blockEntity.setAlphaClient((int) (((((this.leftPos - mouseX) - -257F) * -1F) / 55.5F) * 255));
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
            for (int i = 0; i < ROCKET_PART_TYPES.length; i++) {
                if (ROCKET_PART_TYPES[i] != currentType) {
                    if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 3 + ((27) * i), DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT)) {
                        currentType = ROCKET_PART_TYPES[i];
                        validParts.clear();
                        validParts.addAll(GalacticraftRocketParts.getUnlockedParts(this.menu.player, currentType));
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
            if (currentType != null) {
                for (int i = page * 25; i < validParts.size(); i++) {
                    RocketPart part = validParts.get(i);
                    if (check(mouseX, mouseY, this.leftPos + 9 + ((BOX_WIDTH + 2) * x), this.topPos + 9 + ((BOX_HEIGHT + 2) * y), BOX_WIDTH, BOX_HEIGHT)) {
                        this.blockEntity.setPartClient(RocketPart.getId(this.blockEntity.getLevel().registryAccess(), part), part.type());
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
                for (int i = page * 25; i < GalacticraftRocketParts.getUnlockedParts(this.menu.player).size(); i++) {
                    RocketPart part = GalacticraftRocketParts.getUnlockedParts(this.menu.player).get(i);
                    if (check(mouseX, mouseY, this.leftPos + 9 + ((BOX_WIDTH + 2) * x), this.topPos + 9 + ((BOX_HEIGHT + 2) * y), BOX_WIDTH, BOX_HEIGHT)) {
                        this.blockEntity.setPartClient(RocketPart.getId(this.blockEntity.getLevel().registryAccess(), part), part.type());
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

    @Override
    public void blit(PoseStack stack, int i, int j, int k, int l, int m, int n) {
        blit(stack, i, j, k, l, m, n, 512, 256); //!! if you need to use any other textures other than #TEXTURE use the other blit, specifying the tex size blitV;
    }
}
