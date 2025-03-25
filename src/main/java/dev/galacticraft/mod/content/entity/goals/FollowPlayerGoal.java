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

package dev.galacticraft.mod.content.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

// vanilla copy of net.minecraft.world.entity.ai.goal.FollowMobGoal adapted for players
public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    @Nullable
    private Player following;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;

    public FollowPlayerGoal(Mob mob, double speedModifier, float stopDistance, float areaSize) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.navigation = mob.getNavigation();
        this.stopDistance = stopDistance;
        this.areaSize = areaSize;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean canUse() {
        for (Player player : this.mob.level().getEntitiesOfClass(Player.class, this.mob.getBoundingBox().inflate(this.areaSize), p -> !p.isInvisible())) {
            this.following = player;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.following != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.following) > (double) (this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(PathType.WATER);
        this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.following = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.following != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().setLookAt(this.following, 10.0F, (float) this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double d = this.mob.getX() - this.following.getX();
                double e = this.mob.getY() - this.following.getY();
                double f = this.mob.getZ() - this.following.getZ();
                double g = d * d + e * e + f * f;
                if (!(g <= (this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.following, this.speedModifier);
                } else {
                    this.navigation.stop();
                    if (g <= this.stopDistance) {
                        double h = this.following.getX() - this.mob.getX();
                        double i = this.following.getZ() - this.mob.getZ();
                        this.navigation.moveTo(this.mob.getX() - h, this.mob.getY(), this.mob.getZ() - i, this.speedModifier);
                    }
                }
            }
        }
    }
}