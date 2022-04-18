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

package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.api.attribute.oxygen.EmptyOxygenTank;
import dev.galacticraft.api.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import dev.galacticraft.mod.util.OxygenTankUtil;
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
public class OxygenDecompressorBlockEntity extends MachineBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int CHARGE_SLOT = 0;
    public static final int TANK_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public OxygenDecompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_DECOMPRESSOR, pos, state);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);
        builder.addSlot(TANK_SLOT, SlotType.OXYGEN_TANK, OxygenTankUtil::isOxygenTank, 80, 27);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addLOXTank(OXYGEN_TANK, SlotType.OXYGEN_OUT, 30, 8);
        return builder;
    }

    @Override
    public FluidAmount fluidInvCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
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
        this.trySpreadFluids(OXYGEN_TANK);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        OxygenTank tank = OxygenTankUtil.getOxygenTank(this.itemInv().getSlot(TANK_SLOT));
        if (tank == EmptyOxygenTank.NULL) return Status.NOT_ENOUGH_ITEMS;
        if (tank.getAmount() <= 0) return Status.EMPTY_CANISTER;
        if (this.isTankFull(0)) return Status.FULL;
        return Status.DECOMPRESSING;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            OxygenTank tank = OxygenTankUtil.getOxygenTank(this.itemInv().getSlot(TANK_SLOT));
            OxygenTankUtil.insertLiquidOxygen(tank, this.fluidInv().insertFluid(0, OxygenTankUtil.extractLiquidOxygen(tank, FluidAmount.of(1, 300)), Simulation.ACTION));
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenDecompressorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.OXYGEN_DECOMPRESSOR_HANDLER, syncId, inv, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.RED, StatusType.MISSING_FLUIDS),
        EMPTY_CANISTER(new TranslatableText("ui.galacticraft.machine.status.empty_canister"), Formatting.RED, StatusType.MISSING_FLUIDS),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machine.status.empty_canister"), Formatting.RED, StatusType.MISSING_ITEMS),
        FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        DECOMPRESSING(new TranslatableText("ui.galacticraft.machine.status.decompressing"), Formatting.GREEN, StatusType.WORKING);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static Status get(int index) {
            if (index < 0) return Status.values()[0];
            return Status.values()[index % Status.values().length];
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