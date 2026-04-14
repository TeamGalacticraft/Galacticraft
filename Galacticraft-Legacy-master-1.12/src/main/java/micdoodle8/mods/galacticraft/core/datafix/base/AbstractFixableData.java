/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.datafix.base;

import net.minecraft.util.datafix.IFixableData;

public abstract class AbstractFixableData implements IFixableData
{

    private final GCFix fix;

    protected AbstractFixableData(GCFix fix)
    {
        this.fix = fix;
    }

    public GCFix getGCFix()
    {
        return this.fix;
    }

    @Override
    public int getFixVersion()
    {
        return this.fix.getVersion();
    }

}
