package com.hrznstudio.galacticraft.api.config;

public interface Config {

    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }

    boolean isDebugLogEnabled();
    void setDebugLog(boolean flag);
}
