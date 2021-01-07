/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ThermalArmorItem extends Item {
    private final EquipmentSlot slotType;

    public ThermalArmorItem(Settings settings, EquipmentSlot slotType) {
        super(settings);
        this.slotType = slotType;
    }

    public EquipmentSlot getSlotType() {
        return slotType;
    }

    @Override //should sync with server
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        InventoryComponent inv = GalacticraftComponents.GEAR_INVENTORY_COMPONENT.get(player);
        ItemStack thermalPiece = inv.getStack(getSlotIdForType(getSlotType()));
        if (thermalPiece.isEmpty()) {
            inv.setStack(getSlotIdForType(getSlotType()), player.getStackInHand(hand));
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, player, hand);
    }


    public int getSlotIdForType(EquipmentSlot slotType) {
        switch (slotType) {
            case HEAD:
                return 0;
            case CHEST:
                return 1;
            case LEGS:
                return 2;
            case FEET:
                return 3;
        }
        return -128; //oh no
    }
}