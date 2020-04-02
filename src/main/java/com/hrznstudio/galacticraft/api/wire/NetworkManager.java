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
    private final Map<BlockPos, WireNetwork> networks = new ConcurrentHashMap<>();
    private final Map<WireNetwork, Integer> networkList = new HashMap<>();

    private NetworkManager() {
    }

    public static void createManagerForWorld(ServerWorld world) {
        managers.put(world.dimension.getType().getRawId(), new NetworkManager());
    }

    public static NetworkManager getManagerForWorld(IWorld world) {
        return managers.get(world.getDimension().getType().getRawId());
    }

    public void remove(BlockPos pos) {
        WireNetwork network = this.networks.remove(pos);
        networkList.replace(network, networkList.getOrDefault(network, 1) - 1);
        if (networkList.get(network) == 0) {
            networkList.remove(network);
        }
    }

    public void add(BlockPos pos, WireNetwork value) {
        this.networks.put(pos, value);
        networkList.putIfAbsent(value, 0);
        networkList.replace(value, networkList.getOrDefault(value, 0) + 1);
    }

    public void replace(BlockPos pos, WireNetwork newValue) {
        this.remove(pos);
        this.add(pos, newValue);
    }

    public WireNetwork getNetwork(BlockPos pos) {
        return networks.get(pos);
    }

//    public void removeNetwork(BlockPos pos) {
//        Iterator<BlockPos> iterator = networks.remove(pos).wires.iterator();
//        while (iterator.hasNext()) {
//            networks.remove(iterator.next());
//            iterator.remove();
//        }
//    }

    public void updateNetworks() {
        networkList.keySet().forEach(WireNetwork::tick);
    }

    public void worldClose() {
        this.networks.clear();
    }
}
