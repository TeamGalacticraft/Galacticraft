/*
 * Copyright (c) 2019-2021 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.misc.capes;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.capes.CapeListener;
import dev.galacticraft.mod.api.capes.models.CapePlayer;
import dev.galacticraft.mod.api.capes.models.CapesModel;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class JsonCapes implements CapeListener {

    private static final Logger LOGGER = Galacticraft.LOGGER;
    private final Map<UUID, CapePlayer> capePlayers = new HashMap<>();

    private boolean capesLoaded = false;

    public Map<UUID, CapePlayer> getCapePlayers() {
        return this.capePlayers;
    }

    @Override
    public void loadCapes(CapesModel capesModel) {
        for (CapePlayer player : capesModel.getPlayers()) {
            capePlayers.put(player.getUuid(), player);
        }

        this.capesLoaded = true;
        LOGGER.info("Loaded Capes");
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
        MOON("moon"),
        JUPITER("jupiter"),
        SATURN("saturn"),
        URANUS("uranus"),
        NEPTUNE("neptune"),
        DEVELOPER("developer"),
        ;

        final String key;

        Cape(String key) {
            this.key = key;
        }

        public static Cape valueOfIgnoreCase(String key) {
            return valueOf(key.toUpperCase());
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
