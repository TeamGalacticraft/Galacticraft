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

package com.hrznstudio.galacticraft.component;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.EntitySyncedInventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class GalacticraftComponents implements EntityComponentInitializer, BlockComponentInitializer, ItemComponentInitializer {
    public static final List<Identifier> MACHINE_BLOCKS = new LinkedList<>();
    public static final ComponentType<InventoryComponent> GEAR_INVENTORY_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Constants.MOD_ID, "gear_inv"), InventoryComponent.class);

    public static void register() {
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(GalacticraftComponents.GEAR_INVENTORY_COMPONENT, player -> new EntitySyncedInventoryComponent(12, player), RespawnCopyStrategy.INVENTORY);
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

            public boolean isAcceptableFluid(int tank, FluidVolume volume) {
                return be.isAcceptableFluid(tank, volume);
            }

            @Override
            public void setFluid(int slot, FluidVolume stack) {
                if (isAcceptableFluid(slot, stack)) super.setFluid(slot, stack);
            }

            @Override
            public boolean isAcceptableFluid(int tank) {//how are you supposed to check if its acceptable if you *only* get the tank and no fluid?! also currently unused?
                return canInsert(tank);
            }

            @Override
            public void fromTag(CompoundTag tag) {
                this.clear();
                if (this.getTanks() == 0) return;
                super.fromTag(tag);
            }
        });

        for (Identifier id : MACHINE_BLOCKS) { //CC API v3
            registry.registerFor(id, UniversalComponents.INVENTORY_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getInventory(state, side));
            registry.registerFor(id, UniversalComponents.CAPACITOR_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getCapacitor(state, side));
            registry.registerFor(id, UniversalComponents.TANK_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getFluidTank(state, side));
        }

        MACHINE_BLOCKS.clear();
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry itemComponentFactoryRegistry) {

    }
}
