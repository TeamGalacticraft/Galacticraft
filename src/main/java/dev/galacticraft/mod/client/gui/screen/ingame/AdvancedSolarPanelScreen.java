/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.AdvancedSolarPanelBlockEntity;
import dev.galacticraft.mod.screen.SolarPanelMenu;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AdvancedSolarPanelScreen extends SolarPanelScreen<AdvancedSolarPanelBlockEntity, SolarPanelMenu<AdvancedSolarPanelBlockEntity>> {
    public AdvancedSolarPanelScreen(SolarPanelMenu<AdvancedSolarPanelBlockEntity> handler, Inventory inv, Component title) {
        super(handler, inv, title);
    }

    @Override
    public void appendEnergyTooltip(List<Component> list) {
        if (this.menu.state.isActive()) {
            list.add(Component.translatable(Translations.Ui.GJT, this.menu.getCurrentEnergyGeneration()).setStyle(Constant.Text.Color.LIGHT_PURPLE_STYLE));
        }
    }
}
