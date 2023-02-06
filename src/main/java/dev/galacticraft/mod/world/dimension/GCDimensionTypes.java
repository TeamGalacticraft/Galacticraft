package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

public class GCDimensionTypes {
    public static final ResourceKey<DimensionType> MOON = key("moon");

    public static void bootstrapRegistries(BootstapContext<DimensionType> context) {
        context.register(MOON, new DimensionType(
                OptionalLong.empty(),
                true,
                false,
                false,
                true,
                1.0,
                false,
                false,
                -64,
                384,
                384,
                GCTags.INFINIBURN_MOON,
                GCDimensions.MOON.location(),
                0.1f,
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        ));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<DimensionType> key(@NotNull String id) {
        return Constant.key(Registries.DIMENSION_TYPE, id);
    }
}
