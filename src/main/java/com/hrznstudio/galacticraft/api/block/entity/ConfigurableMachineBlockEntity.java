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
 *
 */

package com.hrznstudio.galacticraft.api.block.entity;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.internal.data.MinecraftServerTeamsGetter;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SidedInventory {
    private final SimpleInventoryComponent inventory = new SimpleInventoryComponent(getInventorySize()) {
        @Override
        public boolean isAcceptableStack(int slot, ItemStack stack) {
            return ConfigurableMachineBlockEntity.this.getFilterForSlot(slot).test(stack) || stack.isEmpty();
        }

        @Override
        public boolean canExtract(int slot) {
            return ConfigurableMachineBlockEntity.this.canHopperExtractItems(slot);
        }

        @Override
        public boolean canInsert(int slot) {
            return ConfigurableMachineBlockEntity.this.canHopperInsertItems(slot);
        }
    };
    private final InventoryWrapper wrapper = InventoryWrapper.of(getInventory());

    private final SimpleTankComponent fluidTank = new SimpleTankComponent(getFluidTankSize(), getFluidTankMaxCapacity()) {
        @Override
        public boolean canExtract(int slot) {
            return ConfigurableMachineBlockEntity.this.canExtractFluid(slot);
        }

        @Override
        public boolean canInsert(int slot) {
            return ConfigurableMachineBlockEntity.this.canInsertFluid(slot);
        }

        @Override
        public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
            for (int i = 0; i < contents.size(); i++) {
                if (isAcceptableFluid(i, fluid)) {
                    fluid = insertFluid(i, fluid, action);
                    if (fluid.isEmpty()) return fluid;
                }
            }

            return fluid;
        }

        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (isAcceptableFluid(tank, fluid)) {
                return super.insertFluid(tank, fluid, action);
            }
            return fluid;
        }

        public boolean isAcceptableFluid(int tank, FluidVolume volume) { //how are you supposed to check if its acceptable if you *only* get the tank and no fluid?!
            return ConfigurableMachineBlockEntity.this.isAcceptableFluid(tank, volume);
        }

        @Override
        public boolean isAcceptableFluid(int tank) {
            return false;
        }
    };

    private final SimpleTankComponent oxygenTank = new SimpleTankComponent(getFluidTankSize(), getFluidTankMaxCapacity()) {
        @Override
        public boolean canExtract(int slot) {
            return ConfigurableMachineBlockEntity.this.canExtractOxygen(slot);
        }

        @Override
        public boolean canInsert(int slot) {
            return ConfigurableMachineBlockEntity.this.canInsertOxygen(slot);
        }

        @Override
        public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
            if (fluid.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                for (int i = 0; i < contents.size(); i++) {
                    fluid = insertFluid(i, fluid, action);
                    if (fluid.isEmpty()) return fluid;
                }
            }

            return fluid;
        }

        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (fluid.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                return super.insertFluid(tank, fluid, action);
            }
            return fluid;
        }

        @Override
        public void setFluid(int slot, FluidVolume stack) {
            if (stack.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                super.setFluid(slot, stack);
            }
        }

        @Override
        public boolean isAcceptableFluid(int tank) { //how are you supposed to check if its acceptable if you *only* get the tank and no fluid?!
            return false;
        }
    };

    private final SimpleCapacitorComponent capacitor = new SimpleCapacitorComponent(getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES) {
        @Override
        public boolean canExtractEnergy() {
            return ConfigurableMachineBlockEntity.this.canExtractEnergy();
        }

        @Override
        public boolean canInsertEnergy() {
            return ConfigurableMachineBlockEntity.this.canInsertEnergy();
        }
    };

    private final SecurityInfo security = new SecurityInfo();
    private final SideConfigInfo sideConfigInfo = new SideConfigInfo(validSideOptions(), 1, getInventorySize(), getFluidTankSize(), getOxygenTankSize());

    private RedstoneState redstoneState = RedstoneState.DISABLED;

    public ConfigurableMachineBlockEntity(BlockEntityType<? extends ConfigurableMachineBlockEntity> blockEntityType) {
        super(blockEntityType);
        this.getCapacitor().getListeners().add(this::markDirty);
        this.getInventory().getListeners().add(this::markDirty);
    }

    protected abstract boolean canExtractEnergy();

    protected abstract boolean canInsertEnergy();

    protected abstract int getEnergyUsagePerTick();

    protected abstract boolean canHopperExtractItems(int slot);

    protected abstract boolean canHopperInsertItems(int slot);

    protected abstract boolean canExtractOxygen(int tank);

    protected abstract boolean canInsertOxygen(int tank);

    protected abstract boolean canExtractFluid(int tank);

    protected abstract boolean canInsertFluid(int tank);

    protected abstract boolean isAcceptableFluid(int tank, FluidVolume volume);

    protected abstract int getInventorySize();

    protected abstract int getOxygenTankSize();

    protected abstract int getFluidTankSize();

    protected Fraction getOxygenTankMaxCapacity() {
        return Fraction.ZERO;
    }

    protected Fraction getFluidTankMaxCapacity() {
        return Fraction.ZERO;
    }

    public abstract List<SideOption> validSideOptions();

    public void setRedstoneState(@NotNull RedstoneState redstoneState) {
        this.redstoneState = redstoneState;
    }

    public MachineStatus getStatusForTooltip() {
        return null;
    }

    protected void decrement(int slot, int amount) {
        ItemStack stack = getInventory().getStack(slot);
        stack.decrement(amount);
        getInventory().setStack(slot, stack);
    }

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

    public final @NotNull SimpleCapacitorComponent getCapacitor() {
        return capacitor;
    }

    public final @NotNull SimpleInventoryComponent getInventory() {
        return inventory;
    }

    public final @NotNull SimpleTankComponent getOxygenTank() {
        return oxygenTank;
    }

    public final @NotNull SimpleTankComponent getFluidTank() {
        return fluidTank;
    }

    public final @NotNull SecurityInfo getSecurity() {
        return security;
    }

    public final @NotNull RedstoneState getRedstoneState() {
        return redstoneState;
    }

    public final @NotNull SideConfigInfo getSideConfigInfo() {
        return sideConfigInfo;
    }

    public final boolean canUse(PlayerEntity player) {
        return this.getSecurity().hasAccess(player);
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean disabled() {
        switch (this.redstoneState) {
            case OFF:
                return this.getWorld().isReceivingRedstonePower(pos);
            case ON:
                return !this.getWorld().isReceivingRedstonePower(pos);
            default:
                return false;
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("Redstone", redstoneState.asString());
        this.capacitor.toTag(tag);
        this.inventory.toTag(tag);
        this.security.toTag(tag);
        this.fluidTank.toTag(tag);
        this.oxygenTank.toTag(tag);
        this.sideConfigInfo.toTag(tag);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.redstoneState = RedstoneState.fromString(tag.getString("Redstone"));
        this.capacitor.fromTag(tag);
        this.inventory.fromTag(tag);
        this.security.fromTag(tag);
        this.fluidTank.fromTag(tag);
        this.oxygenTank.fromTag(tag);
        this.sideConfigInfo.fromTag(tag);
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

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(this.getCachedState(), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public @Nullable CapacitorComponent accessCapacitor(@NotNull BlockState state, @Nullable Direction side) {
        ConfiguredSideOption option = this.getSideConfigInfo().get(ConfigurableMachineBlock.BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), side));
        if (option.getOption().isEnergy()) {
            return capacitor;
        }
        return null;
    }

    public @Nullable InventoryComponent accessInventory(@NotNull BlockState state, @Nullable Direction side) {
        ConfiguredSideOption option = this.getSideConfigInfo().get(ConfigurableMachineBlock.BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), side));
        if (option.getOption().isItem()) {
            return inventory;
        }
        return null;
    }

    public @Nullable TankComponent accessFluidTank(@NotNull BlockState state, @Nullable Direction side) {
        ConfiguredSideOption option = this.getSideConfigInfo().get(ConfigurableMachineBlock.BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), side));
        if (option.getOption().isFluid()) {
            return fluidTank;
        }
        return null;
    }

    public @Nullable TankComponent accessOxygenTank(@NotNull BlockState state, @Nullable Direction side) {
        ConfiguredSideOption option = this.getSideConfigInfo().get(ConfigurableMachineBlock.BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), side));
        if (option.getOption().isOxygen()) {
            return oxygenTank;
        }
        return null;
    }

    public void trySpreadEnergy() { //TODO: actually use components properly
        BlockState state = world.getBlockState(pos);
        for (ConfigurableMachineBlock.BlockFace face : ConfigurableMachineBlock.BlockFace.values()) {
            SideOption option = state.get(((ConfigurableMachineBlock) state.getBlock()).getProperty(face));
            if (option == SideOption.POWER_INPUT || option == SideOption.POWER_OUTPUT) {
                Direction direction = face.toDirection(state.get(Properties.HORIZONTAL_FACING));
                BlockState other = world.getBlockState(pos.offset(direction));

                CapacitorComponent component = CapacitorComponentHelper.INSTANCE.getComponent(world, pos.offset(direction), direction.getOpposite(), "gcr:spread"); //

                if (component != null) {
                    if (option == SideOption.POWER_INPUT && ((ConfigurableMachineBlock) other.getBlock()).getOption(other, face.getOpposite()) == SideOption.POWER_OUTPUT) {
                        if (component.canExtractEnergy() && component.getPreferredType().isCompatibleWith(getCapacitor().getPreferredType())) {
                            int extracted = component.extractEnergy(getCapacitor().getPreferredType(), Math.min(256, getCapacitor().getMaxEnergy() - getCapacitor().getCurrentEnergy()), ActionType.PERFORM);
                            getCapacitor().insertEnergy(getCapacitor().getPreferredType(), extracted, ActionType.PERFORM);
                        }
                    } else {
                        if (component.canInsertEnergy() && component.getPreferredType().isCompatibleWith(getCapacitor().getPreferredType()) && ((ConfigurableMachineBlock) other.getBlock()).getOption(other, face.getOpposite()) == SideOption.POWER_INPUT) {
                            int extracted = getCapacitor().extractEnergy(getCapacitor().getPreferredType(), Math.min(256, component.getMaxEnergy() - component.getCurrentEnergy()), ActionType.PERFORM);
                            component.insertEnergy(getCapacitor().getPreferredType(), extracted, ActionType.PERFORM);
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
        ItemStack stack = inventory.getStack(slot);
        int neededEnergy = Math.min(getBatteryTransferRate(), getCapacitor().getMaxEnergy() - getCapacitor().getCurrentEnergy());
        if (EnergyUtils.isEnergyItem(stack)) {
            this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, EnergyUtils.extractEnergy(stack, neededEnergy, ActionType.PERFORM), ActionType.PERFORM);
            inventory.setStack(slot, stack);
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
        ItemStack stack = inventory.getStack(slot);
        if (EnergyUtils.isEnergyItem(stack)) {
            if (EnergyUtils.getEnergy(stack) < EnergyUtils.getMaxEnergy(stack)) {
                int i = EnergyUtils.insertEnergy(stack, available, ActionType.PERFORM);
                this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, available - i, ActionType.PERFORM);
                inventory.setStack(slot, stack);
            }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        ConfiguredSideOption configuredSideOption = this.getSideConfigInfo().get(ConfigurableMachineBlock.BlockFace.toFace(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING), side));
        if (configuredSideOption.isWildcard()) return IntStream.range(0, getInventorySize()).toArray(); //todo - account for #canHopperExtract
        else return new int[]{configuredSideOption.getValue()};
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

        return accessible && getInventory().canExtract(slot) && inventory.getStack(slot).isItemEqual(stack);
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

    public enum RedstoneState implements StringIdentifiable {
        /**
         * Ignores redstone entirely.
         */
        DISABLED,

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
                    return DISABLED;
            }
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }

    public interface MachineStatus {
        Text getText();
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
        private final int oxygenTanks;

        public SideConfigInfo(List<SideOption> values, int capacitors, int invSize, int tanks, int oxygenTanks) {
            if (!values.contains(SideOption.DEFAULT)) throw new RuntimeException();
            this.values = new ArrayList<>(values);
            this.values.sort(Enum::compareTo);
            this.capacitors = capacitors;
            this.invSize = invSize;
            this.tanks = tanks;
            this.oxygenTanks = oxygenTanks;

            this.front = ConfiguredSideOption.DEFAULT;
            this.back = ConfiguredSideOption.DEFAULT;
            this.left = ConfiguredSideOption.DEFAULT;
            this.right = ConfiguredSideOption.DEFAULT;
            this.top = ConfiguredSideOption.DEFAULT;
            this.bottom = ConfiguredSideOption.DEFAULT;
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
        }

        public void setBackOption(SideOption option) {
            back.setOption(option, getMax(option));
        }

        public void setLeftOption(SideOption option) {
            left.setOption(option, getMax(option));
        }

        public void setRightOption(SideOption option) {
            right.setOption(option, getMax(option));
        }

        public void setTopOption(SideOption option) {
            top.setOption(option, getMax(option));
        }

        public void setBottomOption(SideOption option) {
            bottom.setOption(option, getMax(option));
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

        public int getUpValue() {
            return top.getValue();
        }

        public int getDownValue() {
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
            if (option.isOxygen()) return oxygenTanks;
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
        public ConfiguredSideOption get(@NotNull ConfigurableMachineBlock.BlockFace face) {
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

        public void increment(@NotNull ConfigurableMachineBlock.BlockFace face) {
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
        public void decrement(@NotNull ConfigurableMachineBlock.BlockFace face) {
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
