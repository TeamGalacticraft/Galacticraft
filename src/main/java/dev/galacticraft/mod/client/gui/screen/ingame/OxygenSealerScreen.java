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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.api.screen.SimpleMachineMenu;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.OxygenSealerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenSealerScreen extends MachineScreen<OxygenSealerBlockEntity, SimpleMachineMenu<OxygenSealerBlockEntity>> {
    public OxygenSealerScreen(SimpleMachineMenu<OxygenSealerBlockEntity> handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_SEALER_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new Button(this.leftPos + 60, this.topPos + 50, 80, 15, Component.literal("Disable Seal"), button -> ClientPlayNetworking.send(Constant.Packet.DISABLE_SEAL, PacketByteBufs.create())));
    }

    @Override
    protected void renderForeground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderForeground(matrices, mouseX, mouseY, delta);

        this.font.draw(matrices, Component.translatable("ui.galacticraft.machine.status").append(this.machine.getStatus().name()), this.leftPos + 50, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor());
        this.font.draw(matrices, Component.translatable("ui.galacticraft.machine.status").append(this.machine.getStatus().name()), this.leftPos + 50, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor());
    }
}
