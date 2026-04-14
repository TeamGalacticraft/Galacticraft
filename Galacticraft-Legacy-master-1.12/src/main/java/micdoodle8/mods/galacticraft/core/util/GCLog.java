/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated
@ForRemoval(deadline = "4.1.0")
public class GCLog
{

    private static Logger log = LogManager.getFormatterLogger("Deprecated-GCLog");

    public static void info(String message)
    {
        log.info(message);
    }
    
    public static void info(String message, Object... args)
    {
        log.info(message, args);
    }

    public static void error(String message)
    {
        log.error(message);
    }
    
    public static void error(Exception e, String message)
    {
        log.error(message, e);
    }

    public static void debug(String message)
    {
        if (ConfigManagerCore.enableDebug)
        {
            log.debug(message);
        }
    }

    public static void exception(Exception e)
    {
        log.catching(e);
    }
    
    public static void severe(String message)
    {
        error(message);
    }
}
