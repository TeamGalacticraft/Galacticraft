package dev.galacticraft.mod.misc.cape;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.galacticraft.mod.Galacticraft;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CapesLoader {
    public static Map PLAYERS;

    public static void load() {
        Util.getMainWorkerExecutor().execute(() -> {
            long startLoad = System.currentTimeMillis();
            Gson gson = new GsonBuilder().create();
            Galacticraft.LOGGER.info("Loading capes data...");
            try {
                PLAYERS = gson.fromJson(
                        IOUtils.toString(
                                new URL("https://raw.githubusercontent.com/StellarHorizons/Galacticraft-Rewoven/main/capes.json"),
                                Charset.defaultCharset()
                        ),
                        Map.class
                );
            } catch (IOException e) {
                Galacticraft.LOGGER.warn("Failed to load capes.", e);
            }
            Galacticraft.LOGGER.info("Loaded capes for {} players. (Took {}ms)", PLAYERS.size(), System.currentTimeMillis()-startLoad);
        });
    }
}
