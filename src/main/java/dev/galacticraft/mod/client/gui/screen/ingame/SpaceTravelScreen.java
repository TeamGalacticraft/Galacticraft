/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class SpaceTravelScreen extends Screen {
    private static final ResourceLocation TEXTURE = Constant.id("textures/block/tin_decoration.png");
    private static final String[] POSSIBLE_TEXTS = new String[]{
            Translations.Ui.SMALL_STEP,
            Translations.Ui.GIANT_LEAP,
            Translations.Ui.PREPARE_FOR_ENTRY
    };
    private final int text;
    private String dots = ".";
    private final String planet;
    private final ResourceKey<Level> target;

    public SpaceTravelScreen(String planet, ResourceKey<Level> target) {
        super(GameNarrator.NO_TITLE);
        this.planet = planet;
        this.target = target;
        this.text = (int) (System.currentTimeMillis() % 3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        RenderSystem.setShaderTexture(0, TEXTURE);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(0.0F, this.height, 0.0F).setUv(0.0F, (float) this.height / 32.0F).setColor(200, 200, 200, 255)
                .addVertex(this.width, this.height, 0.0F).setUv((float) this.width / 32.0F, (float) this.height / 32.0F).setColor(200, 200, 200, 255)
                .addVertex(this.width, 0.0F, 0.0F).setUv((float) this.width / 32.0F, 0.0F).setColor(200, 200, 200, 255)
                .addVertex(0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(200, 200, 200, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        if (minecraft.level.random.nextInt(30) == 1) {
            if (dots.equals("...")) {
                dots = ".";
            } else {
                dots += '.';
            }
        }
        graphics.drawCenteredString(this.font, Component.translatable(Translations.Ui.TRAVELING_TO, this.planet), this.width / 2, this.height / 2 - 40, 16777215);
        graphics.drawCenteredString(this.font, Component.translatable(POSSIBLE_TEXTS[this.text]).append(this.dots), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(graphics, mouseX, mouseY, delta);
        if (minecraft.level.dimension().equals(this.target)) this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
