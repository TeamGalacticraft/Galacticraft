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

package dev.galacticraft.mod.storage;

import dev.galacticraft.api.fluid.FluidData;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage;
import net.minecraft.world.item.ItemStack;

import static dev.galacticraft.api.component.GCDataComponents.FLUID_DATA;

public class CanisterFluidStorage extends SingleVariantItemStorage<FluidVariant> {
    private final ContainerItemContext itemContext;

    public CanisterFluidStorage(ContainerItemContext context) {
        super(context);
        this.itemContext = context;
    }

    @Override
    protected FluidVariant getBlankResource() {
        return FluidVariant.blank();
    }

    @Override
    protected FluidVariant getResource(ItemVariant currentVariant) {
        FluidData data = itemContext.getItemVariant().toStack().get(FLUID_DATA);
        return data != null ? data.variant() : getBlankResource();
    }

    @Override
    protected long getAmount(ItemVariant currentVariant) {
        FluidData data = itemContext.getItemVariant().toStack().get(FLUID_DATA);
        return data != null ? data.amount() : 0;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return FluidConstants.BUCKET;
    }

    @Override
    protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, FluidVariant newResource, long newAmount) {
        ItemStack stack = currentVariant.toStack();

        if (newAmount <= 0 || newResource.isBlank()) {
            stack.remove(FLUID_DATA);
        }  else {
            stack.set(FLUID_DATA, new FluidData(newResource, newAmount));
        }

        return ItemVariant.of(stack);
    }

    @Override
    protected boolean canInsert(FluidVariant resource) {
        if (resource.getFluid().is(GCFluidTags.FLUID_CANISTER_EXCLUDED)) {
            return false;
        }

        return super.canInsert(resource);
    }
}
