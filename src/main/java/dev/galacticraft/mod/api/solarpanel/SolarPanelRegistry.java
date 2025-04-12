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

package dev.galacticraft.mod.api.solarpanel;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.api.block.entity.SolarPanel.SolarPanelSource;
import dev.galacticraft.mod.content.GCLightSources;
import dev.galacticraft.mod.content.GCSolarPanelStates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SolarPanelRegistry {
    private static final Map<ResourceKey<Level>, Map<SolarPanelSource, LightSource>> LIGHT_SOURCES = new HashMap<>();
    private static final Map<BlockEntityType<? extends SolarPanel>, Map<SolarPanelSource, ResourceLocation>> SOLAR_PANEL_TEXTURES = new HashMap<>();

    public static void registerLightSources(ResourceKey<Level> key, Map<SolarPanelSource, LightSource> source) {
        for (Map.Entry<SolarPanelSource, LightSource> entry : GCLightSources.DEFAULT_LIGHT_SOURCES.entrySet()) {
            source.putIfAbsent(entry.getKey(), entry.getValue());
        }
        if (LIGHT_SOURCES.put(key, source) != null) {
            Constant.LOGGER.warn("Replacing light source for {}", key.location().toString());
        }
    }

    public static <M extends BlockEntity & SolarPanel, T extends BlockEntityType<M>> void registerSolarPanelTexture(T type, Map<SolarPanelSource, ResourceLocation> textures) {
        for (Map.Entry<SolarPanelSource, ResourceLocation> entry : GCSolarPanelStates.DEFAULT_SOLAR_PANELS.entrySet()) {
            textures.putIfAbsent(entry.getKey(), entry.getValue());
        }
        if (SOLAR_PANEL_TEXTURES.put(type, textures) != null) {
            Constant.LOGGER.warn("Replacing solar panel texture for {}", Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString());
        }
    }

    public static @NotNull Map<SolarPanelSource, LightSource> getLightSources(ResourceKey<Level> key) {
        return LIGHT_SOURCES.getOrDefault(key, GCLightSources.DEFAULT_LIGHT_SOURCES);
    }

    public static @NotNull Map<SolarPanelSource, ResourceLocation> getSolarPanelTextures(BlockEntityType<?> key) {
        return SOLAR_PANEL_TEXTURES.getOrDefault(key, GCSolarPanelStates.DEFAULT_SOLAR_PANELS);
    }
}
