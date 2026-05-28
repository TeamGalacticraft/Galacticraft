package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class ClientPayloadHandlers {
    public static void openCelestialScreen(OpenCelestialScreenPayload payload) {
        Minecraft.getInstance().setScreen(new CelestialSelectionScreen(
                false,
                payload.data(),
                payload.canCreateStations(),
                payload.celestialBody().value(),
                payload.disabledDestinations()
        ));
    }

    private ClientPayloadHandlers() {
    }
}