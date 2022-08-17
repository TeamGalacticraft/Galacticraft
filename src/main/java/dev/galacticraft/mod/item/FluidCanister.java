/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.mixin.BucketItemAccessor;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FluidCanister extends Item /*implements AttributeProviderItem*/ {
    public FluidCanister(Properties settings) {
        super(settings.durability(1620));
    }

    @Override
    public int getEnchantmentValue() {
        return -1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
//        FixedFluidInvView inv = FluidUtil.getFixedFluidInvView(new Ref<>(stack));
//        if (inv.getInvFluid(0).isEmpty()) {
//            tooltip.add(Component.translatable("tooltip.galacticraft.no_fluid"));
//        } else {
//            FluidAmount amount = inv.getInvFluid(0).getAmount_F().mul(FluidAmount.ONE);
//            if (!Screen.hasShiftDown()) {
//                tooltip.add(Component.translatable("tooltip.galacticraft.buckets_fraction", amount.numerator, amount.denominator));
//            } else {
//                tooltip.add(Component.translatable("tooltip.galacticraft.buckets", amount.asInexactDouble()));
//            }
//            tooltip.add(Component.translatable("tooltip.galacticraft.fluid", Registry.FLUID.getId(inv.getInvFluid(0).getRawFluid())));
//        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
//        FixedFluidInvView inv = FluidUtil.getFixedFluidInvView(new Ref<>(stack));
//
//        if (inv.getInvFluid(0).isEmpty()) {
//            BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
//            if (hitResult.getType() == HitResult.Type.MISS) {
//                return TypedActionResult.pass(stack);
//            } else if (hitResult.getType() != HitResult.Type.BLOCK) {
//                return TypedActionResult.pass(stack);
//            } else {
//                BlockPos blockPos = hitResult.getBlockPos();
//                Direction direction = hitResult.getSide();
//                BlockPos blockPos2 = blockPos.offset(direction);
//                if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, stack)) {
//                    BlockState blockState;
//                    blockState = world.getBlockState(blockPos);
//                    if (blockState.getBlock() instanceof FluidDrainable) {
//                        ItemStack s = ((FluidDrainable) blockState.getBlock()).tryDrainFluid(world, blockPos, blockState);
//                        Fluid fluid = s.getItem() instanceof BucketItemAccessor ? ((BucketItemAccessor) s.getItem()).getFluid() : Fluids.EMPTY;
//                        if (fluid != Fluids.EMPTY) {
//                            user.incrementStat(Stats.USED.getOrCreateStat(this));
//                            if (fluid.getBucketFillSound().isPresent()) user.playSound(fluid.getBucketFillSound().get(), 1.0F, 1.0F);
//                            final ItemStack[] stack1 = {stack.copy()};
//                            FluidUtil.getFixedFluidInv(new ArrayReference<>(stack1, 0, Objects::nonNull)).setInvFluid(0, FluidKeys.get(fluid).withAmount(FluidAmount.ONE), Simulation.ACTION);
//                            return TypedActionResult.success(stack1[0], world.isClient());
//                        }
//                    }
//
//                    return TypedActionResult.fail(stack);
//                }
//            }
//        }
        return InteractionResultHolder.fail(stack);
    }

//    @Override
//    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> itemAttributeList) {
//        SimpleFixedFluidInv inv = new SimpleFixedFluidInv(1, FluidAmount.ONE);
//        inv.fromTag(reference.get().getNbt());
//        inv.addListener((view, slot, prev, cur) -> {
//            ItemStack stack = reference.get().copy();
//            stack.setDamage(1620 - inv.getInvFluid(0).getAmount_F().as1620());
//            inv.toTag(stack.getOrCreateNbt());
//            reference.set(stack);
//        }, () -> {});
//        itemAttributeList.offer(inv);
//    }
}
