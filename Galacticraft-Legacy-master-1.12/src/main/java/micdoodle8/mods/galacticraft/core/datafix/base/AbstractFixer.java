/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.datafix.base;

import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class AbstractFixer
{

    protected CompoundDataFixer compoundDataFixer;
    protected ModFixs                 modFixs;

    public AbstractFixer(String modId, int version)
    {
        this.compoundDataFixer = FMLCommonHandler.instance().getDataFixer();
        this.modFixs = compoundDataFixer.init(modId, version);
    }

    public abstract void registerAll();

    protected void registerFixer(AbstractFixableData fixer)
    {
        modFixs.registerFix(fixer.getGCFix().geFixType(), fixer);
    }

}
