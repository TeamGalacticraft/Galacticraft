package com.hrznstudio.galacticraft.api.space;

/**
 * @author Joe van der Zwet (https://joezwet.me)
 */
public interface CelestialBody {
    /**
     *
     * @return minimum rocket tier required to access the body
     */
    RocketTier accessTier();

    /**
     *
     * @return the body that this body orbits.
     */
    CelestialBody getParent();

    /**
     *
     * @return distance from parent, used to show orbit on travel map
     */
    double getOrbitSize();

    /**
     *
     * @return gravity of the body
     */
    float getGravity();

    /**
     *
     * @return whether or not the body has breathable atmosphere.
     */
    boolean hasOxygen();

    /**
     *
     * @return the icon to display on the galaxy map
     */
    CelestialBodyIcon getIcon();

    /**
     *
     * @return translated string of body's name.
     */
    String getName();
}
