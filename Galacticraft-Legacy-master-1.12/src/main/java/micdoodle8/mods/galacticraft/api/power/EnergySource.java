/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.power;

import java.util.List;
import net.minecraft.util.EnumFacing;

public abstract class EnergySource
{

    public static class EnergySourceWireless extends EnergySource
    {

        public final List<ILaserNode> nodes;

        public EnergySourceWireless(List<ILaserNode> nodes)
        {
            this.nodes = nodes;
        }
    }

    public static class EnergySourceAdjacent extends EnergySource
    {

        public final EnumFacing direction;

        public EnergySourceAdjacent(EnumFacing direction)
        {
            this.direction = direction;
        }
    }
}
