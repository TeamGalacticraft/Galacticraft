package com.hrznstudio.galacticraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MoonSkyProperties extends SkyProperties {
    public MoonSkyProperties() {
        super(Float.NaN, false, SkyType.NORMAL, true, true);
    }

    @Override
    public Vec3d adjustSkyColor(Vec3d color, float sunHeight) {
        return Vec3d.ZERO;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }

    @Nullable
    @Override
    public float[] getSkyColor(float skyAngle, float tickDelta) {
        return new float[]{0.0F, 0.0F, 0.0F, 0.0F};
    }

}
