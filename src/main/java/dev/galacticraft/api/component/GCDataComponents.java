package dev.galacticraft.api.component;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.EitherHolder;

import java.util.function.UnaryOperator;

public class GCDataComponents {
    public static final DataComponentType<EitherHolder<RocketCone<?, ?>>> ROCKET_CONE = register("rocket_cone", b -> b
            .persistent(RocketCone.EITHER_CODEC).networkSynchronized(RocketCone.EITHER_STREAM_CODEC));
    public static final DataComponentType<EitherHolder<RocketBody<?, ?>>> ROCKET_BODY = register("rocket_body", b -> b
            .persistent(RocketBody.EITHER_CODEC).networkSynchronized(RocketBody.EITHER_STREAM_CODEC));
    public static final DataComponentType<EitherHolder<RocketFin<?, ?>>> ROCKET_FIN = register("rocket_fin", b -> b
            .persistent(RocketFin.EITHER_CODEC).networkSynchronized(RocketFin.EITHER_STREAM_CODEC));
    public static final DataComponentType<EitherHolder<RocketBooster<?, ?>>> ROCKET_BOOSTER = register("rocket_booster", b -> b
            .persistent(RocketBooster.EITHER_CODEC).networkSynchronized(RocketBooster.EITHER_STREAM_CODEC));
    public static final DataComponentType<EitherHolder<RocketEngine<?, ?>>> ROCKET_ENGINE = register("rocket_engine", b -> b
            .persistent(RocketEngine.EITHER_CODEC).networkSynchronized(RocketEngine.EITHER_STREAM_CODEC));
    public static final DataComponentType<EitherHolder<RocketUpgrade<?, ?>>> ROCKET_UPGRADE = register("rocket_upgrade", b -> b
            .persistent(RocketUpgrade.EITHER_CODEC).networkSynchronized(RocketUpgrade.EITHER_STREAM_CODEC));
    public static final DataComponentType<Integer> OXYGEN = register("oxygen", b -> b
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Integer> COLOR = register("color", b -> b
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Integer> TICKS_UNTIL_COOL = register("ticks_until_cool", b -> b
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Boolean> CREATIVE = register("creative", b -> b
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> op) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Constant.id(id), op.apply(DataComponentType.builder()).build());
    }

    public static void init() {}
}
