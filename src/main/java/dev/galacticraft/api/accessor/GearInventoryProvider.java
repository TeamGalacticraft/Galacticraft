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

package dev.galacticraft.api.accessor;

import dev.galacticraft.api.item.OxygenGear;
import dev.galacticraft.api.item.OxygenMask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;

public interface GearInventoryProvider {
    default Container galacticraft$getGearInv() {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default Container galacticraft$getOxygenTanks() {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default Container galacticraft$getThermalArmor() {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default Container galacticraft$getAccessories() {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default boolean galacticraft$hasMaskAndGear() {
        boolean mask = false;
        boolean gear = false;
        for (int i = 0; i < this.galacticraft$getAccessories().getContainerSize(); i++) {
            Item item = this.galacticraft$getAccessories().getItem(i).getItem();
            if (!mask && item instanceof OxygenMask) {
                mask = true;
                if (gear) break;
            } else if (!gear && item instanceof OxygenGear) {
                gear = true;
                if (mask) break;
            }
        }
        return mask && gear;
    }

    default boolean galacticraft$hasMask() {
        for (int i = 0; i < this.galacticraft$getAccessories().getContainerSize(); i++) {
            Item item = this.galacticraft$getAccessories().getItem(i).getItem();
            if (item instanceof OxygenMask) {
                return true;
            }
        }
        return false;
    }

    default void galacticraft$writeGearToNbt(CompoundTag tag) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default void galacticraft$readGearFromNbt(CompoundTag tag) {
        throw new RuntimeException("This should be overridden by mixin!");
    }
}
