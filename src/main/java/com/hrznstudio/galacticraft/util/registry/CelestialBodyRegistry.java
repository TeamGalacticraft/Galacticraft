package com.hrznstudio.galacticraft.util.registry;

import com.hrznstudio.galacticraft.api.space.CelestialBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joe van der Zwet (https://joezwet.me)
 */
public class CelestialBodyRegistry {

    public static List<CelestialBody> bodies = new ArrayList<>();

    public static CelestialBody register(CelestialBody celestialBody) {
        if(!bodies.contains(celestialBody)) {
            bodies.add(celestialBody);
        }
        return celestialBody;
    }
}
