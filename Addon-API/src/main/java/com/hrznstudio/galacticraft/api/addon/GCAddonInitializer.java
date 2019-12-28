package com.hrznstudio.galacticraft.api.addon;


public interface GCAddonInitializer {
    /**
     * Initialize Galacticraft Addon.
     * @return success or failure (true = load success, false = load failure)
     */
    boolean onInitialize();

    /**
     * The addon's mod id.
     * @return the addon's mod id
     */
    String getModId();
}
