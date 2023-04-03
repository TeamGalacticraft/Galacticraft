/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.screen;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.sync.MenuSyncHandler;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SolarPanelMenu<Machine extends MachineBlockEntity & SolarPanel> extends MachineMenu<Machine> {
    private final boolean followsSun;
    private final boolean nightCollection;

    private boolean[] blockage;
    private @NotNull SolarPanel.SolarPanelSource source;
    private long energyGeneration;

    public SolarPanelMenu(int syncId, @NotNull ServerPlayer player, @NotNull Machine machine) {
        super(syncId, player, machine);

        this.followsSun = machine.followsSun();
        this.nightCollection = machine.nightCollection();
        this.source = machine.getSource();
        this.energyGeneration = machine.getCurrentEnergyGeneration();
    }

    protected SolarPanelMenu(int syncId, @NotNull Inventory inventory, @NotNull FriendlyByteBuf buf, @NotNull MachineType<Machine, ? extends MachineMenu<Machine>> type) {
        this(syncId, inventory, buf, 8, 84, type);
    }

    protected SolarPanelMenu(int syncId, @NotNull Inventory inventory, @NotNull FriendlyByteBuf buf, int invX, int invY, @NotNull MachineType<Machine, ? extends MachineMenu<Machine>> type) {
        super(syncId, inventory, buf, invX, invY, type);

        this.followsSun = buf.readBoolean();
        this.nightCollection = buf.readBoolean();
        this.source = SolarPanel.SolarPanelSource.values()[buf.readByte()];
        this.energyGeneration = buf.readVarLong();
    }

    @Override
    public void registerSyncHandlers(Consumer<MenuSyncHandler> consumer) {
        super.registerSyncHandlers(consumer);

        consumer.accept(MenuSyncHandler.simple(this.machine::getCurrentEnergyGeneration, this::setEnergyGeneration));
        consumer.accept(MenuSyncHandler.simple(this.machine::getSource, this::setSource, SolarPanel.SolarPanelSource.values()));
        this.blockage = new boolean[9];
        consumer.accept(MenuSyncHandler.booleans(this.machine.getBlockage(), this.blockage));
    }

    public boolean @NotNull [/*9*/] getBlockage() {
        return this.blockage;
    }

    public boolean followsSun() {
        return this.followsSun;
    }

    public boolean nightCollection() {
        return this.nightCollection;
    }

    public SolarPanel.@NotNull SolarPanelSource getSource() {
        return this.source;
    }

    public long getCurrentEnergyGeneration() {
        return this.energyGeneration;
    }

    public void setSource(SolarPanel.@NotNull SolarPanelSource source) {
        this.source = source;
    }

    public void setEnergyGeneration(long energyGeneration) {
        this.energyGeneration = energyGeneration;
    }
}
