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

package dev.galacticraft.api.rocket.part;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.config.*;
import dev.galacticraft.api.rocket.part.type.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public enum RocketPartTypes {
    CONE(RocketRegistries.ROCKET_CONE, RocketConeConfig.class, RocketConeType.class, RocketCone.class, Component.translatable(Translations.Ui.CONE)),
    BODY(RocketRegistries.ROCKET_BODY, RocketBodyConfig.class, RocketBodyType.class, RocketBody.class, Component.translatable(Translations.Ui.BODY)),
    FIN(RocketRegistries.ROCKET_FIN, RocketFinConfig.class, RocketFinType.class, RocketFin.class, Component.translatable(Translations.Ui.FINS)),
    BOOSTER(RocketRegistries.ROCKET_BOOSTER, RocketBoosterConfig.class, RocketBoosterType.class, RocketBooster.class, Component.translatable(Translations.Ui.BOOSTER)),
    ENGINE(RocketRegistries.ROCKET_ENGINE, RocketEngineConfig.class, RocketEngineType.class, RocketEngine.class, Component.translatable(Translations.Ui.ENGINE)),
    UPGRADE(RocketRegistries.ROCKET_UPGRADE, RocketUpgradeConfig.class, RocketUpgradeType.class, RocketUpgrade.class, Component.translatable(Translations.Ui.UPGRADE));

    public final ResourceKey<? extends Registry<? extends RocketPart<?, ?>>> key;
    private final Class<? extends RocketPartConfig> config;
    private final Class<? extends RocketPartType> type;
    private final Class<? extends RocketPart> part;
    public final Component name;

    <C extends RocketPartConfig, T extends RocketPartType<C>, P extends RocketPart<C, T>> RocketPartTypes(ResourceKey<? extends Registry<? extends RocketPart<?, ?>>> key, Class<C> config, Class<T> type, Class<P> part, Component name) {
        this.key = key;
        this.config = config;
        this.type = type;
        this.part = part;
        this.name = name;
    }

    public static RocketPartTypes fromPart(ResourceKey<? extends RocketPart<?, ?>> key) {
        assert key.registry().getNamespace().equals(Constant.MOD_ID);
        switch (key.registry().getPath()) {
            case "rocket_cone" -> {
                return CONE;
            }
            case "rocket_body" -> {
                return BODY;
            }
            case "rocket_fin" -> {
                return FIN;
            }
            case "rocket_booster" -> {
                return BOOSTER;
            }
            case "rocket_engine" -> {
                return ENGINE;
            }
            case "rocket_upgrade" -> {
                return UPGRADE;
            }
        }
        throw new RuntimeException();
    }
}
