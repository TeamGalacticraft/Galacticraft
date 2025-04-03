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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.content.entity.goals.FollowPlayerGoal;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GreyEntity extends PathfinderMob implements InventoryCarrier, Npc {

    @Nullable
    private FollowPlayerGoal followPlayerGoal;
    @Nullable
    private GreyAvoidEntityGoal greyAvoidEntityGoal;

    public GreyEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        // this.goalSelector.addGoal(0, new RandomLookAroundGoal(this));
        this.followPlayerGoal = new FollowPlayerGoal(this, 10.0F, 3.0f, 0.8f);
        this.greyAvoidEntityGoal = new GreyAvoidEntityGoal<>(this, Player.class, 3.0f, 0.8, 1);
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 15F));
        this.goalSelector.addGoal(0, this.followPlayerGoal);

    }


    boolean isNearbyPlayer;
    boolean allowedToRemoveFollowingGoal = true;
    int tickAgeNearOrAwayFromPlayer = 0;

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && allowedToRemoveFollowingGoal) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 2.5D);
            isNearbyPlayer = nearestPlayer != null;
            tickAgeNearOrAwayFromPlayer = isNearbyPlayer ? tickAgeNearOrAwayFromPlayer + 1 : 0;
            if (tickAgeNearOrAwayFromPlayer >= 50) {
                goalSelector.removeGoal(followPlayerGoal);
                goalSelector.addGoal(1, greyAvoidEntityGoal);
                allowedToRemoveFollowingGoal = false;
                tickAgeNearOrAwayFromPlayer = 0;
            }
        }
        if (!level().isClientSide && !allowedToRemoveFollowingGoal) {
            tickAgeNearOrAwayFromPlayer++;
            if (tickAgeNearOrAwayFromPlayer >= 80) {
                goalSelector.removeGoal(greyAvoidEntityGoal);
                goalSelector.addGoal(0, followPlayerGoal);
                allowedToRemoveFollowingGoal = true;
                tickAgeNearOrAwayFromPlayer = 0;
            }
        }

    }

    @Override
    public SimpleContainer getInventory() {
        return new SimpleContainer(1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0)
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D)
                .add(Attributes.GRAVITY, 0.9F);
    }

    static class GreyAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

        private final GreyEntity greyEntity;

        public GreyAvoidEntityGoal(GreyEntity greyEntity, Class<T> class_, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(greyEntity, class_, maxDist, walkSpeedModifier, sprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.greyEntity = greyEntity;
        }

    }

}