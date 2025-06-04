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
import dev.galacticraft.impl.universe.celestialbody.type.DecorativePlanetType;
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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
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
    public static final ResourceKey<CelestialBody<?, ?>> SOL = BuiltinObjects.SOL_KEY;
    public static final ResourceKey<CelestialBody<?, ?>> EARTH = BuiltinObjects.EARTH_KEY;
    public static final ResourceKey<CelestialBody<?, ?>> MOON = key("moon");
    public static final ResourceKey<CelestialBody<?, ?>> MERCURY = key("mercury");
    public static final ResourceKey<CelestialBody<?, ?>> VENUS = key("venus");
    public static final ResourceKey<CelestialBody<?, ?>> MARS = key("mars");
    public static final ResourceKey<CelestialBody<?, ?>> ASTEROID = key("asteroid");
    public static final ResourceKey<CelestialBody<?, ?>> JUPITER = key("jupiter");
    public static final ResourceKey<CelestialBody<?, ?>> SATURN = key("saturn");
    public static final ResourceKey<CelestialBody<?, ?>> URANUS = key("uranus");
    public static final ResourceKey<CelestialBody<?, ?>> NEPTUNE = key("neptune");

    public static void bootstrapRegistries(BootstrapContext<CelestialBody<?, ?>> context) {
        HolderGetter<CelestialTeleporter<?, ?>> teleporters = context.lookup(AddonRegistries.CELESTIAL_TELEPORTER);

        Holder.Reference<CelestialBody<?, ?>> sol = context.register(SOL, StarType.INSTANCE.configure(
                new StarConfig(
                        Component.translatable(Translations.CelestialBody.SOL),
                        Component.translatable(Translations.CelestialBody.SOL_DESC),
                        Optional.of(BuiltinObjects.MILKY_WAY_KEY),
                        StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)),
                        IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.SOL, 0, 0, 8, 8, 1.5f, Optional.empty())),
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
        Holder.Reference<CelestialBody<?, ?>> earth = context.register(EARTH, PlanetType.INSTANCE.configure(
                new PlanetConfig(
                        Component.translatable(Translations.CelestialBody.EARTH),
                        Component.translatable(Translations.CelestialBody.EARTH_DESC),
                        Optional.of(SOL),
                        OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1.0F, 1.0, 0.0F, true)),
                        IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.EARTH, 0, 0, 8, 8, Constant.CelestialOverlay.EARTH)),
                        DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                        Level.OVERWORLD,
                        teleporters.getOrThrow(GCTeleporterTypes.OVERWORLD_TELEPORTER),
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
                                .gas(Gases.NITROGEN_DIOXIDE_ID, 0.020)
                                .gas(Gases.IODINE_ID, 0.010)
                                .build(),
                        1.0f,
                        GCCelestialHandlers.OVERWORLD,
                        0,
                        21, //todo
                        15, //todo
                        24000,
                        SatelliteRecipe.create(EARTH_SATELLITE_RECIPE)
                )
        ));

        context.register(MOON, PlanetType.INSTANCE.configure(new PlanetConfig(
                Component.translatable(Translations.CelestialBody.MOON),
                Component.translatable(Translations.CelestialBody.MOON_DESC),
                Optional.of(EARTH),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1 / 0.01F, 20.0, 0.2667, false)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.MOON, 0, 0, 8, 8, Constant.CelestialOverlay.MOON)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                GCDimensions.MOON,
                teleporters.getOrThrow(GCTeleporterTypes.LANDER_CELESTIAL_TELEPORTER),
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
                null
        )));

        context.register(MERCURY, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.MERCURY),
                Component.translatable(Translations.CelestialBody.MERCURY_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(0.24096385542168674698795180722892F, 0.5F, 1.45F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.MERCURY, 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));

        context.register(VENUS, PlanetType.INSTANCE.configure(new PlanetConfig(
                Component.translatable(Translations.CelestialBody.VENUS),
                Component.translatable(Translations.CelestialBody.VENUS_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(0.61527929901423877327491785323111F, 0.75F, 2.0F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.VENUS, 0, 0, 16, 16, Constant.CelestialOverlay.VENUS)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                GCDimensions.VENUS,
                teleporters.getOrThrow(GCTeleporterTypes.LANDER_CELESTIAL_TELEPORTER),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.91F,
                GCCelestialHandlers.DEFAULT,
                1,
                105,
                -180,
                720000L, // 30 times longer than earth
                null
        )));

        context.register(MARS, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.MARS),
                Component.translatable(Translations.CelestialBody.MARS_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1.8811610076670317634173055859803F, 1.25F, 0.1667F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.MARS, 0, 0, 16, 16, Constant.CelestialOverlay.MARS)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));

        context.register(ASTEROID, PlanetType.INSTANCE.configure(new PlanetConfig(
                Component.translatable(Translations.CelestialBody.ASTEROID),
                Component.translatable(Translations.CelestialBody.ASTEROID_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(45.0F, 1.375F, 0.0F, true)),
                SpinningIconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.ASTEROID, 0, 0, 16, 16)),
                AsteroidCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                GCDimensions.ASTEROID,
                teleporters.getOrThrow(GCTeleporterTypes.LANDER_CELESTIAL_TELEPORTER),
                new GasComposition.Builder()
                        .temperature(-2.0)
                        .pressure(0)
                        .build(),
                0.1f,
                GCCelestialHandlers.DEFAULT,
                2,
                -2,
                -2,
                99999999L, // 30 times longer than earth
                null
        )));

        context.register(JUPITER, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.JUPITER),
                Component.translatable(Translations.CelestialBody.JUPITER_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(11.861993428258488499452354874042F, 1.5F, Mth.PI, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.JUPITER, 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));

        context.register(SATURN, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.SATURN),
                Component.translatable(Translations.CelestialBody.SATURN_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(29.463307776560788608981380065717F, 1.75F, 5.45F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(
                        Constant.CelestialBody.SATURN, 0, 0, 16, 16, 1,
                        Optional.of(new IconCelestialDisplayConfig.Decoration(Constant.CelestialBody.SATURN_RINGS, -7.5F, -1.75F, 15.0F, 3.5F, 0, 0, 30, 7))
                )),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));

        context.register(URANUS, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.URANUS),
                Component.translatable(Translations.CelestialBody.URANUS_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(84.063526834611171960569550930997F, 2.0F, 1.38F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(
                        Constant.CelestialBody.URANUS, 0, 0, 16, 16, 1,
                        Optional.of(new IconCelestialDisplayConfig.Decoration(Constant.CelestialBody.URANUS_RINGS, -1.75F, -7.0F, 3.5F, 14.0F, 0, 0, 7, 28))
                )),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));

        context.register(NEPTUNE, DecorativePlanetType.INSTANCE.configure(new DecorativePlanetConfig(
                Component.translatable(Translations.CelestialBody.NEPTUNE),
                Component.translatable(Translations.CelestialBody.NEPTUNE_DESC),
                Optional.of(SOL),
                OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(164.84118291347207009857612267251F, 2.25F, 1.0F, true)),
                IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(Constant.CelestialBody.NEPTUNE, 0, 0, 16, 16)),
                DefaultCelestialRingDisplayType.INSTANCE.configure(new DefaultCelestialRingDisplayConfig()),
                new GasComposition.Builder()
                        .temperature(23.0)
                        .pressure(3.0E-15F)
                        .build(),
                0.166F,
                null
        )));
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<CelestialBody<?, ?>> key(@NotNull String id) {
        return Constant.key(AddonRegistries.CELESTIAL_BODY, id);
    }
}
