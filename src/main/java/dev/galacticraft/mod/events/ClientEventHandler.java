package dev.galacticraft.mod.events;

import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.client.sounds.RocketSound;
import dev.galacticraft.mod.entity.RocketEntity;
import net.minecraft.client.Minecraft;

public class ClientEventHandler {
    public static void init() {
        RocketEvents.STAGE_CHANGED.register(ClientEventHandler::rocketLaunchSound);
    }

    public static void rocketLaunchSound(Rocket rocket, LaunchStage oldStage) {
        if (rocket instanceof RocketEntity rocketEntity && rocket.getStage() == LaunchStage.IGNITED)
            Minecraft.getInstance().getSoundManager().play(new RocketSound(rocketEntity));
    }
}
