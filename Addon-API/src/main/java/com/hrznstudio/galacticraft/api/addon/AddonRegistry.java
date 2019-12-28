package com.hrznstudio.galacticraft.api.addon;

import com.hrznstudio.galacticraft.api.internal.mixin.RegistryAccessor;
import net.minecraft.util.registry.Registry;

public class AddonRegistry {
    public static final Registry<TestClassForRegistry> PLANETS =
            RegistryAccessor.create("gc_planets", () -> TestClassForRegistry.TEST_CLASS_FOR_REGISTRY);

    public static final Registry<TestClassForRegistry> ROCKETS =
            RegistryAccessor.create("gc_rockets", () -> TestClassForRegistry.TEST_CLASS_FOR_REGISTRY);
}
