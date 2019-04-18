package io.github.teamgalacticraft.galacticraft.api.entry;

import net.minecraft.util.math.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class EarthReentryPathfinder {

    private final Position landingPosition;
    private final Position startingPosition;
    private Map<Integer, Position> points = new HashMap<>();

    public EarthReentryPathfinder(Position landingPosition, Position startingPosition) {
        this.landingPosition = landingPosition;
        this.startingPosition = startingPosition;
    }

}
