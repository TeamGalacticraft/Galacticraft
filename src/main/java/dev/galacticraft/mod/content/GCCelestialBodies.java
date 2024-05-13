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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.celestialbody.config.DecorativePlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import dev.galacticraft.impl.universe.celestialbody.type.DecorativePlanet;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.StarType;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.config.ring.DefaultCelestialRingDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.SpinningIconCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.AsteroidCelestialRingDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.DefaultCelestialRingDisplayType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.config.StaticCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.impl.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GCCelestialBodies {
    private static final Int2ObjectMap<Ingredient> EARTH_SATELLITE_RECIPE = new Int2ObjectArrayMap<>(new int[]{16, 32, 8, 1}, new Ingredient[]{Ingredient.of(GCItems.ALUMINUM_INGOT), Ingredient.of(GCItems.TIN_INGOT), Ingredient.of(Items.COPPER_INGOT), Ingredient.of(GCItems.ADVANCED_WAFER)});
    public static final ResourceKey<CelestialBody<?, ?>> EARTH = BuiltinObjects.EARTH_KEY;
    public static final ResourceKey<CelestialBody<?, ?>> MOON = key("moon");
    public static final ResourceKey<CelestialBody<?, ?>> MERCURY = key("mercury");
    public static final ResourceKey<CelestialBody<?, ?>> VENUS = key("venus");
    public static final ResourceKey<CelestialBody<?, ?>> MARS = key("mars");
    public static final ResourceKey<CelestialBody<?, ?>> ASTEROIDS = key("asteroids");
    public static final ResourceKey<CelestialBody<?, ?>> JUPITER = key("jupiter");
    public static final ResourceKey<CelestialBody<?, ?>> SATURN = key("saturn");
    public static final ResourceKey<CelestialBody<?, ?>> URANUS = key("uranus");
    public static final ResourceKey<CelestialBody<?, ?>> NEPTUNE = key("neptune");

    public static void bootstrapRegistries(BootstapContext<CelestialBody<?, ?>> context) {
        HolderGetter<CelestialTeleporter<?, ?>> lookup = context.lookup(AddonRegistries.CELESTIAL_TELEPORTER);
        context.register(BuiltinObjects.SOL_KEY, StarType.INSTANCE.configure(
                new StarConfig(
                        Component.translatable(Translations.CelestialBody.SOL),
                        Component.translatable(Translations.CelestialBody.SOL_DESC),
                        BuiltinObjects.MILKY_WAY_KEY,
                        Optional.empty(),
                        StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)),
                        IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/body_icons.png"), 0, 0, 16, 16, 1.5f, Optional.empty())),
                        DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
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
        ));
        context.register(EARTH, PlanetType.INSTANCE.configure(
                new PlanetConfig(
                        Component.translatable(Translations.CelestialBody.EARTH),
                        Component.translatable(Translations.CelestialBody.EARTH_DESC),
                        BuiltinObjects.MILKY_WAY_KEY,
                        BuiltinObjects.SOL_KEY,
                        OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1.0F, 1.0, 0.0F, true)),
                        IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/body_icons.png"), 0, 16, 16, 16)),
                        DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                        Level.OVERWORLD,
                        lookup.getOrThrow(GCTeleporterTypes.OVERWORLD_TELEPORTER),
                        new GasComposition.Builder()
                                .pressure(1.0f)
                                .temperature(15.0f)
                                .gas(Gases.NITROGEN_ID, 780840.000)
                                .gas(Gases.OXYGEN_ID, 209500.000)
                                .gas(Gases.WATER_VAPOR_ID, 25000.000)
                                .gas(Gases.ARGON_ID, 9300.000)
                                .gas(Gases.CARBON_DIOXIDE_ID, 399.000)
                                .gas(Gases.NEON_ID, 18.000)
                                .gas(Gases.HELIUM_ID, 5.420)
                                .gas(Gases.METHANE_ID, 1.790)
                                .gas(Gases.KRYPTON_ID, 1.140)
                                .gas(Gases.HYDROGEN_ID, 0.550)
                                .gas(Gases.NITROUS_OXIDE_ID, 0.325)
                                .gas(Gases.CARBON_MONOXIDE_ID, 0.100)
                                .gas(Gases.XENON_ID, 0.090)
                                .gas(Gases.OZONE_ID, 0.070)
                                .gas(Gases.NITROUS_DIOXIDE_ID, 0.020)
                                .gas(Gases.IODINE_ID, 0.010)
                                .build(),
                        1.0f,
                        GCCelestialHandlers.OVERWORLD,
                        0,
                        21, //todo
                        15, //todo
                        24000,
                        Optional.of(SatelliteRecipe.create(EARTH_SATELLITE_RECIPE))
                )
        ));

        context.register(MOON, PlanetType.INSTANCE.configure(new PlanetConfig(
                Component.translatable(Translations.CelestialBody.MOON),
                Component.translatable(Translations.CelestialBody.MOON_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.EARTH_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1 / 0.01F, 20.0, 0.2667, false)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/moon.png"), 0, 0, 8, 8)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                GCDimensions.MOON,
                lookup.getOrThrow(GCTeleporterTypes.LANDER_CELESTIAL_TELEPORTER),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                GCCelestialHandlers.DEFAULT,
                1,
                105,
                -180,
                192000L,
                Optional.empty()
        )));

        context.register(MERCURY, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.MERCURY),
                Component.translatable(Translations.CelestialBody.MERCURY_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(0.24096385542168674698795180722892F, 0.5F, 1.45F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/mercury.png"), 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(VENUS, PlanetType.INSTANCE.configure(new PlanetConfig(
                Component.translatable(Translations.CelestialBody.VENUS),
                Component.translatable(Translations.CelestialBody.VENUS_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(0.61527929901423877327491785323111F, 0.75F, 2.0F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/venus.png"), 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                GCDimensions.VENUS,
                lookup.getOrThrow(GCTeleporterTypes.LANDER_CELESTIAL_TELEPORTER),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.0375F,
                GCCelestialHandlers.DEFAULT,
                1,
                105,
                -180,
                720000L, // 30 times longer than earth
                Optional.empty()
        )));

        context.register(MARS, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.MARS),
                Component.translatable(Translations.CelestialBody.MARS_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1.8811610076670317634173055859803F, 1.25F, 0.1667F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/mars.png"), 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(ASTEROIDS, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.ASTEROIDS),
                Component.translatable(Translations.CelestialBody.ASTEROIDS_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(45.0F, 1.375F, 0.0F, true)),
                SpinningIconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/asteroid.png"), 0, 0, 16, 16)),
                AsteroidCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(JUPITER, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.JUPITER),
                Component.translatable(Translations.CelestialBody.JUPITER_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(11.861993428258488499452354874042F, 1.5F, Mth.PI, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/jupiter.png"), 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(SATURN, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.SATURN),
                Component.translatable(Translations.CelestialBody.SATURN_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(29.463307776560788608981380065717F, 1.75F, 5.45F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(
                        Constant.id("textures/gui/celestialbodies/saturn.png"), 0, 0, 16, 16, 1,
                        Optional.of(new IconCelestialDisplayConfig.Decoration(Constant.id("textures/gui/celestialbodies/saturn_rings.png"), -7.5F, -1.75F, 15.0F, 3.5F, 0, 0, 30, 7))
                )),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(URANUS, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.URANUS),
                Component.translatable(Translations.CelestialBody.URANUS_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(84.063526834611171960569550930997F, 2.0F, 1.38F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(
                        Constant.id("textures/gui/celestialbodies/uranus.png"), 0, 0, 16, 16, 1,
                        Optional.of(new IconCelestialDisplayConfig.Decoration(Constant.id("textures/gui/celestialbodies/uranus_rings.png"), -1.75F, -7.0F, 3.5F, 14.0F, 0, 0, 7, 28))
                )),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));

        context.register(NEPTUNE, DecorativePlanet.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.NEPTUNE),
                Component.translatable(Translations.CelestialBody.NEPTUNE_DESC),
                BuiltinObjects.MILKY_WAY_KEY,
                BuiltinObjects.SOL_KEY,
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(164.84118291347207009857612267251F, 2.25F, 1.0F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.id("textures/gui/celestialbodies/neptune.png"), 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                Optional.empty()
        )));
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<CelestialBody<?, ?>> key(@NotNull String id) {
        return Constant.key(AddonRegistries.CELESTIAL_BODY, id);
    }
}
