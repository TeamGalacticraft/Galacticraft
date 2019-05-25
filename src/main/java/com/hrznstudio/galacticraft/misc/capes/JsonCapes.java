package com.hrznstudio.galacticraft.misc.capes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.GalacticraftClient;
import com.hrznstudio.galacticraft.api.capes.CapeListener;
import com.hrznstudio.galacticraft.api.capes.models.CapePlayer;
import com.hrznstudio.galacticraft.api.capes.models.CapesModel;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class JsonCapes implements CapeListener {

    private static final Logger LOGGER = Galacticraft.logger;
    private Map<UUID, CapePlayer> capePlayers = new HashMap<>();

    private boolean capesLoaded = false;

    public Map<UUID, CapePlayer> getCapePlayers() {
        return this.capePlayers;
    }

    @Override
    public void loadCapes(CapesModel capesModel) {
        for(CapePlayer player : capesModel.getPlayers()) {
            capePlayers.put(player.getUuid(), player);
        }

        this.capesLoaded = true;
        LOGGER.info("[Galacticraft] Loaded Capes");
    }

    public boolean areCapesLoaded() {
        return this.capesLoaded;
    }

    public enum Cape {
        SUN("sun"),
        MERCURY("mercury"),
        VENUS("venus"),
        EARTH("earth"),
        MARS("mars"),
        JUPITER("jupiter"),
        SATURN("saturn"),
        URANUS("uranus"),
        NEPTUNE("neptune"),
        DEVELOPER("developer"),
        ;

        String key;
        Cape(String key) {
            this.key = key;
        }

        public boolean equals(String key) {
            return this.key.equals(key);
        }

        public boolean equals(Cape cape) {
            return this.key.equals(cape.key);
        }

        public Identifier getTexture() {
            return new Identifier(Constants.MOD_ID, "textures/cape/cape_" + this.key + ".png");
        }
    }
}
