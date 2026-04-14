/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.datafix;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.datafix.types.TileFixer;

public class PlanetsTileEntityFixer extends TileFixer
{

    public PlanetsTileEntityFixer()
    {
        super(Constants.MOD_ID_PLANETS);
        putFixEntry("GC Beam Reflector");
        putFixEntry("GC Beam Receiver");
        putFixEntry("GC Short Range Telepad");
        putFixEntry("GC Fake Short Range Telepad");
        putFixEntry("GC Astro Miner Base Builder");
        putFixEntry("GC Astro Miner Base");
        putFixEntry("GC Venus Spout");
        putFixEntry("GC Venus Dungeon Spawner");
        putFixEntry("GC Tier 3 Treasure Chest");
        putFixEntry("GC Geothermal Generator");
        putFixEntry("GC Crashed Probe");
        putFixEntry("GC Solar Array Module");
        putFixEntry("GC Solar Array Controller");
        putFixEntry("GC Laser Turret");
        putFixEntry("GC Slimeling Egg");
        putFixEntry("GC Tier 2 Treasure Chest");
        putFixEntry("GC Planet Terraformer");
        putFixEntry("GC Cryogenic Chamber");
        putFixEntry("GC Gas Liquefier");
        putFixEntry("GC Methane Synthesizer");
        putFixEntry("GC Water Electrolyzer");
        putFixEntry("GC Mars Dungeon Spawner");
        putFixEntry("GC Launch Controller");
    }
}
