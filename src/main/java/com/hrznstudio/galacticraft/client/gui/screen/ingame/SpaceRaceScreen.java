/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.ClientPlayNetworkHandlerAccessor;
import com.hrznstudio.galacticraft.api.research.ResearchNode;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceScreen extends Screen {
    private static final Identifier RESEARCH_TEX = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.RESEARCH_PANELS));
    private static final Map<Integer, Float> TIER_TO_X_MAX_DIST = new HashMap<>();

    public static final int BASE_RESEARCH_OFFSET = 25;

    private static final int NODE_WIDTH = 90;
    private static final int NODE_HEIGHT = 38;

    private static final int NODE_X = 0;
    private static final int NODE_UNLOCKABLE_Y = 0;
    private static final int NODE_IN_PROGRESS_Y = 38;
    private static final int NODE_COMPLETE_Y = 76;

    private static final int NODE_DESC_ADDON_Y = 114;
    private static final int NODE_DESC_ADDON_HEIGHT = 24;

    private static final int SLOT_UNLOCKABLE_X = 0;
    private static final int SLOT_IN_PROGRESS_X = 20;
    private static final int SLOT_COMPLETE_X = 40;

    private static final int SLOT_Y = 138;
    private static final int SLOT_WIDTH = 20;
    private static final int SLOT_HEIGHT = 20;

    public double researchScrollX = 0;
    public double researchScrollY = 0;
    private byte resetHoldTime = 0;

    private int widthSize = 0;
    private int heightSize = 0;
    private Menu menu = Menu.MAIN;

    public SpaceRaceScreen() {
        super(new TranslatableText("ui." + Constants.MOD_ID + ".space_race_manager"));
        TIER_TO_X_MAX_DIST.clear();
        TIER_TO_X_MAX_DIST.put(0, 0F);
        for (ResearchNode root : ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().getManager().getRoots()) {
            float x = root.getInfo().getX() + (root.getInfo().getTier() > 1 ? TIER_TO_X_MAX_DIST.get(root.getInfo().getTier() - 1) : 0);
            Queue<ResearchNode> queue = new LinkedList<>(root.getChildren());
            while (!queue.isEmpty()) {
                queue.addAll(queue.peek().getChildren());
                x += queue.poll().getInfo().getX();
            }
            TIER_TO_X_MAX_DIST.put(root.getInfo().getTier(), x);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public void onClose() {
        super.onClose();
        client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "research_scroll"), new PacketByteBuf(Unpooled.buffer().writeDouble(researchScrollX).writeDouble(researchScrollY))));
    }

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        this.widthSize = 0;
        this.heightSize = 0;
        super.resize(minecraftClient_1, int_1, int_2);
    }

    private static void fillSolid(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        int j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }
        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
    }

    @Override
    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, u, v, width, height, 128, 256);
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        // 5% of width
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        if (widthSize < maxWidth) {
            widthSize += Math.min(3, maxWidth - widthSize);
        }
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));
        if (heightSize < maxHeight) {
            heightSize += Math.min(2, maxHeight - heightSize);
        }
        fill(stack, getLeft(), getTop(), getRight(), getBottom(), 0x80000000);
    }

    private void renderForeground(MatrixStack stack, int mouseX, int mouseY) {
        TextRenderer font = this.client.textRenderer;
        DrawableUtils.drawCenteredString(stack, font, new TranslatableText("ui.galacticraft-rewoven.space_race_manager"), this.width / 2, getTop() - 20, 0xFFFFFF);

        if (menu == Menu.MAIN) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            }
            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            }
            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.server_stats"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            }
            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.global_stats"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            }
            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.research"), this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.research"), this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30);
            }
        } else if (menu == Menu.ADD_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
        } else if (menu == Menu.REMOVE_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
        } else if (menu == Menu.TEAM_COLOR) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
        } else if (menu == Menu.TEAM_FLAG) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
        } else if (menu == Menu.RESEARCH) {
            DrawableUtils.drawCenteredString(stack, font, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.research"), this.width / 2, getTop() + 2, 0xFFFFFF);

            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }
            fillSolid(stack.peek().getModel(), this.getLeft() + 10, this.getTop() + 25, this.getRight() - 10, this.getBottom() - 10, 0x0);

            drawResearch(stack, mouseX, mouseY);
        }
    }

    private boolean areParentsComplete(ResearchNode node) {
        if (node.getParents().length > 0) {
            for (ResearchNode parent : node.getParents()) {
                if (!((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().isComplete(parent))
                    return false;
            }
        }
        return true;
    }


    private void drawResearch(MatrixStack matrices, int mouseX, int mouseY) {
        matrices.push();
        List<Runnable> postRenderingThings = new ArrayList<>();
        for (ResearchNode root : ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().getManager().getRoots()) {
            Queue<ResearchNode> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                ResearchNode node = queue.poll();
                if (areParentsComplete(node)) {
                    client.getTextureManager().bindTexture(RESEARCH_TEX);
                    int appTX = getAppropriateNodeTexX(node);
                    int appTY = getAppropriateNodeTexY(node);
                    int basePX = BASE_RESEARCH_OFFSET + (int) (researchScrollX + TIER_TO_X_MAX_DIST.get(node.getInfo().getTier() - 1) + (node.getInfo().getX() * (NODE_WIDTH + 5)));
                    int basePY = BASE_RESEARCH_OFFSET + (int) (researchScrollY + (node.getInfo().getY() * (NODE_HEIGHT + 4)));
                    int posXFit = getPosXToFit(basePX);
                    int posYFit = getPosYToFit(basePY);
                    int texPosXFit = getTexPosXToFit(basePX, appTX, NODE_WIDTH);
                    int texPosYFit = getTexPosYToFit(basePY, appTY, NODE_HEIGHT);
                    int texWidthFit = getWidthToFit(basePX, NODE_WIDTH);
                    int texHeightFit = getHeightToFit(basePY, NODE_HEIGHT);
                    if (texWidthFit > 0 && texHeightFit > 0
                            && texPosXFit != appTX + NODE_WIDTH && texPosYFit != appTY + NODE_HEIGHT) {
                        this.drawTexture(matrices,
                                posXFit,
                                posYFit,
                                texPosXFit,
                                texPosYFit,
                                texWidthFit,
                                texHeightFit
                        );

                        if (check(mouseX, mouseY, posXFit, posYFit, texWidthFit, texHeightFit)) {
                            postRenderingThings.add(() -> {
                                client.getTextureManager().bindTexture(RESEARCH_TEX);
                                drawTexture(matrices, posXFit, getPosYToFit(basePY + 36), texPosXFit, getTexPosYToFit(basePY + 36, NODE_DESC_ADDON_Y, NODE_DESC_ADDON_HEIGHT), texWidthFit, getHeightToFit(basePY + 36, NODE_DESC_ADDON_HEIGHT)); //X matches
                                drawAndAutotrimTextScaleSplit(matrices, basePX + 3, basePY + 39, I18n.translate(node.getInfo().getDescription().getKey(), node.getInfo().getDescription().getArgs()), Formatting.GRAY.getColorValue(), NODE_WIDTH - 3, 0.5F);
                            });
                        }

                        drawAndAutotrimTextScaleSplit(matrices, basePX + 3, basePY + 3, I18n.translate(node.getInfo().getTitle().getKey(), node.getInfo().getTitle().getArgs()), Formatting.DARK_GRAY.getColorValue(), 65536, 1.0F); //no matter what if the line is longer than the bod, it's gonna look bad, si i'll let it bleed out to the side rather than down

                        for (int i = 0; i < node.getInfo().getIcons().length && i < 4; i++) {
                            // 6, 31
                            // 43

                            int baseX = 3 + ((SLOT_WIDTH + 2) * i) + basePX;
                            int baseY = 15 + basePY;
                            client.getTextureManager().bindTexture(RESEARCH_TEX);
                            this.drawTexture(matrices,
                                    getPosXToFit(baseX),
                                    getPosYToFit(baseY),

                                    getTexPosXToFit(baseX, getSlotAppX(node), SLOT_WIDTH),
                                    getTexPosYToFit(baseY, getSlotAppY(node), SLOT_HEIGHT),

                                    getWidthToFit(baseX, SLOT_WIDTH),
                                    getHeightToFit(baseY, SLOT_HEIGHT));

                            //THIS CODE MAKES THE ITEM NOT RENDER IF IT BLEEDS OUT EVEN ONE PIXEL. I CAN'T TRIM THE ITEM RENDER LIKE THE BOX
                            if (getPosXToFit(baseX) == baseX && getPosYToFit(baseY) == baseY
                                    && baseX >= this.getLeft() + 10 && baseY >= this.getTop() + 25
                                    && baseX + SLOT_WIDTH <= this.getRight() - 10 && baseY + SLOT_WIDTH <= this.getBottom() - 10
                                    && baseX <= this.getRight() - 10 && baseY <= this.getBottom() - 10) {
                                itemRenderer.renderGuiItemIcon(node.getInfo().getIcons()[i].asItem().getStackForRender(), baseX + 2, baseY + 2);
                            }
                        }
                    }

                    if (((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().isComplete(node)) {
                        //more than one parent is possible
                        for (ResearchNode child : node.getChildren()) {
                            if (areParentsComplete(child)) {
                                queue.add(child);
                            }
                        }
                    }
                }
            }
        }

        for (Runnable runnable : postRenderingThings) {
            runnable.run();
        }
        postRenderingThings.clear();
        matrices.pop();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (menu == Menu.RESEARCH) {
            if (mouseX > getLeft() && mouseX < getRight() && mouseY > getTop() && mouseY < getBottom()) {
                researchScrollX += deltaX;
                researchScrollY += deltaY;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void drawAndAutotrimTextScaleSplit(MatrixStack matrices, int x, int y, String text, int color, int maxLength, float scale) {
        if (maxLength > 1048576) {
            maxLength = 1048576; //large enough that it shouldn't be reached, small enough it shouldn't overflow.
        }
        double xOffset = 0;
        double yOffset = 0;
        char[] charArray = text.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == ' ') {
                float dist = 0;
                for (int j = i + 1; j < charArray.length; j++) {
                    if (charArray[j] == ' ') {
                        break;
                    }
                    dist += textRenderer.getTextHandler().getWidth(String.valueOf(charArray[j]));
                }
                if (xOffset + dist >= maxLength) {
                    yOffset += textRenderer.fontHeight * scale;
                    xOffset = 0;
                    continue;
                }
                xOffset = xOffset + (textRenderer.getTextHandler().getWidth(" ") * scale);
                continue;
            }

            if (xOffset + x >= this.getLeft() + 10) {
                if (xOffset + x + textRenderer.getTextHandler().getWidth(String.valueOf(c)) < this.getRight() - 10) {
                    if (yOffset + y - (textRenderer.fontHeight * scale) >= this.getTop() + 10 && yOffset + y + (textRenderer.fontHeight * scale) <= this.getBottom() - 10) {
                        matrices.push();
                        matrices.translate(x + xOffset, y + yOffset, getZOffset());
                        matrices.scale(scale, scale, scale);
                        textRenderer.draw(matrices, String.valueOf(c), 0, 0, color);
                        matrices.pop();
                    }
                }
            }
            xOffset = xOffset + (textRenderer.getTextHandler().getWidth(String.valueOf(c)) * scale);
        }
    }

    private int getSlotAppY(ResearchNode node) {
        return SLOT_Y;
    }

    private int getSlotAppX(ResearchNode node) {
        AdvancementProgress progress = ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().getProgress(node);
        if (progress.isDone()) {
            return SLOT_COMPLETE_X;
        } else if (progress.isAnyObtained()) {
            return SLOT_IN_PROGRESS_X;
        } else {
            return SLOT_UNLOCKABLE_X;
        }
    }

    private int getAppropriateNodeTexY(ResearchNode node) {
        AdvancementProgress progress = ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler()).getClientResearchManager().getProgress(node);
        if (progress.isDone()) {
            return NODE_COMPLETE_Y;
        } else if (progress.isAnyObtained()) {
            return NODE_IN_PROGRESS_Y;
        } else {
            return NODE_UNLOCKABLE_Y;
        }
    }

    private int getAppropriateNodeTexX(ResearchNode node) {
        return NODE_X;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu == Menu.MAIN) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                this.onClose();
            }
            if (check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 85, 100, 30)) {
                setMenu(Menu.ADD_PLAYERS);
                return true;
            }
            if (check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 45, 100, 30)) {
                setMenu(Menu.REMOVE_PLAYERS);
                return true;
            }
            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30)) {
                // server stats
            }
            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30)) {
                //global stats
            }
            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30)) {
                setMenu(Menu.RESEARCH);
                return true;
            }
        } else if (menu == Menu.ADD_PLAYERS) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }
        } else if (menu == Menu.REMOVE_PLAYERS) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }
        } else if (menu == Menu.TEAM_COLOR) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }
        } else if (menu == Menu.TEAM_FLAG) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }
        } else if (menu == Menu.RESEARCH) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (menu == Menu.RESEARCH) {
            if (keyCode == GLFW.GLFW_KEY_R) {
                if (++resetHoldTime == 50) {
                    researchScrollX = 0;
                    researchScrollY = 0;
                    resetHoldTime = 0;
                }
            } else {
                resetHoldTime = 0;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float dleta) {
        this.renderBackground(stack);

        if (this.isAnimationComplete()) {
            this.renderForeground(stack, x, y);
            this.drawMouseoverTooltip(stack, x, y);
        }
        super.render(stack, x, y, dleta);
    }

    private void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {

    }

    protected void drawHorizontalLineSolid(MatrixStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        fillSolid(matrices.peek().getModel(), x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLineSolid(MatrixStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }
        fillSolid(matrices.peek().getModel(), x, y1 + 1, x + 1, y2, color);
    }

    private int getBottom() {
        return getTop() + heightSize;
    }

    private int getLeft() {
        return (this.width / 2) - (widthSize / 2);
    }

    private int getTop() {
        return (this.height / 2) - (heightSize / 2);
    }

    private int getRight() {
        return getLeft() + widthSize;
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    private void setMenu(Menu menu) {
        this.menu = menu;
    }

    private int getPosXToFit(int x) {
        return Math.max(x, this.getLeft() + 10);
    }

    private int getPosYToFit(int y) {
        return Math.max(y, this.getTop() + 25);
    }

    private int getTexPosXToFit(int x, int texPosX, int texWidth) {
        int rX = getPosXToFit(x);
        if (rX - x == 0) return texPosX;
        if (rX - x > texWidth) return  texPosX + texWidth;
        return texPosX + (rX - x);
    }

    private int getTexPosYToFit(int y, int texPosY, int texHeight) {
        int rY = getPosYToFit(y);
        if (rY - y == 0) return texPosY;
        if (rY - y > texHeight) return  texPosY + texHeight;
        return texPosY + (rY - y);
    }

    private int getWidthToFit(int x, int width) {
        if (x >= this.getRight() - 10) return 0; //too far past
        if (x < this.getLeft() + 10) return width - Math.abs(x - (this.getLeft() + 10));
        if (x + width >= this.getRight() - 10) return width - (width - Math.abs(x - (this.getRight() - 10)));

        return width;
    }

    private int getHeightToFit(int y, int height) {
        if (y >= this.getBottom() - 10) return 0; //too far past
        if (y < this.getTop() + 25) return height - Math.abs(y - (this.getTop() + 25));
        if (y + height >= this.getBottom() - 10) return getBottom() - 10 - y;

        return height;
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

        return widthSize >= maxWidth && heightSize >= maxHeight;
    }

    private void renderHoveredButton(MatrixStack stack, TextRenderer textRenderer, Text text, int x, int y, int width, int height) {
        RenderSystem.disableBlend();
        stack.push();

        fillSolid(stack.peek().getModel(), x, y, x + width, y + height, 0x1e1e1e);

        drawHorizontalLineSolid(stack, x, x + width, y, 0x3c3c3c);
        drawVerticalLineSolid(stack, x + width, y, y + height, 0x3c3c3c);
        drawHorizontalLineSolid(stack, x + width, x, y + height, 0x3c3c3c);
        drawVerticalLineSolid(stack, x, y, y + height, 0x3c3c3c);

        stack.pop();
        RenderSystem.enableBlend();

        textRenderer.draw(stack, text, x + (width / 2F) - (textRenderer.getTextHandler().getWidth(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private void renderButton(MatrixStack stack, TextRenderer textRenderer, Text text, int x, int y, int width, int height) {
        RenderSystem.disableBlend();
        stack.push();

        fillSolid(stack.peek().getModel(), x, y, x + width, y + height, 0x0);

        drawHorizontalLineSolid(stack, x, x + width, y, 0x2d2d2d);
        drawVerticalLineSolid(stack, x + width, y, y + height, 0x2d2d2d);
        drawHorizontalLineSolid(stack, x + width, x, y + height, 0x2d2d2d);
        drawVerticalLineSolid(stack, x, y, y + height, 0x2d2d2d);

        stack.pop();
        RenderSystem.enableBlend();

        textRenderer.draw(stack, text, x + (width / 2F) - (textRenderer.getTextHandler().getWidth(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
    }

    private enum Menu {
        MAIN,
        ADD_PLAYERS,
        REMOVE_PLAYERS,
        TEAM_COLOR,
        TEAM_FLAG,
        RESEARCH
    }
}