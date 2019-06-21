package com.hrznstudio.galacticraft.api.capes.models;


import com.hrznstudio.galacticraft.misc.capes.JsonCapes;

import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CapePlayer {

    private UUID uuid;
    private String name;
    private String cape;

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public JsonCapes.Cape getCape() {
        JsonCapes.Cape cape = JsonCapes.Cape.valueOfIgnoreCase(this.cape);
        if(cape == null) return JsonCapes.Cape.EARTH;
        else return cape;
    }
}
