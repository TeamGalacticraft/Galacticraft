/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.item;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ThermalArmorItem extends Item {
    private final EquipmentSlot slotType;

    public ThermalArmorItem(Settings settings, EquipmentSlot slotType) {
        super(settings.maxCount(1));
        this.slotType = slotType;
    }

    public EquipmentSlot getSlotType() {
        return slotType;
    }

    @Override //should sync with server
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        FixedItemInv inv = ((GearInventoryProvider)player).getGearInv();
        ItemStack thermalPiece = inv.getInvStack(getSlotIdForType(getSlotType()));
        if (thermalPiece.isEmpty()) {
            inv.setInvStack(getSlotIdForType(getSlotType()), player.getStackInHand(hand), Simulation.ACTION);
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, player, hand);
    }

    public int getSlotIdForType(EquipmentSlot slotType) {
        return switch (slotType) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            default -> -128;
        };
    }
}