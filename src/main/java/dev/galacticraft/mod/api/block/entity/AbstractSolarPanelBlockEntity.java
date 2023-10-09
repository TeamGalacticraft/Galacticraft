package dev.galacticraft.mod.api.block.entity;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.SolarPanelMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSolarPanelBlockEntity extends MachineBlockEntity implements SolarPanel {
    public static final int CHARGE_SLOT = 0;
    protected final boolean[] blockage = new boolean[9];
    protected int blocked = 0;
    public long currentEnergyGeneration = 0;

    public AbstractSolarPanelBlockEntity(MachineType<? extends AbstractSolarPanelBlockEntity, ? extends SolarPanelMenu<? extends AbstractSolarPanelBlockEntity>> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("charge");
        this.drainPowerToStack(CHARGE_SLOT);
        profiler.popPush("blockage");
        this.blocked = 0;
        for (int x = -1; x < 2; x++) { //todo: cache?
            for (int z = -1; z < 2; z++) {
                //noinspection AssignmentUsedAsCondition
                if (this.blockage[(z + 1) * 3 + (x + 1)] = !world.canSeeSky(pos.offset(x, 2, z))) {
                    this.blocked++;
                }
            }
        }
        profiler.pop();
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("push_energy");
        this.trySpreadEnergy(level, state);
        profiler.pop();
        if (this.blocked >= 9) return GCMachineStatuses.BLOCKED;
        if (this.energyStorage().isFull()) return MachineStatuses.CAPACITOR_FULL;
        MachineStatus status = null;
        double multiplier = blocked == 0 ? 1 : this.blocked / 9.0;
        if (this.blocked > 1) status = GCMachineStatuses.PARTIALLY_BLOCKED;
        if (level.isRaining() || level.isThundering()) {
            if (status == null) status = GCMachineStatuses.RAIN;
            multiplier *= 0.5;
        }
        if (!level.isDay()) status = GCMachineStatuses.NIGHT;
        long time = level.getDayTime() % 24000;
        if (time > 6000) time = 12000L - time;

        profiler.push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            this.currentEnergyGeneration = calculateEnergyProduction(time, multiplier);
            this.energyStorage().insert(this.currentEnergyGeneration, transaction);
            transaction.commit();
        }
        profiler.pop();
        return status == null ? GCMachineStatuses.COLLECTING : status;
    }

    protected abstract long calculateEnergyProduction(long time, double multiplier);

    @Override
    public boolean isFaceLocked(BlockFace face) {
        return face == BlockFace.TOP;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new SolarPanelMenu<>(
                    syncId,
                    ((ServerPlayer) player),
                    this
            );
        }
        return null;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, @NotNull FriendlyByteBuf buf) {
        super.writeScreenOpeningData(player, buf);

        buf.writeBoolean(this.followsSun());
        buf.writeBoolean(this.nightCollection());
        buf.writeByte(this.getSource().ordinal());
        buf.writeVarLong(this.getCurrentEnergyGeneration());
    }

    @Override
    public boolean @NotNull [] getBlockage() {
        return this.blockage;
    }

    @Override
    public SolarPanelSource getSource() {
        return this.level.dimensionType().hasCeiling() ? SolarPanelSource.NO_LIGHT_SOURCE : this.level.isDay() ? this.level.isRaining() || this.level.isThundering() ? SolarPanelSource.OVERCAST : SolarPanelSource.DAY : SolarPanelSource.NIGHT;
    }

    @Override
    public long getCurrentEnergyGeneration() {
        return this.currentEnergyGeneration;
    }
}