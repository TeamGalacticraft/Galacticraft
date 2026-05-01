package dev.galacticraft.impl.universe.dimension_properties;

import dev.galacticraft.dynamicdimensions.api.DynamicDimensionProperties;
import org.joml.Vector3f;

public class SpaceDimensionProperties extends DynamicDimensionProperties {
    @Override
    public int priority() {
        return 1001;
    }

    @Override
    public Vector3f baseGravity() {
        return new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public double basePressure() {
        return 0;
    }

    @Override
    public float universalDrag() {
        return 0;
    }

    @Override
    public Vector3f magneticNorth() {
        return new Vector3f(0.0f, 0.0f, 0.0f);
    }
}
