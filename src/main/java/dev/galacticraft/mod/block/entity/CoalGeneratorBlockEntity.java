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

import alexiil.mc.lib.attributes.Simulation;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorBlockEntity extends MachineBlockEntity {
    private static final Object2IntMap<Item> FUEL_MAP = Util.make(new Object2IntArrayMap<>(3), (map) -> {
        map.put(Items.COAL_BLOCK, 320 * 10);
        map.put(Items.COAL, 320);
        map.put(Items.CHARCOAL, 310);
    });

    public static final int CHARGE_SLOT = 0;
    public static final int FUEL_SLOT = 1;

    public Status status = Status.FULL;
    public int fuelLength = 0;
    public int fuelTime = 0;
    private double heat = 0.0d;

    /*
     * Energy stats:
     * T1 machine: uses 30gj/t
     * Coal Generator: generates 120gj/t (max heat)
     */

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.COAL_GENERATOR, pos, state);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 8);
        builder.addSlot(FUEL_SLOT, SlotType.COAL, stack -> FUEL_MAP.containsKey(stack.getItem()), 8, 74);
        return builder;
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (this.fuelLength == 0 && this.itemInv().getInvStack(FUEL_SLOT).isEmpty() && heat <= 0) return Status.NOT_ENOUGH_FUEL;
        if (this.capacitor().getEnergy() >= this.capacitor().getMaxCapacity()) return Status.FULL;
        if (this.heat < 1 && this.fuelLength > 0) return Status.WARMING;
        if (this.heat > 0 && this.fuelLength == 0) return Status.COOLING;
        return Status.ACTIVE;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptDrainPowerToStack(CHARGE_SLOT);
    }

    @Override
    public int getBaseEnergyGenerated() {
        return Galacticraft.CONFIG_MANAGER.get().coalGeneratorEnergyProductionRate();
    }

    @Override
    public int getEnergyGenerated() {
        if (this.getStatus().getType().isActive()) return (int) (getBaseEnergyGenerated() * this.heat);
        return 0;
    }

    @Override
    public void tickWork() {
        if (this.heat > 0 && this.fuelLength == 0) {
            this.heat = Math.max(0, this.heat - 0.04d);
        }
        if (this.getStatus().getType().isActive()) {
            if (this.fuelTime++ >= this.fuelLength) {
                this.fuelTime = 0;
                this.fuelLength = 0;
            }
            if (this.fuelLength == 0) {
                this.fuelTime = 0;
                this.fuelLength = FUEL_MAP.getOrDefault(itemInv().extractStack(FUEL_SLOT, null, ItemStack.EMPTY, 1, Simulation.ACTION).getItem(), 0);
                if (this.fuelLength == 0) return;
            }

            if (this.heat < 1) {
                this.heat = Math.min(1, this.heat + 0.004);
            }
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.COAL_GENERATOR_HANDLER, syncId, inv, this);
        return null;
    }

    public double getHeat() {
        return this.heat;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        /**
         * The generator is active and is generating energy.
         */
        ACTIVE(new TranslatableText("ui.galacticraft.machine.status.active"), Formatting.GREEN, StatusType.WORKING),

        /**
         * The generator is warming up.
         */
        WARMING(new TranslatableText("ui.galacticraft.machine.status.warming"), Formatting.GOLD, StatusType.PARTIALLY_WORKING),

        /**
         * The generator is cooling down.
         */
        COOLING(new TranslatableText("ui.galacticraft.machine.status.cooling"), Formatting.AQUA, StatusType.PARTIALLY_WORKING),

        /**
         * The generator is full.
         */
        FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * The generator is out of fuel.
         */
        NOT_ENOUGH_FUEL(new TranslatableText("ui.galacticraft.machine.status.not_enough_items"), Formatting.GOLD, StatusType.MISSING_ITEMS);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static Status get(int index) {
            if (index < 0) return ACTIVE;
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