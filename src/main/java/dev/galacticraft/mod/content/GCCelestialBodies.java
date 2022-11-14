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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.machinelib.api.gas.Gases;
import dev.galacticraft.mod.content.item.GCItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static dev.galacticraft.impl.universe.BuiltinObjects.MILKY_WAY_KEY;
import static dev.galacticraft.impl.universe.BuiltinObjects.SOL_KEY;

public class GCCelestialBodies {
    public static final Int2ObjectMap<Ingredient> EARTH_SATELLITE_RECIPE = new Int2ObjectArrayMap<>(new int[]{16, 32, 8, 1}, new Ingredient[]{Ingredient.of(GCItem.ALUMINUM_INGOT), Ingredient.of(GCItem.TIN_INGOT), Ingredient.of(Items.COPPER_INGOT), Ingredient.of(GCItem.ADVANCED_WAFER)});
    public static final CelestialBody<PlanetConfig, ? extends CelestialBodyType<PlanetConfig>> EARTH = PlanetType.INSTANCE.configure(
            new PlanetConfig(
                    Component.translatable("planet.galacticraft-api.earth.name"),
                    Component.translatable("planet.galacticraft-api.earth.description"),
                    MILKY_WAY_KEY,
                    SOL_KEY,
                    OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1536000.0, 1.0, 0.0F, true)),
                    IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(new ResourceLocation(Constant.MOD_ID, "textures/body_icons.png"), 0, 16, 16, 16, 1)),
                    Level.OVERWORLD,
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
                    0,
                    21, //todo
                    15, //todo
                    Optional.of(SatelliteRecipe.create(EARTH_SATELLITE_RECIPE))
            )
    );

    public static void register() {
//        Registry.register(AddonRegistry.CELESTIAL_BODY, BuiltinObjects.EARTH_KEY.location(), EARTH);
    }
}
