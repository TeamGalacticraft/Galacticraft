/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.miccore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManagerMicCore
{

    public static boolean loaded;

    static Configuration  configuration;

    public static boolean enableDebug;

    public static void init()
    {
        File oldCoreConfig = new File(MicdoodlePlugin.canonicalConfigDir, "Galacticraft/miccore.conf");
        File coremodConfig = new File(MicdoodlePlugin.canonicalConfigDir, "Galacticraft/MicdoodleCore.cfg");

        if (!ConfigManagerMicCore.loaded)
        {
            if (oldCoreConfig.exists())
            {
                ConfigManagerMicCore.configuration = ConfigManagerMicCore.handleConfigFileMove(new Configuration(oldCoreConfig), coremodConfig);
                oldCoreConfig.delete();
            }
            else
            {
                ConfigManagerMicCore.configuration = new Configuration(coremodConfig);
            }
        }

        ConfigManagerMicCore.configuration.load();
        ConfigManagerMicCore.syncConfig();
    }

    private static Configuration handleConfigFileMove(Configuration oldConfig, File newFile)
    {
        Property logDebugOutput = oldConfig.get(Configuration.CATEGORY_GENERAL, "Enable Debug messages", false);

        Configuration newConfig = new Configuration(newFile);
        newConfig.get(Configuration.CATEGORY_GENERAL, "logDebugOutput", logDebugOutput.getBoolean(), "If `true` Enable debug messages during Galacticraft bytecode injection at startup");
        newConfig.save();

        return newConfig;
    }

    public static void syncConfig()
    {
        try
        {
            ConfigManagerMicCore.enableDebug = ConfigManagerMicCore.configuration.get(Configuration.CATEGORY_GENERAL, "logDebugOutput", false, "If `true` Enable debug messages during Galacticraft bytecode injection at startup").getBoolean(false);
        } catch (final Exception e)
        {
            MicdoodlePlugin.miccoreLogger.error("Problem loading core config (\"MicdoodleCore.conf\")");
        } finally
        {
            if (ConfigManagerMicCore.configuration.hasChanged())
            {
                ConfigManagerMicCore.configuration.save();
            }

            ConfigManagerMicCore.loaded = true;
        }
    }
}
