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

package dev.galacticraft.mod.compat.waila;

import dev.galacticraft.api.block.MachineBlock;
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.machine.MachineConfiguration;
import dev.galacticraft.mod.Constant;
import mcp.mobius.waila.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftWailaPlugin implements IWailaPlugin {
    private static final IBlockComponentProvider COMPONENT_PROVIDER = new IBlockComponentProvider() {
        @Override
        public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (Screen.hasShiftDown()) {
                MachineConfiguration configuration = MachineConfiguration.create();
                configuration.readNbt(accessor.getServerData());
                tooltip.addLine(Component.translatable("ui.galacticraft.machine.redstone.redstone", configuration.getRedstoneActivation().getName()).setStyle(Constant.Text.Color.RED_STYLE));
                if (configuration.getSecurity().getOwner() != null) tooltip.addLine(Component.translatable("ui.galacticraft.machine.security.owned_by", Component.literal(configuration.getSecurity().getOwner().getName()).setStyle(Constant.Text.Color.WHITE_STYLE)).setStyle(Constant.Text.Color.AQUA_STYLE));
            }
        }
    };

    @Override
    public void register(IRegistrar registrar) {
        registrar.addBlockData((data, accessor, config) -> ((MachineBlockEntity) accessor.getTarget()).getConfiguration().writeNbt(data), MachineBlock.class);
        registrar.addComponent(COMPONENT_PROVIDER, TooltipPosition.TAIL, MachineBlock.class);
    }
}
