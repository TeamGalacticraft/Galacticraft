/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.datafix.types;

import java.util.HashMap;
import java.util.Map;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.datafix.base.AbstractFixableData;
import micdoodle8.mods.galacticraft.core.datafix.base.GCFix;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class TileFixer extends AbstractFixableData
{

    private final Map<String, String> tileEntityNames = new HashMap<>();
    private final String              modid;

    protected TileFixer(String modId)
    {
        super(GCFix.TILE_ENTITIES);
        this.modid = modId;
    }

    protected void putFixEntry(String from)
    {
        putFixEntry(from, from.replaceAll(" ", "_").toLowerCase());
    }

    protected void putFixEntry(String from, String to)
    {
        String toLocation = modid + ":" + to;
        tileEntityNames.put("minecraft:" + from.toLowerCase(), toLocation);
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound nbt)
    {
        String loc = nbt.getString("id");

        if (tileEntityNames.containsKey(loc))
        {
            String id = tileEntityNames.get(loc);
            nbt.setString("id", tileEntityNames.get(loc));
            if (ConfigManagerCore.enableDebug)
            {
                BlockPos p = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
                GalacticraftCore.logger.info("DataFixer @ {} -> {}", p.toString(), p.toString(), id);
            }
        }
        return nbt;
    }
}
