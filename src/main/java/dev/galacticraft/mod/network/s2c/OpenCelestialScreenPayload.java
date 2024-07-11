package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenCelestialScreenPayload(RocketData data, Holder<CelestialBody> celestialBody) implements S2CPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCelestialScreenPayload> STREAM_CODEC = StreamCodec.composite(
            RocketData.STREAM_CODEC,
            p -> p.data,
            ByteBufCodecs.holderRegistry(AddonRegistries.CELESTIAL_BODY),
            p -> p.celestialBody,
            OpenCelestialScreenPayload::new
    );

    public static final ResourceLocation ID = Constant.id("open_celestial_screen");
    public static final Type<OpenCelestialScreenPayload> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(ClientPlayNetworking.@NotNull Context context) {
        context.client().execute(() -> context.client().setScreen(new CelestialSelectionScreen(false, this.data(), true, payload.celestialBody().value())));
    }
}
