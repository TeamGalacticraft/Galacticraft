package com.hrznstudio.galacticraft.api.rocket;

public enum LaunchStage {
    IDLE(0),
    WARNING(1),
    IGNITED(2),
    LAUNCHED(3),
    FAILED(4); //ohno :(

    public final int level;

    LaunchStage(int i) {
        level = i;
    }

    public LaunchStage next() {
        if (this.level < LAUNCHED.level) {
            return LaunchStage.values()[level + 1];
        } else {
            return LAUNCHED;
        }
    }
}
