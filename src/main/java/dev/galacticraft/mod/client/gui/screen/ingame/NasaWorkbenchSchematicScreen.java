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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.screen.NasaWorkbenchSchematicMenu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NasaWorkbenchSchematicScreen<M extends NasaWorkbenchSchematicMenu> extends AbstractNasaWorkbenchScreen<M> {
    
    public NasaWorkbenchSchematicScreen(M schematicWorkbenchMenu, Inventory inventory, Component component) {
        super(schematicWorkbenchMenu, inventory, component, Constant.ScreenTexture.SCHEMATIC_UNLOCK_SCREEN, 0);

        this.imageWidth = 176;
        this.imageHeight = 177;

        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    public void init() {
        super.init();

        this.addRenderableWidget(new Button(this.leftPos + (this.imageWidth / 2) - 50, this.topPos + 60, 100, 20, Component.literal("Unlock Schematic"), (button) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(PacketByteBufs.create());
            buf.writeItem(this.menu.getSchematic());
            
            ClientPlayNetworking.send(Constant.Packet.UNLOCK_SCHEMATIC, buf);
        }));
    }
}
