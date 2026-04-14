/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.transmission.tile;

public interface IBufferTransmitter<N> extends ITransmitter
{

    N getBuffer();

    int getCapacity();
}
