package com.hrznstudio.galacticraft.api.rocket;

public enum LaunchStage {
    IDLE,
    WARNING,
    IGNITED,
    LAUNCHED,
    FAILED;


    public LaunchStage next() {
        if (this.ordinal() < LAUNCHED.ordinal()) {
            return LaunchStage.values()[ordinal() + 1];
        } else {
            return LAUNCHED;
        }
    }
}
