package io.github.teamgalacticraft.galacticraft;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer {

    public static Logger logger = LogManager.getLogger("Galacticraft");

    @Override
    public void onInitialize() {
        logger.info("Initializing Galacticraft");
    }
}
