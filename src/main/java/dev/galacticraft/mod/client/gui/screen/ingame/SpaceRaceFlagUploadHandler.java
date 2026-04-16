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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.NativeImage;
import dev.galacticraft.impl.network.c2s.FlagDataPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class SpaceRaceFlagUploadHandler {
    private SpaceRaceFlagUploadHandler() {
    }

    static void handleFilesDrop(SpaceRaceScreen screen, List<Path> paths) {
        if (screen.menu != SpaceRaceMenu.TEAM_FLAG || paths.isEmpty()) {
            return;
        }

        File file = paths.get(0).toFile();
        NativeImage image;
        assert file.exists();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            image = NativeImage.read(fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        if (image.getWidth() != 48 || image.getHeight() != 32) {
            return;
        }

        final NativeImage finalImage = image;
        final DynamicTexture texture = new DynamicTexture(finalImage);
        ResourceLocation location = Constant.id("temp_flag");
        screen.minecraftClient().getTextureManager().register(location, texture);
        screen.minecraftClient().setScreen(new ConfirmFlagScreen(yes -> {
            if (yes) {
                ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(48 * 32 * 3, 48 * 32 * 3);
                for (int y = 0; y < 32; y++) {
                    for (int x = 0; x < 48; x++) {
                        int color = finalImage.getPixelRGBA(x, y);
                        buf.writeByte((color >> 16) & 0xFF)
                                .writeByte((color >> 8) & 0xFF)
                                .writeByte(color & 0xFF);
                    }
                }

                byte[] data;
                if (buf.hasArray()) {
                    data = buf.array();
                } else {
                    data = new byte[buf.readableBytes()];
                    buf.getBytes(buf.readerIndex(), data);
                }

                ClientPlayNetworking.send(new FlagDataPayload(data));
                screen.minecraftClient().getTextureManager().register(screen.teamFlag, texture);
            } else {
                finalImage.close();
            }
            screen.minecraftClient().setScreen(screen);
        }, location, Component.translatable(Translations.SpaceRace.FLAG_CONFIRM), Component.translatable(Translations.SpaceRace.FLAG_CONFIRM_MESSAGE)));
    }
}
