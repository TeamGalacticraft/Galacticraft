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

package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.client.model.block.BasicSolarPanelModel;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class BasicSolarPanelBlockEntityRenderer extends BlockEntityRenderer<BasicSolarPanelBlockEntity> {

    private static Identifier solarPanelTexture = new Identifier(Constants.MOD_ID, "textures/model/solar_panel_basic.png");
    public BasicSolarPanelModel model = new BasicSolarPanelModel();

    @Override
    public void render(BasicSolarPanelBlockEntity entity, double x, double y, double z, float f, int i) {
        this.bindTexture(BasicSolarPanelBlockEntityRenderer.solarPanelTexture);
        int lightmapIndex = this.getWorld().getLightmapIndex(entity.getPos().up(), 0);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (lightmapIndex % 65536), (float) (lightmapIndex / 65536));

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translatef((float) x, (float) y, (float) z);

        GlStateManager.translatef(0.5F, 1.0F, 0.5F);
        this.model.renderPole();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.translatef(0.0F, 1.5F, 0.0F);

        GlStateManager.rotatef(180.0F, 0, 0, 1);
        GlStateManager.rotatef(-90.0F, 0, 1, 0);

        this.model.renderPanel();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableColorLogicOp();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
