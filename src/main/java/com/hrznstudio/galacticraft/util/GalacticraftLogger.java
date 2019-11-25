package com.hrznstudio.galacticraft.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

public class GalacticraftLogger {
    private static final Logger logger = LogManager.getLogger("Galacticraft");
    
    public static void info(CharSequence message) {
        logger.info("[Galacticraft] " + message);
    }
    
    public static void info(CharSequence message, Object... params) {
        logger.info("[Galacticraft] " + message.toString(), params);
    }
    
    public static void info(CharSequence message, Throwable t) {
        logger.info("[Galacticraft] " + message, t);
    }
    
    public static void warn(CharSequence message) {
        logger.warn("[Galacticraft] " + message);
    }
    
    public static void warn(CharSequence message, Object... params) {
        logger.warn("[Galacticraft] " + message.toString(), params);
    }
    
    public static void warn(CharSequence message, Throwable t) {
        logger.warn("[Galacticraft] " + message, t);
    }
    
    public static void warn(Throwable t) {
        logger.warn("[Galacticraft] " + t);
    }
    
    public static void error(CharSequence message) {
        logger.error("[Galacticraft] " + message);
    }
    
    public static void error(CharSequence message, Object... params) {
        logger.error("[Galacticraft] " + message.toString(), params);
    }
    
    public static void error(CharSequence message, Throwable t) {
        logger.error("[Galacticraft] " + message, t);
    }

    public static void error(Throwable t) {
        logger.error("[Galacticraft] " + t);
    }
    
    public static void fatal(CharSequence message) {
        logger.fatal("[Galacticraft] " + message);
    }
    
    public static void fatal(CharSequence message, Object... params) {
        logger.fatal("[Galacticraft] " + message.toString(), params);
    }
    
    public static void fatal(CharSequence message, Throwable t) {
        logger.fatal("[Galacticraft] " + message, t);
    }
    
    public static void fatal(Throwable t) {
        logger.fatal("[Galacticraft] " + t);
    }
    
    
}
