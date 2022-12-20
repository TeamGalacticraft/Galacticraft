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

package dev.galacticraft.api.rocket.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.accessor.ResearchAccessor;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessTypeTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.ConstantTravelPredicateType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RocketPart(MutableComponent name, RocketPartType type, ConfiguredTravelPredicate<?> travelPredicate,
                         boolean hasRecipe, ResourceLocation research) {
    public static final Codec<RocketPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").xmap(Component::translatable, Component::getString).forGetter(RocketPart::name),
            RocketPartType.CODEC.fieldOf("type").forGetter(RocketPart::type),
            ConfiguredTravelPredicate.CODEC.fieldOf("travel_predicate").forGetter(RocketPart::travelPredicate),
            Codec.BOOL.fieldOf("recipe").forGetter(RocketPart::hasRecipe),
            ResourceLocation.CODEC.optionalFieldOf("research").xmap(o -> o.orElse(null), Optional::ofNullable).forGetter(RocketPart::research)
    ).apply(instance, RocketPart::new));

    public static final RocketPart INVALID = Builder.create()
            .name(Component.translatable("tooltip.galacticraft-api.something_went_wrong"))
            .type(RocketPartType.UPGRADE)
            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.BLOCK)))
            .research(new ResourceLocation(Constant.MOD_ID, "unobtainable"))
            .recipe(false)
            .build();

    public RocketPart(@NotNull MutableComponent name, @NotNull RocketPartType type, ConfiguredTravelPredicate<?> travelPredicate, boolean hasRecipe, ResourceLocation research) {
        this.type = type;
        this.name = name;
        this.travelPredicate = travelPredicate;
        this.hasRecipe = hasRecipe;
        this.research = research;
    }

    public static RocketPart deserialize(RegistryAccess manager, Dynamic<?> dynamic) {
        return manager.registryOrThrow(AddonRegistry.ROCKET_PART_KEY).get(new ResourceLocation(dynamic.asString("")));
    }

    public static Registry<RocketPart> getRegistry(RegistryAccess manager) {
        return manager.registryOrThrow(AddonRegistry.ROCKET_PART_KEY);
    }

    public static RocketPart getById(RegistryAccess manager, ResourceLocation id) {
        return getById(getRegistry(manager), id);
    }

    public static ResourceLocation getId(RegistryAccess manager, RocketPart rocketPart) {
        return getId(getRegistry(manager), rocketPart);
    }

    public static RocketPart getById(Registry<RocketPart> registry, ResourceLocation id) {
        return registry.get(id);
    }

    public static ResourceLocation getId(Registry<RocketPart> registry, RocketPart rocketPart) {
        return registry.getKey(rocketPart);
    }

    public boolean isUnlocked(Player player) {
        if (this.research() == null) return true;
        return ((ResearchAccessor) player).hasUnlockedResearch(this.research());
    }

    public static class Builder {
        private MutableComponent name;
        private RocketPartType partType;
        private ConfiguredTravelPredicate<?> travelPredicate = ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS));
        private boolean hasRecipe = true;
        private ResourceLocation research = null;

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }


        public Builder name(MutableComponent name) {
            this.name = name;
            return this;
        }

        public Builder type(RocketPartType type) {
            this.partType = type;
            return this;
        }

        public Builder recipe(boolean hasRecipe) {
            this.hasRecipe = hasRecipe;
            return this;
        }

        public Builder travelPredicate(ConfiguredTravelPredicate<?> travelPredicate) {
            this.travelPredicate = travelPredicate;
            return this;
        }

        public Builder research(ResourceLocation research) {
            this.research = research;
            return this;
        }

        public RocketPart build() {
            if (name == null || partType == null) {
                throw new RuntimeException("Tried to build incomplete RocketPart!");
            }
            return new RocketPart(name, partType, travelPredicate, hasRecipe, research);
        }
    }
}
