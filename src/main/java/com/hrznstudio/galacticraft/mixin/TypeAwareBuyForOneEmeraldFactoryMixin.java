package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.entity.MoonVillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Mixin(targets = "net/minecraft/village/TradeOffers$TypeAwareBuyForOneEmeraldFactory")
public class TypeAwareBuyForOneEmeraldFactoryMixin {

    @Mutable
    @Shadow @Final private Map<VillagerType, Item> map;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DefaultedRegistry;stream()Ljava/util/stream/Stream;"))
    private Stream<VillagerType> skipCheck(DefaultedRegistry<VillagerType> defaultedRegistry) {
        return Stream.empty(); //skip check
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void addMoonVillagerTrades(int i, int j, int experience, Map<VillagerType, Item> map, CallbackInfo ci) {
        this.map = new HashMap<>(this.map);
        this.map.put(MoonVillagerEntity.MOON_VILLAGER_TYPE, Items.AIR);
    }
}
