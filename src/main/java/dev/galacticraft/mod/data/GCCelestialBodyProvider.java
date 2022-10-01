package dev.galacticraft.mod.data;

import dev.galacticraft.api.data.CelestialBodyDataProvider;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMaps;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class GCCelestialBodyProvider extends CelestialBodyDataProvider {
    public GCCelestialBodyProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateCelestialBodies() {
        planet(BuiltinObjects.EARTH_KEY.location())
                .name(Component.translatable("planet.galacticraft-api.earth.name"))
                .description(Component.translatable("planet.galacticraft-api.earth.description"))
                .galaxy(BuiltinObjects.MILKY_WAY_KEY)
                .parent(BuiltinObjects.SOL_KEY)
                .position(OrbitalCelestialPositionType.INSTANCE.configure(new OrbitalCelestialPositionConfig(1536000.0, 1.0, 0.0F, true)))
                .display(IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(new ResourceLocation(Constant.MOD_ID, "textures/body_icons.png"), 0, 16, 16, 16, 1)))
                .world(Level.OVERWORLD)
                .atmosphere(new GasComposition.Builder()
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
                        .build())
                .gravity(1.0f)
                .accessWeight(0)
                .dayTemperature(21)
                .nightTemperature(15)
                .satelliteRecipe(Optional.of(
                        SatelliteRecipe.create(Object2IntSortedMaps.singleton(Ingredient.of(Items.IRON_INGOT.getDefaultInstance()), 0))
                ));

//        planet(Constant.id("moon"))
//                .name(Component.translatable("planet.galacticraft.moon.name"))
//                .description(Component.translatable("planet.galacticraft.moon.desc"))
//                .
    }

    @Override
    public String getName() {
        return "Galacticraft Celestial Bodies";
    }
}
