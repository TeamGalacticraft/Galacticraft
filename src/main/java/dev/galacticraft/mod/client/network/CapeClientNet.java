package dev.galacticraft.mod.client.network;

import dev.galacticraft.mod.misc.cape.CapeMode;
import dev.galacticraft.mod.network.c2s.CapeSelectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public final class CapeClientNet {
    public static void sendSelectionIfOnline(CapeMode mode, @Nullable String gcCapeId) {
        var mc = Minecraft.getInstance();
        if (mc != null && mc.getConnection() != null && mc.player != null) {
            ClientPlayNetworking.send(new CapeSelectionPayload(
                    mode,
                    mode == CapeMode.GC ? gcCapeId : null
            ));
        }
    }

    private CapeClientNet() {}
}