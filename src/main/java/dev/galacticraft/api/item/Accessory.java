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

package dev.galacticraft.api.item;

import net.minecraft.world.entity.LivingEntity;

public interface Accessory {
    default AccessoryType getType() {
        return AccessoryType.ACCESSORY;
    }

    default int getSlot() {
        return AccessoryType.ACCESSORY.getSlot();
    }

    default boolean enablesHearing() {
        return false;
    }

    default void tick(LivingEntity entity) {

    }

    enum AccessoryType {
        THERMAL_HEAD(0),
        THERMAL_CHEST(1),
        THERMAL_PANTS(2),
        THERMAL_BOOTS(3),
        OXYGEN_MASK(6),
        OXYGEN_GEAR(7),
        OXYGEN_TANK_1(4),
        OXYGEN_TANK_2(5),
        FREQUENCY_MODULE(8),
        PARACHUTE(9),
        SHIELD_CONTROLLER(10),
        ACCESSORY(11);

        private final int slot;

        AccessoryType(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return this.slot;
        }
    }
}
