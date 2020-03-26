package com.hrznstudio.galacticraft.hooks;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.config.ConfigManager;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return parent -> ConfigManager.getInstance().getScreen(parent);
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }
}
