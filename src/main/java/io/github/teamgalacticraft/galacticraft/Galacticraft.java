package io.github.teamgalacticraft.galacticraft;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.config.ConfigHandler;
import io.github.teamgalacticraft.galacticraft.container.GalacticraftContainers;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.fluids.GalacticraftFluids;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.misc.Capes;
import io.github.teamgalacticraft.galacticraft.sounds.GalacticraftSounds;
import io.github.teamgalacticraft.galacticraft.world.biome.GCBiomes;
import io.github.teamgalacticraft.galacticraft.world.gen.OreGenerator;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.ModUpdateChecker;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.ModUpdateListener;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.UpdateInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer, ModUpdateListener {

    public static Logger logger = LogManager.getLogger("Galacticraft-Rewoven");
    private static final Marker GALACTICRAFT = MarkerManager.getMarker("Galacticraft");

    private static ConfigHandler configHandler = new ConfigHandler();
    private ModUpdateChecker modUpdateChecker = new ModUpdateChecker(
            Constants.MOD_ID,
            "https://raw.githubusercontent.com/teamgalacticraft/Galacticraft-Rewoven/master/updates.json",
            true
    );

    @Override
    public void onInitialize() {
        logger.info(GALACTICRAFT, "[Galacticraft] Initializing...");

        GalacticraftBlocks.register();
        GalacticraftItems.register();
        GalacticraftFluids.register();
        GalacticraftSounds.register();
        GalacticraftEnergy.register();
        GalacticraftContainers.register();
        GCBiomes.register();
        OreGenerator.register();
        Capes.updateCapeList();

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, Constants.MOD_ID, (Runnable) () -> configHandler.openConfigScreen());
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                logger.error("[Galacticraft] Failed to add modmenu config override. {1}", e);
            }
        }

    }

    @Override
    public void onUpdate(UpdateInfo updateInfo) {
        if (updateInfo.getStatus() == UpdateInfo.VersionStatus.OUTDATED) {
            logger.info("Galacticraft: Rewoven is outdated.");
        }
    }
}
