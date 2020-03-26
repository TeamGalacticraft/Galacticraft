package com.hrznstudio.galacticraft.api.config;

import com.hrznstudio.galacticraft.Galacticraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

public interface ConfigManager {

    static ConfigManager getInstance() {
        return Galacticraft.configManager;
    }

    void save();
    void load();
    Config get();
    @Environment(EnvType.CLIENT) Screen getScreen(Screen parent);
}
