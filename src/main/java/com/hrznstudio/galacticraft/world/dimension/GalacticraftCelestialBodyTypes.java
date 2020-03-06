package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.api.addon.AddonRegistry;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyDisplayInfo;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftCelestialBodyTypes {
public static final CelestialBodyType THE_MOON = register(
        new CelestialBodyType.Builder(new Identifier("galacticraft-rewoven", "the_moon"))
                .translationKey("ui.galacticraft-rewoven.bodies.the_moon")
                .dimension(GalacticraftDimensions.MOON)
                .parent(CelestialBodyType.EARTH)
                .weight(1)
                .gravity(0.16f)
                .display(
                        new CelestialBodyDisplayInfo.Builder()
                                .texture(new Identifier("galacticraft-rewoven", "planet_icons"))
                                .distance(5d)
                                .time(46656000d) // 27 mc days in ticks
                                .build()
                )
                .build()
);

    private static CelestialBodyType register(CelestialBodyType type) {
        return Registry.register(AddonRegistry.CELESTIAL_BODIES, type.getId(), type);
    }

    public static void init() {}
}
