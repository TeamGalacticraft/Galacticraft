/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.api.block;

import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.attribute.NullAutomatable;
import dev.galacticraft.mod.screen.slot.ResourceFlow;
import dev.galacticraft.mod.screen.slot.ResourceType;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public final class AutomationType<T> {
    private static final List<AutomationType<?>> TYPES = new ArrayList<>(16);

    public static final AutomationType<?> NONE = new AutomationType<>(ResourceType.NONE, ResourceFlow.BOTH);
    public static final AutomationType<?> ANY_INPUT = new AutomationType<>(ResourceType.ANY, ResourceFlow.INPUT);
    public static final AutomationType<?> ANY_OUTPUT = new AutomationType<>(ResourceType.ANY, ResourceFlow.OUTPUT);
    public static final AutomationType<?> ANY_IO = new AutomationType<>(ResourceType.ANY, ResourceFlow.BOTH);
    public static final AutomationType<Long> ENERGY_INPUT = new AutomationType<>(ResourceType.ENERGY, ResourceFlow.INPUT);
    public static final AutomationType<Long> ENERGY_OUTPUT = new AutomationType<>(ResourceType.ENERGY, ResourceFlow.OUTPUT);
    public static final AutomationType<Long> ENERGY_IO = new AutomationType<>(ResourceType.ENERGY, ResourceFlow.BOTH);
    public static final AutomationType<FluidVariant> FLUID_INPUT = new AutomationType<>(ResourceType.FLUID, ResourceFlow.INPUT);
    public static final AutomationType<FluidVariant> FLUID_OUTPUT = new AutomationType<>(ResourceType.FLUID, ResourceFlow.OUTPUT);
    public static final AutomationType<FluidVariant> FLUID_IO = new AutomationType<>(ResourceType.FLUID, ResourceFlow.BOTH);
    public static final AutomationType<Gas> GAS_INPUT = new AutomationType<>(ResourceType.GAS, ResourceFlow.INPUT);
    public static final AutomationType<Gas> GAS_OUTPUT = new AutomationType<>(ResourceType.GAS, ResourceFlow.OUTPUT);
    public static final AutomationType<Gas> GAS_IO = new AutomationType<>(ResourceType.GAS, ResourceFlow.BOTH);
    public static final AutomationType<ItemVariant> ITEM_INPUT = new AutomationType<>(ResourceType.ITEM, ResourceFlow.INPUT);
    public static final AutomationType<ItemVariant> ITEM_OUTPUT = new AutomationType<>(ResourceType.ITEM, ResourceFlow.OUTPUT);
    public static final AutomationType<ItemVariant> ITEM_IO = new AutomationType<>(ResourceType.ITEM, ResourceFlow.BOTH);
    
    private final Text name;
    private final ResourceType<T> resource;
    private final ResourceFlow flow;
    private final byte index;

    private AutomationType(ResourceType<T> resource, ResourceFlow flow) {
        TYPES.add(this);
        this.name = resource.getName().copy().append(flow.getName());
        this.resource = resource;
        this.flow = flow;
        this.index = (byte) TYPES.size();
    }

    public ResourceType<T> getResource() {
        return resource;
    }

    public ResourceFlow getFlow() {
        return flow;
    }

    public <OT> boolean willAccept(ResourceType<OT> type) {
        return this.resource.willAcceptResource(type);
    }

    public byte getIndex() {
        return this.index;
    }

    public boolean canFlow(ResourceFlow flow) {
        return this.flow.canFlowIn(flow);
    }

    public boolean isBidirectional() {
        return this.flow == ResourceFlow.BOTH;
    }

    public <OT> boolean equalToOrBroaderThan(AutomationType<OT> other) {
        if (other == this) return true;
        return this.willAccept(other.getResource()) && this.canFlow(other.getFlow());
    }

    public <OT> boolean typeAllows(AutomationType<OT> other) {
        if (other == this) return true;
        return this.willAccept(other.getResource());
    }

    @Contract("_ -> new")
    public @NotNull Automatable<T> getLinkedResources(MachineBlockEntity machine) {
        if (this.resource == ResourceType.ANY || this.resource == ResourceType.NONE) {
            return (Automatable<T>) NullAutomatable.INSTANCE;
        } else if (this.resource == ResourceType.ITEM) {
            return (Automatable<T>) machine.itemStorage();
        } else if (this.resource == ResourceType.ENERGY) {
            return (Automatable<T>) machine.capacitor();
        } else if (this.resource == ResourceType.GAS) {
            return (Automatable<T>) machine.gasStorage();
        } else if (this.resource == ResourceType.FLUID) {
            return (Automatable<T>) machine.fluidInv();
        }
        throw new AssertionError();
    }

    public Text getName() {
        return this.name;
    }

    public static <T> AutomationType<T> getType(byte index) {
        return (AutomationType<T>) TYPES.get(index);
    }
}
