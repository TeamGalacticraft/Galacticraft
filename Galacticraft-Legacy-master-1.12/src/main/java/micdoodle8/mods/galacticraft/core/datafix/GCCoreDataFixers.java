/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.datafix;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.datafix.base.AbstractFixer;

public class GCCoreDataFixers extends AbstractFixer
{

    private static final int VERSION = 1;

    public GCCoreDataFixers()
    {
        super(Constants.MOD_ID_CORE, VERSION);
    }

    @Override
    public void registerAll()
    {
        registerFixer(new CoreTileEntityFixer());
    }

}
