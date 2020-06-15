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

package com.hrznstudio.galacticraft.api.registry;

import com.hrznstudio.galacticraft.accessor.ClientPlayNetworkHandlerAccessor;
import com.hrznstudio.galacticraft.api.research.ResearchManager;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketPartRegistry extends SimpleRegistry<RocketPart> {

    private final HashMap<RocketPartType, List<RocketPart>> parts = new HashMap<>();

    public RocketPartRegistry(RegistryKey<Registry<RocketPart>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
        for (RocketPartType type : RocketPartType.values()) {
            parts.put(type, new ArrayList<>());
        }
    }

    @Override
    public <V extends RocketPart> V add(RegistryKey<RocketPart> key, V entry) {
        parts.get(entry.getType()).add(entry);
        return super.add(key, entry);
    }

    @Environment(EnvType.CLIENT)
    public List<RocketPart> getAvailablePartsForType(PlayerEntity player, RocketPartType type) {
        if (player instanceof ClientPlayerEntity) {
            List<RocketPart> valid = new ArrayList<>();
            ResearchManager manager = ((ClientPlayNetworkHandlerAccessor) ((ClientPlayerEntity) player).networkHandler).getClientResearchManager().getManager();
            for (RocketPart part : parts.get(type)) {
                if (manager.isUnlocked(part)) {
                    valid.add(part);
                }
            }
            return valid;
        }
        return null;
    }

    public List<RocketPart> getAllEntries() {
        return new ArrayList<>(entriesById.values());
    }
}
