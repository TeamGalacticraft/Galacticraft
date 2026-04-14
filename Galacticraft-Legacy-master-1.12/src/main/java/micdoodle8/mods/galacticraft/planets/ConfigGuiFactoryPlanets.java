/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets;

import java.util.Set;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGuiFactoryPlanets implements IModGuiFactory
{

    public static class PlanetsConfigGUI extends GuiConfig
    {

        public PlanetsConfigGUI(GuiScreen parent)
        {
            super(parent, GalacticraftPlanets.getConfigElements(), Constants.MOD_ID_PLANETS, false, false, GCCoreUtil.translate("gc.configgui.planets.title"));
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    public GuiScreen createConfigGui(GuiScreen arg0)
    {
        // TODO Forge 2282 addition!
        return new PlanetsConfigGUI(arg0);
    }

    public boolean hasConfigGui()
    {
        return true;
    }
}
