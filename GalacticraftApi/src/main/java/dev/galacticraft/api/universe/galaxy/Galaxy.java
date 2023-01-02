/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.api.universe.galaxy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.galaxy.GalaxyImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Galaxy {
    Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").xmap(Component::translatable, Component::getString).forGetter(Galaxy::name),
            Codec.STRING.fieldOf("description").xmap(Component::translatable, Component::getString).forGetter(Galaxy::description),
            CelestialPosition.CODEC.fieldOf("position").forGetter(Galaxy::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(Galaxy::display)
    ).apply(instance, Galaxy::create));

    @Contract("_, _, _, _ -> new")
    static @NotNull Galaxy create(@NotNull MutableComponent name, @NotNull MutableComponent description, CelestialPosition<?, ?> position, CelestialDisplay<?, ?> display) {
        return new GalaxyImpl(name, description, position, display);
    }

    static Registry<Galaxy> getRegistry(@NotNull RegistryAccess manager) {
        return manager.registryOrThrow(AddonRegistry.GALAXY_KEY);
    }

    static Galaxy getById(RegistryAccess manager, ResourceLocation id) {
        return getById(getRegistry(manager), id);
    }

    static ResourceLocation getId(RegistryAccess manager, Galaxy galaxy) {
        return getId(getRegistry(manager), galaxy);
    }

    static Galaxy getById(@NotNull Registry<Galaxy> registry, ResourceLocation id) {
        return registry.get(id);
    }

    static ResourceLocation getId(@NotNull Registry<Galaxy> registry, Galaxy galaxy) {
        return registry.getKey(galaxy);
    }

    @NotNull MutableComponent name();

    @NotNull MutableComponent description();

    CelestialPosition<?, ?> position();

    CelestialDisplay<?, ?> display();
}
