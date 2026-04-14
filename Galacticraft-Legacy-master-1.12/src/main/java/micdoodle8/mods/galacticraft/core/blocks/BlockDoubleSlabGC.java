/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.Material;

public class BlockDoubleSlabGC extends BlockSlabGC
{

    public BlockDoubleSlabGC(String name, Material material)
    {
        super(material);
        this.setTranslationKey(name);
    }

    @Override
    public boolean isDouble()
    {
        return true;
    }
}
