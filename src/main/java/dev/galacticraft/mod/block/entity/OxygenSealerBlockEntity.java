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
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.api.registry.RegistryUtil;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.WorldOxygenAccessor;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import dev.galacticraft.mod.util.OxygenTankUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenSealerBlockEntity extends MachineBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int BATTERY_SLOT = 0;
    public static final int LOX_INPUT = 1;
    public static final int OXYGEN_TANK = 0;
    private final Set<BlockPos> set = new HashSet<>();
    public static final byte SEAL_CHECK_TIME = 5 * 20;
    private byte sealCheckTime;
    private boolean oxygenWorld = false;

    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_SEALER, pos, state);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(BATTERY_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);
        builder.addSlot(LOX_INPUT, SlotType.OXYGEN_TANK, OxygenTankUtil.OXYGEN_TANK_EXTRACTABLE, 8, 62);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addLOXTank(OXYGEN_TANK, SlotType.OXYGEN_IN, 30, 8);
        return builder;
    }

    @Override
    public FluidAmount fluidInvCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.sealCheckTime = SEAL_CHECK_TIME;
        CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = RegistryUtil.getCelestialBodyByDimension(world).orElse(null);
        this.oxygenWorld = body == null || body.type().atmosphere(body.config()).breathable();
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(BATTERY_SLOT);
        if (!world.isClient && this.getStatus().getType().isActive()) this.fluidInv().extractFluid(OXYGEN_TANK, Constant.Filter.LOX_ONLY, null, FluidAmount.of1620(set.size()), Simulation.ACTION);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.fluidInv().getInvFluid(OXYGEN_TANK).isEmpty()) return Status.NOT_ENOUGH_OXYGEN;
        return Status.SEALED;
    }

    @Override
    public void tickWork() {
        if (sealCheckTime > 0) {
            sealCheckTime--;
        }
        if (this.getStatus().getType().isActive()) {
            if (sealCheckTime == 0) {
                sealCheckTime = SEAL_CHECK_TIME;
                BlockPos pos = this.getPos();
                if (this.oxygenWorld || (set.isEmpty() && ((WorldOxygenAccessor) world).isBreathable(pos.up()))) {
                    this.setStatus(Status.ALREADY_SEALED);
                    return;
                }
                set.clear();
                Queue<Pair<BlockPos, Direction>> queue = new LinkedList<>();
                List<BlockPos> checked = new LinkedList<>();
                BlockPos pos1 = pos.offset(Direction.UP);
                BlockState state;
                Pair<BlockPos, Direction> pair;
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                queue.add(new Pair<>(pos1, Direction.UP));
                while (!queue.isEmpty()){
                    pair = queue.poll();
                    pos1 = pair.getLeft();
                    state = world.getBlockState(pos1);
                    checked.add(pos1);
                    if (state.isAir() || !Block.isFaceFullSquare(state.getCollisionShape(world, pos1), pair.getRight().getOpposite())) {
                        set.add(pos1);
                        for (Direction direction : Constant.Misc.DIRECTIONS) {
                            mutable.set(pos1).offset(direction);
                            if (!checked.contains(mutable)) {
                                queue.add(new Pair<>(mutable.toImmutable(), direction));
                            }
                        }
                    }
                }
                for (BlockPos pos2 : set) {
                    ((WorldOxygenAccessor) world).setBreathable(pos2, true);
                }
                setStatus(Status.SEALED);
            }
        }
    }

    @Override
    public void idleEnergyDecrement(boolean off) {
        super.idleEnergyDecrement(off);
        if (!set.isEmpty()) {
            for (BlockPos pos1 : set) {
                ((WorldOxygenAccessor) world).setBreathable(pos1, false);
            }
            set.clear();
        }
    }

    @Override
    protected int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        for (BlockPos pos : set) {
            ((WorldOxygenAccessor) world).setBreathable(pos, false);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.OXYGEN_SEALER_HANDLER, syncId, inv, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machine.status.not_enough_oxygen"), Formatting.RED, StatusType.MISSING_FLUIDS),
        AREA_TOO_LARGE(new TranslatableText("ui.galacticraft.machine.status.area_too_large"), Formatting.GOLD, StatusType.OTHER),
        ALREADY_SEALED(new TranslatableText("ui.galacticraft.machine.status.already_sealed"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        SEALED(new TranslatableText("ui.galacticraft.machine.status.sealed"), Formatting.GREEN, StatusType.WORKING);

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