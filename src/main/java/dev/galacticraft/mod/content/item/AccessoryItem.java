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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.mod.content.GCAccessorySlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AccessoryItem extends Item implements Accessory {
    public Holder<SoundEvent> equipSound;

    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior(){

        @Override
        protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            return AccessoryItem.dispenseAccessory(blockSource, itemStack) ? itemStack : super.execute(blockSource, itemStack);
        }
    };

    public static boolean dispenseAccessory(BlockSource blockSource, ItemStack itemStack) {
        BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        List<Player> list = blockSource.level().getEntitiesOfClass(Player.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS);
        if (list.isEmpty()) {
            return false;
        }
        Player player = (Player)list.get(0);
        Container inv = player.galacticraft$getGearInv();
        for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
            if (inv.getItem(slot).isEmpty() && itemStack.is(GCAccessorySlots.SLOT_TAGS.get(slot))) {
                ItemStack itemStack2 = itemStack.split(1);
                inv.setItem(slot, itemStack2);
                player.galacticraft$onEquipAccessory(itemStack, itemStack2);
                return true;
            }
        }
        return false;
    }

    public AccessoryItem(Properties settings, Holder<SoundEvent> equipSound) {
        super(settings.stacksTo(1));
        this.equipSound = equipSound;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public AccessoryItem(Properties settings) {
        this(settings, SoundEvents.ARMOR_EQUIP_GENERIC);
    }

    public Holder<SoundEvent> getEquipSound() {
        return this.equipSound;
    }

    @Override //should sync with server
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Container inv = player.galacticraft$getGearInv();
        ItemStack itemStack = player.getItemInHand(hand);
        for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
            if (itemStack.is(GCAccessorySlots.SLOT_TAGS.get(slot))) {
                ItemStack itemStack2 = inv.getItem(slot);
                if (8 <= slot && slot < 11 && !itemStack2.isEmpty()) {
                    continue;
                }
                if (ItemStack.matches(itemStack, itemStack2) || (itemStack2.getItem() instanceof OxygenTankItem && OxygenTankItem.getStorage(itemStack2).getAmount() > 0)) {
                    if (slot == GCAccessorySlots.OXYGEN_TANK_1_SLOT) {
                        continue;
                    }
                    return InteractionResultHolder.fail(itemStack);
                }
                if (!level.isClientSide()) {
                    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                }
                ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndClear();
                ItemStack itemStack4 = player.isCreative() ? itemStack.copy() : itemStack.copyAndClear();
                inv.setItem(slot, itemStack4);
                player.galacticraft$onEquipAccessory(itemStack3, itemStack4);
                return InteractionResultHolder.sidedSuccess(itemStack3, level.isClientSide());
            }
        }
        return super.use(level, player, hand);
    }
}
