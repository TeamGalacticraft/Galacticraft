/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.entity;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

/**
 * Implement into entities that can be loaded with cargo
 */
public interface ICargoEntity
{

    enum EnumCargoLoadingState
    {
        FULL, EMPTY, NOTARGET, NOINVENTORY, SUCCESS
    }

    class RemovalResult
    {

        public final EnumCargoLoadingState resultState;
        @Nonnull public final ItemStack resultStack;

        public RemovalResult(EnumCargoLoadingState resultState, @Nonnull ItemStack resultStack)
        {
            this.resultState = resultState;
            this.resultStack = resultStack;
        }
    }

    EnumCargoLoadingState addCargo(ItemStack stack, boolean doAdd);

    RemovalResult removeCargo(boolean doRemove);
}
