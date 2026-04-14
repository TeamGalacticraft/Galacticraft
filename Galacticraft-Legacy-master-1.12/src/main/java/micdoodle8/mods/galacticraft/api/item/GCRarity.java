/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;

public interface GCRarity extends IRarity
{
    @Override
    default TextFormatting getColor()
    {
        return TextFormatting.BLUE;
    }
    
    @Override
    default String getName()
    {
        return "Space";
    }
}
