package com.hrznstudio.galacticraft.api.capes.models;


import com.hrznstudio.galacticraft.misc.capes.JsonCapes;

import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CapePlayer {

    private UUID uuid;
    private String name;
    private JsonCapes.Cape cape;

    public CapePlayer(String uuid, String name, String cape) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.cape = JsonCapes.Cape.valueOf(cape);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public JsonCapes.Cape getCape() {
        return cape;
    }
}
