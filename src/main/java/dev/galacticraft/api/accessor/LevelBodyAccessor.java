package dev.galacticraft.api.accessor;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public interface LevelBodyAccessor {
    default @Nullable Holder<CelestialBody<?, ?>> galacticraft$getCelestialBody() {
        throw new RuntimeException("This should be overridden by mixin!");
    }
}
