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

package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.impl.ItemCapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import nerdhub.cardinal.components.api.component.ComponentContainer;
import nerdhub.cardinal.components.api.component.extension.CopyableComponent;
import nerdhub.cardinal.components.api.event.ItemComponentCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class InfiniteBatteryItem extends Item implements ItemComponentCallback {
    public InfiniteBatteryItem(Settings settings) {
        super(settings);
        ItemComponentCallback.registerSelf(this);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack repairMaterial) {
        return false;
    }

    @Override
    public void initComponents(ItemStack stack, ComponentContainer<CopyableComponent<?>> components) {
        components.put(UniversalComponents.CAPACITOR_COMPONENT, new ItemCapacitorComponent(Integer.MAX_VALUE, GalacticraftEnergy.GALACTICRAFT_JOULES) {
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
