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

package dev.galacticraft.mod.client.render.dimension;

import dev.galacticraft.mod.api.dimension.GalacticDimensionEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MarsDimensionEffects extends GalacticDimensionEffects {
    public static final MarsDimensionEffects INSTANCE =
            new MarsDimensionEffects(Float.NaN, true, SkyType.NORMAL, false, false);

    private final Minecraft minecraft = Minecraft.getInstance();

    public MarsDimensionEffects(float cloudLevel, boolean hasGround, SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    @Override
    public boolean isFoggyAt(int camX, int camY) {
        return false;
    }

    public static float marsNightFactor(ClientLevel level, float partialTicks) {
        // Vanilla sky angle [0,1)
        float t = level.getTimeOfDay(partialTicks);

        // Sun height: +1 = noon, 0 = horizon, -1 = midnight
        float sunHeight = Mth.cos(t * Mth.TWO_PI);

        // When is it still definitely full day?
        float fullDayThreshold   = 0.1F;

        // When do we consider it fully night?
        float fullNightThreshold = -0.1F;

        if (sunHeight >= fullDayThreshold) {
            // Full day
            return 0.0F;
        }
        if (sunHeight <= fullNightThreshold) {
            // Full night
            return 1.0F;
        }

        // Map sunHeight from [fullDayThreshold .. fullNightThreshold] to [0..1]
        float x = (fullDayThreshold - sunHeight) / (fullDayThreshold - fullNightThreshold);
        x = Mth.clamp(x, 0.0F, 1.0F);

        // Smoothstep
        return x * (2.0F - x);
    }

    private static float warmEdgeFromNightFactor(float night) {
        // night = 0 (day), 1 (night).
        // Make a bell curve: 0 → 1 → 0 with peak at twilight (night ≈ 0.5)
        float x = 1.0F - Math.abs(night - 0.5F) * 2.0F; // 0..1..0
        return Mth.clamp(x, 0.0F, 1.0F);
    }

    @Override
    public Vec3 getFogColor(ClientLevel level, float partialTicks, Vec3 cameraPos, CubicSampler.Vec3Fetcher fetcher) {
        float night = marsNightFactor(level, partialTicks);
        float day   = 1.0F - night;

        // Day fog: dusty orange-brown
        float dayR   = 0xBE / 255.0F;
        float dayG   = 0x78 / 255.0F;
        float dayB   = 0x50 / 255.0F;

        // Night fog: dark brown
        float nightR = 0x28 / 255.0F;
        float nightG = 0x18 / 255.0F;
        float nightB = 0x10 / 255.0F;

        float r = dayR * day + nightR * night;
        float g = dayG * day + nightG * night;
        float b = dayB * day + nightB * night;

        // Mild warm boost near sunrise/sunset
        float edge = warmEdgeFromNightFactor(night) * 0.6F;

        float warmR = 0xE6 / 255.0F;
        float warmG = 0x96 / 255.0F;
        float warmB = 0x50 / 255.0F;

        r = Mth.lerp(edge, r, warmR);
        g = Mth.lerp(edge, g, warmG);
        b = Mth.lerp(edge, b, warmB);

        return new Vec3(r, g, b);
    }

    @Override
    public Vec3 getSkyColor(ClientLevel level, float partialTicks) {
        float night = marsNightFactor(level, partialTicks);
        float day   = 1.0F - night;


        float dayR   = 0xD2 / 255.0F;
        float dayG   = 0x8C / 255.0F;
        float dayB   = 0x5A / 255.0F;


        float nightR = 0x32 / 255.0F;
        float nightG = 0x21 / 255.0F;
        float nightB = 0x17 / 255.0F;

        float r = dayR * day + nightR * night;
        float g = dayG * day + nightG * night;
        float b = dayB * day + nightB * night;


        float edge = warmEdgeFromNightFactor(night) * 0.8F;

        float warmR = 0xF2 / 255.0F;
        float warmG = 0xA4 / 255.0F;
        float warmB = 0x5A / 255.0F;

        r = Mth.lerp(edge, r, warmR);
        g = Mth.lerp(edge, g, warmG);
        b = Mth.lerp(edge, b, warmB);

        return new Vec3(r, g, b);
    }

    @Override
    public boolean tickRain(ClientLevel level, Camera camera, int ticks) {
        return false;
    }
}
