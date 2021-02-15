/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.api.block.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.api.pipe.Pipe;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.items.BatteryItem;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.ItemCapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GalacticraftComponents implements BlockComponentInitializer, ItemComponentInitializer {
    public static void register() {
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
                    return be.getNetwork().insertEnergy(be.getPos(), null, type, amount, actionType);
                } else {
                    return amount;
                }
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
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
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
