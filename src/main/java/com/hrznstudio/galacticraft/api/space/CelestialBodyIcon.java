package com.hrznstudio.galacticraft.api.space;

import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface CelestialBodyIcon {
    Identifier getTexture();
    int getX();
    int getY();
    int getWidth();
    int getHeight();
}
