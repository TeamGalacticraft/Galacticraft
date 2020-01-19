package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;

public class GravityUtil {

    public static double getGravityForEntity(Entity entity) {
        Optional<CelestialBodyType> type = CelestialBodyType.getByDimType(entity.world.dimension.getType());
        if(type.isPresent()) {
            if(entity instanceof LivingEntity) {
                return type.get().getGravity() * 0.08d;
            }
        }
        return 0.08d;
    }
}
