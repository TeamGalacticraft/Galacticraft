/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin.client;

import dev.galacticraft.api.client.accessor.ClientSatelliteAccessor;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientSatelliteAccessor {
    private final @Unique Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> satellites = new HashMap<>();
    private final @Unique List<SatelliteListener> listeners = new ArrayList<>();

    @Override
    public Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> galacticraft$getSatellites() {
        return this.satellites;
    }

    @Override
    public void galacticraft$addSatellite(ResourceLocation id, CelestialBody<SatelliteConfig, SatelliteType> satellite) {
        this.satellites.put(id, satellite);
        for (SatelliteListener listener : this.listeners) {
            listener.onSatelliteUpdated(satellite, true);
        }
    }

    @Override
    public void galacticraft$removeSatellite(ResourceLocation id) {
        CelestialBody<SatelliteConfig, SatelliteType> removed = this.satellites.remove(id);
        for (SatelliteListener listener : this.listeners) {
            listener.onSatelliteUpdated(removed, false);
        }
    }

    @Override
    public void addListener(SatelliteListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(SatelliteListener listener) {
        this.listeners.remove(listener);
    }
}
