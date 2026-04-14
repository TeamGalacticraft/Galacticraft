/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

import io.netty.buffer.ByteBuf;

public class PacketRotateRocket extends PacketBase
{

    private int entityID;
    private float entityPitch;
    private float entityYaw;

    public PacketRotateRocket()
    {
        super();
    }

    public PacketRotateRocket(Entity rotateableEntity)
    {
        super(GCCoreUtil.getDimensionID(rotateableEntity.world));
        this.entityID = rotateableEntity.getEntityId();
        this.entityPitch = rotateableEntity.rotationPitch;
        this.entityYaw = rotateableEntity.rotationYaw;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        super.encodeInto(buffer);
        buffer.writeInt(this.entityID);
        buffer.writeFloat(this.entityPitch);
        buffer.writeFloat(this.entityYaw);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        super.decodeInto(buffer);
        this.entityID = buffer.readInt();
        this.entityPitch = buffer.readFloat();
        this.entityYaw = buffer.readFloat();
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        Entity entity = player.world.getEntityByID(this.entityID);

        if (entity != null)
        {
            entity.rotationPitch = this.entityPitch;
            entity.rotationYaw = this.entityYaw;
        }
    }
}
