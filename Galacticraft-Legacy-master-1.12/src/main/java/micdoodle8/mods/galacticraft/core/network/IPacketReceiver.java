/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.network;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;

public interface IPacketReceiver
{

    /**
     * Note this can be called during the init constructor of the entity's
     * superclass, if this is a subclass of the IPacketReceiver entity. So make
     * sure any fields referenced in getNetworkedData() are either in the
     * superclass, or add some null checks!!
     */
    void getNetworkedData(ArrayList<Object> sendData);

    void decodePacketdata(ByteBuf buffer);
}
