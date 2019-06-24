package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.api.space.RocketTier;

/**
 * @author Joe van der Zwet (https://joezwet.me)
 */
public class RocketTierUtil {

    public static RocketTier createTier(int weight) {
        return () -> weight;
    }
}
