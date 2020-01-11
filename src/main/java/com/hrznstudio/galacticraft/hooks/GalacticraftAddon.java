package com.hrznstudio.galacticraft.hooks;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.addon.AddonInitializer;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftCelestialBodyTypes;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftGases;

public class GalacticraftAddon implements AddonInitializer {

    @Override
    public void onAddonInitialize() {
        long startAddonInit = System.currentTimeMillis();
        Galacticraft.logger.info("[Galacticraft] Started loading addon core.");

        GalacticraftGases.init();
        GalacticraftCelestialBodyTypes.init();

        Galacticraft.logger.info("[Galacticraft] Finished loading addon core. (Took {}ms)", System.currentTimeMillis() - startAddonInit);
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }
}
