package com.hrznstudio.galacticraft.api.wire;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NetworkManager {

    private static Map<Integer, NetworkManager> managers = new HashMap<>();
    /**
     * A map containing all the networks in the current world.
     * Cleared on world close.
     *
     * @see com.hrznstudio.galacticraft.mixin.ServerWorldMixin
     */
    public final ConcurrentMap<UUID, WireNetwork> networks = new ConcurrentHashMap<>();

    private final int id;

    private NetworkManager(int id) {
        this.id = id;
    }

    public static void createManagerForWorld(ServerWorld world) {
        managers.put(world.dimension.getType().getRawId(), new NetworkManager(world.dimension.getType().getRawId()));
    }

    public static NetworkManager getManagerForDimension(DimensionType type) {
        return managers.get(type.getRawId());
    }

    public WireNetwork getNetwork(UUID uuid) {
        return networks.get(uuid);
    }

    public void addNetwork(WireNetwork network) {
        networks.put(network.getId(), network);
    }

    public void removeNetwork(UUID id) {
        networks.remove(id);
    }

    public void updateNetworks() {
        for (WireNetwork network : this.networks.values()) {
            network.tick();
        }
    }

    public void worldClose() {
        for (WireNetwork network : networks.values()) {
            network.wires.clear();
        }
        networks.clear();
        managers.remove(id);
    }
}
