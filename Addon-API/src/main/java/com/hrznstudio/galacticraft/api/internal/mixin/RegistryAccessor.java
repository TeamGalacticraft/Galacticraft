package com.hrznstudio.galacticraft.api.internal.mixin;

import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin({Registry.class})
public abstract class RegistryAccessor {
    @Shadow
    public static <T> Registry<T> create(String string_1, Supplier<T> supplier_1) {
        // if an @Accessor, @Invoker or @Shadow method isn't abstract the body is ignored.
        return null;
    }
}
