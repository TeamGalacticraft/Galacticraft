/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.energy.tile;

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public abstract class TileBaseElectricBlockWithInventory extends TileBaseElectricBlock implements ISidedInventory, IInventoryDefaults
{

    public TileBaseElectricBlockWithInventory(String tileName)
    {
        super(tileName);
    }

    @Override
    public EnumFacing getElectricInputDirection()
    {
        return EnumFacing.byHorizontalIndex(((this.getBlockMetadata() & 3) + 1) % 4);
    }

    @Override
    public ItemStack getBatteryInSlot()
    {
        return this.getStackInSlot(0);
    }
}
