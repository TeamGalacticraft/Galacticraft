/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.api.dimension;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class GalacticDimensionEffects extends DimensionSpecialEffects {
    public GalacticDimensionEffects(float cloudLevel, boolean hasGround, SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    public float getStarBrightness(Level level, float par1) {
        float timeOfDay = level.getTimeOfDay(par1);
        float f2 = 1.0F - (Mth.cos(timeOfDay * Mth.TWO_PI) * 2.0F + 0.25F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        return f2 * f2 * 0.75F;
    }

    public abstract Vec3 getFogColor(ClientLevel level, float partialTicks, Vec3 cameraPos, CubicSampler.Vec3Fetcher fetcher);

    public abstract Vec3 getSkyColor(ClientLevel level, float partialTicks);

    public abstract boolean tickRain(ClientLevel level, Camera camera, int ticks);

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return null;
    }
}
