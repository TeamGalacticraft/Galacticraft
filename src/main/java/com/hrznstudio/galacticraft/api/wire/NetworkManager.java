package com.hrznstudio.galacticraft.api.wire;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {

    private static Map<Integer, NetworkManager> managers = new HashMap<>();
    /**
     * A map containing all the networks in the current world.
     * Cleared on world close.
     *
     * @see com.hrznstudio.galacticraft.mixin.ServerWorldMixin
     */
    private final Map<BlockPos, WireGraph> networks = new ConcurrentHashMap<>();
    private final Map<WireGraph, Integer> networkList = new HashMap<>();

    private NetworkManager() {
    }

    public static void createManagerForWorld(ServerWorld world) {
        managers.put(world.dimension.getType().getRawId(), new NetworkManager());
    }

    public static NetworkManager getManagerForDimension(int id) {
        return managers.get(id);
    }

    public static NetworkManager getManagerForWorld(IWorld world) {
        return getManagerForDimension(world.getDimension().getType().getRawId());
    }

    public void removeWire(BlockPos pos) {
        WireGraph network = this.networks.remove(pos);
        networkList.replace(network, networkList.getOrDefault(network, 1) - 1);
        if (networkList.get(network) == 0) {
            networkList.remove(network);
        }
    }

    public void addWire(BlockPos pos, WireGraph value) {
        this.networks.put(pos, value);
        networkList.putIfAbsent(value, 0);
        networkList.replace(value, networkList.getOrDefault(value, 0) + 1);
    }

    public void transferWire(BlockPos pos, WireGraph newValue) {
        this.removeWire(pos);
        this.addWire(pos, newValue);
    }

    public WireGraph getNetwork(BlockPos pos) {
        return networks.get(pos);
    }

    public void updateNetworks() {
    }

    public void worldClose() {
        this.networks.clear();
    }
}
