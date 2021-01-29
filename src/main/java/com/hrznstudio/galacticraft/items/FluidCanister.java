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

import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FluidCanister extends Item {
    public FluidCanister(Settings settings) {
        super(settings.maxDamage(1000));
    }

    @Override
    public int getEnchantability() {
        return -1;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        TankComponent component = ComponentHelper.TANK.getComponent(stack);
        if (component.getContents(0).isEmpty()) {
            tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.no_fluid"));
        } else {
            Fraction fraction = component.getContents(0).getAmount().multiply(Fraction.ONE);
            if (!Screen.hasShiftDown()) {
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets_fraction", fraction.getNumerator(), fraction.getDenominator()));
            } else {
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.buckets", fraction.doubleValue()));
            }
            tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.fluid", Registry.FLUID.getId(component.getContents(0).getFluid())));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        TankComponent tank = ComponentHelper.TANK.getComponent(stack);

        if (tank.isEmpty()) {
            BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
            if (hitResult.getType() == HitResult.Type.MISS) {
                return TypedActionResult.pass(stack);
            } else if (hitResult.getType() != HitResult.Type.BLOCK) {
                return TypedActionResult.pass(stack);
            } else {
                BlockPos blockPos = hitResult.getBlockPos();
                Direction direction = hitResult.getSide();
                BlockPos blockPos2 = blockPos.offset(direction);
                if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, stack)) {
                    BlockState blockState;
                    if (tank.isEmpty()) {
                        blockState = world.getBlockState(blockPos);
                        if (blockState.getBlock() instanceof FluidDrainable) {
                            Fluid fluid = ((FluidDrainable) blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
                            if (fluid != Fluids.EMPTY) {
                                user.incrementStat(Stats.USED.getOrCreateStat(this));
                                user.playSound(fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                                ItemStack stack1 = stack.copy();
                                ComponentHelper.TANK.getComponent(stack1).setFluid(0, new FluidVolume(fluid, Fraction.ONE));

                                return TypedActionResult.success(stack1, world.isClient());
                            }
                        }

                        return TypedActionResult.fail(stack);
                    }
                }
            }
        }
        return TypedActionResult.fail(stack);
    }
}
