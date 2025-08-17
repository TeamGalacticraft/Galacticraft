package dev.galacticraft.impl.gametest;

import dev.galacticraft.machinelib.api.gametest.TestModifiers;
import net.minecraft.gametest.framework.GameTestHelper;

public class AtmosphericContext {
    private final GameTestHelper helper;

    public AtmosphericContext(GameTestHelper helper) {
        this.helper = helper;
    }

    static {
        TestModifiers.register(AtmosphereDependent.class, (arguments, helper, clazz, inst, variant, annotation) -> {
            arguments.add(new AtmosphericContext(helper));
        });
    }
}
