package com.hrznstudio.galacticraft.api.capes.models;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CapesModel {

    private List<CapePlayer> players;

    public CapesModel(List<CapePlayer> players) {
        this.players = players;
    }

    public List<CapePlayer> getPlayers() {
        return this.players;
    }
}
