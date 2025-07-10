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

package dev.galacticraft.impl.internal.mixin.gear;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static dev.galacticraft.mod.content.GCAccessorySlots.*;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Entity implements GearInventoryProvider, OwnableEntity {

    private final @Unique SimpleContainer gearInv = this.galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, OXYGEN_TANK_1_SLOT);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, PET_THERMAL_SLOT);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, OXYGEN_MASK_SLOT, OXYGEN_GEAR_SLOT);

    TamableAnimalMixin() {
        super(null, null);
    }

    @Override
    public SimpleContainer galacticraft$getGearInv() {
        return this.gearInv;
    }

    @Override
    public Container galacticraft$getOxygenTanks() {
        return this.tankInv;
    }

    @Override
    public Container galacticraft$getThermalArmor() {
        return this.thermalArmorInv;
    }

    @Override
    public Container galacticraft$getAccessories() {
        return this.accessoryInv;
    }

    @Override
    public long galacticraft$oxygenConsumptionRate() {
        TamableAnimal animal = (TamableAnimal) (Object) this;
        LivingEntity owner = animal.getOwner();
        if (owner == null || (!owner.isSpectator() && animal.distanceToSqr(owner) >= 4096.0)) {
            return 0;
        } else if (animal instanceof Wolf) {
            return Galacticraft.CONFIG.wolfOxygenConsumptionRate();
        } else if (animal instanceof Cat) {
            return Galacticraft.CONFIG.catOxygenConsumptionRate();
        } else if (animal instanceof Parrot) {
            return Galacticraft.CONFIG.parrotOxygenConsumptionRate();
        }
        return Galacticraft.CONFIG.playerOxygenConsumptionRate();
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.gearInv.createTag(this.registryAccess()));
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.gearInv.fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), this.registryAccess());
    }
}
