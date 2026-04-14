/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.world;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionalCoord
{

    public int posX, posY, posZ;
    public int dimId;

    public DimensionalCoord(int posX, int posY, int posZ, int dimId)
    {
        this.posX  = posX;
        this.posY  = posY;
        this.posZ  = posZ;
        this.dimId = dimId;
    }

    public DimensionalCoord(double posX, double posY, double posZ, int dimId)
    {
        this((int) Math.floor(posX), (int) Math.floor(posX), (int) Math.floor(posX), dimId);
    }

    public DimensionalCoord(Entity entity)
    {
        this.posX  = (int) entity.posX;
        this.posY  = (int) entity.posY;
        this.posZ  = (int) entity.posZ;
        this.dimId = entity.world.provider.getDimension();
    }

    public DimensionalCoord(BlockPos pos, World world)
    {
        this(pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension());
    }

    public static DimensionalCoord toCoord(TileEntity tileEntity)
    {
        return new DimensionalCoord(tileEntity.getPos(), tileEntity.getWorld());
    }
}
