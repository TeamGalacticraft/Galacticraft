package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.api.space.CelestialBody;
import net.minecraft.entity.Entity;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GravityUtil {

    public static void updatePosition(Entity entity, double motionX, double motionY, double motionZ) {
        if (entity.world.dimension instanceof CelestialBody) {
            if (!entity.noClip) {

            }
        }
    }
}
