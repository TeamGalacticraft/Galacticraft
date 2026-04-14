/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.miccore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@IFMLLoadingPlugin.TransformerExclusions(value =
{"micdoodle8.mods.miccore"})
@MCVersion("1.12.2")
public class MicdoodlePlugin implements IFMLLoadingPlugin, IFMLCallHook
{

    public static Logger       miccoreLogger = LogManager.getLogger("MicdoodleCore");
    public static boolean      hasRegistered = false;
    public static final String mcVersion     = MinecraftForge.MC_VERSION;
    public static File         mcDir;
    public static File         canonicalConfigDir;

    @Override
    public String[] getASMTransformerClass()
    {
        final String[] asmStrings =
        {"micdoodle8.mods.miccore.MicdoodleTransformer"};

        if (!MicdoodlePlugin.hasRegistered)
        {
            final List<String> asm = Arrays.asList(asmStrings);

            for (final String s : asm)
            {
                try
                {
                    final Class<?> c = Class.forName(s);

                    if (c != null)
                    {
                        miccoreLogger.info("Successfully Registered Transformer");
                    }
                }
                catch (final Exception ex)
                {
                    miccoreLogger.error("Error while running transformer " + s);
                    return null;
                }
            }

            MicdoodlePlugin.hasRegistered = true;
        }

        return asmStrings;
    }

    @Override
    public String getModContainerClass()
    {
        return "micdoodle8.mods.miccore.MicdoodleModContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "micdoodle8.mods.miccore.MicdoodlePlugin";
    }

    @Override
    public Void call() throws Exception
    {
        return null;
    }

    private static Constructor<?> sleepCancelledConstructor;
    private static Constructor<?> orientCameraConstructor;
    private static String         eventContainerClass = "micdoodle8.mods.galacticraft.core.event.EventHandlerGC";

    public static void onSleepCancelled()
    {
        try
        {
            if (MicdoodlePlugin.sleepCancelledConstructor == null)
            {
                MicdoodlePlugin.sleepCancelledConstructor = Class.forName(MicdoodlePlugin.eventContainerClass + "$SleepCancelledEvent").getConstructor();
            }

            MinecraftForge.EVENT_BUS.post((Event) MicdoodlePlugin.sleepCancelledConstructor.newInstance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void orientCamera()
    {
        try
        {
            if (MicdoodlePlugin.orientCameraConstructor == null)
            {
                MicdoodlePlugin.orientCameraConstructor = Class.forName(MicdoodlePlugin.eventContainerClass + "$OrientCameraEvent").getConstructor();
            }

            MinecraftForge.EVENT_BUS.post((Event) MicdoodlePlugin.orientCameraConstructor.newInstance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getAccessTransformerClass()
    {
        boolean deobfuscated = true;

        try
        {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        }
        catch (final Exception e)
        {}

        if (deobfuscated)
        {
            return "micdoodle8.mods.miccore.MicdoodleAccessTransformerDeObf";
        }
        return "micdoodle8.mods.miccore.MicdoodleAccessTransformer";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        if (data.containsKey("mcLocation"))
        {
            MicdoodlePlugin.mcDir = (File) data.get("mcLocation");
            File configDir = new File(mcDir, "config");
            String canonicalConfigPath;
            try
            {
                canonicalConfigPath = configDir.getCanonicalPath();
                canonicalConfigDir = configDir.getCanonicalFile();
            }
            catch (IOException ioe)
            {
                throw new LoaderException(ioe);
            }
            if (!canonicalConfigDir.exists())
            {
                miccoreLogger.debug("No config directory found, creating one: %s", canonicalConfigPath);
                boolean dirMade = canonicalConfigDir.mkdir();

                if (!dirMade)
                {
                    miccoreLogger.error("Unable to create the config directory %s", canonicalConfigPath);
                    throw new LoaderException();
                }

                miccoreLogger.info("Config directory created successfully");
            }

            ConfigManagerMicCore.init();
        }
    }
}
