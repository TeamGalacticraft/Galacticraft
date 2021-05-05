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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenMaskItem extends Item {
    public OxygenMaskItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        FixedItemInv inv = ((GearInventoryProvider)player).getGearInv();
        if (inv.getInvStack(4).isEmpty()) {
            inv.setInvStack(4, player.getStackInHand(hand), Simulation.ACTION);
            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, player, hand);
    }

    public DyeColor getColor() {
        return null;
    }


    public static class WhiteOxygenMaskItem extends OxygenMaskItem {
        public WhiteOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.WHITE;
        }
    }

    public static class GreyOxygenMaskItem extends OxygenMaskItem {
        public GreyOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.GRAY;
        }
    }

    public static class BlackOxygenMaskItem extends OxygenMaskItem {
        public BlackOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.BLACK;
        }
    }

    public static class OrangeOxygenMaskItem extends OxygenMaskItem {
        public OrangeOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.ORANGE;
        }
    }

    public static class MagentaOxygenMaskItem extends OxygenMaskItem {
        public MagentaOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.MAGENTA;
        }
    }

    public static class LightBlueOxygenMaskItem extends OxygenMaskItem {
        public LightBlueOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.LIGHT_BLUE;
        }
    }

    public static class YellowOxygenMaskItem extends OxygenMaskItem {
        public YellowOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.YELLOW;
        }
    }

    public static class LimeOxygenMaskItem extends OxygenMaskItem {
        public LimeOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.LIME;
        }
    }

    public static class PinkOxygenMaskItem extends OxygenMaskItem {
        public PinkOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.PINK;
        }
    }

    public static class LightGreyOxygenMaskItem extends OxygenMaskItem {
        public LightGreyOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.LIGHT_GRAY;
        }
    }

    public static class CyanOxygenMaskItem extends OxygenMaskItem {
        public CyanOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.CYAN;
        }
    }

    public static class PurpleOxygenMaskItem extends OxygenMaskItem {
        public PurpleOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.PURPLE;
        }
    }

    public static class BlueOxygenMaskItem extends OxygenMaskItem {
        public BlueOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.PURPLE;
        }
    }

    public static class BrownOxygenMaskItem extends OxygenMaskItem {
        public BrownOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.BROWN;
        }
    }

    public static class GreenOxygenMaskItem extends OxygenMaskItem {
        public GreenOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.GREEN;
        }
    }

    public static class RedOxygenMaskItem extends OxygenMaskItem {
        public RedOxygenMaskItem(Settings settings) {
            super(settings);
        }
        @Override
        public DyeColor getColor() {
            return DyeColor.RED;
        }
    }
}
