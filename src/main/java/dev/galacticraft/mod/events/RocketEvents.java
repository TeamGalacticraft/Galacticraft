package dev.galacticraft.mod.events;

import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RocketEvents {
    Event<StageChanged> STAGE_CHANGED = EventFactory.createArrayBacked(StageChanged.class, callbacks -> (rocket, launchStage) -> {
        for (StageChanged launchCallback : callbacks)
            launchCallback.onStageChanged(rocket, launchStage);
    });

    interface StageChanged {
        void onStageChanged(Rocket rocket, LaunchStage oldStage);
    }
}
