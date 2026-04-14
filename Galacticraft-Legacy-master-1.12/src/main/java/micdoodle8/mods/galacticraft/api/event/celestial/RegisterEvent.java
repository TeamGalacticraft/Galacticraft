/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.event.celestial;

import micdoodle8.mods.galacticraft.api.galaxies.ICelestial;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RegisterEvent extends Event
{

    public ICelestial celestialObject;

    public RegisterEvent(ICelestial celestialObject, ModContainer mod)
    {
        this.celestialObject = celestialObject;
        celestialObject.setOwnerId(mod.getModId());
    }
}
