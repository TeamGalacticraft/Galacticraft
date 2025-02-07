/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.MenuData;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class SolarPanelMenu<Machine extends MachineBlockEntity & SolarPanel> extends MachineMenu<Machine> {
    private final boolean followsSun;
    private final boolean nightCollection;

    private final boolean[] blockage = new boolean[9];
    private @NotNull SolarPanel.SolarPanelSource source;
    private long energyGeneration;

    public SolarPanelMenu(MenuType<? extends SolarPanelMenu<Machine>> type, int syncId, @NotNull Player player, @NotNull Machine machine) {
        super(type, syncId, player, machine);

        this.followsSun = machine.followsSun();
        this.nightCollection = machine.nightCollection();
        this.source = machine.getSource();
        this.energyGeneration = machine.getCurrentEnergyGeneration();
    }

    protected SolarPanelMenu(MenuType<? extends SolarPanelMenu<Machine>> type, int syncId, @NotNull Inventory inventory, @NotNull BlockPos pos) {
        this(type, syncId, inventory, pos, 8, 84);
    }

    protected SolarPanelMenu(MenuType<? extends SolarPanelMenu<Machine>> type, int syncId, @NotNull Inventory inventory, @NotNull BlockPos pos, int invX, int invY) {
        super(type, syncId, inventory, pos, invX, invY);

        this.followsSun = this.be.followsSun();
        this.nightCollection = this.be.nightCollection();
        this.source = this.be.getSource();
        this.energyGeneration = this.be.getCurrentEnergyGeneration();
    }

    @Override
    public void registerData(@NotNull MenuData data) {
        super.registerData(data);

        data.registerLong(this.be::getCurrentEnergyGeneration, this::setEnergyGeneration);
        data.registerEnum(SolarPanel.SolarPanelSource.values(), this.be::getSource, this::setSource);
        data.registerBits(9, this.be.getBlockage(), this.blockage);
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

    @Override
    public boolean isFaceLocked(@NotNull BlockFace face) {
        return face == BlockFace.TOP;
    }
}
