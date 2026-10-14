/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.config;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class GCConfigUtil {
    /**
     * Dimensions disabled for a particular running server.
     *
     * <p>This is intentionally snapshotted. Editing the config while a server is
     * already running must not suddenly make an already-loaded ServerLevel
     * "disabled". Changes therefore take effect on the next server start.</p>
     */
    private static final Map<MinecraftServer, Set<ResourceLocation>> ACTIVE_DISABLED_DIMENSIONS = Collections.synchronizedMap(new WeakHashMap<>());

    private static final Set<String> WARNED_INVALID_DIMENSION_IDS = ConcurrentHashMap.newKeySet();

    private static final Set<String> WARNED_PROTECTED_DIMENSION_IDS = ConcurrentHashMap.newKeySet();

    private GCConfigUtil() {}

    /**
     * Returns the disabled dimensions for this server.
     *
     * <p>The set is created the first time this method is called for the server
     * and remains unchanged for that server's lifetime.</p>
     */
    public static Set<ResourceLocation> disabledDimensions(MinecraftServer server) {
        synchronized (ACTIVE_DISABLED_DIMENSIONS) {
            return ACTIVE_DISABLED_DIMENSIONS.computeIfAbsent(
                    server,
                    ignored -> Collections.unmodifiableSet(readConfiguredDisabledDimensions())
            );
        }
    }

    public static boolean isDimensionDisabled(
            MinecraftServer server,
            ResourceKey<Level> dimension
    ) {
        return dimension != null && isDimensionDisabled(server, dimension.location());
    }

    public static boolean isDimensionDisabled(
            MinecraftServer server,
            ResourceLocation dimension
    ) {
        if (dimension == null) {
            return false;
        }

        /*
         * The overworld is fundamental to Minecraft's server lifecycle,
         * player recovery, respawning, etc. Never allow Galacticraft's
         * dimension blacklist to remove it.
         */
        if (Level.OVERWORLD.location().equals(dimension)) {
            return false;
        }

        return disabledDimensions(server).contains(dimension);
    }

    /**
     * Returns true when this world should not be used as a destination.
     *
     * <p>This covers both explicitly-disabled dimensions and dimensions which
     * simply do not have a ServerLevel at runtime.</p>
     */
    public static boolean isDimensionUnavailable(
            MinecraftServer server,
            ResourceKey<Level> dimension
    ) {
        if (dimension == null) {
            return true;
        }

        return isDimensionDisabled(server, dimension) || server.getLevel(dimension) == null;
    }

    public static boolean isDimensionUnavailable(
            MinecraftServer server,
            ResourceLocation dimension
    ) {
        if (dimension == null) {
            return true;
        }

        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, dimension);

        return isDimensionUnavailable(server, key);
    }

    /**
     * Returns the world for a destination, or null if it is disabled/unavailable.
     */
    public static ServerLevel getEnabledLevel(
            MinecraftServer server,
            ResourceKey<Level> dimension
    ) {
        if (isDimensionDisabled(server, dimension)) {
            return null;
        }

        return server.getLevel(dimension);
    }

    /**
     * Celestial destinations hidden from the celestial selection screen.
     *
     * <p>This contains both the existing celestial-screen-only blacklist and
     * every physically disabled dimension.</p>
     */
    public static List<ResourceLocation> disabledCelestialScreenDestinations(
            MinecraftServer server
    ) {
        Set<ResourceLocation> destinations = new LinkedHashSet<>();

        for (String rawId : Galacticraft.CONFIG.disabledCelestialScreenDimensions()) {
            ResourceLocation id = parseDimensionId(rawId, "disabled celestial screen destination");

            if (id != null) {
                destinations.add(id);
            }
        }

        destinations.addAll(disabledDimensions(server));

        return new ArrayList<>(destinations);
    }

    /**
     * Returns true if a celestial body's physical world is disabled/unavailable.
     *
     * <p>Decorative bodies and stars which have no dimension return false.</p>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean isCelestialBodyUnavailable(
            MinecraftServer server,
            CelestialBody<?, ?> body
    ) {
        if (body == null) {
            return true;
        }

        if (!(body.type() instanceof Landable landable)) {
            return false;
        }

        ResourceKey<Level> world = landable.world(body.config());

        return isDimensionUnavailable(server, world);
    }

    /**
     * Returns true if this satellite's parent body ultimately cannot be used.
     *
     * <p>This is useful for preventing stations orbiting a disabled planet from
     * becoming orphan worlds whose re-entry destination does not exist.</p>
     */
    public static boolean isParentBodyUnavailable(
            MinecraftServer server,
            Registry<CelestialBody<?, ?>> celestialBodies,
            CelestialBody<?, ?> body
    ) {
        if (body == null || body.parent().isEmpty()) {
            return false;
        }

        try {
            return isCelestialBodyUnavailable(
                    server,
                    body.parentValue(celestialBodies)
            );
        } catch (RuntimeException e) {
            Constant.LOGGER.warn(
                    "Celestial body {} references a parent that could not be resolved.",
                    body,
                    e
            );

            return true;
        }
    }

    /**
     * Forget the snapshot after shutdown.
     *
     * <p>This mainly matters for integrated servers, where another world can be
     * opened later in the same JVM.</p>
     */
    public static void clearServer(MinecraftServer server) {
        synchronized (ACTIVE_DISABLED_DIMENSIONS) {
            ACTIVE_DISABLED_DIMENSIONS.remove(server);
        }
    }

    private static Set<ResourceLocation> readConfiguredDisabledDimensions() {
        Set<ResourceLocation> dimensions = new LinkedHashSet<>();

        for (String rawId : Galacticraft.CONFIG.disabledDimensions()) {
            ResourceLocation id = parseDimensionId(rawId, "disabled dimension");

            if (id == null) {
                continue;
            }

            if (Level.OVERWORLD.location().equals(id)) {
                if (WARNED_PROTECTED_DIMENSION_IDS.add(id.toString())) {
                    Constant.LOGGER.warn(
                            "Ignoring '{}' in disabled_dimensions because the overworld " +
                                    "cannot safely be disabled.",
                            id
                    );
                }

                continue;
            }

            dimensions.add(id);
        }

        if (!dimensions.isEmpty()) {
            Constant.LOGGER.info("Galacticraft dimension blacklist for this server: {}", dimensions);
        }

        return dimensions;
    }

    private static ResourceLocation parseDimensionId(
            String rawId,
            String description
    ) {
        if (rawId == null || rawId.isBlank()) {
            return null;
        }

        String trimmed = rawId.trim();

        try {
            return ResourceLocation.parse(trimmed);
        } catch (Exception e) {
            if (WARNED_INVALID_DIMENSION_IDS.add(trimmed)) {
                Constant.LOGGER.warn(
                        "Ignoring invalid {} id '{}'. Expected format namespace:path.",
                        description,
                        trimmed
                );
            }

            return null;
        }
    }
}