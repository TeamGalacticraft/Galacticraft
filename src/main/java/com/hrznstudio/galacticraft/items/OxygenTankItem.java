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

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.item.InventoryComponent;
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
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenTankItem extends Item {
    public OxygenTankItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            list.add(stack);
            stack = stack.copy();
            UniversalComponents.TANK_COMPONENT.get(stack).setFluid(0, new FluidVolume(GalacticraftFluids.OXYGEN, ComponentHelper.TANK.getComponent(stack).getMaxAmount_F(0)));
            list.add(stack);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        lines.add(new TranslatableText("tooltip.galacticraft-rewoven.oxygen_remaining", ((int)(OxygenUtils.getOxygen(stack).asInexactDouble() * 100.0D) + "/" + (int)(OxygenUtils.getMaxOxygen(stack).asInexactDouble() * 100.0D))));
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) { //should sync with server
        InventoryComponent component = GalacticraftComponents.GEAR_INVENTORY_COMPONENT.get(player);
        if (component.getStack(6).isEmpty()) {
            component.setStack(6, player.getStackInHand(hand).copy());
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        } else if (component.getStack(7).isEmpty()) {
            component.setStack(7, player.getStackInHand(hand).copy());
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
    }
}
