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

package dev.galacticraft.impl.universe.galaxy;

import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.display.config.EmptyCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.EmptyCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.StaticCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record GalaxyImpl(@NotNull MutableComponent name,
                         @NotNull MutableComponent description,
                         CelestialPosition<?, ?> position,
                         CelestialDisplay<?, ?> display) implements Galaxy {

    public static void bootstrapRegistries(@NotNull BootstapContext<Galaxy> context) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GalaxyImpl) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.display, that.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, position, display);
    }

    @Contract(pure = true)
    @Override
    public @NotNull
    String toString() {
        return "GalaxyImpl[" +
                "name=" + name + ", " +
                "description=" + description + ", " +
                "position=" + position + ", " +
                "display=" + display + ']';
    }
}
