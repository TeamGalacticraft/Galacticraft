/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;

public class TileEntityCompactNasaWorkbench extends TileEntity
{

    public TileEntityCompactNasaWorkbench()
    {
    }

    public boolean onActivated(EntityPlayer entityPlayer)
    {
        entityPlayer.openGui(GalacticraftCore.instance, GuiIdsCore.NASA_WORKBENCH_ROCKET, this.world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        return true;
    }
}
