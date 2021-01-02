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

package com.hrznstudio.galacticraft.api.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.WorldRendererAccessor;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.internal.data.MinecraftServerTeamsGetter;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SidedInventory, Tickable {
    private final InventoryWrapper wrapper = InventoryWrapper.of(getInventory());

    private final SecurityInfo security = new SecurityInfo();
    private final SideConfigInfo sideConfigInfo = new SideConfigInfo(this, validSideOptions(), 1, getInventorySize(), getFluidTankSize());

    private MachineStatus status = MachineStatus.EMPTY;
    private RedstoneState redstone = RedstoneState.IGNORE;


    public ConfigurableMachineBlockEntity(BlockEntityType<? extends ConfigurableMachineBlockEntity> blockEntityType) {
        super(blockEntityType);
        this.getCapacitor().getListeners().add(this::markDirty);
        this.getInventory().getListeners().add(this::markDirty);
    }

    /**
     * Returns whether this machine may have energy extracted from it.
     * @return whether this machine may have energy extracted from it.
     */
    public abstract boolean canExtractEnergy();

    /**
     * Returns whether this machine may have energy inserted into it.
     * @return whether this machine may have energy inserted into it.
     */
    public abstract boolean canInsertEnergy();

    /**
     * The amount of energy that the machine uses in a tick.
     * @return The amount of energy that the machine uses in a tick.
     */
    protected abstract int getEnergyUsagePerTick();

    /**
     * Returns whether a hopper may extract items from the given slot.
     * @param slot The slot to test
     * @return
     */
    public abstract boolean canHopperExtractItems(int slot);

    public abstract boolean canHopperInsertItems(int slot);

    public abstract boolean canExtractFluid(int tank);

    public abstract boolean canInsertFluid(int tank);

    public abstract boolean isAcceptableFluid(int tank, FluidVolume volume);

    public abstract int getInventorySize();

    public abstract int getFluidTankSize();

    public Fraction getFluidTankMaxCapacity() {
        return Fraction.ZERO;
    }

    public abstract List<SideOption> validSideOptions();

    public void setRedstone(@NotNull RedstoneState redstone) {
        this.redstone = redstone;
    }

    public @NotNull MachineStatus getStatus() {
        return status;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public void setStatus(int index) {
        setStatus(getStatus(index));
    }

    protected abstract MachineStatus getStatus(int index);

    /**
     * The max energy that this machine can hold. Override for machines that should hold more.
     *
     * @return Energy capacity of this machine.
     */
    public int getMaxEnergy() {
        return Galacticraft.configManager.get().machineEnergyStorageSize();
    }

    /**
     * @return The {@link Predicate} for the given slot of {@link #getInventory()}.
     */
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return (stack -> true);
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 50;
    }

    public final @NotNull CapacitorComponent getCapacitor() {
        return UniversalComponents.CAPACITOR_COMPONENT.get(this);
    }

    public final @NotNull InventoryComponent getInventory() {
        return UniversalComponents.INVENTORY_COMPONENT.get(this);
    }

    public final @NotNull TankComponent getFluidTank() {
        return UniversalComponents.TANK_COMPONENT.get(this);
    }

    public final @Nullable CapacitorComponent getCapacitor(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction == null || this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction)).getOption().isEnergy())
            return getCapacitor();
        return null;
    }

    public final @Nullable InventoryComponent getInventory(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction == null || this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction)).getOption().isItem())
            return getInventory();
        return null;
    }

    public final @Nullable TankComponent getFluidTank(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction == null || this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction)).getOption().isFluid())
            return getFluidTank();
        return null;
    }

    public final @NotNull SecurityInfo getSecurity() {
        return security;
    }

    public final @NotNull RedstoneState getRedstone() {
        return redstone;
    }

    public final @NotNull SideConfigInfo getSideConfigInfo() {
        return sideConfigInfo;
    }

    public final boolean canUse(PlayerEntity player) {
        return this.getSecurity().hasAccess(player);
    }

    protected void decrement(int slot, int amount) {
        ItemStack stack = getInventory().getStack(slot);
        stack.decrement(amount);
        getInventory().setStack(slot, stack);
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean disabled() {
        switch (this.redstone) {
            case OFF:
                return this.getWorld().isReceivingRedstonePower(pos);
            case ON:
                return !this.getWorld().isReceivingRedstonePower(pos);
            default:
                return false;
        }
    }

//    @Override
//    public void tick() {
//        if (disabled()) {
//
//        }
//
//        machineTick();
//    }

//    public abstract void machineTick();

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        this.getCapacitor().writeToNbt(tag);
        this.getInventory().writeToNbt(tag);
        this.getFluidTank().writeToNbt(tag);
        this.security.toTag(tag);
        this.sideConfigInfo.toTag(tag);
        this.redstone.toTag(tag);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.getCapacitor().readFromNbt(tag);
        this.getInventory().readFromNbt(tag);
        this.getFluidTank().readFromNbt(tag);
        this.security.fromTag(tag);
        this.sideConfigInfo.fromTag(tag);
        this.redstone = RedstoneState.fromTag(tag);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.sideConfigInfo.fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        this.sideConfigInfo.toTag(tag);
        return tag;
    }

    public boolean canInsert(int slot, ItemStack stack) {
        return getInventory().insertStack(slot, stack, ActionType.TEST).isEmpty();
    }

    public void insert(int slot, ItemStack stack) {
        if (canInsert(slot, stack)) {
            getInventory().insertStack(slot, stack, ActionType.PERFORM);
        } else {
            throw new RuntimeException();
        }
    }

    public void trySpreadEnergy() {
        if (this.getCapacitor().canExtractEnergy()) {
            for (BlockFace face : BlockFace.values()) {
                ConfiguredSideOption option = this.getSideConfigInfo().get(face);
                if (option.getOption() == SideOption.POWER_OUTPUT) {
                    Direction dir = face.toDirection(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    CapacitorComponent component = ComponentHelper.CAPACITOR.getComponent(world, pos.offset(dir), dir.getOpposite());
                    if (component != null) {
                        if (component.canInsertEnergy()) {
                            int i = this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, component.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, component.getMaxEnergy() - component.getCurrentEnergy(), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
                            if (i != 0) {
                                Galacticraft.logger.debug( i + "gJ wasted?!");
                            }
                        }
                    }
                }
            }
        }
    }

    public void trySpreadFluids(int tank) {
        if (this.getFluidTank().getTanks() > tank && tank >= 0 && this.canExtractFluid(tank) && !this.getFluidTank().getContents(tank).isEmpty()) {
            for (BlockFace face : BlockFace.values()) {
                ConfiguredSideOption option = this.getSideConfigInfo().get(face);
                if (option.getOption() == SideOption.FLUID_OUTPUT) {
                    Direction dir = face.toDirection(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    TankComponent component = TankComponentHelper.INSTANCE.getComponent(world, pos.offset(dir), dir.getOpposite());
                    if (component != null) {
                        for (int i = 0; i < component.getTanks(); i++) {
                            if (component.canInsert(i)) {
                                FluidVolume a = this.getFluidTank().insertFluid(tank, component.insertFluid(this.getFluidTank().takeFluid(tank, component.getMaxCapacity(i).subtract(component.getContents(i).getAmount()), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
                                if (!a.isEmpty()) {
                                    Galacticraft.logger.debug(a.getAmount().toString() + " " + Registry.FLUID.getId(a.getFluid()) + " wasted?!");
                                }
                                if (this.getFluidTank().getContents(tank).isEmpty()) return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void idleEnergyDecrement(boolean off) {
        if (getEnergyUsagePerTick() > 0 && getEnergyUsagePerTick() / 20 > 0) {
            if (EnergyUtils.Values.getTick() % ((75 * (getEnergyUsagePerTick() / 20)) * (off ? 2 : 1)) == 0) {
                getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            }
        }
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (getCapacitor().getCurrentEnergy() >= getCapacitor().getMaxEnergy()) {
            return;
        }
        ItemStack stack = getInventory().getStack(slot);
        int neededEnergy = Math.min(getBatteryTransferRate(), getCapacitor().getMaxEnergy() - getCapacitor().getCurrentEnergy());
        if (EnergyUtils.isEnergyItem(stack)) {
            this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, EnergyUtils.extractEnergy(stack, neededEnergy, ActionType.PERFORM), ActionType.PERFORM);
            getInventory().setStack(slot, stack);
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #getInventory}.
     *
     * @param slot The slot id of the item
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(getBatteryTransferRate(), getCapacitor().getCurrentEnergy());
        if (available <= 0) {
            return;
        }
        ItemStack stack = getInventory().getStack(slot);
        if (EnergyUtils.isEnergyItem(stack)) {
            if (EnergyUtils.getEnergy(stack) < EnergyUtils.getMaxEnergy(stack)) {
                int i = EnergyUtils.insertEnergy(stack, available, ActionType.PERFORM);
                this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, available - i, ActionType.PERFORM);
                getInventory().setStack(slot, stack);
            }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        ConfiguredSideOption configuredSideOption = this.getSideConfigInfo().get(BlockFace.toFace(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING), side));
        if (configuredSideOption.isWildcard()) {
            return IntStream.range(0, getInventorySize()).toArray();
        } else {
            return new int[]{configuredSideOption.getValue()};
        }
    }

    @Override
    public final boolean canExtract(int slot, ItemStack stack, Direction dir) {
        int[] slots = getAvailableSlots(dir);
        boolean accessible = false;
        for (int i : slots) {
            if (slot == i) {
                accessible = true;
                break;
            }
        }

        return accessible && getInventory().canExtract(slot) && getInventory().getStack(slot).isItemEqual(stack);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        int[] slots = getAvailableSlots(dir);
        boolean accessible = false;
        for (int i : slots) {
            if (slot == i) {
                accessible = true;
                break;
            }
        }

        return accessible && getInventory().insertStack(stack, ActionType.TEST).isEmpty() && canInsert(slot, stack);
    }

    public List<BlockFace> getNonConfigurableSides() {
        return Collections.emptyList();
    }

    @Override
    public int getMaxCountPerStack() {
        return wrapper.getMaxCountPerStack();
    }

    @Override
    public void onOpen(PlayerEntity player) {
    }

    @Override
    public void onClose(PlayerEntity player) {
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return wrapper.isValid(slot, stack);
    }

    @Override
    public int count(Item item) {
        return wrapper.count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return wrapper.containsAny(items);
    }

    @Override
    public int size() {
        return wrapper.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapper.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return wrapper.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return wrapper.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return wrapper.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        wrapper.setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return security.hasAccess(player);
    }

    @Override
    public void clear() {
        wrapper.clear();
    }

    @Override
    public void sync() {
        BlockEntityClientSerializable.super.sync();
        this.world.updateNeighbors(pos, this.getCachedState().getBlock());
    }

    public enum RedstoneState implements StringIdentifiable {
        /**
         * Ignores redstone entirely.
         */
        IGNORE,

        /**
         * When powered with redstone, the machine turns off.
         */
        OFF,

        /**
         * When powered with redstone, the machine turns on.
         */
        ON;

        public static RedstoneState fromString(String string) {
            switch (string.toUpperCase()) {
                case "OFF":
                    return OFF;
                case "ON":
                    return ON;
                default:
                    return IGNORE;
            }
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }

        public void toTag(CompoundTag tag) {
            tag.putString("Redstone", this.asString());
        }

        public static RedstoneState fromTag(CompoundTag tag) {
            return fromString(tag.getString("Redstone"));
        }
    }

    public interface MachineStatus {
        MachineStatus EMPTY = new MachineStatus() {
            @Override
            public @NotNull Text getName() {
                return Constants.Misc.EMPTY;
            }

            @Override
            public @NotNull StatusType getType() {
                return StatusType.OTHER;
            }

            @Override
            public int getIndex() {
                return 0;
            }
        };

        @NotNull Text getName();

        @NotNull StatusType getType();

        int getIndex();

        enum StatusType {
            /**
             * The machine is active
             */
            WORKING(true),
            /**
             * THe machine is active, but at reduced efficiency.
             */
            PARTIALLY_WORKING(true),
            /**
             * The machine is missing a resource it needs to function.
             * Should not be an item, fluid or energy.
             *
             * @see #MISSING_ENERGY
             * @see #MISSING_FLUIDS
             * @see #MISSING_ITEMS
             */
            MISSING_RESOURCE(false),
            /**
             * The machine is missing a fluid it needs to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_FLUIDS(false),
            /**
             * The machine does not have the amount of energy needed to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_ENERGY(false),
            /**
             * The machine does not have the items needed to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_ITEMS(false),
            /**
             * The machine's output is blocked/full.
             */
            OUTPUT_FULL(false),
            /**
             *
             */
            OTHER(false),
            /**
             * The machine is off (manual redstone power-off).
             */
            OFF(false);

            final boolean active;

            StatusType(boolean active) {
                this.active = active;
            }

            public boolean isActive() {
                return this.active;
            }
        }
    }

    public static class SecurityInfo {
        private UUID owner;
        private String username;
        private Identifier team;
        private Publicity publicity;

        protected SecurityInfo() {
            this.owner = null;
            this.publicity = Publicity.PUBLIC;
            this.team = null;
            this.username = "";
        }

        public boolean isOwner(PlayerEntity player) {
            return isOwner(player.getUuid());
        }

        public boolean isOwner(UUID uuid) {
            if (owner == null) owner = uuid;
            return this.owner.equals(uuid);
        }

        public boolean hasAccess(PlayerEntity player) {
            switch (publicity) {
                case PUBLIC:
                    return true;
                case SPACE_RACE:
                    return (((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()) != null)
                            && ((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()).players.containsKey(owner);
                case PRIVATE:
                    return isOwner(player);
            }
            return false;
        }

        public Publicity getPublicity() {
            return publicity;
        }

        public void setPublicity(Publicity publicity) {
            this.publicity = publicity;
        }

        public boolean hasOwner() {
            return this.owner != null;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public UUID getOwner() {
            return this.owner;
        }

        public void setOwner(PlayerEntity owner) {
            if (!this.hasOwner()) {
                this.owner = owner.getUuid();
            }
            this.username = owner.getEntityName();
        }

        public Identifier getTeam() {
            return team;
        }

        public boolean hasTeam() {
            return team != null;
        }

        public CompoundTag toTag(CompoundTag tag) {
            CompoundTag compoundTag = new CompoundTag();
            if (this.hasOwner()) {
                compoundTag.putUuid("owner", this.owner);
            }
            compoundTag.putString("username", this.username);
            compoundTag.putString("publicity", this.publicity.asString());
            if (this.hasTeam()) {
                compoundTag.putString("team", team.toString());
            }
            tag.put("security", compoundTag);
            return tag;
        }

        public void fromTag(CompoundTag tag) {
            CompoundTag compoundTag = tag.getCompound("security");

            if (compoundTag.contains("owner")) {
                if (!this.hasOwner()) {
                    this.owner = compoundTag.getUuid("owner");
                }
            }

            if (compoundTag.contains("team")) {
                if (!this.hasTeam()) {
                    this.team = new Identifier(compoundTag.getString("team"));
                }
            }

            this.username = compoundTag.getString("username");
            this.publicity = Publicity.valueOf(compoundTag.getString("publicity"));
        }


        public enum Publicity implements StringIdentifiable {
            PUBLIC,
            SPACE_RACE,
            PRIVATE;

            @Override
            public String asString() {
                return this.toString();
            }
        }
    }

    public static class SideConfigInfo {
        private final ConfiguredSideOption front;
        private final ConfiguredSideOption back;
        private final ConfiguredSideOption left;
        private final ConfiguredSideOption right;
        private final ConfiguredSideOption top;
        private final ConfiguredSideOption bottom;
        private final List<SideOption> values;
        private final int capacitors;
        private final int invSize;
        private final int tanks;
        private final ConfigurableMachineBlockEntity blockEntity;

        public SideConfigInfo(ConfigurableMachineBlockEntity blockEntity, List<SideOption> values, int capacitors, int invSize, int tanks) {
            if (!values.contains(SideOption.DEFAULT)) throw new RuntimeException();
            this.values = new ArrayList<>(values);
            this.values.sort(Enum::compareTo);
            this.capacitors = capacitors;
            this.invSize = invSize;
            this.tanks = tanks;
            this.blockEntity = blockEntity;

            this.front = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.back = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.left = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.right = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.top = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.bottom = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
        }

        public SideOption getFrontOption() {
            return front.getOption();
        }

        public SideOption getBackOption() {
            return back.getOption();
        }

        public SideOption getLeftOption() {
            return left.getOption();
        }

        public SideOption getRightOption() {
            return right.getOption();
        }

        public SideOption getUpOption() {
            return top.getOption();
        }

        public SideOption getDownOption() {
            return bottom.getOption();
        }

        public void setFrontOption(SideOption option) {
            front.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setBackOption(SideOption option) {
            back.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setLeftOption(SideOption option) {
            left.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setRightOption(SideOption option) {
            right.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setTopOption(SideOption option) {
            top.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setBottomOption(SideOption option) {
            bottom.setOption(option, getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public int getFrontValue() {
            return front.getValue();
        }

        public int getBackValue() {
            return back.getValue();
        }

        public int getLeftValue() {
            return left.getValue();
        }

        public int getRightValue() {
            return right.getValue();
        }

        public int getTopValue() {
            return top.getValue();
        }

        public int getBottomValue() {
            return bottom.getValue();
        }

        public int incrementFront() {
            return front.increment();
        }

        public int incrementBack() {
            return back.increment();
        }

        public int incrementLeft() {
            return left.increment();
        }

        public int incrementRight() {
            return right.increment();
        }

        public int incrementUp() {
            return top.increment();
        }

        public int incrementDown() {
            return bottom.increment();
        }

        public int decrementFront() {
            return front.decrement();
        }

        public int decrementBack() {
            return back.decrement();
        }

        public int decrementLeft() {
            return left.decrement();
        }

        public int decrementRight() {
            return right.decrement();
        }

        public int decrementUp() {
            return top.decrement();
        }

        public int decrementDown() {
            return bottom.decrement();
        }

        private int getMax(SideOption option) {
            if (option.isEnergy()) return capacitors;
            if (option.isFluid()) return tanks;
            if (option.isItem()) return invSize;
            return 1;
        }

        public CompoundTag toTag(CompoundTag tag) {
            tag.put("front", front.toTag(new CompoundTag()));
            tag.put("back", back.toTag(new CompoundTag()));
            tag.put("left", left.toTag(new CompoundTag()));
            tag.put("right", right.toTag(new CompoundTag()));
            tag.put("top", top.toTag(new CompoundTag()));
            tag.put("bottom", bottom.toTag(new CompoundTag()));
            return tag;
        }

        public void fromTag(CompoundTag tag) {
            front.fromTag(tag.getCompound("front"));
            back.fromTag(tag.getCompound("back"));
            left.fromTag(tag.getCompound("left"));
            right.fromTag(tag.getCompound("right"));
            top.fromTag(tag.getCompound("top"));
            bottom.fromTag(tag.getCompound("bottom"));
        }

        /**
         * Please do not modify the returned {@link ConfiguredSideOption}
         * @param face the block face to pull the option from
         * @return a {@link ConfiguredSideOption} assignd to the given face.
         */
        public ConfiguredSideOption get(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    return front;
                case TOP:
                    return top;
                case BACK:
                    return back;
                case RIGHT:
                    return right;
                case LEFT:
                    return left;
                case BOTTOM:
                    return bottom;
            }
            throw new RuntimeException();
        }

        public void set(@NotNull BlockFace face, SideOption option) {
            switch (face) {
                case FRONT:
                    setFrontOption(option);
                    break;
                case TOP:
                    setTopOption(option);
                    break;
                case BACK:
                    setBackOption(option);
                    break;
                case RIGHT:
                    setRightOption(option);
                    break;
                case LEFT:
                    setLeftOption(option);
                    break;
                case BOTTOM:
                    setBottomOption(option);
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        public void increment(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    front.increment();
                    break;
                case TOP:
                    top.increment();
                    break;
                case BACK:
                    back.increment();
                    break;
                case RIGHT:
                    right.increment();
                    break;
                case LEFT:
                    left.increment();
                    break;
                case BOTTOM:
                    bottom.increment();
                    break;
            }
        }
        public void decrement(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    front.decrement();
                    break;
                case TOP:
                    top.decrement();
                    break;
                case BACK:
                    back.decrement();
                    break;
                case RIGHT:
                    right.decrement();
                    break;
                case LEFT:
                    left.decrement();
                    break;
                case BOTTOM:
                    bottom.decrement();
                    break;
            }
        }
    }
}
