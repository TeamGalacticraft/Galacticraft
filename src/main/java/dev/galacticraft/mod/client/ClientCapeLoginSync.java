package dev.galacticraft.mod.client;

import dev.galacticraft.mod.client.network.CapeClientNet;
import dev.galacticraft.mod.misc.cape.CapeMode;
import dev.galacticraft.mod.misc.cape.ClientCapePrefs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public final class ClientCapeLoginSync {
    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientCapePrefs prefs = ClientCapePrefs.load();
            CapeClientNet.sendSelectionIfOnline(
                    prefs.mode,
                    prefs.mode == CapeMode.GC ? prefs.gcCapeId : null
            );
        });
    }

    private ClientCapeLoginSync() {}
}