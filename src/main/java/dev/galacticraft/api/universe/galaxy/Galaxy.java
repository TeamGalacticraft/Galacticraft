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

package dev.galacticraft.api.universe.galaxy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.codec.StreamCodecs;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.BuiltinObjects;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.config.EmptyCelestialDisplayConfig;
import dev.galacticraft.api.universe.display.type.EmptyCelestialDisplayType;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.api.universe.position.config.StaticCelestialPositionConfig;
import dev.galacticraft.api.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Galaxy(@NotNull Component name,
                     @NotNull Component description,
                     CelestialPosition<?, ?> position,
                     CelestialDisplay<?, ?> display) {
    public static final Codec<Galaxy> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(Galaxy::name),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Galaxy::description),
            CelestialPosition.CODEC.fieldOf("position").forGetter(Galaxy::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(Galaxy::display)
    ).apply(instance, Galaxy::create));
    public static final Codec<Holder<Galaxy>> CODEC = RegistryFileCodec.create(AddonRegistries.GALAXY, DIRECT_CODEC);
    public static final Codec<HolderSet<Galaxy>> LIST_CODEC = RegistryCodecs.homogeneousList(AddonRegistries.GALAXY, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Galaxy> STREAM_CODEC = StreamCodecs.ofRegistryEntry(AddonRegistries.GALAXY);

    public static void bootstrapRegistries(@NotNull BootstrapContext<Galaxy> context) {
        context.register(BuiltinObjects.MILKY_WAY_KEY, createMilkyWay());
    }

    @Contract(" -> new")
    public static @NotNull Galaxy createMilkyWay() {
        return Galaxy.create(
                Component.translatable(Translations.Galaxy.MILKY_WAY),
                Component.translatable(Translations.Galaxy.MILKY_WAY_DESCRIPTION),
                StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)),
                EmptyCelestialDisplayType.INSTANCE.configure(EmptyCelestialDisplayConfig.INSTANCE)
        );
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Galaxy create(@NotNull Component name, @NotNull Component description, CelestialPosition<?, ?> position, CelestialDisplay<?, ?> display) {
        return new Galaxy(name, description, position, display);
    }
}
