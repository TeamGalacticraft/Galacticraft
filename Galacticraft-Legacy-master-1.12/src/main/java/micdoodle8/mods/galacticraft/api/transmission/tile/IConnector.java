/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import net.minecraft.util.EnumFacing;

/**
 * Applied to TileEntities that can connect to an electrical OR oxygen network.
 *
 * @author Calclavia, micdoodle8
 */
public interface IConnector
{

    /**
     * @return If the connection is possible.
     */
    boolean canConnect(EnumFacing direction, NetworkType type);
}
