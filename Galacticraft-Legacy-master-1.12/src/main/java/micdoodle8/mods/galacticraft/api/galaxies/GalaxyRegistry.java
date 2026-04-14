/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.event.celestial.RegisterEvent;
import micdoodle8.mods.galacticraft.api.util.stream.CelestialCollector;
import micdoodle8.mods.galacticraft.core.util.list.CelestialList;
import micdoodle8.mods.galacticraft.core.util.list.ImmutableCelestialList;

public class GalaxyRegistry
{

    static CelestialList<SolarSystem>                   solarSystems    = CelestialList.create();
    static CelestialList<Planet>                        planets         = CelestialList.create();
    static CelestialList<Moon>                          moons           = CelestialList.create();
    static CelestialList<Satellite>                     satellites      = CelestialList.create();
    static CelestialList<CelestialObject>               objects         = CelestialList.create();
    static Map<Planet, CelestialList<Moon>>             moonList        = new HashMap<>();
    static Map<CelestialBody, CelestialList<Satellite>> satelliteList   = new HashMap<>();
    static Map<SolarSystem, CelestialList<Planet>>      solarSystemList = new HashMap<>();

    public static void refreshGalaxies()
    {
        moonList.clear();
        satelliteList.clear();
        solarSystemList.clear();
        for (Moon moon : getMoons())
        {
            Planet planet = moon.getParentPlanet();
            CelestialList<Moon> list = moonList.get(planet);
            if (list == null)
            {
                list = CelestialList.create();
            }
            list.add(moon);
            moonList.put(planet, list);
        }
        for (Satellite satellite : getSatellites())
        {
            CelestialBody celestialBody = satellite.getParentPlanet();
            CelestialList<Satellite> list = satelliteList.get(celestialBody);
            if (list == null)
            {
                list = CelestialList.create();
            }
            list.add(satellite);
            satelliteList.put(celestialBody, list);
        }
        for (Planet planet : getPlanets())
        {
            SolarSystem solarSystem = planet.getParentSolarSystem();
            CelestialList<Planet> list = solarSystemList.get(solarSystem);
            if (list == null)
            {
                list = CelestialList.create();
            }
            list.add(planet);
            solarSystemList.put(solarSystem, list);
        }
    }

    /**
     * Returns the CelestialObject that matches the given TranslationKey. Iterates through EVERY registerd object
     *
     * @param  translationkey
     *
     * @return                CelestialObject
     */
    public static CelestialObject getCelestialObjectFromTranslationKey(String translationkey)
    {
        for (CelestialObject celestialObject : objects)
        {
            if (celestialObject.getTranslationKey().equals(translationkey))
            {
                return celestialObject;
            }
        }
        return null;
    }

    /**
     * Returns the CelestialBody of the given DimensionID. Iterates through, Planets, Moons & Satellites only
     *
     * @param  dimensionID the DIM Id of the CelestialBody
     *
     * @return             CelestialBody with the DimensionID provided
     */
    public static CelestialBody getCelestialBodyFromDimensionID(int dimensionID)
    {
        for (Planet planet : planets)
        {
            if (planet.getDimensionID() == dimensionID)
            {
                return planet;
            }
        }
        for (Moon moon : moons)
        {
            if (moon.getDimensionID() == dimensionID)
            {
                return moon;
            }
        }
        for (Satellite satellite : satellites)
        {
            if (satellite.getDimensionID() == dimensionID)
            {
                return satellite;
            }
        }
        return null;
    }

    public static List<Planet> getPlanetsForSolarSystem(SolarSystem solarSystem)
    {
        if (solarSystemList.get(solarSystem) == null)
        {
            return new ArrayList<>();
        }
        return solarSystemList.get(solarSystem);
    }

    public static List<Moon> getMoonsForPlanet(Planet planet)
    {
        if (moonList.get(planet) == null)
        {
            return new ArrayList<>();
        }
        return moonList.get(planet);
    }

    public static List<Satellite> getSatellitesForCelestialBody(CelestialBody celestialBody)
    {
        if (satelliteList.get(celestialBody) == null)
        {
            return new ArrayList<>();
        }
        return satelliteList.get(celestialBody);
    }

    public static CelestialBody getPlanetOrMoonFromTranslationkey(String translationKey)
    {
        for (Planet planet : planets)
        {
            if (planet.getTranslationKey().equals(translationKey))
            {
                return planet;
            }
        }
        for (Moon moon : moons)
        {
            if (moon.getTranslationKey().equals(translationKey))
            {
                return moon;
            }
        }
        return null;
    }

    public static void register(Object object)
    {
        if (object instanceof SolarSystem)
        {
            SolarSystem solarSystem = (SolarSystem) object;
            RegisterEvent registerEvent = new RegisterEvent(solarSystem, Loader.instance().activeModContainer());
            solarSystems.add(solarSystem);
            objects.add(solarSystem);
            MinecraftForge.EVENT_BUS.post(registerEvent);
        }
        if (object instanceof Planet)
        {
            Planet planet = (Planet) object;
            RegisterEvent registerEvent = new RegisterEvent(planet, Loader.instance().activeModContainer());
            planets.add(planet);
            objects.add(planet);
            MinecraftForge.EVENT_BUS.post(registerEvent);
        }
        if (object instanceof Moon)
        {
            Moon moon = (Moon) object;
            RegisterEvent registerEvent = new RegisterEvent(moon, Loader.instance().activeModContainer());
            moons.add(moon);
            objects.add(moon);
            MinecraftForge.EVENT_BUS.post(registerEvent);
        }
        if (object instanceof Satellite)
        {
            Satellite satellite = (Satellite) object;
            RegisterEvent registerEvent = new RegisterEvent(satellite, Loader.instance().activeModContainer());
            satellites.add(satellite);
            objects.add(satellite);
            MinecraftForge.EVENT_BUS.post(registerEvent);
        }
        if (object instanceof CelestialBody)
        {
            CelestialBody celestialType = (CelestialBody) object;
            String unlocalizedPrefix = ((CelestialBody) object).getUnlocalizedNamePrefix();
            if (!unlocalizedPrefix.equals("unset"))
            {
                ((CelestialBody) object).setType(CelestialType.create(unlocalizedPrefix));
            }
            RegisterEvent registerEvent = new RegisterEvent(celestialType, Loader.instance().activeModContainer());
            objects.add(celestialType);
            MinecraftForge.EVENT_BUS.post(registerEvent);
        }
    }

    /**
     * Returns a read-only list containing all registered CelestialObjects
     */
    public static ImmutableCelestialList<CelestialObject> getAllRegisteredObjects()
    {
        return ImmutableCelestialList.of(objects);
    }

    /**
     * Returns a read-only list containing all registered Solar Systems
     */
    public static ImmutableCelestialList<SolarSystem> getSolarSystems()
    {
        return ImmutableCelestialList.of(solarSystems);
    }

    /**
     * Returns a read-only list containing all registered Planets
     */
    public static ImmutableCelestialList<Planet> getPlanets()
    {
        return ImmutableCelestialList.of(planets);
    }

    /**
     * Returns a read-only list containing all registered Moons
     */
    public static ImmutableCelestialList<Moon> getMoons()
    {
        return ImmutableCelestialList.of(moons);
    }

    /**
     * Returns a read-only list containing all registered Satellites
     */
    public static ImmutableCelestialList<Satellite> getSatellites()
    {
        return ImmutableCelestialList.of(satellites);
    }

    /**
     * Returns a read-only list containing all CelestialObjects that are reachable
     */
    public static ImmutableCelestialList<CelestialBody> getAllReachableBodies()
    {//@noformat
        return ImmutableCelestialList.from(
            planets.stream().filter(CelestialBody.filterReachable()).collect(CelestialCollector.toList()),
            moons.stream().filter(CelestialBody.filterReachable()).collect(CelestialCollector.toList())
        );
    }

    /**
     * Returns a read-only list containing all CelestialObjects registered by the provided ModContainer
     */
    public static ImmutableCelestialList<CelestialObject> getCelestialObjectsFromMod(ModContainer modContainer)
    {
        return getCelestialObjectsFromMod(modContainer.getModId());
    }

    /**
     * Returns a read-only list containing all CelestialObjects registered by the provided modId
     */
    public static ImmutableCelestialList<CelestialObject> getCelestialObjectsFromMod(String modId)
    {
        return objects.stream().filter(CelestialObject.filter(modId)).collect(CelestialCollector.toList()).toUnmodifiableList();
    }

    public static String[] getAllTransltionKeys()
    {
        return objects.stream().map(key -> key.getTranslationKey()).collect(Collectors.toList()).toArray(new String[objects.size()]);
    }

    // -- DEPRECIATED METHODS -- //

    /**
     * @ReplaceWith {@link GalaxyRegistry#getPlanetOrMoonFromTranslationkey(String translationKey)}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.getPlanetOrMoonFromTranslationkey(String translationKey)")
    public static CelestialBody getCelestialBodyFromUnlocalizedName(String unlocalizedName)
    {
        return getPlanetOrMoonFromTranslationkey(unlocalizedName);
    }

    /**
     * @ReplaceWith {@link GalaxyRegistry#register(T object)}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.register(T object)")
    public static boolean registerSolarSystem(SolarSystem solarSystem)
    {
        GalaxyRegistry.register(solarSystem);
        return solarSystems.contains(solarSystem);
    }

    /**
     * @ReplaceWith {@link GalaxyRegistry#register(T object)}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.register(T object)")
    public static boolean registerPlanet(Planet planet)
    {
        GalaxyRegistry.register(planet);
        return planets.contains(planet);
    }

    /**
     * @ReplaceWith {@link GalaxyRegistry#register(T object)}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.register(T object)")
    public static boolean registerMoon(Moon moon)
    {
        GalaxyRegistry.register(moon);
        return moons.contains(moon);
    }

    /**
     * @ReplaceWith {@link GalaxyRegistry#register(T object)}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.register(T object)")
    public static boolean registerSatellite(Satellite satellite)
    {
        GalaxyRegistry.register(satellite);
        return satellites.contains(satellite);
    }

    /**
     * Returns a read-only map containing Solar System Names and their associated Solar Systems.
     *
     * @ReplaceWith {@link GalaxyRegistry#getSolarSystems()}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.getSolarSystems()")
    public static Map<String, SolarSystem> getRegisteredSolarSystems()
    {
        return solarSystems.getRegistered();
    }

    /**
     * Returns a read-only map containing Planet Names and their associated Planets.
     *
     * @ReplaceWith {@link GalaxyRegistry#getPlanets()}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.getPlanets()")
    public static Map<String, Planet> getRegisteredPlanets()
    {
        return planets.getRegistered();
    }

    /**
     * Returns a read-only map containing Moon Names and their associated Moons.
     *
     * @ReplaceWith {@link GalaxyRegistry#getMoons()}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.getMoons()")
    public static Map<String, Moon> getRegisteredMoons()
    {
        return moons.getRegistered();
    }

    /**
     * Returns a read-only map containing Satellite Names and their associated Satellite.
     *
     * @ReplaceWith {@link GalaxyRegistry#getSatellites()}
     */
    @Deprecated
    @ReplaceWith("GalaxyRegistry.getSatellites()")
    public static Map<String, Satellite> getRegisteredSatellites()
    {
        return satellites.getRegistered();
    }
}
