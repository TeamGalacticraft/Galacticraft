package dev.galacticraft.api.rocket.part;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.config.*;
import dev.galacticraft.api.rocket.part.type.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public enum RocketPartTypes {
    CONE(RocketRegistries.ROCKET_CONE, RocketConeConfig.class, RocketConeType.class, RocketCone.class, Component.translatable("ui.galacticraft.cone")),
    BODY(RocketRegistries.ROCKET_BODY, RocketBodyConfig.class, RocketBodyType.class, RocketBody.class, Component.translatable("ui.galacticraft.body")),
    FIN(RocketRegistries.ROCKET_FIN, RocketFinConfig.class, RocketFinType.class, RocketFin.class, Component.translatable("ui.galacticraft.fins")),
    BOOSTER(RocketRegistries.ROCKET_BOOSTER, RocketBoosterConfig.class, RocketBoosterType.class, RocketBooster.class, Component.translatable("ui.galacticraft.booster")),
    BOTTOM(RocketRegistries.ROCKET_BOTTOM, RocketBottomConfig.class, RocketBottomType.class, RocketBottom.class, Component.translatable("ui.galacticraft.engine")),
    UPGRADE(RocketRegistries.ROCKET_UPGRADE, RocketUpgradeConfig.class, RocketUpgradeType.class, RocketUpgrade.class, Component.translatable("ui.galacticraft.upgrade"));

//    private final ResourceKey<? extends Registry<? super RocketPart<?, ?>>> key;
    private final Class<? extends RocketPartConfig> config;
    private final Class<? extends RocketPartType> type;
    private final Class<? extends RocketPart> part;
    public final Component name;

    <C extends RocketPartConfig, T extends RocketPartType<C>, P extends RocketPart<C, T>> RocketPartTypes(Object key, Class<C> config, Class<T> type, Class<P> part, Component name) {
//        this.key = key;
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
            case "rocket_bottom" -> {
                return BOTTOM;
            }
            case "rocket_upgrade" -> {
                return UPGRADE;
            }
        }
        throw new RuntimeException();
    }
}
