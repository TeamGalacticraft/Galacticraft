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
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import dev.galacticraft.api.registry.RegistryUtil;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.screen.OxygenCollectorScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenCollectorBlockEntity extends MachineBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_TANK = 0;

    public int collectionAmount = 0;
    private boolean oxygenWorld = false;

    public OxygenCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.OXYGEN_COLLECTOR, pos, state);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        CelestialBody<CelestialBodyConfig, ? extends Landable<CelestialBodyConfig>> body = RegistryUtil.getCelestialBodyByDimension(world).orElse(null);
        this.oxygenWorld = body == null || body.type().atmosphere(body.config()).breathable();
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addLOXTank(OXYGEN_TANK, SlotType.OXYGEN_OUT, 31, 8);
        return builder;
    }

    @Override
    public FluidAmount fluidInvCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    private int collectOxygen() {
        if (!this.oxygenWorld) {
            int minX = this.pos.getX() - 5;
            int minY = this.pos.getY() - 5;
            int minZ = this.pos.getZ() - 5;
            int maxX = this.pos.getX() + 5;
            int maxY = this.pos.getY() + 5;
            int maxZ = this.pos.getZ() + 5;

            float leafBlocks = 0;

            for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = world.getBlockState(pos);
                if (state.isAir()) {
                    continue;
                }
                if (state.getBlock() instanceof LeavesBlock && !state.get(LeavesBlock.PERSISTENT)) {
                    leafBlocks++;
                } else if (state.getBlock() instanceof CropBlock) {
                    leafBlocks += 0.75F;
                }
            }

            if (leafBlocks < 2) return 0;

            double oxyCount = 20 * (leafBlocks / 14.0F);
            return (int) Math.ceil(oxyCount) / 20; //every tick
        }
        return 183 / 20;
    }

    private boolean canCollectOxygen() {
        if (!oxygenWorld) {
            int minX = this.pos.getX() - 5;
            int minY = this.pos.getY() - 5;
            int minZ = this.pos.getZ() - 5;
            int maxX = this.pos.getX() + 5;
            int maxY = this.pos.getY() + 5;
            int maxZ = this.pos.getZ() + 5;

            float leafBlocks = 0;

            for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = world.getBlockState(pos);
                if (state.isAir()) {
                    continue;
                }
                if (state.getBlock() instanceof LeavesBlock && !state.get(LeavesBlock.PERSISTENT)) {
                    if (++leafBlocks >= 2) break;
                } else if (state.getBlock() instanceof CropBlock) {
                    if ((leafBlocks += 0.75) >= 2) break;
                }
            }
            return leafBlocks >= 2;
        }
        return true;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
        this.trySpreadFluids(0);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.isTankFull(OXYGEN_TANK)) return Status.FULL;
        if (!canCollectOxygen()) return Status.NOT_ENOUGH_LEAVES;
        return Status.COLLECTING;
    }

    @Override
    public void tickWork() {
        this.collectionAmount = 0;
        if (this.getStatus().getType().isActive()) {
            this.collectionAmount = collectOxygen();
            this.fluidInv().insertFluid(OXYGEN_TANK, FluidKeys.get(GalacticraftFluid.LIQUID_OXYGEN).withAmount(FluidAmount.of(collectionAmount, 100)), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new OxygenCollectorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        COLLECTING(new TranslatableText("ui.galacticraft.machine.status.collecting"), Formatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_LEAVES(new TranslatableText("ui.galacticraft.machine.status.not_enough_leaves"), Formatting.RED, StatusType.MISSING_RESOURCE),
        FULL(new TranslatableText("ui.galacticraft.machine.status.full"), Formatting.GOLD, StatusType.OUTPUT_FULL);

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