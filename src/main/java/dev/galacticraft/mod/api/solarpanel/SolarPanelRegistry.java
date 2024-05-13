/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
    private static final WorldLightSources DEFAULT_LIGHT_SOURCE = new WorldLightSources(
            Constant.ScreenTexture.DEFAULT_LIGHT_SOURCES,
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_SUN).setStyle(Constant.Text.Color.YELLOW_STYLE), 1.0, 1.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_MOON).setStyle(Constant.Text.Color.GRAY_STYLE), 0.07, 1.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_RAIN).setStyle(Constant.Text.Color.BLUE_STYLE), 1.0, 2.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.Color.WHITE_STYLE), 0.0, 1.0));
    private static final Map<ResourceKey<Level>, WorldLightSources> LIGHT_SOURCES = new HashMap<>();
    private static final Map<BlockEntityType<? extends SolarPanel>, ResourceLocation> SOLAR_PANEL_TEXTURES = new HashMap<>();

    public static void registerLightSources(ResourceKey<Level> key, WorldLightSources source) {
        if (LIGHT_SOURCES.put(key, source) != null) {
            Constant.LOGGER.warn("Replacing ligt source for {}", key.location().toString());
        }
    }

    public static <M extends BlockEntity & SolarPanel, T extends BlockEntityType<M>> void registerSolarPanelTexture(T type, ResourceLocation texture) {
        if (SOLAR_PANEL_TEXTURES.put(type, texture) != null) {
            Constant.LOGGER.warn("Replacing solar panel texture for {}", Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).toString());
        }
    }

    public static @NotNull WorldLightSources getLightSource(ResourceKey<Level> key) {
        return LIGHT_SOURCES.getOrDefault(key, DEFAULT_LIGHT_SOURCE);
    }

    public static @NotNull ResourceLocation getSolarPanelTexture(BlockEntityType<?> key) {
        return SOLAR_PANEL_TEXTURES.getOrDefault(key, Constant.ScreenTexture.DEFAULT_SOLAR_PANELS);
    }
}
