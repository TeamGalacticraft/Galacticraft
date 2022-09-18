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

package dev.galacticraft.mod.network;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import dev.galacticraft.mod.screen.GCPlayerInventoryScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;

/**
 * Handles server-bound (C2S) packets.
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCServerPacketReceiver {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "open_gc_inv"), (server, player, handler, buf, responseSender) -> server.execute(() -> player.openMenu(new SimpleMenuProvider(GCPlayerInventoryScreenHandler::new, Component.empty()))));

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "bubble_max"), (server, player, handler, buf, responseSender) -> {
            byte max = buf.readByte();
            server.execute(() -> {
                if (player.containerMenu instanceof BubbleDistributorScreenHandler sHandler) {
                    BubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.getSecurity().hasAccess(player)) {
                        if (max > 0) {
                            machine.setTargetSize(max);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "bubble_visible"), (server, player, handler, buf, responseSender) -> {
            boolean visible = buf.readBoolean();
            server.execute(() -> {
                if (player.containerMenu instanceof BubbleDistributorScreenHandler sHandler) {
                    BubbleDistributorBlockEntity machine = sHandler.machine;
                    if (machine.getSecurity().hasAccess(player)) {
                        machine.bubbleVisible = visible;
                    }
                }
            });
        });
    }
}
