/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.phys.Vec3;

public class RocketTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void exhaustIgnitesEntitiesTest(GameTestHelper context) {
        RocketEntity rocket = context.spawn(GCEntityTypes.ROCKET, new Vec3(2.5, 4, 2.5));
        rocket.setNoGravity(true);
        Pig below = context.spawn(EntityType.PIG, new BlockPos(2, 1, 2));
        Pig aside = context.spawn(EntityType.PIG, new BlockPos(6, 1, 6));
        Pig passenger = context.spawn(EntityType.PIG, new BlockPos(2, 4, 2));
        passenger.startRiding(rocket, true);

        rocket.setLaunchStage(LaunchStage.LAUNCHED);
        rocket.igniteEntitiesInExhaust();

        if (!below.isOnFire()) {
            context.fail("Expected the entity below the rocket to be set on fire on the server");
        } else if (aside.isOnFire()) {
            context.fail("Expected the entity next to the exhaust not to be set on fire");
        } else if (passenger.isOnFire()) {
            context.fail("Expected the passenger not to be set on fire");
        } else {
            context.succeed();
        }
    }
}
