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

import dev.galacticraft.mod.content.entity.OliGrubEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class LatchWhenCloseGoal extends Goal {
    private final OliGrubEntity grub;
    private final double speed;
    private final double latchRange;

    private Player target;

    /**
     * @param grub        the entity
     * @param speed       navigation speed while approaching
     * @param latchRange  distance at which it attempts to latch in blocks
     */
    public LatchWhenCloseGoal(OliGrubEntity grub, double speed, double latchRange) {
        this.grub = grub;
        this.speed = speed;
        this.latchRange = latchRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        if (grub.level().isClientSide) return false;
        if (grub.isLatched()) return false;

        Player nearest = grub.level().getNearestPlayer(grub, 6.0);
        if (nearest == null || nearest.isCreative() || nearest.isSpectator()) return false;
        if (holdsRedstone(nearest)) return false;

        this.target = nearest;
        return true;
    }

    private static boolean holdsRedstone(Player p) {
        return p.getMainHandItem().is(Items.REDSTONE) || p.getOffhandItem().is(Items.REDSTONE); //TODO change from redstone in future
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && !grub.isLatched() && !holdsRedstone(target);
    }

    @Override
    public void start() {
        // nothing
    }

    @Override
    public void stop() {
        this.target = null;
        grub.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        grub.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double dist = grub.distanceTo(target);
        if (dist > latchRange) {
            grub.getNavigation().moveTo(target, speed);
        } else {
            if (grub.onGround()) {
                grub.setJumping(true);
                grub.setDeltaMovement(grub.getDeltaMovement().add(
                        (target.getX() - grub.getX()) * 0.05,
                        0.25,
                        (target.getZ() - grub.getZ()) * 0.05
                ));
            }
            grub.tryLatchOnto(target);
        }
    }
}