package io.github.teamgalacticraft.galacticraft.blocks.decoration;

import io.github.teamgalacticraft.galacticraft.api.blocks.AbstractDirectionalBlock;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
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
