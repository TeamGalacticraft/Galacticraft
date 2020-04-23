/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.api.wire;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.mixin.ServerWorldMixin;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {

    private static Map<Integer, NetworkManager> managers = new HashMap<>();
    /**
     * A map containing all the networks in the current world.
     * Cleared on world close.
     *
     * @see ServerWorldMixin
     */
    private final Map<BlockPos, WireNetwork> networks = new ConcurrentHashMap<>();
    private final Map<WireNetwork, Integer> networkRefs = new HashMap<>();

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
        WireNetwork network = this.networks.remove(pos);
        networkRefs.put(network, networkRefs.getOrDefault(network, 1) - 1);
        assert network != null && networkRefs.get(network) != null;

        if (networkRefs.get(network) == 0) {
            networkRefs.remove(network);
        }
    }

    public void addWire(BlockPos pos, WireNetwork value) {
        if (!this.networks.containsKey(pos)) {
            networkRefs.putIfAbsent(value, 0);
            networkRefs.put(value, networkRefs.getOrDefault(value, 0) + 1);
            this.networks.put(pos, value);
        } else {
            throw new IllegalArgumentException("already added wire");
        }
    }

    public void transferWire(BlockPos pos, WireNetwork newValue) {
        this.removeWire(pos);
        this.addWire(pos, newValue);
    }

    public WireNetwork getNetwork(BlockPos pos) {
        return networks.get(pos);
    }

    public void updateNetworks(ServerWorld world) {
        Set<WireNetwork> set = new HashSet<>(networkRefs.keySet());
        for (WireNetwork network : set) {
            for (BlockPos pos : network.getQuery()) {
                if (world.getBlockEntity(pos) instanceof EnergyAttributeProvider) {
                    world.getBlockState(pos).updateNeighbors(world, pos, 10);
                }
            }
            network.clearQuery();
            List<BlockPos> consumers = network.getConsumers();
            List<BlockPos> producers = network.getProducers();
            if (consumers.size() == 0 || producers.size() == 0) continue;

            int available = 0;
            for (BlockPos pos : producers) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof EnergyAttributeProvider) {
                    if (((EnergyAttributeProvider) entity).getEnergyAttribute().canExtractEnergy()) {
                        available += ((EnergyAttributeProvider) entity).getEnergyAttribute().getCurrentEnergy();
                    }
                }
            }
            int i = consumers.size();
            int amountPerMachine = available / Math.max(1, i--);
            for (BlockPos pos : consumers) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof EnergyAttributeProvider) {
                    if (((EnergyAttributeProvider) entity).getEnergyAttribute().canInsertEnergy()) {
                        available -= (amountPerMachine - ((EnergyAttributeProvider) entity).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerMachine, Simulation.ACTION));
                    }
                }

                amountPerMachine = available / Math.max(1, i--);
            }
        }
    }

    public void worldClose() {
        this.networks.clear();
    }
}
