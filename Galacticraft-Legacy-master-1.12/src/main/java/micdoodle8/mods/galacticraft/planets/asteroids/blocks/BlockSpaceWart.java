/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import net.minecraft.block.BlockNetherWart;

public class BlockSpaceWart extends BlockNetherWart
{

    public BlockSpaceWart(String assetName)
    {
        super();
        this.setTickRandomly(false);
        this.setTranslationKey(assetName);
    }
}
