package com.hrznstudio.galacticraft.config;

import com.google.gson.annotations.Expose;
import com.hrznstudio.galacticraft.api.config.Config;

public class ConfigImpl implements Config {

    @Expose private boolean debugLog = false;

    @Override
    public boolean isDebugLogEnabled() {
        return this.debugLog;
    }

    @Override
    public void setDebugLog(boolean flag) {
        this.debugLog = flag;
    }
}
