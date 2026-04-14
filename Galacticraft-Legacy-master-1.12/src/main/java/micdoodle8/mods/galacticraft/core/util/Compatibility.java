/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util;

import java.util.Optional;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public enum Compatibility
{

    ACTUALLYADDITIONS("actuallyadditions"),
    APPLIEDENERGISTICS2("appliedenergistics2"),
    BIOMESOPLENTY("biomesoplenty"),
    BUILDCRAFT("buildcraftcore"),
    BUILDCRAFT_TRANSPORT("buildcrafttransport"),
    BUILDCRAFT_ENERGY("buildcraftenergy"),
    CUBIC_CHUNKS("cubicchunks"),
    ENDERIO("enderio"),
    GRECHTECH("gregtech", "gregtech_addon"),
    INDUSTRIALCRAFT("ic2"),
    JEI("jei"),
    MATTEROVERDRIVE("matteroverdrive"),
    MEKANISM("mekanism"),
    PLAYER_API("PlayerAPI"),
    RENDER_PLAYER_API("RenderPlayerAPI"),
    PNEUMATICCRAFT("pneumaticcraft"),
    SPONGEFORGE("spongeforge"),
    TINKERS_CONSTRUCT("tconstruct"),
    WAILA("waila");


    private final String                 modid;
    private final String                 displayName;
    private final boolean                isLoaded;
    private final Optional<ModContainer> modContainer;

    Compatibility(String modid, String altModid)
    {
        final boolean loaded = (Loader.isModLoaded(modid) || Loader.isModLoaded(altModid));
        this.modid = modid;
        this.isLoaded = loaded;
        this.modContainer = Loader.instance().getModList().stream().filter(m -> m.getModId().equals(modid)).findFirst();
        this.displayName = loaded ? modContainer.get().getName() : "";
    }

    Compatibility(String modid)
    {
        final boolean loaded = Loader.isModLoaded(modid);
        this.modid = modid;
        this.isLoaded = loaded;
        this.modContainer = Loader.instance().getModList().stream().filter(m -> m.getModId().equals(modid)).findFirst();
        this.displayName = loaded ? modContainer.get().getName() : "";
    }

    public String modid()
    {
        return modid;
    }

    public String displayName()
    {
        return displayName;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public String getVersion()
    {
        if (modContainer.isPresent())
        {
            return modContainer.get().getVersion();
        }
        return "unspecified";
    }
}
