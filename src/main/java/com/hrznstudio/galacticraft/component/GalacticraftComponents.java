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
import com.hrznstudio.galacticraft.api.block.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.api.pipe.Pipe;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.items.BatteryItem;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.ItemCapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import io.github.cottonmc.component.fluid.impl.ItemTankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.SyncedInventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GalacticraftComponents implements EntityComponentInitializer, BlockComponentInitializer, ItemComponentInitializer {
    public static final List<Identifier> MACHINE_BLOCKS = new LinkedList<>();
    public static final ComponentKey<InventoryComponent> GEAR_INVENTORY_COMPONENT = ComponentRegistry.getOrCreate(new Identifier(Constants.MOD_ID, "gear_inv"), InventoryComponent.class);

    public static void register() {
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(GalacticraftComponents.GEAR_INVENTORY_COMPONENT, player -> new SyncedInventoryComponent<>(12, GEAR_INVENTORY_COMPONENT, (ComponentProvider) player), RespawnCopyStrategy.INVENTORY);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {

        registry.registerFor(WireBlockEntity.class, UniversalComponents.CAPACITOR_COMPONENT, be -> new SimpleCapacitorComponent(be.getMaxTransferRate(), GalacticraftEnergy.GALACTICRAFT_JOULES) {
            @Override
            public int getCurrentEnergy() {
                return 0;
            }

            @Override
            public int insertEnergy(EnergyType type, int amount, ActionType actionType) {
                if (type.isCompatibleWith(getPreferredType())) {
                    if (be.getWorld() != null && !be.getWorld().isClient) {
                        return be.getNetwork().insertEnergy(be.getPos(), null, type, amount, actionType);
                    }
                }
                return amount;
            }

            @Override
            public int extractEnergy(EnergyType type, int amount, ActionType actionType) {
                return 0;
            }

            @Override
            public int generateEnergy(World world, BlockPos pos, int amount) {
                return amount;
            }

            @Override
            public List<Runnable> getListeners() {
                return Collections.emptyList();
            }

            @Override
            public void listen(@NotNull Runnable listener) {
            }

            @Override
            public SimpleCapacitorComponent setCurrentEnergy(int amount) {
                return this;
            }

            @Override
            public SimpleCapacitorComponent setMaxEnergy(int amount) {
                return this;
            }

            @Override
            public boolean canExtractEnergy() {
                return true;
            }

            @Override
            public boolean canInsertEnergy() {
                return true;
            }
        });

        registry.registerFor(FluidPipeBlockEntity.class, UniversalComponents.TANK_COMPONENT, be -> new SimpleTankComponent(1, Fraction.of(10, 1000)) {
            @Override
            public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
                if (be.getWorld() != null && !be.getWorld().isClient && be.getData() == Pipe.FluidData.EMPTY && be.getNetwork() != null) {
                    Pipe.FluidData data = be.getNetwork().insertFluid(be.getPos(), null, fluid, action);
                    if (action == ActionType.PERFORM) {
                        if (data == null) {
                            return fluid;
                        }
                        be.setData(data);
                    }
                    return new FluidVolume(data.getFluidVolume().getFluid(), fluid.getAmount().subtract(data.getFluidVolume().getAmount()));
                }
                return fluid;
            }

            @Override
            public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
                return insertFluid(fluid, action);
            }

            @Override
            public boolean canInsert(int slot) {
                return be.getWorld() != null && !be.getWorld().isClient && be.getData() == Pipe.FluidData.EMPTY && be.getNetwork() != null;
            }

            @Override
            public FluidVolume removeFluid(int slot, ActionType action) {
                return FluidVolume.EMPTY;
            }

            @Override
            public FluidVolume takeFluid(int slot, Fraction amount, ActionType action) {
                return FluidVolume.EMPTY;
            }

            @Override
            public void setFluid(int slot, FluidVolume stack) {
                insertFluid(stack, ActionType.PERFORM);
            }

            @Override
            public boolean isAcceptableFluid(int tank) {
                return true;
            }

            @Override
            public FluidVolume getContents(int slot) {
                return FluidVolume.EMPTY;
            }
        });

        for (Identifier id : MACHINE_BLOCKS) {
            registry.registerFor(id, UniversalComponents.INVENTORY_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getInventory(state, side));
            registry.registerFor(id, UniversalComponents.CAPACITOR_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getCapacitor(state, side));
            registry.registerFor(id, UniversalComponents.TANK_COMPONENT, (state, world, pos, side) -> ((ConfigurableMachineBlockEntity) world.getBlockEntity(pos)).getFluidTank(state, side));
        }

        MACHINE_BLOCKS.clear();
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.registerFor((item) -> item instanceof OxygenTankItem, UniversalComponents.TANK_COMPONENT, stack -> {
            ItemTankComponent component = new ItemTankComponent(1, Fraction.of(1, 100).multiply(Fraction.of(stack.getItem().getMaxDamage(), 1000))) {
                @Override
                public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
                    if (fluid.getFluid().isIn(GalacticraftTags.OXYGEN) || fluid.isEmpty()) {
                        return super.insertFluid(tank, fluid, action);
                    } else {
                        return fluid;
                    }
                }

                @Override
                public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
                    return insertFluid(0, fluid, action);
                }

                @Override
                public void setFluid(int slot, FluidVolume stack) {
                    if (stack.getFluid().isIn(GalacticraftTags.OXYGEN) || stack.isEmpty()) {
                        super.setFluid(slot, stack);
                    }
                }
            };
            component.listen(() -> stack.setDamage(stack.getItem().getMaxDamage() - (int)(component.getContents(0).getAmount().doubleValue() * 1000.0D)));
            return component;
        });

        registry.registerFor((item) -> item instanceof BatteryItem, UniversalComponents.CAPACITOR_COMPONENT, stack -> {
            ItemCapacitorComponent component = new ItemCapacitorComponent(((BatteryItem)stack.getItem()).getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES);
            component.listen(() -> stack.setDamage(component.getMaxEnergy() - component.getCurrentEnergy()));
            return component;
        });

        registry.registerFor(GalacticraftItems.INFINITE_BATTERY, UniversalComponents.CAPACITOR_COMPONENT, stack -> new ItemCapacitorComponent(Integer.MAX_VALUE, GalacticraftEnergy.GALACTICRAFT_JOULES) {
            @Override
            public boolean canInsertEnergy() {
                return false;
            }

            @Override
            public SimpleCapacitorComponent setCurrentEnergy(int amount) {
                return this;
            }

            @Override
            public int extractEnergy(EnergyType type, int amount, ActionType actionType) {
                return amount;
            }

            @Override
            public int insertEnergy(EnergyType type, int amount, ActionType actionType) {
                return amount;
            }

            @Override
            public int getCurrentEnergy() {
                return Integer.MAX_VALUE;
            }
        });
    }
}
