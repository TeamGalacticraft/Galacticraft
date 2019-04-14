package io.github.teamgalacticraft.galacticraft.world.gen.decorator;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;

public class GalacticraftDecorators {
    public static final Decorator<CraterDecoratorConfig> CRATER = Registry.register(Registry.DECORATOR, "water_lake", new CraterDecorator(CraterDecoratorConfig::deserialize));

    public static void init() {

    }
}
