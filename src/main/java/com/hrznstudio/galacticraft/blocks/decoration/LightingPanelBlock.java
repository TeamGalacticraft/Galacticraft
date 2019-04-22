package com.hrznstudio.galacticraft.blocks.decoration;

import com.hrznstudio.galacticraft.api.blocks.AbstractDirectionalBlock;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class LightingPanelBlock extends AbstractDirectionalBlock {

    private float lightLevel = 6.0f;

    public LightingPanelBlock(Settings settings) {
        super(settings);
        settings.strength(2.0f, 2.0f);
    }

    public LightingPanelBlock(Settings settings, float lightLevel) {
        super(settings);
        settings.strength(2.0f, 2.0f);
        this.lightLevel = lightLevel;
    }

}
