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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class SpaceTravelScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constant.MOD_ID, "textures/block/tin_decoration.png");
    private static final String[] POSSIBLE_TEXTS = new String[]{
            I18n.get("ui.galacticraft.small_step"),
            I18n.get("ui.galacticraft.giant_leap"),
            I18n.get("ui.galacticraft.prepare_for_entry")
    };
    private static final Component TRAVELLING_TO = Component.translatable("ui.galacticraft.travelling_to");
    private final int text;
    private String dots = ".";
    private final String planet;
    private final ResourceKey<Level> target;

    protected SpaceTravelScreen(String planetKey, ResourceKey<Level> target) {
        super(NarratorManager.EMPTY);
        this.planet = I18n.get(planetKey);
        this.target = target;
        this.text = (int) (System.currentTimeMillis() % 3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        RenderSystem.setShaderTexture(0, TEXTURE);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).color(200, 200, 200, 255).uv(0.0F, (float)this.height / 32.0F).endVertex();
        bufferBuilder.vertex(this.width, this.height, 0.0D).color(200, 200, 200, 255).uv((float)this.width / 32.0F, (float)this.height / 32.0F).endVertex();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).color(200, 200, 200, 255).uv((float)this.width / 32.0F, 0.0F).endVertex();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).color(200, 200, 200, 255).uv(0.0F, 0.0F).endVertex();
        tessellator.end();
        if (minecraft.level.random.nextInt(30) == 1) {
            if (dots.equals("...")) {
                dots = ".";
            } else {
                dots += '.';
            }
        }
        drawCenteredString(matrices, this.font, TRAVELLING_TO.append(this.planet), this.width / 2, this.height / 2 - 40, 16777215);
        drawCenteredString(matrices, this.font, Component.literal(POSSIBLE_TEXTS[this.text] + dots), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        if (minecraft.level.dimension().equals(this.target)) this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
