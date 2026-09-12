/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.machinelib.api.machine.configuration.IOFace;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.transfer.ResourceFlow;
import dev.galacticraft.machinelib.api.transfer.ResourceType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.content.block.entity.machine.OxygenDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class OxygenDetectorMenu extends MachineMenu<OxygenDetectorBlockEntity> {
    public OxygenDetectorMenu(int syncId, Player player, OxygenDetectorBlockEntity machine) {
        super(GCMenuTypes.OXYGEN_DETECTOR, syncId, player, machine);
    }

    public OxygenDetectorMenu(int syncId, Inventory inv, BlockPos pos) {
        super(GCMenuTypes.OXYGEN_DETECTOR, syncId, inv, pos, 8, 84);
    }

    /*
        NOTE: This is obviously temporary. This was the only way I could use configuration panel.
        Can be fixed AFTER changes in: https://github.com/TeamGalacticraft/MachineLib/pull/27
    */
    @Override
    public void cycleFaceConfig(BlockFace face, boolean reverse, boolean reset) {
        IOFace option = this.configuration.get(face);
        ResourceType type = option.getType();
        option.setOption(type == ResourceType.NONE ? ResourceType.ANY : ResourceType.NONE, ResourceFlow.INPUT);

        this.be.setChanged();
    }
}
