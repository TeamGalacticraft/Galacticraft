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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.api.attribute.GasStorage;
import dev.galacticraft.api.fluid.FluidStack;
import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.api.gas.GasStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import dev.galacticraft.mod.lookup.storage.MachineGasStorage;
import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.GasSlotSettings;
import dev.galacticraft.mod.screen.slot.SlotSettings;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.GenericStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenCompressorBlockEntity extends MachineBlockEntity {
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_TANK_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public OxygenCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_COMPRESSOR, pos, state);
    }

    @Override
    protected MachineItemStorage.Builder createInventory(MachineItemStorage.Builder builder) {
        builder.addSlot(SlotSettings.Builder.create(8, 62, SlotType.CHARGE).filter(Constant.Filter.Item.CAN_EXTRACT_ENERGY).build());
        builder.addSlot(SlotSettings.Builder.create(80, 27, SlotType.OXYGEN_TANK).filter(Constant.Filter.Item.CAN_INSERT_OXYGEN).build());
        return builder;
    }

    @Override
    protected MachineGasStorage.Builder createGasStorage(MachineGasStorage.Builder builder) {
        builder.addSlot(GasSlotSettings.Builder.create(30, 8, SlotType.OXYGEN_IN).capacity(MAX_OXYGEN).filter(Constant.Filter.Gas.OXYGEN).build());
        return builder;
    }

    @Override
    public long energyExtractionRate() {
        return 0;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    protected void tickDisabled() {

    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.fluidInv().getFluid(OXYGEN_TANK).isEmpty()) return Status.NOT_ENOUGH_OXYGEN;
        Storage<Gas> gasStorage = ContainerItemContext.ofSingleSlot(this.itemStorage().getSlot(OXYGEN_TANK_SLOT)).find(GasStorage.ITEM);
        if (gasStorage == null) return Status.NOT_ENOUGH_ITEMS;
        if (!gasStorage.supportsInsertion() || gasStorage.simulateInsert(Gas.OXYGEN, Long.MAX_VALUE, null) == 0) return Status.CONTAINER_FULL;
        return Status.COMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            Storage<Gas> gasStorage = ContainerItemContext.ofSingleSlot(this.itemStorage().getSlot(OXYGEN_TANK_SLOT)).find(GasStorage.ITEM);
            GasStack fluidStack = this.gasStorage().simulateExtraction(OXYGEN_TANK, Long.MAX_VALUE);
            try (Transaction transaction = Transaction.openOuter()) {
                GenericStorageUtil.move(fluidStack.gas(), this.gasStorage(), gasStorage, fluidStack.amount(), transaction);
                transaction.commit();
            }
        }
    }

    @Override
    public long energyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.OXYGEN_COMPRESSOR_HANDLER, syncId, inv, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machine.status.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft.machine.status.missing_tank"), Formatting.RED, StatusType.MISSING_ITEMS),
        CONTAINER_FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        COMPRESSING(new TranslatableText("ui.galacticraft.machine.status.compressing"), Formatting.GREEN, StatusType.WORKING);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}