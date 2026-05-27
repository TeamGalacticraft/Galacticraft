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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MoonChickenEntity extends Chicken {
    public MoonChickenEntity(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes().add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D);
    }

    @Nullable
    @Override
        public MoonChickenEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return GCEntityTypes.MOON_CHICKEN.create(serverLevel);
    }

    @Override
    public boolean galacticraft$hasMask() {
        return true;
    }

    @Override
    public boolean galacticraft$hasGear() {
        return true;
    }

    @Override
    public String galacticraft$tankSize(int i) {
        return Constant.Item.SMALL_OXYGEN_TANK;
    }
}