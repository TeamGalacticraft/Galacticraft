package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.GCBiomePropertyAccessor;
import com.hrznstudio.galacticraft.api.biome.BiomeProperty;
import com.hrznstudio.galacticraft.api.biome.BiomePropertyType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(Biome.class)
public class BiomeMixin implements GCBiomePropertyAccessor {
    @Shadow @Final private Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
    @Unique
    private final Map<BiomePropertyType<?>, BiomeProperty<?>> properties = new HashMap<>();

    @Override
    public <T> T getProperty(@NotNull BiomePropertyType<T> type) {
        return (T)properties.getOrDefault(type, type.create()).getValue();
    }

    @Override
    public <T> void setProperty(BiomePropertyType<T> type, T value) {
        this.properties.put(type, type.create(value));
    }

    @Override
    public Map<BiomePropertyType<?>, BiomeProperty<?>> getProperties() {
        return properties;
    }
}
