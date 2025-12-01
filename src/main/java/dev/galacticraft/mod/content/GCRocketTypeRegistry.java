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

import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.rocket.part.type.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.resources.ResourceKey;

public class GCRocketTypeRegistry {
    private final GCRegistry<ResourceKey<?>> rocketParts;
    private final GCRegistry<RocketConeType<?>> rocketCones;
    private final GCRegistry<RocketBodyType<?>> rocketBodies;
    private final GCRegistry<RocketFinType<?>> rocketFins;
    private final GCRegistry<RocketBoosterType<?>> rocketBoosters;
    private final GCRegistry<RocketEngineType<?>> rocketEngines;
    private final GCRegistry<RocketUpgradeType<?>> rocketUpgrades;

    public GCRocketTypeRegistry() {
        this(Constant.MOD_ID);
    }

    public GCRocketTypeRegistry(String modId) {
        this.rocketParts = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_PARTS, modId);
        this.rocketCones = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_CONE_TYPE, modId);
        this.rocketBodies = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_BODY_TYPE, modId);
        this.rocketFins = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_FIN_TYPE, modId);
        this.rocketBoosters = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_BOOSTER_TYPE, modId);
        this.rocketEngines = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_ENGINE_TYPE, modId);
        this.rocketUpgrades = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_UPGRADE_TYPE, modId);
    }

    public GCRegistry<RocketConeType<?>> getRocketCones() {
        return rocketCones;
    }

    public GCRegistry<RocketBodyType<?>> getRocketBodies() {
        return rocketBodies;
    }

    public GCRegistry<RocketFinType<?>> getRocketFins() {
        return rocketFins;
    }

    public GCRegistry<RocketBoosterType<?>> getRocketBoosters() {
        return rocketBoosters;
    }

    public GCRegistry<RocketEngineType<?>> getRocketEngines() {
        return rocketEngines;
    }

    public GCRegistry<RocketUpgradeType<?>> getRocketUpgrades() {
        return rocketUpgrades;
    }

    public void registerPartType(String id, ResourceKey<?> key) {
        rocketParts.register(id, key);
    }

    public <T extends RocketConeType<?>> T registerCone(String name, T object) {
        return rocketCones.register(name, object);
    }

    public <T extends RocketBodyType<?>> T registerBody(String name, T object) {
        return rocketBodies.register(name, object);
    }

    public <T extends RocketFinType<?>> T registerFin(String name, T object) {
        return rocketFins.register(name, object);
    }

    public <T extends RocketBoosterType<?>> T registerBooster(String name, T object) {
        return rocketBoosters.register(name, object);
    }

    public <T extends RocketEngineType<?>> T registerEngine(String name, T object) {
        return rocketEngines.register(name, object);
    }

    public <T extends RocketUpgradeType<?>> T registerUpgrade(String name, T object) {
        return rocketUpgrades.register(name, object);
    }
}
