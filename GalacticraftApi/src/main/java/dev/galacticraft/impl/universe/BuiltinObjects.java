/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.impl.universe;

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import dev.galacticraft.impl.universe.celestialbody.type.DecorativePlanet;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.StarType;
import dev.galacticraft.impl.universe.display.config.EmptyCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.EmptyCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.StaticCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.impl.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.machinelib.api.gas.Gases;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BuiltinObjects {
    public static final ResourceKey<Galaxy> MILKY_WAY_KEY = ResourceKey.create(AddonRegistry.GALAXY_KEY, new ResourceLocation(Constant.MOD_ID, "milky_way"));
    public static final Galaxy MILKY_WAY = Galaxy.create(
            Component.translatable("galaxy.galacticraft-api.milky_way.name"),
            Component.translatable("galaxy.galacticraft-api.milky_way.description"),
            StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)),
            EmptyCelestialDisplayType.INSTANCE.configure(EmptyCelestialDisplayConfig.INSTANCE)
    );

    public static final ResourceKey<CelestialBody<?, ?>> SOL_KEY = ResourceKey.create(AddonRegistry.CELESTIAL_BODY_KEY, new ResourceLocation(Constant.MOD_ID, "sol"));
    public static final CelestialBody<StarConfig, ? extends CelestialBodyType<StarConfig>> SOL = StarType.INSTANCE.configure( // TODO: possibly move this out of the api along with earth?
            new StarConfig(
                    Component.translatable("star.galacticraft-api.sol.name"),
                    Component.translatable("star.galacticraft-api.sol.description"),
                    MILKY_WAY_KEY,
                    StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)),
                    IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(new ResourceLocation(Constant.MOD_ID, "textures/body_icons.png"), 0, 0, 16, 16, 1.5f)),
                    new GasComposition.Builder()
                            .pressure(28)
                            .gas(Gases.HYDROGEN_ID, 734600.000)
                            .gas(Gases.HELIUM_ID, 248500.000)
                            .gas(Gases.OXYGEN_ID, 7700.000)
                            .gas(Gases.NEON_ID, 1200.000)
                            .gas(Gases.NITROGEN_ID, 900.000)
                            .build(),
                    28.0f,
                    1,
                    5772
            )
    );

    public static final ResourceKey<CelestialBody<?, ?>> EARTH_KEY = ResourceKey.create(AddonRegistry.CELESTIAL_BODY_KEY, new ResourceLocation(Constant.MOD_ID, "earth"));

    public static void register() {
        Registry.register(AddonRegistry.CELESTIAL_POSITION_TYPE, new ResourceLocation(Constant.MOD_ID, "static"), StaticCelestialPositionType.INSTANCE);
        Registry.register(AddonRegistry.CELESTIAL_POSITION_TYPE, new ResourceLocation(Constant.MOD_ID, "orbital"), OrbitalCelestialPositionType.INSTANCE);

        Registry.register(AddonRegistry.CELESTIAL_DISPLAY_TYPE, new ResourceLocation(Constant.MOD_ID, "empty"), EmptyCelestialDisplayType.INSTANCE);
        Registry.register(AddonRegistry.CELESTIAL_DISPLAY_TYPE, new ResourceLocation(Constant.MOD_ID, "icon"), IconCelestialDisplayType.INSTANCE);

        Registry.register(AddonRegistry.GALAXY, MILKY_WAY_KEY.location(), MILKY_WAY);

        Registry.register(AddonRegistry.CELESTIAL_BODY_TYPE, new ResourceLocation(Constant.MOD_ID, "star"), StarType.INSTANCE);
        Registry.register(AddonRegistry.CELESTIAL_BODY_TYPE, new ResourceLocation(Constant.MOD_ID, "planet"), PlanetType.INSTANCE);
        Registry.register(AddonRegistry.CELESTIAL_BODY_TYPE, new ResourceLocation(Constant.MOD_ID, "decorative_planet"), DecorativePlanet.INSTANCE);

        Registry.register(AddonRegistry.CELESTIAL_BODY, SOL_KEY.location(), SOL);
    }
}
