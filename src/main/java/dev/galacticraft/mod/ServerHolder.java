package dev.galacticraft.mod;

import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class ServerHolder {
    private static MinecraftServer SERVER;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER = null);
    }

    public static MinecraftServer get() { return SERVER; }
}