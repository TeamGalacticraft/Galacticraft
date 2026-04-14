/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets;

import java.util.List;
import micdoodle8.mods.galacticraft.core.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public interface IPlanetsModule
{

    public void preInit(FMLPreInitializationEvent event);

    public void init(FMLInitializationEvent event);

    public void postInit(FMLPostInitializationEvent event);

    public void serverInit(FMLServerStartedEvent event);

    public void serverStarting(FMLServerStartingEvent event);

    public void getGuiIDs(List<Integer> idList);

    public Object getGuiElement(Side side, int ID, EntityPlayer player, World world, int x, int y, int z);

    public Configuration getConfiguration();

    public void syncConfig();

    public default void register(Class<? extends TileEntity> tileEntityClass, String key)
    {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(Constants.MOD_ID_PLANETS, key));
    }
}
