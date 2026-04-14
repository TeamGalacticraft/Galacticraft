/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;

public interface ISortableBlock
{

    EnumSortCategoryBlock getCategory(int meta);
}
