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

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.attribute.GcApiAttributes;
import dev.galacticraft.api.attribute.oxygen.OxygenTank;
import dev.galacticraft.api.attribute.oxygen.OxygenTankImpl;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attribute.misc.ArrayReference;
import dev.galacticraft.mod.attribute.oxygen.InfiniteOxygenTank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankItem extends Item implements AttributeProviderItem {
    private int ticks = (int) (Math.random() * 500.0);
    private final int size;

    public OxygenTankItem(Settings settings, int size) {
        super(settings.maxDamage(size));
        this.size = size;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        if (this.isIn(group)) {
            final ItemStack[] stack = new ItemStack[]{new ItemStack(this)};
            stack[0].setDamage(this.size);
            list.add(stack[0]);
            stack[0] = stack[0].copy();

            if (this.size > 0) {
                GcApiAttributes.OXYGEN_TANK.getFirst(new ArrayReference<>(stack, 0, itemStack -> itemStack.getItem() instanceof OxygenTankItem)).setAmount(this.size);
                list.add(stack[0]);
            }
        }
    }

    @Override
    public int getEnchantability() {
        return -1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.size <= 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        if (this.size > 0){
            OxygenTank tank = GcApiAttributes.OXYGEN_TANK.getFirst(stack);
            lines.add(new TranslatableText("tooltip.galacticraft.oxygen_remaining", tank.getAmount() + "/" + tank.getCapacity()).setStyle(Constant.Text.getStorageLevelColor(1.0 - ((double)tank.getAmount() / (double)tank.getCapacity()))));
        } else {
            lines.add(new TranslatableText("tooltip.galacticraft.oxygen_remaining", new TranslatableText("tooltip.galacticraft.infinite").setStyle(Constant.Text.getRainbow(++ticks))));
            lines.add(new TranslatableText("tooltip.galacticraft.creative_only").setStyle(Constant.Text.LIGHT_PURPLE_STYLE));
        }
        if (ticks >= 500) ticks -= 500;
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) { //should sync with server
        if (((GearInventoryProvider) user).getOxygenTanks().getInsertable().insert(user.getStackInHand(hand).copy()).isEmpty()) {
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> to) {
        ItemStack ref = reference.get().copy();
        if (this.size > 0) {
            OxygenTankImpl tank = new OxygenTankImpl(this.size);
            tank.fromTag(ref.getOrCreateNbt());
            tank.toTag(ref.getOrCreateNbt());
            ref.setDamage(this.size - tank.getAmount());
            reference.set(ref);
            tank.listen((view, previous) -> {
                        ItemStack stack = reference.get().copy();
                        stack.setDamage(this.size - view.getAmount());
                        tank.toTag(stack.getOrCreateNbt());
                        reference.set(stack);
                    }
            );
            to.offer(tank);
        } else {
            to.offer(InfiniteOxygenTank.INSTANCE);
        }
    }
}
