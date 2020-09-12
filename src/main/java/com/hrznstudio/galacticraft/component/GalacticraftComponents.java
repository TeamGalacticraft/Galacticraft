package com.hrznstudio.galacticraft.component;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.component.impl.SimpleOxygenTankComponent;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.cottonmc.component.item.impl.EntitySyncedInventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class GalacticraftComponents implements EntityComponentInitializer, BlockComponentInitializer {
    public static final ComponentKey<SimpleOxygenTankComponent> OXYGEN_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Constants.MOD_ID, "oxygen_tank"), SimpleOxygenTankComponent.class);
    public static final List<Identifier> MACHINE_BLOCKS = new LinkedList<>();

    public static void register() {
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(UniversalComponents.INVENTORY_COMPONENT, player -> {
            EntitySyncedInventoryComponent inventory = new EntitySyncedInventoryComponent(12, player);
            ((GCPlayerAccessor) player).setGearInventory(inventory);
            return inventory;
        }, RespawnCopyStrategy.INVENTORY);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.CAPACITOR_COMPONENT, be -> new SimpleCapacitorComponent(be.getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES) {
            @Override
            public boolean canExtractEnergy() {
                return be.canExtractEnergy();
            }

            @Override
            public boolean canInsertEnergy() {
                return be.canInsertEnergy();
            }

            @Override
            public void fromTag(CompoundTag tag) {
                if (getMaxEnergy() == 0) return;
                super.fromTag(tag);
            }
        });

        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.INVENTORY_COMPONENT, be -> new SimpleInventoryComponent(be.getInventorySize()) {
            @Override
            public boolean isAcceptableStack(int slot, ItemStack stack) {
                return be.getFilterForSlot(slot).test(stack) || stack.isEmpty();
            }

            @Override
            public boolean canExtract(int slot) {
                return be.canHopperExtractItems(slot);
            }

            @Override
            public boolean canInsert(int slot) {
                return be.canHopperInsertItems(slot);
            }

            @Override
            public void fromTag(CompoundTag tag) {
                this.clear();
                if (be.size() == 0) return;
                super.fromTag(tag);
            }
        });

        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.TANK_COMPONENT, be -> new SimpleTankComponent(be.getFluidTankSize(), be.getFluidTankMaxCapacity()) {
            @Override
            public boolean canExtract(int slot) {
                return be.canExtractFluid(slot);
            }

            @Override
            public boolean canInsert(int slot) {
                return be.canInsertFluid(slot);
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
                return be.isAcceptableFluid(tank, volume);
            }

            @Override
            public boolean isAcceptableFluid(int tank) {
                return false;
            }

            @Override
            public void fromTag(CompoundTag tag) {
                this.clear();
                if (this.getTanks() == 0) return;
                super.fromTag(tag);
            }
        });

        registry.registerFor(ConfigurableMachineBlockEntity.class, GalacticraftComponents.OXYGEN_COMPONENT, be -> new SimpleOxygenTankComponent(be.getOxygenTankSize(), be.getOxygenTankMaxCapacity()) {
            @Override
            public boolean canExtract(int slot) {
                return be.canExtractOxygen(slot);
            }

            @Override
            public boolean canInsert(int slot) {
                return be.canInsertOxygen(slot);
            }

            @Override
            public void fromTag(CompoundTag tag) {
                this.clear();
                if (getTanks() == 0) return;
                super.fromTag(tag);
            }
        });

//        for (Identifier id : MACHINE_BLOCKS) {
//            registry.registerFor(id, UniversalComponents.INVENTORY_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getInventory(state, side));
//            registry.registerFor(id, UniversalComponents.CAPACITOR_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getCapacitor(state, side));
//            registry.registerFor(id, UniversalComponents.TANK_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getFluidTank(state, side));
//            registry.registerFor(id, GalacticraftComponents.OXYGEN_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getOxygenTank(state, side));
//        }

        MACHINE_BLOCKS.clear();
    }
}
