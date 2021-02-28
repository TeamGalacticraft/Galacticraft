/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.accessor.GearInventoryProvider;
import com.hrznstudio.galacticraft.attribute.GalacticraftAttributes;
import com.hrznstudio.galacticraft.attribute.oxygen.InfiniteOxygenTank;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTankImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenTankItem extends Item implements AttributeProviderItem {
    private float hue = 0;

    public OxygenTankItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        if (this.isIn(group)) {
            final ItemStack[] stack = new ItemStack[]{new ItemStack(this)};
            stack[0].setDamage(stack[0].getMaxDamage());
            list.add(stack[0]);
            stack[0] = stack[0].copy();

            if (this.getMaxDamage() > 0) {
                GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(new Reference<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        return stack[0];
                    }

                    @Override
                    public boolean set(ItemStack itemStack) {
                        stack[0] = itemStack;
                        return true;
                    }

                    @Override
                    public boolean isValid(ItemStack stack) {
                        return stack.getItem() instanceof OxygenTankItem;
                    }
                }).setAmount(this.getMaxDamage());
                list.add(stack[0]);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        if (this.getMaxDamage() > 0){
            OxygenTank tank = GalacticraftAttributes.OXYGEN_TANK_ATTRIBUTE.getFirst(stack);
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.oxygen_remaining", tank.getAmount() + "/" + tank.getCapacity()));
        } else {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.oxygen_remaining", new TranslatableText("tooltip.galacticraft-rewoven.infinite").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(HSBtoRGB(hue += 0.005))))));
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.creative_only").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            if (hue > 1.0f) hue -= 1.0f;
        }
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) { //should sync with server
        FixedItemInv inv = ((GearInventoryProvider)player).getGearInv();
        if (inv.getInvStack(6).isEmpty()) {
            inv.setInvStack(6, player.getStackInHand(hand).copy(), Simulation.ACTION);
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        } else if (inv.getInvStack(7).isEmpty()) {
            inv.setInvStack(7, player.getStackInHand(hand).copy(), Simulation.ACTION);
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
    }

    @Override
    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> itemAttributeList) {
        ItemStack ref = reference.get().copy();
        if (ref.getMaxDamage() > 0) {
            OxygenTankImpl tank = new OxygenTankImpl(ref.getMaxDamage());
            tank.fromTag(ref.getOrCreateTag());
            tank.toTag(ref.getOrCreateTag());
            ref.setDamage(ref.getMaxDamage() - tank.getAmount());
            reference.set(ref);
            itemAttributeList.offer(tank.listen(view -> {
                        ItemStack stack = reference.get().copy();
                        stack.setDamage(stack.getMaxDamage() - view.getAmount());
                        tank.toTag(stack.getOrCreateTag());
                        reference.set(stack);
                    }
            ));
        } else {
            itemAttributeList.offer(new InfiniteOxygenTank());
        }
    }

    /**@see java.awt.Color#HSBtoRGB*/
    private static int HSBtoRGB(float hue) {
        int r = 0, g = 0, b = 0;
        if ((float) 1 == 0) {
            r = g = b = (int) (255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = 0.0f;
            float q = (1.0f - f);
            float t = (1.0f - ((1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
}
