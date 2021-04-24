/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.Constants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class SpaceTravelScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/block/tin_decoration.png");
    private static final String[] POSSIBLE_TEXTS = new String[]{
            I18n.translate("ui.galacticraft.small_step"),
            I18n.translate("ui.galacticraft.giant_leap"),
            I18n.translate("ui.galacticraft.prepare_for_entry")
    };
    private static final TranslatableText TRAVELLING_TO = new TranslatableText("ui.galacticraft.travelling_to");
    private final int text;
    private String dots = ".";
    private final String planet;
    private final RegistryKey<World> target;

    protected SpaceTravelScreen(String planetKey, RegistryKey<World> target) {
        super(NarratorManager.EMPTY);
        this.planet = I18n.translate(planetKey);
        this.target = target;
        this.text = MinecraftClient.getInstance().world.random.nextInt(POSSIBLE_TEXTS.length);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).color(200, 200, 200, 255).texture(0.0F, (float)this.height / 32.0F).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).color(200, 200, 200, 255).texture((float)this.width / 32.0F, (float)this.height / 32.0F).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).color(200, 200, 200, 255).texture((float)this.width / 32.0F, 0.0F).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).color(200, 200, 200, 255).texture(0.0F, 0.0F).next();
        tessellator.draw();
        if (client.world.random.nextInt(30) == 1) {
            if (dots.equals("...")) {
                dots = ".";
            } else {
                dots += '.';
            }
        }
        drawCenteredText(matrices, this.textRenderer, TRAVELLING_TO.append(this.planet), this.width / 2, this.height / 2 - 40, 16777215);
        drawCenteredText(matrices, this.textRenderer, new LiteralText(POSSIBLE_TEXTS[this.text] + dots), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        if (client.world.getRegistryKey().equals(this.target)) this.client.openScreen(null);
    }

    public boolean isPauseScreen() {
        return false;
    }
}
