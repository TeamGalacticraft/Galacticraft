package io.github.teamgalacticraft.galacticraft;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.fluids.GalacticraftFluids;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.sounds.GalacticraftSounds;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer {

    public static Logger logger = LogManager.getLogger("Galacticraft-Fabric");
    private static final Marker GALACTICRAFT = MarkerManager.getMarker("Galacticraft");

    @Override
    public void onInitialize() {
        logger.info(GALACTICRAFT, "Initializing Galacticraft");

        GalacticraftBlocks.register();
        GalacticraftItems.register();
        GalacticraftFluids.register();
        GalacticraftSounds.register();
        GalacticraftEnergy.register();

        /* This currently crashes the game when it tries to load the config menu.
        if(FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, Constants.MOD_ID, (Runnable) () -> configHandler.openConfigScreen());
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                logger.error("[Galacticraft] Failed to add modmenu config override.", e);
            }
        }
        */
    }

}
