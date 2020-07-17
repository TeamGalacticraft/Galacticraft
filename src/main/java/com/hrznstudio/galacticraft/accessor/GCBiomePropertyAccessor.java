package com.hrznstudio.galacticraft.accessor;

import com.hrznstudio.galacticraft.api.biome.BiomeProperty;
import com.hrznstudio.galacticraft.api.biome.BiomePropertyType;

import java.util.Map;

public interface GCBiomePropertyAccessor {
    <T> T getProperty(BiomePropertyType<T> type);

    <T> void setProperty(BiomePropertyType<T> type, T value);

    Map<BiomePropertyType<?>, BiomeProperty<?>> getProperties();
}
