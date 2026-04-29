/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.api.fluid.FluidData;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCFluids;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.galacticraft.api.component.GCDataComponents.FLUID_DATA;

public class FluidCanisterItem extends Item {
    public static StorageView<FluidVariant> getStorage(ItemStack stack) {
        StorageView<FluidVariant> storage = (StorageView<FluidVariant>) ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        assert storage != null;
        return storage;
    }

    public FluidCanisterItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        FluidData data = stack.get(FLUID_DATA);

        if (data == null || data.variant().isBlank()) {
            return Component.translatable("item.galacticraft.fluid_canister");
        }

        Component fluidName = FluidVariantAttributes.getName(data.variant()).plainCopy();

        return Component.translatable("item.galacticraft.fluid_canister.filled", fluidName);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        FluidData data = stack.get(FLUID_DATA);

        if (data != null && !data.variant().isBlank()) {
            Component fluidName;
            if (data.variant().getFluid().isSame(GCFluids.LIQUID_OXYGEN)) {
                fluidName = Component.literal("LOX");
            } else {
                fluidName = FluidVariantAttributes.getName(data.variant()).plainCopy();
            }
            long amountMb = data.amount() / 81; // Convert Fabric droplets to mB

            tooltip.add(Component.translatable("tooltip.galacticraft.fluid_canister.fluid_info", fluidName, amountMb)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.galacticraft.fluid_canister.empty").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        StorageView<FluidVariant> storage = FluidCanisterItem.getStorage(stack);
        return Math.round(13.0F - (float) (storage.getCapacity() - storage.getAmount()) * 13.0F / (float) storage.getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StorageView<FluidVariant> storage = FluidCanisterItem.getStorage(stack);
        float scale = 1.0F - ((float) storage.getAmount() / (float) storage.getCapacity());
        return Constant.Text.getStorageLevelColor(scale);
    }
}
