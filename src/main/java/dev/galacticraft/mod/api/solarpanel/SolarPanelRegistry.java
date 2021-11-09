/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SolarPanelRegistry {
    private static final WorldLightSources DEFAULT_LIGHT_SOURCE = new WorldLightSources(
            Constant.ScreenTexture.DEFAULT_LIGHT_SOURCES,
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.sun").setStyle(Constant.Text.YELLOW_STYLE), 1.0, 1.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.moon").setStyle(Constant.Text.GRAY_STYLE), 0.07, 1.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.rain").setStyle(Constant.Text.BLUE_STYLE), 1.0, 2.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.unknown").setStyle(Constant.Text.WHITE_STYLE), 0.0, 1.0));
    private static final Map<RegistryKey<World>, WorldLightSources> LIGHT_SOURCES = new HashMap<>();
    private static final Map<BlockEntityType<? extends SolarPanel>, Identifier> SOLAR_PANEL_TEXTURES = new HashMap<>();

    public static void registerLightSources(RegistryKey<World> key, WorldLightSources source) {
        if (LIGHT_SOURCES.put(key, source) != null) {
            Galacticraft.LOGGER.warn("Replacing ligt source for {}", key.getValue().toString());
        }
    }

    public static <M extends BlockEntity & SolarPanel, T extends BlockEntityType<M>> void registerSolarPanelTexture(T type, Identifier texture) {
        if (SOLAR_PANEL_TEXTURES.put(type, texture) != null) {
            Galacticraft.LOGGER.warn("Replacing solar panel texture for {}", Objects.requireNonNull(Registry.BLOCK_ENTITY_TYPE.getId(type)).toString());
        }
    }

    public static @NotNull WorldLightSources getLightSource(RegistryKey<World> key) {
        return LIGHT_SOURCES.getOrDefault(key, DEFAULT_LIGHT_SOURCE);
    }

    public static @NotNull Identifier getSolarPanelTexture(BlockEntityType<?> key) {
        return SOLAR_PANEL_TEXTURES.getOrDefault(key, Constant.ScreenTexture.DEFAULT_SOLAR_PANELS);
    }
}
