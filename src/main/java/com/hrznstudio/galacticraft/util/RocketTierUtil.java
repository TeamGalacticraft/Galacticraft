package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.api.space.RocketTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketTierUtil {

    public static RocketTier createTier(int weight) {
        return () -> weight;
    }
}
