package com.hrznstudio.galacticraft.api.space;

import net.minecraft.util.Identifier;

/**
 * @author Joe van der Zwet (https://joezwet.me)
 */
public interface CelestialBodyIcon {
    Identifier getTexture();
    int getX();
    int getY();
    int getWidth();
    int getHeight();
}
