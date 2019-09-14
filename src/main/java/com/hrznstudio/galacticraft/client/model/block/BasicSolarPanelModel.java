/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.client.model.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class BasicSolarPanelModel extends Model {
    private Cuboid panelMain;
    private Cuboid sideHorizontal0;
    private Cuboid sideVertical0;
    private Cuboid sideVertical2;
    private Cuboid sideVertical1;
    private Cuboid sideHorizontal1;
    private Cuboid sideHorizontal3;
    private Cuboid sideHorizontal2;
    private Cuboid pole;

    public BasicSolarPanelModel() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.panelMain = new Cuboid(this, 0, 0);
        this.panelMain.addBox(-23F, -0.5F, -23F, 46, 1, 46);
        this.panelMain.setRotationPoint(0F, 0F, 0F);
        this.panelMain.setTextureSize(256, 128);
        this.panelMain.mirror = true;
        this.setRotation(this.panelMain, 0F, 0F, 0F);
        this.sideHorizontal0 = new Cuboid(this, 0, 48);
        this.sideHorizontal0.addBox(-24F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal0.setRotationPoint(0F, 0F, 0F);
        this.sideHorizontal0.setTextureSize(256, 128);
        this.sideHorizontal0.mirror = true;
        this.setRotation(this.sideHorizontal0, 0F, 0F, 0F);
        this.sideVertical0 = new Cuboid(this, 94, 48);
        this.sideVertical0.addBox(-24F, -1.1F, 23F, 48, 1, 1);
        this.sideVertical0.setRotationPoint(0F, 0F, 0F);
        this.sideVertical0.setTextureSize(256, 128);
        this.sideVertical0.mirror = true;
        this.setRotation(this.sideVertical0, 0F, 0F, 0F);
        this.sideVertical2 = new Cuboid(this, 94, 48);
        this.sideVertical2.addBox(-24F, -1.1F, -24F, 48, 1, 1);
        this.sideVertical2.setRotationPoint(0F, 0F, 0F);
        this.sideVertical2.setTextureSize(256, 128);
        this.sideVertical2.mirror = true;
        this.setRotation(this.sideVertical2, 0F, 0F, 0F);
        this.sideVertical1 = new Cuboid(this, 94, 48);
        this.sideVertical1.addBox(-24F, -1.1F, -0.5F, 48, 1, 1);
        this.sideVertical1.setRotationPoint(0F, 0F, 0F);
        this.sideVertical1.setTextureSize(256, 128);
        this.sideVertical1.mirror = true;
        this.setRotation(this.sideVertical1, 0F, 0F, 0F);
        this.sideHorizontal1 = new Cuboid(this, 0, 48);
        this.sideHorizontal1.addBox(-9F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal1.setRotationPoint(0F, 0F, 0F);
        this.sideHorizontal1.setTextureSize(256, 128);
        this.sideHorizontal1.mirror = true;
        this.setRotation(this.sideHorizontal1, 0F, 0F, 0F);
        this.sideHorizontal3 = new Cuboid(this, 0, 48);
        this.sideHorizontal3.addBox(23F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal3.setRotationPoint(0F, 0F, 0F);
        this.sideHorizontal3.setTextureSize(256, 128);
        this.sideHorizontal3.mirror = true;
        this.setRotation(this.sideHorizontal3, 0F, 0F, 0F);
        this.sideHorizontal2 = new Cuboid(this, 0, 48);
        this.sideHorizontal2.addBox(8F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal2.setRotationPoint(0F, 0F, 0F);
        this.sideHorizontal2.setTextureSize(256, 128);
        this.sideHorizontal2.mirror = true;
        this.setRotation(this.sideHorizontal2, 0F, 0F, 0F);
        this.pole = new Cuboid(this, 94, 50);
        this.pole.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3);
        this.pole.setRotationPoint(0F, 0F, 0F);
        this.pole.setTextureSize(256, 128);
        this.pole.mirror = true;
        this.setRotation(this.pole, 0F, 0F, 0F);
    }

    private void setRotation(Cuboid model, float x, float y, float z) {
        model.rotationPointX = x;
        model.rotationPointY = y;
        model.rotationPointZ = z;
    }

    public void renderPanel() {
        this.panelMain.render(0.0625F);
        this.sideHorizontal0.render(0.0625F);
        this.sideVertical0.render(0.0625F);
        this.sideVertical2.render(0.0625F);
        this.sideVertical1.render(0.0625F);
        this.sideHorizontal1.render(0.0625F);
        this.sideHorizontal3.render(0.0625F);
        this.sideHorizontal2.render(0.0625F);
    }

    public void renderPole() {
        this.pole.render(0.0625F);
    }
}