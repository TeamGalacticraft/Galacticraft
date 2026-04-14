/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.datafix.base;

import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;

public enum GCFix
{

    TILE_ENTITIES(FixTypes.BLOCK_ENTITY);

    private IFixType fixType;

    GCFix(IFixType fixType)
    {
        this.fixType = fixType;
    }

    public IFixType geFixType()
    {
        return fixType;
    }

    public int getVersion()
    {
        return this.ordinal() + 1;
    }
}
