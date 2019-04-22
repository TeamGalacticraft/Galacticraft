package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.api.configurable.SideOptions;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BlockOptionUtils {

    public static Map<Direction, SideOptions> getDefaultSideOptions() {
        Map<Direction, SideOptions> map = new HashMap<>();
        for (Direction direction : Direction.values()) {
            map.put(direction, SideOptions.BLANK);
        }
        return map;
    }
}
