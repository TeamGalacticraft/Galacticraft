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

package dev.galacticraft.mod.client.render.entity.feature.gear;

import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public enum OxygenMaskTextureOffset {
    NORMAL(0, 0),
    WHITE(40, 0),
    GREY(80, 0),
    BLACK(0, 20),
    ORANGE(40, 20),
    MAGENTA(80, 20),
    LIGHT_BLUE(0, 40),
    YELLOW(40, 40),
    LIME(80, 40),
    PINK(0, 60),
    LIGHT_GREY(40, 60),
    CYAN(80, 60),
    PURPLE(0, 80),
    BLUE(40, 80),
    BROWN(80, 80),
    GREEN(0, 100),
    RED(40, 100);

    private static final HashMap<DyeColor, OxygenMaskTextureOffset> colorMap;

    static {
        colorMap = new HashMap<>();
        colorMap.put(DyeColor.WHITE, OxygenMaskTextureOffset.WHITE);
        colorMap.put(DyeColor.GRAY, OxygenMaskTextureOffset.GREY);
        colorMap.put(DyeColor.BLACK, OxygenMaskTextureOffset.BLACK);
        colorMap.put(DyeColor.ORANGE, OxygenMaskTextureOffset.ORANGE);
        colorMap.put(DyeColor.MAGENTA, OxygenMaskTextureOffset.MAGENTA);
        colorMap.put(DyeColor.LIGHT_BLUE, OxygenMaskTextureOffset.LIGHT_BLUE);
        colorMap.put(DyeColor.YELLOW, OxygenMaskTextureOffset.YELLOW);
        colorMap.put(DyeColor.LIME, OxygenMaskTextureOffset.LIME);
        colorMap.put(DyeColor.PINK, OxygenMaskTextureOffset.PINK);
        colorMap.put(DyeColor.LIGHT_GRAY, OxygenMaskTextureOffset.LIGHT_GREY);
        colorMap.put(DyeColor.CYAN, OxygenMaskTextureOffset.CYAN);
        colorMap.put(DyeColor.PURPLE, OxygenMaskTextureOffset.PURPLE);
        colorMap.put(DyeColor.BLUE, OxygenMaskTextureOffset.BLUE);
        colorMap.put(DyeColor.BROWN, OxygenMaskTextureOffset.BROWN);
        colorMap.put(DyeColor.GREEN, OxygenMaskTextureOffset.GREEN);
        colorMap.put(DyeColor.RED, OxygenMaskTextureOffset.RED);
    }

    public final int X;
    public final int Y;

    OxygenMaskTextureOffset(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public static int getX(@Nullable DyeColor color) {
        return (color != null) ? colorMap.get(color).X : NORMAL.X;
    }

    public static int getY(@Nullable DyeColor color) {
        return (color != null) ? colorMap.get(color).Y : NORMAL.Y;
    }

    public static int getWidth() {
        return 40;
    }

    public static int getHeight() {
        return 20;
    }
}
