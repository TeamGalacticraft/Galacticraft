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

package dev.galacticraft.mod.content.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class LaunchRocketTrigger extends SimpleCriterionTrigger<LaunchRocketTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, conditions -> {
            if (player.getVehicle() instanceof RocketEntity rocket) {
                if (rocket.getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                    return player.getVehicle().getPassengers().size() >= 1 && player.getVehicle().getPassengers().get(0) instanceof ServerPlayer;
                }
            }
            return false;
        });
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player)
                        )
                        .apply(instance, TriggerInstance::new)
        );
    }
}
