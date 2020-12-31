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

package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.RefineryBlockEntity;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryScreenHandler extends MachineScreenHandler<RefineryBlockEntity> {
    private final Property fluidOil = Property.create();
    private final Property fluidFuel = Property.create();

    private final Inventory inventory;

    public RefineryScreenHandler(int syncId, PlayerEntity playerEntity, RefineryBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.REFINERY_HANDLER);
        addProperty(status);
        addProperty(fluidOil);
        addProperty(fluidFuel);
        this.inventory = blockEntity.getInventory().asInventory();
        // Energy slot
        this.addSlot(new ChargeSlot(this.inventory, 0, 8, 7));
        this.addSlot(new Slot(this.inventory, 1, 123, 7) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return blockEntity.getFilterForSlot(1).test(stack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 2, 153, 7) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return blockEntity.getFilterForSlot(2).test(stack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });


        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, 144));
        }

    }

    public RefineryScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (RefineryBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void sendContentUpdates() {
        fluidOil.set((int) (blockEntity.getFluidTank().getContents(0).getAmount().doubleValue() * 1000.0D));
        fluidFuel.set((int) (blockEntity.getFluidTank().getContents(1).getAmount().doubleValue() * 1000.0D));
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        if (fluidOil.get() != 0) {
            blockEntity.getFluidTank().setFluid(0, new FluidVolume(GalacticraftFluids.CRUDE_OIL, Fraction.ofThousandths(fluidOil.get())));
        } else {
            blockEntity.getFluidTank().setFluid(0, FluidVolume.EMPTY);
        }
        if (fluidFuel.get() != 0) {
            blockEntity.getFluidTank().setFluid(1, new FluidVolume(GalacticraftFluids.FUEL, Fraction.ofThousandths(fluidFuel.get())));
        } else {
            blockEntity.getFluidTank().setFluid(1, FluidVolume.EMPTY);
        }
    }
}
